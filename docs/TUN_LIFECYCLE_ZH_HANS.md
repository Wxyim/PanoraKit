# MonadBox TUN 会话与 fd 生命周期

> 记录 TUN 从建立（`prepare`）→ 交接（`start`）→ 停止（`stop`/teardown）的 fd 归属规则、
> 「僵尸 VPN」根因与修复、Go 启动失败路径，以及启动状态标记（`markRuntimeStarted`）的取舍。
> 与 [TUN_PERFORMANCE_ANALYSIS_ZH_HANS.md](TUN_PERFORMANCE_ANALYSIS_ZH_HANS.md)（性能）和
> [TUN_POWER_ANALYSIS_ZH_HANS.md](TUN_POWER_ANALYSIS_ZH_HANS.md)（电量）互补，本文聚焦正确性与生命周期。

## 1. 结论速览

- TUN fd 从 `VpnService.Builder.establish()` 返回即让 Android VPN 会话**立即生效**（通知栏图标出现）；
  真正的数据通路要等 fd 交给 Go（`startTun`）后才建立。
- 关键风险窗口是「`prepare` 后、`start` 前」：此时会话已生效、fd 尚未被 Go 接管。若 teardown 落在此窗口
  （START_STICKY 重建被 toggle-off 打断、establish 后冷启动失败、start/stop 竞争），established fd 会泄漏，
  形成「开关已关、通知栏 VPN 图标仍在」的**僵尸 VPN**。
- 修复采用进程级 fd tracker（`leakedTunFd`）+ CAS 原子认领 + `ParcelFileDescriptor.adoptFd` 关闭，
  保证任意时刻 fd 只被一个路径关闭，杜绝双重关闭与 fd 号复用误关。
- Go `startTun` 失败路径端到端闭环：Go 关闭 fd（F_GETFD 守卫防双关）→ 返回码 1 → C++ 透传 → Kotlin 抛错 →
  `SessionRuntime.rollback`，runtime 进入 Failed 而非假 Running。
- 同一窗口还有个非 fd 副作用：UID 预热窗若以 `prepare` 为锚点，会在首包到达前被配置编译/核心加载/App→UID
  发布吃掉，启动首包洪峰的 UID miss 便既不重试又被负缓存 5s，表现为最早几条「最近请求」显示 Unknown App。
  因此预热窗在 `start()`（数据面真正收包）重新锚定（详见
  [TUN_PERFORMANCE_ANALYSIS_ZH_HANS.md](TUN_PERFORMANCE_ANALYSIS_ZH_HANS.md) 第 3.2 节）。
  预热窗只是第一层兜底：`find-process-mode=strict` 下未被 `process`/`uid` 规则命中的连接，以及
  HTTP/SOCKS 入站、packetaddr 出口这类不写 `RawAddr` 的连接，核心根本不会发起解析。第二轮补齐了这两条：
  `MetadataSocketAddrs` 在地址缺失时重建 socket 地址，`tunnel/conn.go` 在 App 轮询连接列表期间（最近 10s
  内有过 `QueryConnections`）由采样器按 8 条/tick、每连接 ≤3 次尝试补齐 `metadata.Uid`，procfs 回退同时
  支持端口匹配与 v4-mapped 双表（详见同上第 3.2 节）。

## 2. fd 归属规则

一个 TUN fd 从诞生到销毁只有三个状态，由 `VpnTunTransport` 的 `deviceLock` + 进程级 `leakedTunFd`
（`AtomicInteger`，companion）共同约束：

| 阶段 | tracker | `pendingDevice` | fd 所有者 | 说明 |
| --- | --- | --- | --- | --- |
| `prepare()` 后 | = fd | = device | Kotlin（`VpnTunTransport`） | 会话已生效，等待交接 |
| `start()` 后 | = -1 | = null | Go（`rTun`） | CAS 认领成功，fd 交给 Go |
| 已关闭 | = -1 | = null | 无 | `adoptFd().use{}` 或 Go `stopTun` 已关闭 |

`start()` 的 `check(leakedTunFd.compareAndSet(pending.fd, -1))` 是关键：

- 认领成功 → 清空 tracker、`pendingDevice=null`，随后把 fd 交给 Go，Go 拥有唯一所有权。
- 认领失败（外部 `closeLeakedTunFd` 已抢先关闭）→ 抛错中止，绝不把已关闭的 fd 交给 Go。

## 3. 僵尸 VPN 根因

`VpnService.establish()` 使 VPN 会话立即生效，但 `Clash.stopTun()`（Go `closeCurrentTunLocked`）只在
`rTun != nil` 时才关闭 fd。当运行被破坏在「`prepare` 后、`start` 前」窗口：

1. 系统夜间回收进程，START_STICKY 重建 `TunService`；
2. 重建的 `onCreate` 走 `runtime.start()`，`prepare()` 已 establish（图标出现），`start()` 尚未执行或被打断；
3. 用户次日开 app 关闭 VPN → teardown 落在窗口内 → Go `rTun` 为 nil，`stopTun()` 不关 fd；
4. established fd 泄漏 → 会话存活 → 通知栏图标残留 → 首包进入无人读取的 fd，形成僵尸 VPN。

