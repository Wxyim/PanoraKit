# MonadBox TUN 全链路性能分析（VPN 开启 → 请求经 TUN）

> 基于当前源码的静态分析。链路涉及 `modules/runtime/service/.../VpnTunTransport.kt`、
> `lib/native/go/native/tun.go`、`lib/native/cpp/main.cpp`、mihomo（`listener/sing_tun`）与
> sing-tun。真机实测（CPU 占用、首字节时延、吞吐、电量）仍需在目标设备上验证。

## 1. 结论速览

- 数据面每**字节**开销来自用户态协议栈（默认 `mips`）的收发与转发；每**连接**固定开销来自
  出站 socket 的 `protect`/`markSocket`（JNI + Binder）与按需的 UID/进程解析（JNI + Binder + procfs 回退）。
- 启动路径已把「VPN 建立（Android IPC）」「配置编译/加载（Go JNI）」「App→UID 表发布」三路并行；剩余串行段是
  GLOBAL selection 引导（`patchSelector` 改核心，须在编译后）与 `transport.start`（`sing_tun.New` + 栈初始化）。
- 原风险最高三项均已处理：UID/进程解析按 `find-process-mode=strict` 按需门控并加正/负缓存；
  `protect`/`markSocket` 经 `semaphore(4)` 串行化属安全上限，保持不动；隐藏日志热点已加级别过滤（P0）。

## 2. 链路全景

```
App 进程 socket ──路由表──> tun0 (VpnService fd, MTU 1500, 非阻塞)
        │
        ▼
mihomo sing-tun 监听器  (lib/native/go/native/tun/tun.go)
   stack = mips（默认）| gvisor | mixed | system
        │  DNSHijack 端口 53 → resolver（fake-ip 默认）
        ▼
tunnel.Tunnel 入站处理（tunnel/tunnel.go）
   元数据构建 → 规则匹配（DOMAIN / GEOSITE / REGEX / IP-CIDR / GEOIP / PROCESS / UID）
        │  (resolve-ip 时触发上游 DNS；process 规则触发 UID 解析)
        ▼
出站 dial（component/dialer）
   dialer.DefaultSocketHook：
     1) platform.ShouldBlockConnection()   // 每连接 2 个 syscall (dup/close)
     2) app.MarkSocket(fd)  → JNI → Kotlin VpnService.protect()  // 每连接 1 次 Binder
        ▼
代理适配器（shadowsocks/trojan/vmess/… 加密）→ 物理网卡 → 远端
```

主要实现位置：

- VPN 参数与 fd 建立：`modules/runtime/service/src/main/kotlin/service/runtime/session/VpnTunTransport.kt`
- fd 交给 Go TUN：`lib/native/go/native/tun.go` → `tun/tun.go` → mihomo `listener/sing_tun`
- UID/进程解析桥：`delegate/init.go`（resolver + socket hook）→ `app/tun.go` → `remoteTun` → JNI → Kotlin `queryUid`
- 编排：`SessionRuntime.startInternal`（并行 prepare/compile、启动打点）

## 3. 分段性能分析

### 3.1 启动路径（冷启动 / 热启动 / 重载）

已做的优化：

- `startInternal` 用 `coroutineScope { async }` 把 `transport.prepare()`（`VpnService.establish` 约 0.5–1s IPC）与
  `compileAndLoad()`（Go JNI 编译+加载）并行，缩短总墙钟时间。
- 冷启动跳过 `reset()` + `hub.ApplyConfig(empty)`，注释称省约 200–500ms。
- 每步用 `measureStartupStep` 打点（`RuntimeStartupLogStore`），便于定位瓶颈。
- 重载按 `effectiveFingerprint` 跳过未变化的部分。

已并入并行、剩余串行段（`startInternal` 其余顺序执行，受「patchSelector 改核心」与「避免并发 JNI」约束）：

- App→UID 表发布（`startInstalledAppsPublisher` → `NotifyInstalledAppsChanged`）已并入 `coroutineScope` 三路 async，
  与 `transport.prepare`、`compileAndLoad` 并行；它只写 Go 侧 RWMutex 保护的 app-uid 表（与核心状态不相交），且须在
  `transport.start` 前完成以保证首包可解析包名。
