# 2026 现代 Android 应用设计与工程规范

[简体中文](SPEC_ZH_HANS.md) | [English](SPEC.md)

**状态**：Draft
**适用范围**：2026 年现代 Android 应用；尤其适用于工具类、控制台类、配置类、编辑器类、调试类、网络类、复杂设置类应用。
**目标**：为新项目设计、旧项目整改、vibe coding 评审、架构重构、设计系统沉淀、质量门禁建立统一规范。

Android 官方当前的现代化方向明确强调：分层架构、单向数据流、UI state 与 state holder、Repository、自适应布局、Compose 设计系统、语义与测试、edge-to-edge、前台服务约束、以及面向大屏与多窗口的运行时适配。Compose 允许在 Material 基础上扩展或自建 design system，而不是被默认样式绑定。([Android Developers][1])

---

## 1. 规范语言

本规范使用以下术语：

* **MUST**：必须满足
* **MUST NOT**：禁止
* **SHOULD**：强烈建议满足，除非存在充分、明确、可审计的例外理由
* **SHOULD NOT**：原则上不应采用
* **MAY**：可选

---

## 2. 规范目标

本规范用于约束以下事项：

* 产品分层与能力暴露
* 信息架构与任务流组织
* 对象模型与状态机
* UI 架构与 Compose 设计系统
* Adaptive UI 与多窗口支持
* 平台约束适配
* 安全、隐私、调试边界
* 性能、无障碍、测试与发布门禁

本规范 **MUST NOT** 被解释为某一种视觉流派说明书。
本规范 **MUST** 被解释为一套产品、设计、工程、质量协同约束。

---

## 3. 产品定义

现代 Android 应用，尤其是复杂工具型应用，**MUST** 被定义为：

> 由对象模型、状态机、任务流、设计系统、运行时适配与质量门禁共同构成的产品系统。

应用 **MUST NOT** 被定义为“若干页面的集合”。
应用 **MUST NOT** 以“界面是否像某平台现有产品”作为主要完成标准。
应用 **SHOULD** 以以下目标作为一级评价维度：

* **可理解**：当前状态、原因、风险、结果清晰可见
* **可操作**：关键任务路径短、误触成本低、反馈及时
* **可恢复**：高影响变更可预演、可撤销、可回滚
* **可扩展**：对象关系、能力边界、模块职责可持续演进
* **可适配**：在手机、平板、折叠屏、桌面窗口、多窗口下保持一致可用
* **可验证**：状态、组件、交互与性能具备自动化验证入口

---

## 4. 适用产品类型

本规范优先适用于以下类型：

* 配置管理器
* 代理 / VPN / 网络控制台
* 编辑器 / IDE 辅助工具
* 日志 / 监控 / 诊断工具
* 高状态密度设置类应用
* 导入 / 导出 / 同步 / 版本控制类工具
* 多对象、多策略、多面板的生产力工具

若产品具备以下任一特征，则 **SHOULD** 采用本规范：

* 状态密度高
* 误操作成本高
* 用户层级差异大
* 对象关系复杂
* 需要长期维护
* 需要跨窗口或大屏适配

---

## 5. 总体原则

### 5.1 任务优先

应用表层 **MUST** 以用户任务组织，而非以内部实现术语组织。
顶层结构 **SHOULD** 优先呈现：

* 当前状态
* 当前目标
* 当前可执行动作
* 常见故障修复路径
* 当前影响范围

### 5.2 状态优先

每个主页面 **MUST** 先回答“现在发生了什么”，再回答“可以点什么”。
纯按钮堆叠、入口堆叠、设置项堆叠 **SHOULD NOT** 作为主界面组织方式。

### 5.3 解释优先

复杂系统 **SHOULD** 提供“为什么”的解释层。
系统 **MUST NOT** 长期依赖用户自行推断配置、策略、状态与结果之间的关系。

### 5.4 恢复优先

高影响操作 **MUST** 有确认、预演、差异或恢复机制。
“改完即生效且不可回退”的策略 **SHOULD NOT** 作为默认交互。

### 5.5 自适应优先

