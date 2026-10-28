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

| | 旧实现 | 当前 |
| --- | --- | --- |
| 正常关闭捕获 | 20ms diff 采样 | `Manager.Leave()` 回调同步记录，关闭调用时即入 buffer |
| 兜底扫描 | 活跃 20ms / 空闲 250ms | 正常 **250ms**；只有 live 连接需要 UID 补齐且重试到期时短暂 **20ms** |
| 新连接唤醒 | 等下一次 20ms tick | `Manager.Join()` 回调立即唤醒兜底扫描 |
| 每 tick 数据 | `Snapshot()` → 读 `/proc/<pid>/statm` + 切片分配 | `Range` 直遍历，无 `/proc`，timer/map 跨 tick 复用 |
| 短连接捕获 | 存活 ≥20ms 必捕获 | 正常 `Close()` 路径 100% 捕获；极短且绕过 `Close()` 的异常移除走 250ms 兜底 |

电量影响：有长连接时不再永久维持 50Hz timer；正常关闭由事件同步捕获，`closedAt` 也改为真实关闭调用时间。
250ms 兜底只负责异常移除、过期和 UID 补齐后的状态回落。kernel 侧回调通过 `config/kernel-patches/`
在 `sync-kernel` 后自动应用。

### 3.2 Go GC

- `debug.SetMemoryLimit(RAM/4)` + `debug.SetGCPercent(200)`：有硬上限兜底时降低 GC 频率 → 减少数据面
  上的 STW 停顿尖峰（停顿期 CPU 波动也是电量尖峰）。峰值堆略升但被上限封顶。净电量：中性偏正。

### 3.3 日志

- `init()` 常驻 drainer 按 `log.Level()` 过滤，非 `[APP]` 的 Debug 级不再跨 JNI 写 logcat。
- 电量：logcat 高频率写入在弱网/DNS 劫持频繁时是可见 CPU+无线电项；过滤后大幅降低。

### 3.4 UID 解析（Kotlin 侧）

- 正缓存 15s / 预热后负缓存 5s / 容量 2048：新四元组首次才走 Binder，重复四元组命中缓存。预热窗在
  `transport.start()`（数据面开始收包）重新锚定 3s，窗内 miss 先重试（3×5ms、上限约 +15ms）再决定是否负缓存，
  以免冷启动首包洪峰被负缓存 5s（电量上只影响启动 3s，稳态不变）。核心侧补齐发出的查询带 `force` 标志：
  只复用正缓存、不读也不写负缓存，代价受 Go 侧预算约束（每连接 ≤3 次、间隔 ≥500ms），稳态唤醒不变。
- 电量：减少 `ConnectivityService` Binder 往返 → 减少唤醒系统服务与进程切换。
- 核心侧补齐（新增）：`find-process-mode != off` 时，活跃连接在 socket 存活期间先做有界补齐。App 轮询
  列表时每 tick 最多 8 条、每连接最多 3 次；后台时每 tick 最多 4 条、每连接最多 2 次，保证后台关闭后
  回放的最近请求也有 UID/系统标记。保留条目的重试与已投递 Unknown 行的修复仍只在轮询期间进行，后台不会
  无限重试。正/负缓存与 Kotlin 侧共用，稳态下每个新连接通常只多一次 Binder 查询。
- procfs 回退：先精确匹配完整 `local_address`，未命中才按端口匹配（仅同端口 UID 一致时采用），并覆盖
  v4-mapped v6 表，减少 miss 后的重复读表。

## 4. 客户端轮询矩阵

