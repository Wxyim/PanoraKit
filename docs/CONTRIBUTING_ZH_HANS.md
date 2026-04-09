# 参与贡献 MonadBox

[简体中文](CONTRIBUTING_ZH_HANS.md) | [English](CONTRIBUTING.md)

文档中心：[README_ZH_HANS.md](../docs/README_ZH_HANS.md)

本文档定义参与贡献约束。环境初始化、native 构建链与 release 打包流程见 [DEVELOP_ZH_HANS.md](DEVELOP_ZH_HANS.md)。

## 1. 规范范围

本仓库约束分为三类：

- 静态风格约束
- 架构约束
- 回归防守约束

约束均通过 Gradle 任务自动校验，并作为合并前置条件。

## 2. 代码风格

Kotlin 与 C/C++ 风格通过项目级 IDE 配置统一。

1. 使用 Android Studio 或 IntelliJ IDEA 打开项目。
2. 进入 `File -> Settings -> Editor -> Code Style -> C/C++ and Kotlin`。
3. 选择 `Scheme: Project`。

语言规则：

| 语言 | 缩进 | 通配导入 |
| --- | --- | --- |
| Kotlin / KTS | 4 空格 | 禁止 |
| Go | 制表符 | 不适用 |
| C / C++ | 2 空格 | 不适用 |
| Markdown | 按需保留行尾空格 | 不适用 |

提交前执行：

```sh
.\gradlew.bat spotlessApply
```

## 3. UI 契约校验

任务：`checkUiContracts`

```sh
.\gradlew.bat checkUiContracts
```

校验目标：

- `app/src` 与 `modules/feature` 中的 Destination 声明
- `config/ui-capability-registry.txt` 中的登记完整性
- 导航图中的路由可达性
- `settingsSection` 与 `app/src/screen/settings/SettingPager.kt` 的一致性
- 嵌套导航图 Destination 的显式登记（除 `RootGraph` 外）

注册表格式（管道符分隔，共 8 列）：

```text
# destination|screenFile|capabilityId|ownerModule|uiType|entryMode|settingsSection|implementationFiles
SomeScreen|path/to/SomeScreen.kt|some-id|feature/module|top-level|navigation||path/to/SomeScreen.kt
AnotherScreen|path/to/AnotherScreen.kt|another-id|app|top-level|navigation|ui-settings|path/to/AnotherScreen.kt
```

列定义：`destination`（路由类名）、`screenFile`（文件路径）、`capabilityId`（kebab-case 标识）、`ownerModule`（模块子目录路径）、`uiType`（`system`、`top-level`、`detail`、`editor`）、`entryMode`（`start`、`navigation`、`implicit`）、`settingsSection`（`ui-settings`、`more` 或留空）、`implementationFiles`（分号分隔路径列表）。

失败处理：

1. 查看 `build/reports/ui-contracts/` 报告。
2. 补齐缺失登记项。
3. 对齐 `settingsSection` 与 `SettingPager.kt`。

## 4. Compose 生命周期收集

Compose UI 状态必须使用：

```kotlin
val state by viewModel.uiState.collectAsStateWithLifecycle()
```

一次性副作用（导航、Toast、瞬时消息）必须使用：

```kotlin
CollectFlowWithLifecycle(viewModel.navigationEvents) { event ->
    navController.navigate(event.route)
}
```

参考实现：`app/src/presentation/component/CollectFlowWithLifecycle.kt`

Compose 页面禁用模式：

- `collectAsState()`
- `LaunchedEffect(Unit) { flow.collect { ... } }`

## 5. Application 启动约束

`app/src/App.kt` 仅承担进程级初始化。延迟与运行时敏感启动逻辑统一放在 `app/src/startup/AppStartupCoordinator.kt`。

```text
App.onCreate()
  -> 进程级初始化（DI、语言）
  -> ensureDeferredStartupInitialized()
       -> AppStartupCoordinator
```

附加门控：`AppTrafficStatisticsCollector` 依赖 `ProxyFacade.isRunning == true`。

贡献规则：

- 不向 `App.kt` 添加延迟启动逻辑
- 新增启动步骤通过协调器接入

## 6. 现代化基线

任务：`checkModernizationBaseline`

```sh
.\gradlew.bat checkModernizationBaseline
```

不变式：

| 不变式 | 范围 |
| --- | --- |
| 禁止 `collectAsState()` | app/feature Compose 源码 |
| 禁止裸写 `LaunchedEffect { flow.collect }` | app/feature Compose 源码 |
| `App.kt` 保持精简 | app 模块 |
| 保留 `lifecycle-runtime-compose` 依赖 | 关键 feature 模块 |

该任务为强约束，不接受用于绕过规则的内联豁免。

## 7. 提交检查清单

1. `.\gradlew.bat spotlessApply`
2. `.\gradlew.bat check`
3. `.\gradlew.bat test`
4. 新增或重命名 Destination 后更新 `config/ui-capability-registry.txt`。

## 8. 许可证

MonadBox 采用 [AGPLv3](https://www.gnu.org/licenses/agpl-3.0.html) 许可。请保留现有许可证头部。

## 9. 相关文档

- 开发指南：[DEVELOP_ZH_HANS.md](../docs/DEVELOP_ZH_HANS.md)
- 性能文档：[PERFORMANCE_ZH_HANS.md](../docs/PERFORMANCE_ZH_HANS.md)
- English 文档中心：[README.md](../docs/README.md)