应用 **MUST** 面向运行时窗口空间设计，而不是面向设备名义类别设计。
Android 官方明确推荐基于窗口大小与运行时可用空间设计 adaptive UI，并提供 `NavigationSuiteScaffold`、`ListDetailPaneScaffold`、`SupportingPaneScaffold` 等模式以支持在不同窗口尺寸间切换导航与多面板布局。([Android Developers][2])

---

## 6. 用户层级模型

应用 **MUST** 以 **初级 / 中级 / 高级** 三层能力模型设计。
应用 **MUST NOT** 仅以“简洁模式 / 专业模式”二元切换替代分层暴露。
应用 **MUST NOT** 做成三套割裂产品。

### 6.1 初级层（L1）

**目标**：直接可用。
**定义**：用户无需理解底层对象，即可完成主任务，并在失败时获得明确修复路径。

L1 层 **MUST** 支持：

* 导入 / 创建核心对象
* 启动 / 停止 / 保存 / 应用等主任务
* 当前状态总览
* 当前生效对象展示
* 健康检查或诊断入口
* 面向非专家的故障解释

L1 层 **MUST NOT** 默认暴露：

* 原始规则树
* 脚本 / 插件 / 扩展配置
* 低层协议细节
* 破坏性高级开关
* 条件性复杂调试能力

### 6.2 中级层（L2）

**目标**：理解原因并优化。
**定义**：用户开始关心系统为何如此决策、为何出错、如何优化与排障。

L2 层 **SHOULD** 支持：

* 解释链
* 差异摘要
* 健康报告
* 按对象查看状态来源
* 常见失败阶段说明
* 中间策略或中间状态的可见性

### 6.3 高级层（L3）

**目标**：精确控制与深度调试。
**定义**：用户需要对象级控制、版本管理、原始导出、批量操作和高级诊断。

L3 层 **MAY** 支持：

* 结构化编辑与文本编辑双视图
* 对象树与依赖关系
* 原始 trace
* 原始导出
* 批量操作
* 变更 diff / merge / rollback
* 条件性高级调试

### 6.4 分层暴露规则

应用 **MUST** 保持同一对象在三层中的概念一致。
应用 **SHOULD** 只改变信息密度与动作深度，不改变对象定义。
高级能力 **MUST** 可达；高级能力 **MUST NOT** 默认压在表层。
低层能力 **MUST NOT** 伪装成表层安全动作。

---

## 7. 信息架构规范

### 7.1 顶层导航

顶层导航 **SHOULD** 面向任务与状态，而非实现术语。
推荐顶层导航类别：

* 仪表盘
* 主任务
* 活动 / 会话 / 日志
* 配置 / 资源
* 工具箱
* 设置

顶层导航 **SHOULD NOT** 直接采用如下实现术语作为全局一级入口：

* Rule
* Rewrite
* Script
* Engine
* Kernel
* Advanced
* Internal

### 7.2 页面骨架

每个主页面 **SHOULD** 遵循以下骨架：

1. 状态摘要
2. 主动作
3. 次级动作
4. 对象列表或详情
5. 风险、诊断或补充说明

### 7.3 导航与多面板

复杂对象型应用 **SHOULD** 支持 canonical layout：

* 小窗口：单面板
* 中窗口：导航 + 列表 / 详情
* 大窗口：列表 + 详情 + 辅助面板

Compose 官方已提供适配性导航与多面板布局能力，并建议在运行时窗口变化时动态切换导航形态。([Android Developers][3])

---

## 8. Adaptive UI 规范

### 8.1 基本要求

应用 **MUST** 根据运行时窗口大小与可用空间适配。
应用 **MUST NOT** 依赖 `screenWidthDp >= 600` 一类静态阈值作为长期核心策略。
应用 **MUST NOT** 依赖锁方向、禁 resize、或平板特判来规避适配。
Android 官方明确要求应用处理窗口运行时变化、方向变化、分屏与桌面窗口模式，并反对以过时限制规避适配。([Android Developers][2])

### 8.2 推荐实现

应用 **SHOULD**：

* 基于窗口大小类派生顶层导航
* 在中大窗口提供 list-detail 或 supporting pane
* 在运行时窗口变化时保持状态连续
* 将窗口信息提升为 UI state，而不是零散读配置

### 8.3 大屏与多窗口

