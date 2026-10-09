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
