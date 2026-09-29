# MonadBox TUN 电量分析报告（全面）

> 基于当前源码的静态分析 + 数量级估算，不等同于单台设备实测。真实耗电受 SoC、网络制式、代理流量、屏幕
> 亮度与系统后台策略影响。TUN 模式的总体电量 = **数据面转发**（随流量变化） + **客户端轮询/通知**（受 UI
> 与亮屏门控）+ **native 常驻成本**（本次优化重点）+ **启动瞬态**。

## 1. 电量模型

```
总耗电 ≈ 数据面(流量驱动) + 客户端轮询(门控) + native 常驻(固定) + 启动瞬态
```

- **数据面**：每字节 = 用户态协议栈（mips）收发 + 加密；每连接 = `protect`/`markSocket`（JNI+Binder，`semaphore(4)`）+ 按需 UID 解析。
- **客户端轮询**：全部有亮屏/前台/订阅门控（见 §4），UI 不看时自动降频或停止。
- **native 常驻**：采样器、GC、日志 drainer——固定小头，但此前每 50ms 读一次 `/proc` 属可消除浪费。
- **启动瞬态**：冷启动期间 CPU 高频（编译/加载/TUN 栈初始化），已并行化压缩时长。

## 2. 数据面电量

| 项 | 成本 | 本次变化 |
| --- | --- | --- |
| 每字节协议栈 | 用户态收发+转发，随吞吐线性 | 无 |
| 每连接 `protect`/`markSocket` | 1 次 JNI + 1 次 Binder + 2 syscall，`semaphore(4)` 串行 | 无（安全上限，保持） |
| 每连接 UID 解析 | JNI+Binder+procfs 回退 | 正缓存 15s / 负缓存 5s → miss 大幅下降 |
| 加密（AEAD/TLS） | 低端 SoC 上可观，随流量 | 无 |
| MTU | 9000→1500：同吞吐下**包数更多**，每包 syscall/中断略增 | 已对齐 1500（用户指定） |

**MTU 权衡说明**：1500 相对 9000 每字节多约 1 次包级处理（低吞吐时影响可忽略）；但消除了大 MTU 的 PMTU
黑洞重传（一次重传成本远高于包数增量），并保证与移动网络兼容。电量上基本中性、正确性收益为主。

## 3. native 常驻成本（本次优化重点）

### 3.1 最近关闭采样器

| | 改动前 | 改动后 |
| --- | --- | --- |
| 周期 | 50ms 固定 | 活跃 **20ms** / 空闲退避 **250ms** |
| 每 tick 数据 | `Snapshot()` → 读 `/proc/<pid>/statm` + 切片分配 | `Range` 直遍历，无 `/proc`，空闲 tick 近零 |
| 空闲唤醒 | 20/s | 4/s（-80%，相对 20ms 固定 -92%） |
| `/proc` 读取 | 20/s | 0 |
| 短连接捕获 | 存活 ≥50ms 必捕获 | 存活 ≥20ms 必捕获（提升） |

电量影响：空闲态（VPN 无流量）是主要受益场景——移除每 50ms 的 `/proc` 文件读取与分配，并把常驻定时器
频率降至 4/s，允许 CPU 更深空闲。活跃期（有流量）设备本就因转发而唤醒，20ms 采样的增量成本可忽略。

### 3.2 Go GC

- `debug.SetMemoryLimit(RAM/4)` + `debug.SetGCPercent(200)`：有硬上限兜底时降低 GC 频率 → 减少数据面
  上的 STW 停顿尖峰（停顿期 CPU 波动也是电量尖峰）。峰值堆略升但被上限封顶。净电量：中性偏正。

### 3.3 日志

- `init()` 常驻 drainer 按 `log.Level()` 过滤，非 `[APP]` 的 Debug 级不再跨 JNI 写 logcat。
- 电量：logcat 高频率写入在弱网/DNS 劫持频繁时是可见 CPU+无线电项；过滤后大幅降低。

### 3.4 UID 解析（Kotlin 侧）

- 正缓存 15s / 预热后负缓存 5s / 容量 2048：新四元组首次才走 Binder，重复四元组命中缓存。
- 电量：减少 `ConnectivityService` Binder 往返 → 减少唤醒系统服务与进程切换。

## 4. 客户端轮询矩阵

| 轮询 | 周期 | 触发条件 | 电量等级 | 本次 |
| --- | --- | --- | --- | --- |
| 流量（ProxyFacade） | 2s / 后台 60s | 前台 + 亮屏 + 运行中；否则 60s 空转 | 中 | 未动 |
| 显示模式同步 | 1.5s / 后台 10s | 前台 + 亮屏 + 运行中；否则不查询核心 | 低 | 已门控 |
| 连接列表（ConnectionActivityRepository） | 1s | 前台 + 亮屏 + 运行中；否则挂起 | 中 | 已门控 |
| 运行状态快照 | 1s TTL 缓存 + 「快照实例 + 选择」记忆化 | 有订阅者 | 低 | 已加记忆化 |
| 代理组 | 1s 合并窗 / 500ms TTL 缓存 | 有订阅者 | 低 | 已加记忆化 |
| 代理组（延迟测试期） | 请求 500ms / 真正重建 1s | 代理页可见 + 测试进行中 | 低 | 已降频 |
| 本地 Tun 流量通知 | 2s | 亮屏 + 开启流量通知；否则自停；文本未变则跳过重建 | 中 | 已降耗 |
| RootTun 通知 | 4s / 熄屏 30s（亮屏广播提前唤醒） | RootTun 运行；文本未变则跳过重建 | 中 | 已降耗 |
| 流量统计落盘 | 5s 采样 / 30s 批量写 MMKV | 运行中；非前台或熄屏降为 30s 采样 | 低 | 已降耗 |
| Root 日志记录 | 2s | Root 模式 + 日志开启 | 中 | 未动 |
| 核心日志流 | 无消费者时不打开；空闲 20s 自停 | 有人取日志（日志页 / 录制） | 中 | 已按需 |
| 最近请求历史 | 1s 取连接快照 | 前台 + 亮屏 + 运行中 | 中 | native 采样器降耗 + 已门控 |