大屏与桌面窗口模式下，应用 **SHOULD** 支持：

* 左侧导航或对象树
* 中央详情
* 右侧辅助面板
* 同时查看多个上下文
* 可独立关闭或折叠的 supporting pane

---

## 9. 设计系统规范

### 9.1 路线

应用 **SHOULD** 采用 **Material-informed, information-first** 路线。
即：使用 Material 3 / Compose 提供的主题、基础组件、自适应导航与交互基础，但产品语义 **MUST** 由自定义 design system 承载。Compose 官方明确说明 Material 是推荐方案，但并非强制，可扩展 `MaterialTheme` 或构建完整自定义主题系统。([Android Developers][4])

### 9.2 设计系统层次

应用 **MUST** 至少定义：

* 主题 token
* 语义色 token
* 形状 token
* 边框与层级 token
* 字体层级
* 状态 token
* 动效 token
* 组件规范
* 语义与无障碍契约

### 9.3 语义色

应用 **SHOULD** 至少有以下语义色域：

* `Primary/Brand`
* `Success/Active`
* `Info/Network`
* `Warning/Override`
* `Danger`
* `Neutral`

约束如下：

* 背景 **SHOULD** 低饱和
* 前景 **MUST** 高辨识
* 危险操作 **MUST** 显式危险语义
* 当前生效状态 **MUST** 稳定可见
* 只读项 **MUST NOT** 长得像主动作
* 同一语义 **MUST** 全局一致

### 9.4 组件族

应用 **SHOULD** 沉淀以下基础组件族：

* `StatusBadge`
* `SemanticActionButton`
* `HealthBanner`
* `MetricCard`
* `SettingsRow`
* `TimelineRow`
* `DiffViewer`
* `NavigationSurface`
* `EditorCommandBar`
* `TracePanel`

### 9.5 列表与设置页

列表与设置页 **MUST** 一眼可区分：

* 可点击项
* 开关项
* 危险项
* 只读项
* 当前状态项

列表与设置页 **SHOULD** 使用柔性分割，包括：

* 轻量 divider
* inset divider
* 容器层级
* 组标题
* 留白层级

仅靠标题与说明文字的明度差建立全部层级，**SHOULD NOT** 视为合格设计。

---

## 10. 对象模型规范

复杂应用 **MUST** 先定义对象模型，再定义页面。
对象模型 **MUST NOT** 由页面临时字段反推。

### 10.1 通用对象

复杂工具型应用 **SHOULD** 至少建模以下对象：

* `Profile`：完整配置或工作快照
* `Source` / `Subscription`：外部来源与同步信息
* `Policy` / `StrategyGroup`：中间决策集合
* `RuleSet` / `Matcher`：匹配规则集合
* `Session` / `ActivityTrace`：一次活动、请求或会话过程
* `HealthReport`：健康与异常摘要
* `WorkspaceSnapshot`：本地草稿、历史版本、回滚点

### 10.2 对象最小字段

每个核心对象 **MUST** 至少具有：

* 稳定 ID
* 用户可见名称
* 生命周期状态
* 最近更新时间
* 来源或 owner
* 是否可编辑
* 风险级别
* 与当前生效状态的关系

### 10.3 对象关系

对象关系 **MUST** 显式建模。
对象关系 **MUST NOT** 隐含在字符串、页面顺序或临时映射中。

---

## 11. 状态与状态机规范

### 11.1 单一状态源

业务真相 **MUST** 存在单一状态源。
UI **MUST NOT** 直接以控件局部状态作为业务真相。
Compose 与 Android 架构文档均要求采用单向数据流，UI 读取 state、发送 event；涉及业务逻辑时，状态应由 state holder 或 ViewModel 统一持有。([Android Developers][5])

### 11.2 生命周期状态

可运行对象 **SHOULD** 至少定义：

* `Idle`
* `Preparing`
* `Active`
* `Degraded`
* `Failed`
* `Stopping`
* `Stopped`

### 11.3 变更状态

可编辑对象 **SHOULD** 至少定义：

* `Synced`
* `Modified`
* `Conflicted`
* `Applying`
* `Applied`
* `Invalid`
* `Reverted`

### 11.4 错误模型

