# MonadBox 弹窗 / 半窗动画掉帧分析与优化

> 基于当前源码与依赖产物（compose-ui 1.11.4、haze 1.7.2、liquid 1.1.1、Miuix 0.8.8）的静态分析。
> 真机帧率（janky frames、frame overrun）仍需在目标设备上验证，本文只给结论与验证方法。

## 1. 结论速览

- 弹窗 / 半窗动画掉帧的根因不是动画本身，而是**动画期间整个窗口被逐帧重新录制**，
  并且**整屏背景恰好带着离屏 layer 录制**（haze 源、liquid glass）。
- Miuix 的遮罩与面板动画本身是廉价实现：遮罩用 `drawBehind` 画一个带 alpha 的矩形，
  面板位移 / 缩放用 `graphicsLayer`（只改 layer 属性，不重录 display list），见
  `DialogContentLayoutKt.DialogContentLayout_rvfs2_Q$lambda$13$3$0` 与
  `BottomSheetContentLayoutKt.BottomSheetContentLayout_jaDAVCM$lambda$13$3$0`。
- 半窗（独立 Activity 版）额外每帧提交一次 `window.attributes`，每次都会让系统重算「窗口背后模糊」。

## 2. 绘制模型（为什么 layer 决定帧成本）

Compose 把整棵 UI 画进 `AndroidComposeView` 这一个 View 的 RenderNode。只要窗口里有任意动画，
View 的 display list 就会重录，**没有被包进自身 layer 的子树会逐帧重跑 `draw`**：

- `NodeCoordinator.draw()`：有 `OwnedLayer` 就 `drawLayer` 复用缓存，没有就执行 draw 修饰符链。
- `NodeCoordinator.invalidateLayer()`：失效只会向上传播到**最近的一层 layer** 就停止。
- `RenderNodeLayer.drawLayer()`：只有 `isDirty` 且已有 display list 时才 `renderNode.record(...)`。

因此「谁包了 layer」直接决定动画 frame 里有没有大块重录。

## 3. 本次修复的不变量

- 主 Pager 自带一层（`app/src/main/kotlin/MainActivity.kt` 的 `pagerModifier`）：
  页面内容（含各页的 haze 源、liquid glass 源）只在页面自身变化时重录。
- 弹窗 / 半窗默认渲染在**根 Scaffold**（`AppDialog` / `AppActionBottomSheet` 的
  `renderInRootScaffold = true`，与 Miuix 上游默认一致）：动画发生在缓存背景的兄弟节点上。
- 应用自绘的全屏叠加（首页分流下拉、代理悬浮面板）各自带一层 `graphicsLayer()`。
- 不允许存在「有 source 无 effect」的 haze 源：`hazeSource` 会在每次 draw 时把整棵内容录进
  离屏 layer，没有被任何 `hazeEffect` 消费就是纯浪费。

**新增全屏叠加动画时的规则**：要么让它由根 Scaffold 承载，要么给它自己加一层
`Modifier.graphicsLayer()`，否则它会顺带把整页和玻璃背景一起逐帧重录。

## 4. 已排除的怀疑项

- Miuix 遮罩：`drawBehind + drawRect`（颜色 alpha 逐帧变化），不是整屏离屏 alpha 合成。
- 半窗模糊：`WindowBlurEffect` 现在只在半径真正变化时提交 WindowManager 事务；
  `ProxySheetContent` 的模糊按 `POPUP_BLUR_STEP_COUNT` 档位渐变，不再逐帧提交；
  半径为 0（半窗关闭 / 动画收敛后）时窗口不再挂 `FLAG_BLUR_BEHIND`，避免零半径仍让系统保留模糊区域。
- 弹窗内容列表：dialog / sheet 内的长列表均已使用 Lazy 容器，不存在首帧全量组合。

## 5. 验证方法

```bash
adb shell dumpsys gfxinfo com.github.nomadboxlab.monadbox reset
# 打开 / 关闭若干次弹窗、半窗、首页下拉
adb shell dumpsys gfxinfo com.github.nomadboxlab.monadbox framestats
```

关注 `Janky frames` 与 p95/p99 帧耗时；也可用 Perfetto 抓 `gfx` / `view` 轨道对比
「有弹窗动画」与「无弹窗动画」的 frame timeline。

## 6. 后续可选

- 若仍需要更低的半窗模糊成本：把 `POPUP_BLUR_STEP_COUNT` 降为 `1`（模糊一次性到位，
  窗口事务从 3 次降到 1 次，代价是渐入观感变弱）。
- 半窗目前是「独立 Activity + `WindowBottomSheet`（自带 Dialog 窗口）」，
  改用 `SuperBottomSheet`（在 Activity 窗口内渲染）可少一层窗口合成，但需要在
  `ProxySheetActivity` 里补 `Scaffold`，并复核安全区 / 拖拽行为，属于需要真机确认的改动。

## 7. 补充：全新安装后「第一个浮层」的一次性成本

现象：全新安装后**第一次启动**、第一次打开任意弹框 / 半窗（例如首页切换分流模式且无配置文件时的
错误弹框）时，入场动画会掉帧（上滑「一顿一顿」）；同进程后续浮层正常，第 2 次启动起也正常。

根因与 §1~§3 的「逐帧重录」不是同一件事：这是**首次把这种浮层真正光栅化 / 合成上屏**的一次性
代价（圆角裁剪 + 整屏尺寸的 layer + 变换，加上与内容并列的整屏遮罩兄弟节点）。它既不是弹框 Java
代码冷启动（弹框相关类已进 baseline profile，现象依旧），也不是首次启动的后台任务争抢 CPU。