- GLOBAL selection 引导（`restoreGlobalSelectionBeforeTransport`）因 `patchSelector` 会改写已加载的核心，必须严格在
  `compileAndLoad` 之后串行执行，未并入并行路径。
- `transport.start` 内部 `sing_tun.New` + `tunStack.Start()`（gvisor/mips 栈初始化）可达数百毫秒。
- `TunService` 启动本身（`startForegroundService`、通知、`initializeServiceGlobal`）也占用冷启动串行段。

建议：

- 对 `sing_tun.New` 的关键路径打点（native 侧已有 Debug 日志，但建议在 startup log 里带毫秒数）。

### 3.2 数据面主路径（每包 / 每连接）

每字节成本：

- 用户态栈 `mips`（Mihomo IP Stack，默认）在 TUN fd 上批量读（`LinuxTUN.BatchRead`），
  内部按 MTU 分段、维护 TCP/UDP 状态、NAT。相比内核栈，每包多一次用户态处理，但换来应用无关性。
- `setBlocking(false)`，sing-tun 用 epoll 事件循环，避免忙等。

每连接成本（重点）：

1. **出站 socket 标记**：`dialer.DefaultSocketHook`（`delegate/init.go`）在**每个出站连接**上执行
   `ShouldBlockConnection()`（`dup`+`close` 两个 syscall）再 `app.MarkSocket(fd)`。
   `remoteTun.markSocket` 经 `semaphore(4)` 串行化 → C++ `ATTACH_JNI` → Kotlin `VpnService.protect(fd)`（Binder 到 ConnectivityService）。
   这是 TUN 模式下保证不环路所必需的，但每次建连约 1 次 JNI + 1 次 Binder + 2 次 syscall。
2. **UID/进程解析**：`process.DefaultPackageNameResolver`（`delegate/init.go`）在 CMFA 构建里是
   `metadata.Uid` 唯一来源。mihomo 默认 `find-process-mode=strict`：只有规则里存在 `process`/`uid` 类规则时才触发。
   触发时链路为：`app.QuerySocketUid` → `remoteTun.querySocketUid`（`C.CString`×2 + semaphore）→ JNI →
   Kotlin `ConnectivityManager.getConnectionOwnerUid`（Binder）→ 失败回退 `ProcFsUidResolver`（读 `/proc/net/tcp[6]`）。
   已实现 Kotlin 侧正/负缓存：正缓存 **15s**、预热窗（启动 3s）结束后的负缓存 **5s**、容量 **2048**、按 TTL 淘汰，
   键为 `(protocol, srcIp:port, dstIp:port)`。短连接场景（每请求新 src 端口）只在**新四元组首次出现**时走一次
   JNI+Binder，重复四元组命中缓存；预热窗内 miss 不落负缓存以避免误伤启动期尚未发布的 socket。
3. **连接元数据/规则匹配**：规则数越多、正则越多成本越高；`IP-CIDR`/`GEOIP` 匹配依赖 IP 集与 MMDB，
   命中缓存后为 O(1)~O(log n)。fake-ip 模式下域名规则不触发二次解析，但 IP 类规则需要 `ResolveIP`。

建议：

- 正/负缓存已落地（见上）；resolver 本身由 mihomo `find-process-mode=strict` 按需门控（仅 `process`/`uid` 规则或
  `always` 时、每连接至多一次），无需在 `delegate/init.go` 额外加门控。
- Kotlin 端字符串→地址解析已改为手写快速解析（替换 `URL()`+`InetAddress.getByName`，见 `Net.kt`），
  去掉每次 miss 的 URL 对象分配并规避 Android 对中括号 IPv6 字面量的平台差异。
- `querySocketUid` 的字符串跨 JNI 改整型编码（`C.CString`/`NewStringUTF`/解析三步合并）收益约 10~20% miss 路径，
  但需跨 Go/C++/Kotlin 改 JNI ABI、无法在本仓库离线验证，且正缓存已大幅降低 miss 频率——暂缓，列为 P3。

### 3.3 DNS 链路

