# 开发指南

[简体中文](DEVELOP_ZH_HANS.md) | [English](DEVELOP.md)

本文档定义环境、构建流程与贡献约束。

## 1. 平台与工具链

本地支持平台：Windows、macOS、Linux。

CI 平台：Ubuntu（GitHub Actions）。

必需工具链：

- JDK 21
- Android SDK（compile/target 36）
- Android NDK 27.3.13750724
- PATH 中可用 Kotlin 命令（用于 `scripts/native-build.main.kts`）

版本权威来源：[gradle.properties](../gradle.properties)

## 2. 一次性本地初始化

在仓库根目录复制模板：

| 模板文件 | 本地文件 |
| --- | --- |
| `local.properties.example` | `local.properties` |
| `startup-gate.local.properties.example` | `startup-gate.local.properties` |
| `signing.properties.example` | `signing.properties`（仅 release） |

请勿提交本地凭据和机器路径。

## 3. Native + Gradle 构建顺序

APK 打包依赖 native 产物。

Linux/macOS：

```bash
./scripts/sync-kernel.sh alpha
kotlin scripts/native-build.main.kts --all
./gradlew build
```

Windows PowerShell：

```powershell
./scripts/sync-kernel.ps1 alpha
kotlin scripts/native-build.main.kts --all
.\gradlew.bat build
```

必需 native 输出：

- `build/native/cpp/obj/*/libbridge.so`
- `build/native/go/*/libclash.so`

## 4. 校验任务

在仓库根目录执行：

```bash
./gradlew spotlessApply
./gradlew checkUiContracts
./gradlew checkModernizationBaseline
./gradlew test
./gradlew lint
```

Windows 等价命令：

```powershell
.\gradlew.bat spotlessApply
.\gradlew.bat checkUiContracts
.\gradlew.bat checkModernizationBaseline
.\gradlew.bat test
.\gradlew.bat lint
```

根任务 `check` 已包含 `checkUiContracts` 与 `checkModernizationBaseline`。

## 5. 模块拓扑

依赖方向：

`app -> modules/feature/* -> shared/data/runtime modules`

| 模块组 | 职责 |
| --- | --- |
| `app/` | 入口、启动编排、主导航、Android 打包 |
| `modules/feature/*` | 面向用户功能 |
| `modules/data/*` | 持久化与仓储层 |
| `modules/runtime/*` | 运行时 API/客户端/服务边界 |
| `modules/ui/`, `modules/locale/`, `modules/platform/`, `modules/core/` | 共享基础能力 |

变更优先限定在单模块内。

## 6. APK 构建产物

| 命令 | 产物目录 |
| --- | --- |
| `./gradlew assembleDebug` | `build/app/outputs/apk/debug/` |
| `./gradlew assembleRelease` | `build/app/outputs/apk/release/` |

## 7. Android 模拟器工作流

需确保 Android SDK 的 `emulator` 与 `adb` 工具已加入 PATH。

- (1) 列出可用 AVD：

```sh
emulator -list-avds
```

- (2) 启动指定模拟器：

```sh
emulator -avd <AVD_NAME>
```

- (3) 等待设备启动完成（脚本场景建议执行）：

```sh
adb devices -l
adb wait-for-device
adb shell getprop sys.boot_completed
```

- (4) 构建并安装 Debug APK 到当前运行的模拟器：

```sh
.\gradlew.bat :app:installDebug
```

- (5) 可选：启动应用（替换占位符）：

```sh
adb shell am start -n <applicationId>/<launcherActivity>
```

- (6) 用定向日志验证运行时链路：

```sh
adb logcat -v time | rg -i "clash|runtime|traffic|override"
```

- (7) 验证打包差异时重装 release APK：

```sh
adb install -r build/app/outputs/apk/release/MonadBox-universal-release.apk
```

- (8) 回归测试前可选清理安装态：

```sh
adb uninstall <applicationId>
```