## 4. 修复设计

- `VpnTunTransport.kt`：`deviceLock` 同步 `pendingDevice`；`prepare()` 替换前先 `closePendingDeviceLocked()`
  关闭旧 fd；`start()`/`stop()` 原子认领；`closePendingDeviceLocked()` 用
  `ParcelFileDescriptor.adoptFd(fd).use {}` 关闭泄漏 fd。
- companion `leakedTunFd`：进程级 tracker + `closeLeakedTunFd()`；`prepare` 注册、`start`/关闭路径用
  `compareAndSet`/`getAndSet` 原子认领，防止外部关闭后 fd 号复用被误关。
- 兜底接入点：
  - `TunService.onDestroy`：先 `Clash.stopTun()`（Go 拥有时关闭）再 `closeLeakedTunFd()`（tracker=-1 时 no-op）。
  - `ProxyFacade.forceCloseLocalRuntime`（LocalTun 分支）：覆盖服务主线程卡死、`onDestroy` 延后时客户端的强制关闭。
  - `SessionRuntime.stopInternal`/`rollback`：经 `transport.stop()` → `closePendingDeviceLocked()` + `stopTun()`。

所有关闭路径都经 tracker 的 CAS 门控，单次所有权；Go 接管后 tracker 清空，外部关闭为 no-op。

## 5. Go 启动失败路径

`lib/native/go/native/tun.go` `startTun` 失败时：

- Go 侧用 `F_GETFD` 守卫关闭 fd：sing-tun 以 `os.NewFile` 包装 fd，错误路径可能已关、也可能未关；
  `F_GETFD` 对已关闭描述符返回错误，因此恰好关闭一次，杜绝双重关闭（避免误关复用描述符）。
- 返回码 1 经 C++ `nativeStartTun`（`jint`）透传；Kotlin `Clash.startTun` 校验非 0 即抛错；
  `SessionRuntime` 捕获后 `rollback`，runtime 正确进入 Failed。

修复前：返回码被忽略，fd 泄漏且 runtime 静默保持 Running（假运行、隧道已死）。修复后端到端一致。

## 6. 启动状态标记（onCreate `markRuntimeStarted`）保留说明

`TunService.onCreate` 在 `runtime.start()` 之前调用 `StatusProvider.markRuntimeStarted(ProxyMode.Tun)`；
`onStarted` 成功后再次调用。该提前标记**刻意保留**，删除会引入回归：

- 磁贴（`ProxyTileService.currentSnapshot`，同进程每 1s 读 `isRuntimeActive`）在 start 窗口会回退到 Idle 闪烁。
- 过夜 reconcile（`ProxyFacade.isLocalServiceLive`）依赖该标记；`getRunningServices` 在部分 OEM 上不可靠。
- `startProxy` 的陈旧标记判定 `isTunStarting() && !isRuntimeActive` 会误伤进行中的启动，并重复触发
  `VpnService.prepare()`（部分 OEM 会吊销已建立会话/重复弹窗）。

首页 UI 在 start 窗口显示的是 `runtimeSnapshot` 的 `Starting` 相位；所有失败路径
（`reportFailure`/`.onFailure`/`onStopped`）都会立即 `markRuntimeStopped`，所谓 phantom 仅存在于真实启动
失败时的极短窗口且自纠正。

## 7. 残留风险

- **JNI 层抛错且 Go 未执行**（`get_string`/`new_global` 内存级失败）：fd 未跟踪会泄漏。极端罕见、既有问题，
  因无法区分「Go 已关」与「未执行」，不做补关以免双重关闭。
- **reestablish（访问控制重连）失败**：Go 失败现会关闭新 fd → VPN 掉线 + `sendAccessControlApplyFailed` 通知
  （此前是静默僵尸）。`reestablishTransport` 无旧会话回滚（既有设计缺口），如需真正「失败保持旧会话」需另补回滚逻辑。
- **F_GETFD 守卫的 fd 复用窗口**：sing-tun 已关 + 其他 goroutine 恰好复用同一 fd 号，微秒级、错误路径 +
  `rTunLock` 内，可忽略。

## 8. 真机验证方法

- 复现原始路径：开启 VPN → 后台息屏过夜 → 次日打开 app 关闭 VPN，确认按钮变停止且通知栏图标**同步消失**。
- 快速开关 3–5 次，确认无图标残留、无 fd 泄漏（`ls -l /proc/<pid>/fd` 观察 tun fd 随开关增减）。
- 触发 Go 失败：临时构造非法网关/栈配置强制 `tun.Start` 失败，确认 VPN 图标消失、runtime 进入 Failed、有失败通知。
- `dumpsys vpn` / `dumpsys connectivity` 检查无遗留会话；`dumpsys activity services` 确认 TunService 已销毁。