错误 **MUST NOT** 仅为一段字符串。
错误 **SHOULD** 结构化，至少包含：

* category
* phase
* impact
* retryability
* suggested action
* raw cause
* user-visible message

---

## 12. 架构规范

### 12.1 分层架构

应用 **MUST** 至少分为：

* UI 层
* Data 层

复杂应用 **SHOULD** 增设：

* Domain 层
* System Integration 层
* Observability 层

Android 官方推荐应用至少具备 UI 层与 Data 层，并可根据复杂度增加 Domain 层；同时建议 Repository 作为数据边界，集中业务逻辑与数据整合。([Android Developers][6])

### 12.2 Repository

Repository **MUST**：

* 作为数据访问边界
* 聚合本地与网络数据源
* 输出稳定数据流
* 封装底层实现差异
* 可替换、可单测

### 12.3 State holder / ViewModel

涉及业务逻辑的 screen state **SHOULD** 由可保留 state holder 或 ViewModel 持有。
这类状态持有者 **SHOULD** 在 Activity 重建后保留状态或能够恢复最近稳定状态。Android 官方明确指出，业务逻辑 state holder 应在配置变化后保留状态，并在必要时重建一致状态。([Android Developers][7])

### 12.4 Offline-first

只要应用依赖网络，关键读取路径 **SHOULD** 采用 offline-first 思路。
Repository **SHOULD** 同时具备本地与网络数据源。
至少关键读取操作 **MUST** 在无网络时仍可运行。Android 官方明确指出，offline-first 应优先本地数据、至少保证关键 reads 离线可用，并要求有不依赖网络的本地数据源。([Android Developers][8])

---

## 13. 配置与变更管理规范

### 13.1 基本要求

配置、草稿、策略与规则 **MUST** 被视为结构化资产。
配置 **MUST NOT** 被视为只读文本 blob。

### 13.2 变更流程

高影响变更 **SHOULD** 支持以下流程：

1. 解析
2. 校验
3. 预演
4. 差异查看
5. 应用
6. 失败恢复
7. 历史记录

### 13.3 必备能力

复杂配置型应用 **SHOULD** 提供：

* 导入前体检
* 应用前 diff
* 更新后差异摘要
* 冲突提示
* 稳定版本回滚
* 无效项与覆盖关系提示

### 13.4 编辑器

编辑器 **SHOULD** 同时支持：

* 结构化编辑
* 文本编辑
* 错误定位
* 变更高亮
* 命令栏
* 应用前预演

---

## 14. 可解释性规范

### 14.1 解释链

复杂系统 **SHOULD** 提供标准解释链。
通用模板如下：

`Input → Match / Decision → Intermediate Policy → Output → Result`

错误模板如下：

`Phase → Root Cause → Impact → Suggested Action`

### 14.2 展示规则

表层 **MUST** 先展示结论。
中层 **SHOULD** 展示原因。
高级层 **MAY** 展示原始 trace。
系统 **MUST NOT** 只输出底层原文并要求用户自行解释。

---

## 15. 可观测性与日志规范

### 15.1 日志层级

应用 **SHOULD** 至少定义：

* `UserVisible`
* `Operational`
* `Diagnostic`
* `Security`
* `Failure`

### 15.2 结构化日志

关键日志 **SHOULD** 至少包含：

* timestamp
* object id
* action
* phase
* status
* correlation id
* configuration version
* error category

### 15.3 用户可见日志

用户可见日志 **MUST** 优先表达：

* 发生了什么
* 影响了什么
* 是否已恢复
* 下一步建议

用户可见日志 **MUST NOT** 直接暴露底层技术原文作为唯一内容。

### 15.4 调试导出

调试导出包 **SHOULD** 标准化，至少包含：

* 应用版本
* 平台版本
* 关键状态快照
* 最近失败链
* 脱敏日志
* 配置版本标识

---

## 16. Android 平台约束规范

### 16.1 Edge-to-edge

目标 SDK 35+ 的应用，在 Android 15+ 上默认 edge-to-edge；目标 Android 16 时，退出 edge-to-edge enforcement 的方式已不可用。应用 **MUST** 正确处理 system bars、gesture insets 与 IME。([Android Developers][9])

要求如下：