所有高频轮询均已按亮屏/前台/订阅门控：显示模式同步与连接列表现在跟随「前台 + 亮屏」起停，
不可见时不再查询核心。

## 5. 本次优化电量影响汇总

| 改动 | 位置 | 电量收益 |
| --- | --- | --- |
| 日志流按需订阅 + 退订 | `native/log.go`、`Clash.kt`、`main.cpp` | 无人看日志时每条核心日志的 marshal/JNI/decode 与 logcat 转发 → 0；不再重复 fan-out |
| 载荷记忆化 + 去掉流量字段 | `SessionRuntime.kt`、`native/tunnel.go` | 代理页停留期间不再每 tick 全量重算/重编码（命中缓存时零重建） |
| 流量统计采样分级 | `TrafficStatisticsCollector.kt` | 后台/熄屏唤醒 12/min→2/min |
| RootTun 熄屏轮询放宽 | `RootTunService.kt` | 熄屏状态 IPC 7.5/min→2/min，亮屏立即补刷 |
| 采样器：Range 替代 Snapshot + 空闲退避 | `tunnel/conn.go` | 空闲 `/proc` 20/s→0；唤醒 20/s→4/s；空闲 tick 近零 |
| 采样器：50ms→20ms（活跃） | `tunnel/conn.go` | 捕获提升；活跃期增量可忽略 |
| 日志级别过滤 | `native/log.go` | 每 DNS 劫持不再写 logcat |
| UID 正/负缓存 | `VpnTunTransport.kt` | Binder 频率大降 |
| GOGC 200 + 内存上限 | `native/main.go` | GC 停顿尖峰减少 |
| 启动三路并行 | `SessionRuntime.kt` | 冷启动高功耗段缩短 |
| 快速地址解析 | `Net.kt` | 减少每 miss 的分配/GC |
| MTU 1500 | 多处 | 避免 PMTU 重传；包数增量可忽略 |
| 显示模式同步门控 | `DefaultProxyModeController.kt` | 后台/熄屏期间 1.5s 核心查询 → 0 |
| 连接快照门控 | `ConnectionActivityRepository.kt` | 后台/熄屏期间 1s 连接 JSON 查询 → 0 |
| 通知文本去重 | `ServiceNotificationManager.kt`、`RootTunService.kt` | 空闲时不再重建 builder/PendingIntent/notify |
| 磁贴更新去重 + IO 读取 | `ProxyTileService.kt` | 面板可见期间只在状态变化时更新 |
| 配置/选择反序列化缓存 | `ProfileStore.kt`、`ProfileManager.kt` | 每次快照刷新少 2~3 次全量 JSON 解析 |
| 首页流量重组下沉 + 旋转层化 | `HomeViewModel.kt`、`HomePager.kt`、`TrafficDisplay.kt`、`NodeCard.kt` | 2s 流量更新不再重组整页；空闲卡片不再跑旋转动画 |

净结论：**数据面/客户端轮询电量不变或改善，native 空闲常驻成本显著下降**——TUN 无流量时的后台电耗是
本次优化的主要受益场景。

## 6. 剩余电量优化点

1. 连接列表 1s 轮询门控：仅当「统计/最近请求」页可见时轮询（当前运行中即轮询）。需权衡：近期请求历史只在
   UI 展示时有意义，UI 关闭时可暂停采样+轮询。中收益、产品取舍。**已完成**：改为「前台 + 亮屏」时 1s 轮询，
   否则挂起；代价是 App 不可见期间开关的短连接不进入历史。
2. 事件驱动采样（hook `Manager.Leave()`）：可 100% 捕获且省掉全部定时唤醒；需改 mihomo kernel，
   `sync-kernel.sh` 每次重克隆上游、无 patch 机制，不可持续。
3. 更细间隔（10ms）：收益递减，不推荐。
4. 通知/日志周期按需收紧：维持现状即可（已有门控）。
5. 连接采样做到端到端事件驱动（管理器 `Leave()` 回调 → 直接推给 App），可同时去掉 1s 轮询与 20ms 采样：
   与第 2 条同样的内核改动限制，暂不可行。

## 7. 真机验证方法

- 场景（各 30 分钟，同一设备/网络/配置）：Tun 无流量、Tun 持续下载、亮屏首页、流量通知开/关、RootTun。
- 命令：`adb shell dumpsys batterystats --reset` → 运行场景 → `adb shell dumpsys batterystats <pkg>`
  对比；辅助 `adb shell dumpsys cpuinfo`、`adb shell dumpsys netstats`（避免把网络耗电误判为轮询）。
- 重点对比项：空闲态 TUN 的 CPU 时间、wakelock 持有、`/proc` 读取次数（可用 `strace -c -e openat,read`
  或 `systrace` 佐证采样器优化）、logcat 写入频率（开启/关闭日志界面）。
