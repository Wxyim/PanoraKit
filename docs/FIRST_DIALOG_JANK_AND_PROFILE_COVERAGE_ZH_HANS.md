# 首次弹框掉帧 与 基线 profile 覆盖率 修复说明

> 2026-10-09 · 适用范围：首次弹框预热（`:ui`）、首次引导向导可驱动性、基线 profile 采集 journey
>
> 结论：两条问题均已修复并经真机 / 采集报告验证。设备侧首次弹框不再掉帧；采集 8 条 journey
> 在一轮内 `best_effort_exercised=56/56`、`required_failed=0`，profile 覆盖主界面 / 向导 / 浮层 /
> 配置 / 编辑器。

## 概要

本轮从「全新安装后第一次打开弹框时入场动画掉帧」出发，排查出两条独立但相互印证的问题：

1. **用户可见**：首次浮层的入场动画掉帧（上滑一顿一顿 / 闪）。同进程后续浮层正常，第 2 次启动起正常。
2. **工程侧**：基线 profile 采集**从未走出首次引导向导**，导致主界面与弹框栈完全没有 AOT 覆盖。

## 一、首次弹框掉帧

### 现象

- 仅**全新安装 + 第一次启动**的**第一个**浮层出现；同进程后续浮层正常。
- 手动先打开任意一个**可见**浮层（例如引导页「调整界面风格 → 主题模式」半窗）后，后续弹框全部顺滑。
- 表现为**上滑过程中掉帧**：动画在跑，但停顿 / 闪。

### 根因

**首次把一个「真实可见浮层」光栅化 / 合成上屏**的一次性代价：圆角裁剪 + 整屏尺寸 layer + 变换，
以及与内容并列的整屏遮罩兄弟节点。既不是弹框 Java 代码冷启动，也不是首次启动后台任务争抢 CPU。

排除过程（均为真机实测）：

| 尝试 | 结果 |
|---|---|
| 预热浮层用 `alpha = 0` 遮挡 | ❌ 无效：零 alpha layer 可整段跳过绘制，没有像素上屏 |
| 把预热浮层平移到屏幕外（被裁剪） | ❌ 无效：同理，没有像素上屏 |
| **按真实几何绘制、仅用 `alpha = 0.004`（≈1/255）遮挡** | ✅ 掉帧消失 |
| 手动打开任意可见浮层一次 | ✅ 同样消失（与上一条互相印证） |

### 修复

- 新增 `OverlayWarmUpHost`（`:ui`）：进程内第一次启动时**不可见地预热两块浮层骨架**——
  ① 弹框（标题 + 确认按钮内容）、② 半窗（主题模式：标题 + 三个选项行）。
  两者都按真实表面色 / 真实圆角裁剪 / 真实 layer 绘制，进场 36 帧 + 退场 24 帧各跑满后拆除；
  每进程仅一次。
- 宿主：`MainActivity`（已安装用户）与 `OnboardingBaseActivity`（全新安装必走引导向导）。
- 预热浮层同时用 `clearAndSetSemantics {}` 清空语义：像素照画，但**不发布「幽灵无障碍节点」**。
- 不变量写入 `docs/UI_MODAL_RENDERING_PERF_ZH_HANS.md` §7。