* Activity 初始化 **SHOULD** 启用 edge-to-edge
* 列表、底栏、FAB、输入区 **MUST** 正确处理 insets
* 关键动作 **MUST NOT** 被系统栏遮挡
* 横屏、多窗口、输入法弹出场景 **MUST** 单独验收

### 16.2 Predictive Back

应用 **SHOULD** 支持 predictive back。
若存在自定义返回、面板关闭、编辑器退出确认、栈内动画，**MUST** 使用受支持的现代返回 API。Compose 提供 `PredictiveBackHandler`；Android 官方明确建议尽快迁移至 predictive back 兼容实现。Android 16 还将 predictive back 扩展到 3-button navigation 场景。([Android Developers][10])

### 16.3 前台服务

应用若使用前台服务，**MUST** 为每个 FGS 声明正确 service type，并满足对应 manifest 与运行时权限要求。Android 14+ 已要求声明合适的 foreground service type。([Android Developers][11])

### 16.4 本地网络权限

若应用需要本地网络发现、局域网调试、LAN 连接、设备互联，**MUST** 预留本地网络权限路径与降级策略。Android 17 引入 `ACCESS_LOCAL_NETWORK` 运行时权限，并默认阻断 target 37+ 应用的本地网络访问；Android 16 为过渡阶段。([Android Developers][12])

### 16.5 VPN 与 per-app 场景

若应用涉及 VPN、网络接管或 per-app routing，**SHOULD** 从设计初期考虑：

* 用户授权
* always-on
* per-app VPN
* 断连恢复
* 通知可见性
* 生命周期一致性

Android 官方的 VPN 文档明确将 custom VPN、always-on 与 per-app VPN 作为标准能力域。([Android Developers][13])

---

## 17. 安全与隐私规范

### 17.1 环境分离

应用 **MUST** 区分：

* 调试环境
* 测试环境
* 生产环境

调试信任策略、弱化校验策略、开发用证书或特殊网络配置 **MUST NOT** 进入生产构建。

### 17.2 Network Security Configuration

如使用 Network Security Configuration，调试专用信任策略 **SHOULD** 通过 debug-only 方式声明。Android 官方明确支持通过 Network Security Configuration 控制 trust anchors、cleartext policy、debug-overrides 与从 Android 17 起的 ECH 行为。([Android Developers][14])

### 17.3 日志与导出最小化

调试数据采集 **MUST** 最小化。
敏感导出 **MUST**：

* 导出前确认
* 风险提示
* 支持脱敏
* 可删除
* 明示保存位置

### 17.4 能力声明边界

应用 **MUST NOT** 做绝对化安全或调试承诺，例如：

* 所有连接均可解密
* 所有 App 均可抓包
* 所有 HTTPS 请求均可内容级分析

若能力受平台、证书信任、pinning、ECH 或目标应用策略限制，应用 **MUST** 明示边界。Android 7.0 起，target API 24+ 的应用默认不再信任用户安装的 CA；Network Security Configuration 可进一步自定义 trust anchors；Android 17 为支持的 TLS 客户端 / 服务器引入 opportunistic ECH，会减少某些握手可见信息。([Android Developers][14])

---

## 18. 性能规范

### 18.1 关键路径

以下路径 **MUST** 被定义为关键性能路径：

* 冷启动
* 首屏状态建立
* 列表滚动
* 对象详情切换
* 搜索与筛选
* 面板展开 / 折叠
* 编辑器输入
* 保存 / 应用配置
* 大体量日志加载

### 18.2 Compose 性能约束

Compose UI **SHOULD**：

* 避免无意义重组
* 避免在组合路径执行昂贵计算
* 维持参数与状态稳定性
* 将业务逻辑移出 composable 主体
* 使用合适的 remember、derived state、懒加载与状态提升

### 18.3 Baseline Profile

应用 **SHOULD** 提供 app-specific Baseline Profile，并以关键用户路径生成与验证。Android 官方明确指出，Baseline Profiles 可使代码执行速度从首次启动起提升约 30%，Compose 也建议应用在默认 profile 之外补充 app-specific profile。([Android Developers][15])

### 18.4 性能验收

每个发布版本 **SHOULD** 至少验证：