| 轮询 | 周期 | 触发条件 | 电量等级 | 本次 |
| --- | --- | --- | --- | --- |
| 流量（ProxyFacade） | 2s | 前台 + 亮屏 + 运行中；否则 Flow 挂起，无空转 timer | 中 | 已去后台空转 |
| 显示模式同步 | 5s | 前台 + 亮屏 + 运行中；否则 Flow 挂起，无空转 timer | 低 | 已去后台空转 |
| 连接列表（ConnectionActivityRepository） | 有订阅者 1s / 无订阅者 5s | 前台 + 亮屏 + 运行中；否则挂起 | 中 | 已按订阅门控 |
| 运行状态快照 | 1s TTL 缓存 + 「快照实例 + 选择」记忆化 + 重建后内容相等即复用 + 本地模式内容戳（未变时零 marshal/decode）+ 呈现结果按「载荷实例 + 生效选择」复用 | 有订阅者 | 低 | 已加记忆化 + 内容戳复用 |
| 运行状态快照（RootTun 跨进程） | 每次刷新都被服务端戳挡在「载荷未变」之外：不传输、不反序列化 | 有订阅者 + RootTun 模式 | 低 | 已加戳复用 |
| 代理组 | 1s 合并窗 / 500ms TTL 缓存 | 有订阅者 | 低 | 已加记忆化 |
| 代理组（延迟测试期） | 请求 500ms / 真正重建 1s | 代理页可见 + 测试进行中 | 低 | 已降频 |
| 本地 Tun 流量通知 | 2s | 亮屏 + 开启流量通知；否则自停；文本未变则跳过重建 | 中 | 已降耗 |
| RootTun 通知 | 4s / 熄屏 30s（亮屏广播提前唤醒） | RootTun 运行；文本未变则跳过重建 | 中 | 已降耗 |
| 流量统计落盘 | 5s 采样 / 30s 批量写 MMKV | 运行中；非前台或熄屏降为 30s 采样并主动刷新核心累计总量，回前台立即补采一次，停止前落最终结算点 | 低 | 已降耗 |
| Root 日志记录 | 2s | Root 模式 + 日志开启 | 中 | 未动 |
| 核心日志流 | 无消费者时不打开；空闲 20s 自停 | 有人取日志（日志页 / 录制） | 中 | 已按需 |
| 最近请求历史 | 1s（统计页可见）/ 5s（保温）取连接快照 | 前台 + 亮屏 + 运行中；正常关闭由 native `Manager.Leave()` 事件捕获，250ms 兜底；保留窗口 ≥6s 保证不漏；未被取走的关闭保留 30 分钟（单次响应最多回放 200 条，积压分多次 poll 排干），故后台期间的历史在回到前台后补记（后台零唤醒） | 中 | 事件驱动 + 订阅门控 + 两档过期 |
| 最近请求列表重建 | 统计页可见时随 1s 连接快照重建：起始时间按字符串缓存，行静态字段（应用身份/链/拒绝标记）60s TTL 缓存；无搜索词时先截断到 100 行再建行 | 统计页可见 | 低 | 已降耗（解析 O(n log n)→O(n)，建行 O(n)→≤100/次） |

所有高频轮询均已按亮屏/前台/订阅门控：显示模式同步与连接列表跟随「前台 + 亮屏」起停，
不可见时不再查询核心；连接列表进一步在「无订阅者」时降到 5s 保温档（唯一消费方是流量统计页，
保温只为下次打开时历史不为空）。

## 5. 本次优化电量影响汇总

| 改动 | 位置 | 电量收益 |
| --- | --- | --- |
| 日志流按需订阅 + 退订 | `native/log.go`、`Clash.kt`、`main.cpp` | 无人看日志时每条核心日志的 marshal/JNI/decode 与 logcat 转发 → 0；不再重复 fan-out |
| 载荷记忆化 + 去掉流量字段 | `SessionRuntime.kt`、`native/tunnel.go` | 代理页停留期间不再每 tick 全量重算/重编码（命中缓存时零重建） |
| 本地模式运行载荷戳 | `native/tunnel.go`、`Clash.kt` | 载荷未变时代理页 2 秒的 marshal + JNI 字符串 + 反序列化 → 一次 64 位调用（O(全部节点)→O(1)）；复查戳在锁外取值，native 遍历不占锁 |
| 最近请求列表重建 | `DefaultTrafficStatisticsExplorer.kt`、`AppIdentityResolver.kt` | 统计页每秒 ISO-8601 解析 O(n log n)→O(n)；建行 O(n)→≤100；`PackageManager` 查询按包缓存 |
| 流量统计采样分级 | `TrafficStatisticsCollector.kt` | 后台/熄屏唤醒 12/min→2/min |
| RootTun 熄屏轮询放宽 | `RootTunService.kt` | 熄屏状态 IPC 7.5/min→2/min，亮屏立即补刷 |
| 关闭事件驱动 + 250ms 兜底 | `tunnel/conn.go`、`config/kernel-patches/` | 正常关闭 100% 捕获，长连接场景不再永久 50Hz |
| UID 补齐短时 20ms | `tunnel/conn.go` | 只在 UID 重试到期时加速，Join 回调立即唤醒 |
| 采样器：Range + timer/map 复用 | `tunnel/conn.go` | 无 `/proc` 读取；timer/map 不再每 tick 分配 |
| 日志级别过滤 | `native/log.go` | 每 DNS 劫持不再写 logcat |
| UID 正/负缓存 | `VpnTunTransport.kt` | Binder 频率大降 |
| UID 补齐（轮询 8 条/tick、后台 4 条/tick、每连接有界） | `tunnel/conn.go`、`native/app/tun.go`、`ProcFsUidResolver.kt` | 残留 Unknown App 消除；后台只做有界一次性补查，避免回放时已无 socket |
| GOGC 200 + 内存上限 | `native/main.go` | GC 停顿尖峰减少 |
| 启动三路并行 | `SessionRuntime.kt` | 冷启动高功耗段缩短 |
| 快速地址解析 | `Net.kt` | 减少每 miss 的分配/GC |
| MTU 1500 | 多处 | 避免 PMTU 重传；包数增量可忽略 |
| 显示模式同步门控 + 兜底降频 | `DefaultProxyModeController.kt` | 后台/熄屏期间 1.5s 核心查询 → 0；前台兜底 1.5s→5s |
| 连接快照门控 + 订阅降频 | `ConnectionActivityRepository.kt` | 后台/熄屏期间 1s 连接 JSON 查询 → 0；无订阅者时 1s→5s（前台其余时间 -80%） |
| 关闭连接窗口自适应 | `tunnel/conn.go` | 固定 2s → 下限 6s 并跟随实测轮询间隔，配合 5s 保温档不漏关闭记录 |
| 连接历史去重 | `ConnectionHistoryManager.kt` | 关闭连接去重由 O(缓冲) 线性扫描改为 O(1) id 集合 |
| 载荷内容复用 + 体积告警 | `SessionRuntime.kt`、`UiConfiguration.kt`、`RootTunRuntimeRecovery.kt` | TTL 到期但内容未变时不再重编码/以新实例下发；≥512 KiB 打印告警，`TransactionTooLargeException` 单独报因 |
| 日志页增量解析 | `LogRecordService.kt`、`LogViewModel.kt` | 实时缓冲新增写入选代次，未变化时不再重复解析 2000 行 |
| 通知文本去重 | `ServiceNotificationManager.kt`、`RootTunService.kt` | 空闲时不再重建 builder/PendingIntent/notify |
| 磁贴更新去重 + IO 读取 | `ProxyTileService.kt` | 面板可见期间只在状态变化时更新 |
| 配置/选择反序列化缓存 | `ProfileStore.kt`、`ProfileManager.kt` | 每次快照刷新少 2~3 次全量 JSON 解析 |
| 首页流量重组下沉 + 旋转层化 | `HomeViewModel.kt`、`HomePager.kt`、`TrafficDisplay.kt`、`NodeCard.kt` | 2s 流量更新不再重组整页；空闲卡片不再跑旋转动画 |
| root 载荷戳复用 | `SessionRuntime.kt`、`RootTunRemoteClient.kt`、`RootTunPayloadCache.kt` | RootTun 下载荷未变时每次刷新的整份 binder 传输 + JSON decode → 一次两 long 的调用 |
| 选择查找批量化 | `ClashManager.kt`、`SelectionLookup.kt`、`SelectionDao.kt` | 每次快照 2N+1 次 MMKV 读 → 1~2 次；首批选择写入 N 次编码 → 1 次 |
| 列表行分块/键记忆化 | `NodeGrid.kt`、`NodeGroups.kt`、`NodeContent.kt`、`Proxy.kt` | 代理页重组不再重算 O(行数) 分块与 key 字符串 |