> 追加（重组件，两轮才修对）：首页首次错误弹框修好后，**设置 → 主题配色**首次打开仍有卡顿——
> 浮层机制本身已被预热（所以弹框不卡了），但该面板的内容是 Miuix 的 `ColorPicker`（HSV / OkHSV /
> OkLab / OkLch 四种模型 + 渐变 + 滑条，单个类就有 152KB），比预热用的"标题 + 三行选项"重一个量级。
>
> 第一轮把它按同样方式并入 `OverlayWarmUpHost`，为规避软件渲染采集模拟器的稳定性风险改用
> `alpha = 0`「只预热组合」。**用户复测仍然卡**——零 alpha 会让绘制阶段整段跳过，而拾色器的一次性
> 成本恰恰在绘制：每个滑条在 `drawWithCache` 里建 `Brush.horizontalGradient` + `Stroke`，Alpha 滑条
> 还要建几百个矩形的棋盘格 `Path`，指示器建 `Brush.radialGradient`；这些着色器 / 路径首次执行时
> 全冷。
>
> 最终方案：把「真实绘制」预热从进程启动**挪到拾色器入口所在的页面**——设置页与引导「个性化」步的
> `ThemeColorPickerItem` 行内（`ThemeColorPickerDrawWarmUp`，`:ui`）。用户看到「主题配色」这一行后的
> 两帧就开始预热，等真正点开面板时绘制路径已热；进程启动路径保持干净（只保留两块浮层骨架）。
> 预热块用 320dp 宽（贴近真实面板内容宽，梯度按真实尺度建立）+ 真实内容（`ColorPicker` + 十六进制框）
> 以 `alpha = 0.004` **真实绘制**，但通过自定义 `Layout` 对外测量为 **1x1**：既不推动宿主行布局
> （避免预热期间出现布局跳变），又保证绘制阶段照常执行（0x0 才可能被跳过，1x1 是"最小但确实会画"
> 的尺寸）；语义同样清空，不留幽灵节点。
>
> 重组件与"浮层骨架"的**分档规则**（更新版）：
>
> | 冷的部分 | 预热方式 | 宿主 | 原因 |
> |---|---|---|---|
> | 浮层骨架（遮罩 / 动画 / layer / 圆角裁剪） | **真实绘制**（`alpha = 0.004`） | 进程启动（`OverlayWarmUpHost`） | 它的成本在栅格/合成，零 alpha 或离屏会整段跳过；两块骨架共用同一套机制，一次就够 |
> | 重组件内容（如 `ColorPicker`） | **真实绘制**（`alpha = 0.004`，小尺寸） | **入口页面行内**（`ThemeColorPickerDrawWarmUp`），不进启动路径 | 成本大头同样在首次绘制（渐变 / 着色器 / 路径首建），零 alpha 同样会跳过；放进启动路径则每次冷启动都要付一份全尺寸绘制代价（软件渲染的采集模拟器曾因此崩过），挪到入口页面既避开启动关键路径、又保证用户点击前已热 |

## 二、基线 profile 采集覆盖率

### 现象（修复前 profile 实测）

```
NavHost / HomePagerKt / TrafficDisplayKt / 任何 presentation/screen 组合物  → 0 条
SuperDialogKt / DialogContentLayoutKt / DialogLayout / DialogEntry          → 0 条
只有 startup + onboarding + DI/theme
```

采集一直停在向导第 0 步，主界面与弹框栈从未被 AOT 编译——这也与「第 2 次启动才顺滑」的现象一致。

### 逐项修复（每项都有采集报告佐证）

| 症状 | 根因 | 修复 |
|---|---|---|
| 判定「没有向导界面」，一步不走 | 第 0 步是无标题 hero，唯一控件是图标箭头，而 `OnboardingSurfaceLabels` 只列了后续步骤标题 | 判定预算 1s→6s；补 `Start`/`开始`/`MonadBox`/`Hello Word` 标记 + 推进控件兜底 |
| 箭头在无障碍树里**完全不存在** | 真正接收点击的是压在 Icon 上的 `AndroidView(View)`，它没有任何描述 | 把标签挂到该 View 上，Icon 改装饰（**同时修复真实无障碍缺陷**） |
| 点击被 reveal 动画吞掉 | 按钮 680ms 后才出现，再 420ms 渐显入场 | 点击前等待收敛 + `tapUntilGone` 重试 + `clickNode` 容忍任意 UiAutomator 拒绝 → **节点点击失败即按 bounds 点击** |
| 条款步 `Next` 恒禁用、8 步空转 | 复选框未勾上；旧实现「读到 stale 就放弃」 | 勾选改为「重试 + 重查 + 点击/坐标兜底 + 勾选后校验」，并给出「仍禁用」的精确诊断 |
| `checkboxes=[none]`（真正卡点） | 采集设备屏幕仅 **320x640**，步骤内容区 `ScrollView` 仅 111px 高，段落已占满 → 复选框行在首屏之下（不可见节点被 UiAutomator 过滤） | **在该 `ScrollView` 自身 bounds 内滑动**（按屏幕比例滑会滑到标题区，等于没滚） |
| `edit_save` 找不到编辑入口 | journey 标签过期：实际是行内图标动作 `Edit`/`More` + 弹层 `Edit Text`/`Profile Options`；且**必须先确认在配置页**（代理页节点列表头部也有 "More"） | 标签对齐真实文案；`clickFirstMatching` 全面接入 bounds 兜底；进入前校验配置页 |
| `edit_save` 报 `app is not in front (com.android.vpndialogs)` | 上一条腿启动 VPN 后系统授权弹窗仍在前面 | 该腿先清理系统级弹窗（`appOnly=false`） |
| `configuration_import` 找不到来源选择器 | 类型行在面板内容区，探测却用普通预算 1s（面板内容组合远晚于其固定 confirm 动作） | 两处探测改用 `SheetStepTimeoutMs`（6s）；失败时附**面板内节点转储** |
| `home_mode_switch` 时好时坏 | 面板行在 ~180ms 入场动画中移动；且 App 停在其他主页时，**代理页的模式文案**会被误认为首页徽标 | 点击前等 400ms 停稳；新增**首页专属标记**（`UPLOAD`/`DOWNLOAD`）；`navigateToHomePage()` 改为**先点底部 Home tab 并校验** |
| 启动代理后徽标突然点不动 | `By.pkg(app)` 也会匹配 **App 自家通知**里的同名文本（SystemUI 窗口渲染，不在屏幕上） | 新增 `findVisibleObject`（`visibleBounds` 非空且在显示范围内），所有标签探针/点击改为**只认屏幕上真实可见的节点** |
| `start_stop_proxy` 的「假覆盖」 | 旧实现把 VPN 授权弹窗**取消**了 → 隧道没启动 → 「停止」命中模式标签 "VPN" 却报成功 | 改为**接受授权**，并用真实状态文案校验 `Running` → `Tap to start` 的往返跃迁 |
| 中文环境会静默掉覆盖 | 多个标签表中文过期：`ModeSwitchLabels` 缺 `Proxy.Mode.*` 的「规则/全局」，空闲态实际是「轻触启动」 | 全量对账中英标签（`Confirm/Cancel`、`Add Profile`、`Profiles/No profiles`、`Edit/More`、`Edit Text/Profile Options`、`Save*`、`Profile Type/Subscription URL`、`Blank Config`、`Start/Stop`、`Onboarding*`） |