- 默认 `enhanced-mode=fake-ip`（`28.0.0.0/8`，`store-fake-ip=true`），
  nameserver 为 `223.5.5.5 / 119.29.29.29 / 8.8.4.4 / 1.0.0.1` 并追加 `system://`。
- DNS 劫持：`dnsHijacking=true`（默认）时 Go 侧收到 `dns="0.0.0.0"`，劫持**所有**目的 `:53`；
  关闭时只劫持 portal `172.19.0.2:53`（及 IPv6）。`VpnService.Builder.addDnsServer` 把 172.19.0.2 推给应用。
- sing-tun 对 TUN DNS 地址做了 `AddSystemDnsBlacklist`，避免 `system://` 解析回环（`listener/sing_tun/server.go`）。

开销点：

- 新 host 首连接若命中 IP 类规则（`IP-CIDR`/`GEOIP`）需一次真实解析（即使 fake-ip），
  首字节时延 = 一次上游 DNS RTT（直连 223.5.5.5 等）。mihomo DNS 缓存命中后无此成本。
- 每条被劫持的 DNS 报文都会触发 `log.Debugln("[DNS] hijack ...")`（见 3.5 日志开销）。

建议：

- 以域名/geosite 规则为主，减少 `ResolveIP` 触发；确需真实 IP 的服务（如部分支付/游戏 SDK）加入
  `fake-ip-filter`，避免「先 fake-ip 再二次解析」。
- 保持 `store-fake-ip`，重启后缓存仍生效；就近/低延迟 nameserver 对首字节有直接帮助。

### 3.4 出站适配层

- 加密 CPU 成本：AEAD（shadowsocks 等）在低端 SoC 上可观；TLS 握手（trojan/vmess/tls）对短连接更贵。
- 移动网络小包/丢包场景：UDP 类流量若无专用 UDP 出口会走 UDP-over-TCP 兜底，时延和重传变差。
- 多路复用（`smux`/`yamux`）可摊薄握手与包头开销，但引入共享头阻塞（HOL）风险，需按节点能力选择。
- 拥塞控制：mihomo 支持 `congestion-controller`（如 BBR 系），长肥管道/弱网收益明显；当前 native TUN
  `LC.Tun` 未显式设置，走 mihomo 默认。

建议（profile 级，非代码改动）：

- 对视频/下载为主的节点启用多路复用与 `bbr` 类拥塞控制；开启 UDP 转发。
- 避免对需要低抖动实时流量（语音）的节点开多路复用。

### 3.5 Go 运行时 / JNI 边界 / 日志

- 内存：`applyMemoryLimit` 设 `debug.SetMemoryLimit(RAM/4)`（下限 256MiB、上限 1GiB），并已 `debug.SetGCPercent(200)`：
  有硬内存上限兜底时提高 GOGC 可降低数据面 GC 频率（更少停顿）。`GOMAXPROCS` 保持默认核数。
- `forceGc` 在服务停止时 `runtime.GC()+FreeOSMemory()`，属停机路径，可接受；切勿放到数据面。
- **日志是隐藏热点**：mihomo `log.Debugln` 无条件 `fmt.Sprintf` 并无缓冲 channel `logCh` 派发
  （`mihomo/log/log.go: newLog` 不看 level）。MonadBox 运行时订阅了 logcat，每条日志（含每 DNS 劫持的
  Debugln）都会经 Go channel → C 回调 → JNI → Kotlin `Channel(32)` → UI，跨一次完整边界。
  - 风险 A：日志量大时数据面 goroutine（DNS/连接路径）被日志派发拖慢。
  - 风险 B：若运行中无人订阅，`logCh <- event` 会让日志调用方 goroutine 阻塞——这在
    `subscribeLogcat` 尚未完成、但数据面已起来的时间窗内是潜在卡点。
  - 建议：生产保持 `log-level: info`；诊断完毕关闭日志界面订阅；必要时在 Go 侧对 Debug 日志做级别过滤
    （mihomo 上游多数 `Debugln` 已由 `log.IsDebug()` 包住，但 `sing_tun/dns.go` 的 DNS 劫持日志没有）。