关键证据（都来自真机复测）：

1. 手动打开一次任意**可见**浮层（例如引导页「调整界面风格 → 主题模式」拉起的半窗）后，之后的弹框
   全部顺滑 → 这份成本是「第一个浮层」付掉的；
2. 用 `alpha = 0` 或把浮层移到屏幕外（被裁剪）做的「不可见预热」**完全无效**——两者都不会产生
   可见像素，也就不会触发那次光栅 / 合成；
3. 把预热改成**按真实几何绘制、只用 `alpha = 0.004`（≈1/255）遮盖**后，全新安装首次启动的第一次
   弹框不再掉帧。

由此得到的不变量：`OverlayWarmUpHost` 的预热浮层必须**真正产生像素**——表面色、圆角裁剪、layer、
内容都按真实弹框 / 半窗来画，只允许用极低 alpha 遮挡，**不要**改回 `alpha = 0`、离屏平移或透明背景
「优化」；唯一例外是 window dim（它与内容并列，开启会真的压暗屏幕）。两处宿主（`MainActivity` 与
首次引导 `OnboardingBaseActivity`）都要挂，因为全新安装会先走引导向导。

追加：真机上 `OverlayWarmUpHost` 的两块浮层改在**独立、输入透明的窗口**里绘制（compose
`Dialog` 加上 `FLAG_NOT_TOUCHABLE | FLAG_NOT_FOCUSABLE`、窗口 alpha 0.004、清 dim、
`renderInRootScaffold = false`）：预热照旧真实绘制同样内容（上面的不变量不变），但在
WindowManager 层就收不到任何输入——启动后约 1 秒不再吞触摸 / 返回手势 / 输入法。模拟器
（含软件渲染的采集 AVD）保留原 app 窗口形态，采集与 profile 采样保持已验证过的样子。取色器
预热的同套窗口机制见 §7.1。

### 7.1 重组件：同一条不变量，宿主是「入口页面 + 独立输入透明窗口」

设置 → 界面 → **主题配色**面板（Miuix `ColorPicker`）是这条规则的四轮现场：

- 第一轮：并入启动预热、用 `alpha = 0`（担心全尺寸绘制拖累软件渲染的采集模拟器）——**用户侧
  仍然卡**。零 alpha 让绘制阶段整段跳过，而它的一次性成本恰恰在绘制：每个滑条在 `drawWithCache`
  里建 `Brush.horizontalGradient` + `Stroke`，Alpha 滑条再建几百个矩形组成的棋盘格 `Path`，
  指示器建 `Brush.radialGradient`。
- 第二轮：把「真实绘制」挪到入口页面行内（`alpha = 0.004`，320dp 宽，自定义 `Layout` 对外报
  1x1 以不推动行布局）——**内容不卡了，滑入动画仍卡**。
- 第三轮：剩余冷点在**面板自身在全高下的首次图层录制 / 光栅化 + 内容在面板容器里的首次布局
  绘制**——只有真正「显示一次面板」才会发生。改为在入口页面不可见地真实打开真实面板：滑入动画
  修复，但面板放在 app 自己的浮层宿主里，**宿主在显示期间吞掉全部触摸**（快速点击入口行没反应；
  Composition 看不到被吞的点击——命中测试止步于宿主的 consume 层）。窗口级 relay 兜底试验后回退。
- 第四轮（最终形态）：面板放进**专属窗口**渲染——compose `Dialog` +
  `FLAG_NOT_TOUCHABLE | FLAG_NOT_FOCUSABLE` + 窗口 alpha 0.004 + 清 dim + `dismissOnBackPress =
  false`，并以 `renderInRootScaffold = false` 让面板注册到该窗口内本地 `Scaffold` 的浮层宿主。

最终实现 `ThemeColorPickerSheetWarmUp`（设置页 / 引导「个性化」步行内）：

- 与真实打开**完全一致**的面板（标题 / 关闭确认动作 / 内容 / nested scroll）；
- 窗口在 WindowManager 层就不可触摸、不聚焦：**触摸、返回手势、输入法全部穿透**，对用户零代价
  ——这是与第三轮的本质区别，也是这条规则的最终形态；
- 窗口以 `alpha = 0.004` 隐藏（≠0，否则渲染会跳过——§7 的不变量），内容清空语义、不留幽灵节点；
- 按墙钟持住约 600ms（enter 是 `folmeSpring(response = 0.38)` 时间驱动弹簧，按帧数在低帧率设备
  会持不够），随后拆除；每进程一次；宿主行出现后两帧启动，远早于用户伸手去点。

补充（模拟器分档）：取色器预热在模拟器上退化为**轻量内容绘制**（仍以 `alpha = 0.004` 真实绘制
拾色器内容，保住 profile 采样），不再打开整块面板——全尺寸面板绘制在软件渲染的采集 AVD 上是
稳定性风险（连续两次采集掉设备后降档）；真机仍走独立输入透明窗口 + 真实面板。

结论：**凡是一次性成本「大头在首次显示」的重组件 / 重浮层，都不要只预热组合、也不要挤进启动
路径——在入口页面把它真实显示一次（不可见）；如果它必须借用浮层宿主，就把预热实例放进一个
输入透明的独立窗口，别让预热挡住用户。**