### 结果

采集报告（8 条 leg，多轮）：

```
required_total=N required_failed=0 best_effort_total=56 best_effort_exercised=56 best_effort_skipped=0

onboarding            wizard 'Start' -> Start -> Next -> Next -> Next -> Enter App; shell reached
home_mode_switch      mode badge 'Rule' expanded, selected 'Direct'; toast dialog dismissed via 'Confirm'
configuration_import  add profile 'Add Profile' -> source 'Blank Config' -> confirm 'Confirm' -> editor
start_stop_proxy      start 'Tap to start' -> 'Running' -> stopped
edit_save             edit 'More' -> 'Edit Text' -> save 'Save'
```

profile 规模与覆盖（数值随每次重新生成略有浮动，此处为 2026-10-09 采集结果）：

```
baseline-prof.txt 39,614 行 / startup-prof.txt 20,745 行（修复前 ~17.5k）

NavHost 171 | HomePagerKt 51 | TrafficDisplayKt 88 | BottomBarContent 9
SuperDialogKt 11 | DialogContentLayoutKt 94 | BottomSheetContentLayoutKt 129
OnboardingPersonalizeActivity 24 | ProfilesPagerBodyKt 186 | ProfileAddSheetKt 147 | CodeEditorKt 29
ColorPickerKt 88（主题配色面板预热后一并入册）
```

## 三、顺带修复的其他缺陷

- **无障碍**：引导向导开始按钮此前对读屏软件是「无标签的可点击控件」，现已带本地化标签。
- **无障碍**：预热浮层曾发布一个约 1 秒的「幽灵弹框」节点（读屏可聚焦，也污染采集摘要），现已清空语义。
- **采集可信度**：`start_stop_proxy` 的启停改为状态校验，杜绝「没启动也报成功」。

## 四、影响面与风险

- 运行时新增：`OverlayWarmUpHost` 每进程一次、约 1 秒、**不可见**（`alpha = 0.004`），无布局/交互影响；
  窗口遮罩（dim）保持关闭。
- App 侧仅两处行为改动：`OnboardingPage.kt`（开始按钮标签）、`OverlayWarmUpHost` 挂载点
  （`MainActivity` / `OnboardingBaseActivity`）。
- 其余改动集中在 `:performance:baselineprofile` 的采集脚本，不进 APK 运行时。

## 五、维护约束

1. `OverlayWarmUpHost` 必须**真正产生像素**：不要改回 `alpha = 0`、离屏平移或透明背景
   （见 `UI_MODAL_RENDERING_PERF_ZH_HANS.md` §7）。
   例外见 §一追加：**重组件内容**按"只预热组合"处理，并且不要在每次进程启动都让它做首次绘制
   ——分档依据是"冷在哪一半"（骨架冷在栅格/合成，重内容冷在组合）。
2. 新增 / 改词 UI 时同步更新 `BenchmarkJourneys.kt` 顶部的标签表（报告会兜底暴露，但提前对齐更省事）。
3. 采集设备是 320x640：涉及内容区的操作**必须在目标 `ScrollView` 的 bounds 内滚动**。
4. 探针一律使用 `findVisibleObject`，避免匹配到 App 自有但不在屏幕上的节点（通知行等）。