净结论：**数据面/客户端轮询电量不变或改善，native 空闲常驻成本显著下降**——TUN 无流量时的后台电耗是
本次优化的主要受益场景。

## 6. 剩余电量优化点

1. 连接列表轮询门控（可见 1s / 保温 5s）：仅当「统计/最近请求」页可见时高频率轮询。需权衡：近期请求历史只在 UI 展示时有意义，
   UI 关闭时可暂停采样+轮询。**已完成**：有订阅者时 1s、无订阅者时 5s 保温、后台/熄屏挂起。原先的代价（App
   不可见期间开关的短连接不进入历史）**也已解决**：不再靠后台轮询，而是让核心区分「已被轮询取走」与「尚未取走」，
   未取走的关闭保留 30 分钟、上限 500 条；为避免 RootTun 路径的 AIDL JSON 字符串撑爆 binder 事务，单次响应最多回放
   200 条并按最旧优先，积压由之后几次轮询排干，因此后台既不唤醒也不丢历史（仅超过该窗口的历史会丢）。
2. 事件驱动采样（hook `Manager.Leave()`）：**已完成**。`config/kernel-patches/` 在
   `sync-kernel.{sh,ps1}` 克隆后自动应用回调补丁；正常关闭同步记录，250ms 兜底只做异常移除、
   过期和 UID 状态回落，长连接下不再永久 20ms 采样。仍需真机 `batterystats` 验证。
3. 更细间隔（10ms）：收益递减，不推荐。
4. 通知/日志周期按需收紧：维持现状即可（已有门控）。
5. 端到端事件推送（管理器 `Leave()` 回调 → 直接推给 App）：native 侧已完成缓冲，但 App 仍用
   1s/5s 快照轮询来兼容 `QueryConnections`、RootTun 和现有 UI 数据流；彻底去掉轮询需要单独设计
   跨进程事件通道。

## 7. 真机验证方法

- 场景（各 30 分钟，同一设备/网络/配置）：Tun 无流量、Tun 持续下载、亮屏首页、流量通知开/关、RootTun。
- 命令：`adb shell dumpsys batterystats --reset` → 运行场景 → `adb shell dumpsys batterystats <pkg>`
  对比；辅助 `adb shell dumpsys cpuinfo`、`adb shell dumpsys netstats`（避免把网络耗电误判为轮询）。
- 重点对比项：空闲态 TUN 的 CPU 时间、wakelock 持有、`/proc` 读取次数（可用 `strace -c -e openat,read`
  或 `systrace` 佐证采样器优化）、logcat 写入频率（开启/关闭日志界面）。