* 冷启动时间
* 首屏可用时间
* 列表与日志滚动稳定性
* 关键交互的延迟与抖动
* 重配置后的性能回归
* 长时运行内存增长趋势

---

## 19. 无障碍与语义规范

### 19.1 生产要求

无障碍与语义 **MUST** 视为生产要求。
Compose semantics 同时服务无障碍、autofill 与 testing。([Android Developers][16])

### 19.2 组件语义

所有自定义组件 **MUST** 定义：

* role
* label
* state description
* selected / checked / enabled
* clickability
* danger / warning 状态
* value / progress / range（如适用）

### 19.3 状态表达

应用 **MUST NOT** 仅依赖颜色表达状态。
状态 **SHOULD** 至少由以下两类信号中的两类表达：

* 颜色
* 文本
* 图标
* 边框 / 容器
* 位置 / 结构差异

### 19.4 复杂容器语义

复杂列表项、卡片容器、命令条与状态条 **SHOULD** 视情况使用语义合并或重设。Compose 官方提供 `mergeDescendants`、`clearAndSetSemantics` 等能力以控制语义树。([Android Developers][17])

---

## 20. 测试规范

### 20.1 测试层级

应用 **MUST** 建立分层测试体系，至少包括：

* 单元测试
* Repository / data layer 测试
* 状态机测试
* Compose UI 测试
* 关键路径集成测试

### 20.2 状态机测试

关键对象的状态迁移 **MUST** 可测试。
至少覆盖：

* 正常路径
* 失败路径
* 中断路径
* 重试路径
* 冲突路径
* 恢复路径

### 20.3 Compose UI 测试

Compose UI 测试 **MUST** 基于稳定语义节点。
自定义组件若无法被稳定选择、断言与交互，**MUST** 视为未达到可测试要求。Android 官方指出，Compose 测试依赖语义树工作。([Android Developers][18])

### 20.4 最低必测路径

以下路径 **MUST** 纳入最低覆盖：

1. 首次启动
2. 主任务成功路径
3. 主任务失败路径
4. 权限拒绝与降级
5. 配置修改与撤销
6. adaptive 布局切换
7. edge-to-edge / IME / system bars 场景
8. 返回导航与面板关闭
9. 高风险操作确认
10. 导出与清除

---

## 21. 面向网络控制 / VPN / 代理类应用的补充规范

> 本章为专业补充；其对象建模与能力分级方法同样适用于其他复杂工具型应用。

### 21.1 核心对象

此类应用 **SHOULD** 至少建模：

* `Profile`
* `Subscription`
* `PolicyGroup`
* `RuleSet`
* `SessionTrace`
* `HealthReport`
* `WorkspaceSnapshot`

### 21.2 三层能力映射

#### 初级层

**MUST** 提供：

* 导入配置或订阅
* 启动 / 停止
* 当前连接状态
* 当前生效配置
* 当前策略
* 一键测试
* 常见故障修复入口

#### 中级层

**SHOULD** 提供：

* 命中链摘要
* 当前出站路径
* DNS 结果
* 更新差异
* 健康体检
* 可读错误解释

#### 高级层

**MAY** 提供：

* 规则对象编辑
* 策略组编辑
* 原始 trace
* 原始流量导出
* 批量操作
* 版本回滚
* 条件性内容级调试

### 21.3 解释链模板

推荐解释链：

`App → DNS → RuleSet → Rule → PolicyGroup → Outbound → Result`

推荐失败链：

`FailedStage → RootCause → Impact → SuggestedAction`

### 21.4 抓包与 MITM 边界

此类应用若提供抓包或解密调试，文案 **MUST** 明确区分：

1. **会话级观察**
2. **原始流量观察**
3. **内容级调试**

应用 **MUST NOT** 将第三类能力描述为普遍可用能力。
Android 的 `VpnService` 允许应用建立自定义 VPN，并处理通过 VPN 接口转发的流量，因此会话级和原始流量观察可作为产品能力；但 TLS 内容级观察是否成立，受目标 App 的信任链、pinning、平台隐私增强与实际库支持影响，并无平台层全局保证。([Android Developers][13])

---

## 22. 现有项目整改规范

### 22.1 适用场景

本章适用于：