### 3.6 统计 / UI 查询与数据面争用

- `queryConnections`（连接列表）：客户端 `ConnectionActivityRepository` 按 **1s** 轮询（`ClashManager`/`SessionRuntime`
  侧 100ms TTL 只做去重）。每次全量 JSON 序列化 + JNI；连接多时是明显 CPU 尖峰。
- 最近关闭采样器：`tunnel/conn.go` 的 `observeRecentClosed` 每 **20ms** 监视连接增减以保留「最近请求」历史。
  **已优化**：原实现每 tick 调 `statistic.DefaultManager.Snapshot()`（会读 `/proc/<pid>/statm` 并分配连接切片），
  改为直接 `DefaultManager.Range` 遍历（免 /proc 读取与切片分配），并加空闲 fast-path——无存活连接且无待处理项时
  tick 近零成本（`alive` map 按需分配）。采样间隔由 50ms 降到 **20ms**：存活 **≥20ms** 的连接必然被捕获；
  成本侧，活跃 tick 仍是锁自由的轻量遍历，空闲 tick 近零，故降间隔对 CPU 影响很小。
- **空闲退避（电量平衡）**：20ms 定时器常驻会阻止 CPU 在安静设备上进入更深空闲态（即使空闲 tick 的 CPU 成本近零，
  每 20ms 的定时唤醒本身是功耗杠杆）。因此采样循环改为自适应：有存活连接或有待保留/待处理项时保持 **20ms**；
  三态全空（空闲）时退避到 **250ms**，出现活动立即恢复 20ms。代价仅是「空闲窗口内打开并关闭的首个连接」不被观测，
  活跃期捕获不变。电量评估：活跃期 CPU 仍被数据面主导、20ms 影响可忽略；空闲期定时唤醒从 50/s 降到 4/s（-92%），
  是本次 20ms 改动的电量补偿。
- **正确性与捕获边界（已模拟验证）**：改动前后捕获语义等价——`c.ID()`≡`UUID.String()`、`c.Info()`≡快照指针，
  检测/入 buffer/淘汰逻辑逐行相同；空闲 fast-path 仅在三态（存活/seen/buffer）全空时短路，均为 no-op，不会漏迁移
  已关闭连接。用 4000 个随机场景（含大量 0~30ms 超短连接）对比新旧算法，捕获集合 **0 差异**（仅 buffer 顺序随 Go
  map 随机迭代变化，不影响正确性）。采样式捕获的固有边界：存活 **≥20ms** 的连接必然被捕获；存活 **<20ms** 的连接为
  概率捕获（约 `L/20ms`）。模拟实测（20ms 间隔，0~99ms 均匀分布）：总体捕获率 **89.1%**，分段为 0~10ms 22.3%、
  10~20ms 72.2%、20ms 及以上 **100%**（50ms 间隔时分别为 73.7% / 8.8% / 28.3% / 48.1% / 78.6%）。
- `queryTrafficSnapshot` 前台 2s、后台 60s 轮询，成本低（`down_scale_traffic` 已压成 30bit 计数）。
- `queryRuntimeDataSnapshot` 300ms TTL；代理组 500ms TTL。均在 UI 订阅时才会被调用（`WhileSubscribed`）。

建议：

- 连接列表分页/增量仍待办；但「最近请求」历史依赖保留关闭连接，纯分页需保持该语义（产品取舍，未动）。
- 客户端状态轮询节奏已按亮屏/前台分级（与 `docs/POWER_ANALYSIS_ZH_HANS.md` 一致），维持即可。
- 采样间隔已由 50ms 降到 **20ms**（见上），超短连接捕获显著提升；若仍需更细可到 10ms（概率 `L/10`，活跃 tick ×5），
  或走事件驱动（hook `Manager.Leave()`，可 100% 捕获，但需改 mihomo kernel；`lib/mihomo/mihomo` 由
  `sync-kernel.sh` 每次重克隆上游、无 patch 机制，改动会被冲掉，不可持续）。配合空闲退避，20ms 的
  「捕获率/电量」平衡点已成立；对 UI「最近请求」场景无需再细化。