## 8. Release 签名

签名来源优先级：

1. Gradle 参数
2. 环境变量
3. `signing.properties`

本地签名配置：

1. 复制 [signing.properties.example](../signing.properties.example) 到 `signing.properties`。
2. 填写配置：

```properties
keystore.path=/absolute/or/relative/path/to/release.jks
keystore.password=
key.alias=
key.password=
```

CI 变量：

- `YUMEBOX_KEYSTORE_PATH`
- `YUMEBOX_KEYSTORE_PASSWORD`
- `YUMEBOX_KEY_ALIAS`
- `YUMEBOX_KEY_PASSWORD`

验证命令：

```sh
.\gradlew.bat :app:assembleRelease
```

## 9. 常用脚本

| 脚本 | 功能 |
| --- | --- |
| `scripts/sync-kernel.{sh,ps1,bat}` | 同步内核源与频道 |
| `kotlin scripts/native-build.main.kts --all` | 构建 native 产物 |
| `scripts/repo-health.{sh,ps1}` | 仓库健康检查 |
| `scripts/setup-release-signing.{sh,ps1}` | 初始化 release 签名 |
| `scripts/llm-runtime-fuzz.{sh,ps1}` | 运行时 fuzz 辅助 |

## 10. 故障排查

| 现象 | 处理 |
| --- | --- |
| Release 构建在打包前失败 | 检查 native 输出并重跑 native 构建脚本 |
| Gradle daemon 不稳定 | 检查 `gradle.properties` JVM 参数；排查时使用 `--no-configuration-cache` |
| UI 契约校验失败 | 更新 `config/ui-capability-registry.txt` 并与实现对齐 |

## 11. 性能工作流

当前性能工程路径采用测试模块驱动，核心在 `:performance:baselineprofile`。

模块位置：

- Gradle 模块：`modules/performance/baselineprofile`
- Baseline Profile 生成器：`BaselineProfileGenerator.kt`
- 启动 benchmark：`StartupBenchmarks.kt`
- 共享交互路径：`BenchmarkJourneys.kt`

常用命令：

```bash
./gradlew :performance:baselineprofile:connectedCheck \
  -Pandroid.testInstrumentationRunnerArguments.androidx.benchmark.enabledRules=BaselineProfile

./gradlew :performance:baselineprofile:connectedCheck \
  -Pandroid.testInstrumentationRunnerArguments.androidx.benchmark.enabledRules=Macrobenchmark
```

当前限制：

- `:app` 使用自定义 Android source set 布局。
- 在当前 AGP 9 组合下，该布局与端到端 `androidx.baselineprofile` 插件链路不兼容。

## 12. 规范范围

本仓库约束分为三类：

- 静态风格约束
- 架构约束
- 回归防守约束

约束均通过 Gradle 任务自动校验，并作为合并前置条件。

## 13. 代码风格

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

## 14. UI 契约校验

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

## 15. Compose 生命周期收集

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

## 16. Application 启动约束

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

## 17. 现代化基线

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

## 18. 提交检查清单

1. `.\gradlew.bat spotlessApply`
2. `.\gradlew.bat check`
3. `.\gradlew.bat test`
4. 新增或重命名 Destination 后更新 `config/ui-capability-registry.txt`。

## 19. 许可证

MonadBox 自有代码和 fork 衍生代码采用 [AGPL-3.0-only](https://www.gnu.org/licenses/agpl-3.0.html) 许可。请保留现有许可证头部。

根目录 `LICENSE` 不会把第三方依赖或同步上游源码重授权。新增依赖或发布二进制前，请先查看 [LICENSING_ZH_HANS.md](../docs/LICENSING_ZH_HANS.md)。

## 20. 相关文档

- 文档中心：[README_ZH_HANS.md](README_ZH_HANS.md)
- 许可策略与第三方依赖清单：[LICENSING_ZH_HANS.md](../docs/LICENSING_ZH_HANS.md)