* 功能已堆积但缺少结构的项目
* 可运行但状态混乱的项目
* 依赖 vibe coding 快速迭代、欠缺系统约束的项目
* 需要进行现代化重构的存量项目

### 22.2 高风险判定

满足任一项即 **SHOULD** 判定为高风险：

* 页面直接读写全局单例
* 配置文本直接驱动 UI
* 业务状态散落于 composable 局部状态
* 缺乏 ViewModel / state holder 边界
* Repository 仅作透传
* 语义色写死在页面
* 以静态宽度阈值作为长期适配策略
* 未处理 insets / IME / edge-to-edge
* 自定义组件缺少 semantics
* 无 Compose UI 测试
* 无关键变更确认或回滚
* 无性能基线

### 22.3 整改优先级

#### P0：稳定性与合规

**MUST** 优先处理：

* 崩溃
* 状态错乱
* 关键路径不可用
* 平台权限 / 前台服务不合规
* edge-to-edge 遮挡
* 危险操作无确认

#### P1：结构重建

**MUST** 完成：

* 核心对象建模
* UI state 建模
* Repository 边界建立
* ViewModel / state holder 整理
* design token 初步抽取

#### P2：产品能力重建

**SHOULD** 完成：

* 解释链
* 差异查看
* 回滚机制
* 自适应布局重构
* 组件语义补齐
* 自动化测试补齐

#### P3：专业能力增强

**MAY** 完成：

* 深度调试
* 批量工具能力
* 多窗格控制台
* 自动体检与自动恢复
* 原始导出与高级筛选

---

## 23. 评审清单

### 23.1 产品评审

* 一级导航是否面向任务而非内部术语
* 初 / 中 / 高三层能力是否清晰
* 当前状态是否一眼可见
* 错误是否可理解
* 高风险能力是否被隔离

### 23.2 信息架构评审

* 页面是否遵循“摘要 → 动作 → 详情”
* 对象是否可追踪
* 是否支持解释链
* 是否支持差异或变更预览

### 23.3 架构评审

* 是否采用单向数据流
* 是否存在统一状态源
* 是否存在明确 state holder / ViewModel
* Repository 是否承担真实边界职责
* 本地、网络与系统集成是否分层

### 23.4 Android 平台评审

* 是否支持 adaptive UI
* 是否正确处理 insets 与 edge-to-edge
* 是否考虑 predictive back
* 是否满足 FGS type 要求
* 是否具备权限降级路径
* 是否考虑本地网络访问变化

### 23.5 质量评审

* 自定义组件是否具备语义
* 是否存在 UI 测试与状态机测试
* 是否存在性能基线
* 是否存在日志脱敏与调试导出规范

---

## 24. 最低可接受标准

一个符合本规范的现代 Android 复杂应用，至少 **MUST** 满足：

1. 具有明确对象模型
2. 具有统一 UI state
3. 具有 state holder / ViewModel 边界
4. 具有 Repository 边界
5. 具有 adaptive 布局策略
6. 具有 edge-to-edge 与 inset 处理
7. 具有语义化 design system 雏形
8. 具有初 / 中 / 高三层能力暴露
9. 具有关键变更确认机制
10. 具有最小恢复或回退能力
11. 具有 Compose UI 测试入口
12. 具有关键路径性能基线

不满足上述条件的项目，**SHOULD NOT** 视为完成现代化重构。

---

## 25. 实施顺序

### 25.1 新项目

新项目 **SHOULD** 按以下顺序实施：

1. 定义对象模型
2. 定义状态机与 UI state
3. 定义顶层 IA
4. 定义 adaptive 布局策略
5. 定义 design token 与组件族
6. 建立 Repository 与状态边界
7. 实现主任务路径
8. 实现解释链与恢复机制
9. 补齐平台约束处理
10. 补齐测试、语义与性能基线

### 25.2 旧项目

旧项目 **SHOULD** 按以下顺序整改：

1. P0 稳定性与合规
2. P1 结构重建
3. P2 产品能力重建
4. P3 专业能力增强

---

## 26. 仓库级规范声明模板