## 4. 优先级优化清单

| 优先级 | 项 | 位置 | 影响 | 改动量 |
| --- | --- | --- | --- | --- |
| P0 ✅ | UID 缓存：正缓存 15s、预热后负缓存 5s、按 TTL 淘汰、容量 2048 | `VpnTunTransport.queryUid`/`cacheUid` | 降低短连接与 miss 场景下的 JNI+Binder+procfs 频率 | 已实现 |
| P0 ✅ | 日志：`init()` 常驻订阅者加级别过滤（对齐 `subscribeLogcat`） | `lib/native/go/native/log.go` | 消除每 DNS 劫持的 logcat 写入；常驻订阅者保证 logCh 不阻塞 | 已实现 |
| P1 ✅ | `querySocketUid` 字符串→地址解析改手写快速解析（去 `URL()`+`getByName`，规避中括号 IPv6 平台差异） | `core/util/Net.kt` | 减少每次 miss 的 URL 分配与解析成本 | 已实现 |
| P1 ✅ | 日志：运行期始终有订阅（常驻 drainer + 级别过滤已实现，真机复验待办） | `lib/native/go/native/log.go` | 消除每 DNS 劫持 JNI 开销与潜在阻塞 | 已实现 |
| P1 ✅ | MTU 统一对齐 1500（本地 TUN + RootTun + 模型默认 + 编辑器占位符） | `VpnTunTransport`/`tun/tun.go`/`RootTunConfig`/`Editors.kt` | 消除两种模式的 MTU 漂移与 PMTU 兼容性风险 | 已实现 |
| P1 ✅ | GSO 不启用：本地/root 的 `LC.Tun.GSO` 均保持 false，Go 侧强制 `tun.enable=false` 使 profile 覆盖无法开启 | `tun/tun.go`/`tun/root.go`/`config/override.go` | 保持稳定分段行为，避免未经验证的吞吐风险 | 已确认 |
| P2 ✅ | 启动并行化：App→UID 发布并入三路 async，与 transport.prepare/compileAndLoad 并行；GLOBAL selection 因 patchSelector 改核心保持串行 | `SessionRuntime.startInternal` | 冷启动再省一段 | 已实现 |
| P2 ✅ | 最近关闭采样器：`Range` 替代 `Snapshot()`（免 `/proc/statm` 读取）+ 空闲 fast-path；间隔 50ms→20ms 提升短连接捕获，空闲退避 250ms 平衡电量（唤醒 50/s→4/s） | `tunnel/conn.go` | 后台轮询开销 + 短连接可观测性 + 电量 | 已实现 |
| P2 | 连接列表分页/增量 | `SessionRuntime.queryConnections` | UI 常开时 CPU | 中 |
| P2 ✅ | `GOGC` 调优：`debug.SetGCPercent(200)` 配合硬内存上限 | `native/main.go` | 高吞吐下 GC 抖动 | 已实现 |
| P3 | `querySocketUid` 字符串跨 JNI 改整型编码（Go/C++/Kotlin ABI 变更，本仓库离线无法验证，收益受缓存稀释） | `tun.go`+`main.cpp`+`TunInterface` | 再省 ~10~20% miss 路径 | 暂缓 |
| P3 | profile 级：多路复用 + BBR + UDP 转发 + fake-ip-filter | 配置/文档 | 弱网与首字节 | 无代码 |

## 5. 真机验证方法

- 抓启动分步耗时：启动后读 `RuntimeStartupLogStore`（startup log），对比各 `measureStartupStep` 的 `cost`。
- 数据面：`adb shell top -H`、`adb shell dumpsys cpuinfo`，分别测空闲 / 大下载 / 高频短连接（网页浏览）三种负载。
- UID/JNI 热点：`adb shell dumpsys binder` + `systrace` 抓 `VpnService.protect` 与 `getConnectionOwnerUid` 频率。
- 栈对比：同一设备同一网络，切换 `mips / gvisor / mixed / system` 各测 5 分钟 iperf3（TCP+UDP）与首字节时延。
- 日志影响：开启/关闭日志界面，对比 DNS 劫持频率高时的 CPU。