```md
# Project Spec Statement

This project follows the 2026 Modern Android Design and Engineering Specification.

## Core Rules

- The app uses layered architecture, unidirectional data flow, UI state, state holders, and repository boundaries.
- The UI uses an adaptive-first, edge-to-edge, information-first Compose design system.
- Product capabilities are exposed in three tiers: beginner, intermediate, advanced.
- Core objects are structurally modeled; high-impact changes require confirmation, diff, preview, or recovery.
- Custom components must provide semantics, accessibility support, and stable test hooks.
- Android platform constraints—including permissions, foreground services, window changes, local-network access, and system back behavior—are treated as product constraints, not post-release patches.

## Merge Gates

A feature MUST NOT be considered complete when any of the following is missing:

1. Stable object model
2. Single source of UI truth
3. Adaptive-layout acceptance
4. Edge-to-edge acceptance
5. Critical-path tests
6. High-risk action protection
7. Component semantics
8. Minimum performance baseline
```

---

## 27. 结语

本规范的核心结论如下：

* 现代 Android 应用 **不是** 页面集合，而是产品系统。
* 工具型应用的现代化 **不是** 默认 Material 化，而是信息优先、状态优先、恢复优先、自适应优先。
* 初级用户需要直接可用；中级用户需要看懂原因；高级用户需要完整控制。
* Android 平台约束、窗口变化、权限变化、前台服务、edge-to-edge、predictive back、本地网络访问变化，均 **MUST** 视为一等产品约束。
* 没有对象模型、状态架构、自适应布局、语义、测试与性能基线的项目，**不应** 视为完成现代化工程化。

---

[1]: https://developer.android.com/topic/architecture/recommendations?utm_source=chatgpt.com "Recommendations for Android architecture"
[2]: https://developer.android.com/develop/ui/compose/layouts/adaptive?utm_source=chatgpt.com "About adaptive layouts | Jetpack Compose - Android Developers"
[3]: https://developer.android.com/develop/ui/compose/layouts/adaptive/build-adaptive-navigation?utm_source=chatgpt.com "Build adaptive navigation | Jetpack Compose"
[4]: https://developer.android.com/develop/ui/compose/designsystems/custom?utm_source=chatgpt.com "Custom design systems in Compose - Android Developers"
[5]: https://developer.android.com/develop/ui/compose/architecture?utm_source=chatgpt.com "Compose UI Architecture - Android Developers"
[6]: https://developer.android.com/topic/architecture?utm_source=chatgpt.com "Guide to app architecture - Android Developers"
[7]: https://developer.android.com/topic/architecture/ui-layer/stateholders?utm_source=chatgpt.com "State holders and UI state | App architecture"
[8]: https://developer.android.com/topic/architecture/data-layer/offline-first?utm_source=chatgpt.com "Build an offline-first app | App architecture - Android Developers"
[9]: https://developer.android.com/develop/ui/views/layout/edge-to-edge?utm_source=chatgpt.com "Display content edge-to-edge in views - Android Developers"
[10]: https://developer.android.com/guide/navigation/custom-back/predictive-back-gesture?utm_source=chatgpt.com "Add support for the predictive back gesture | App architecture"
[11]: https://developer.android.com/develop/background-work/services/fgs/service-types?utm_source=chatgpt.com "Foreground service types | Background work"
[12]: https://developer.android.com/privacy-and-security/local-network-permission?utm_source=chatgpt.com "Local network permission | Privacy - Android Developers"
[13]: https://developer.android.com/develop/connectivity/vpn?utm_source=chatgpt.com "VPN | Connectivity - Android Developers"
[14]: https://developer.android.com/privacy-and-security/security-config?utm_source=chatgpt.com "Network security configuration - Android Developers"
[15]: https://developer.android.com/develop/ui/compose/performance/baseline-profiles?utm_source=chatgpt.com "Use a baseline profile | Jetpack Compose - Android Developers"
[16]: https://developer.android.com/develop/ui/compose/accessibility/semantics?utm_source=chatgpt.com "Semantics | Jetpack Compose - Android Developers"
[17]: https://developer.android.com/develop/ui/compose/accessibility/merging-clearing?utm_source=chatgpt.com "Merging and clearing | Jetpack Compose - Android Developers"
[18]: https://developer.android.com/develop/ui/compose/testing?utm_source=chatgpt.com "Test your Compose layout - Android Developers"
