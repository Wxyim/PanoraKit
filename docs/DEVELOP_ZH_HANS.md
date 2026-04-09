# 开发指南

[简体中文](DEVELOP_ZH_HANS.md) | [English](DEVELOP.md)

文档中心：[README_ZH_HANS.md](../docs/README_ZH_HANS.md)

本文档定义环境与构建流程。代码规范与架构约束见 [CONTRIBUTING_ZH_HANS.md](CONTRIBUTING_ZH_HANS.md)。

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

## 11. 相关文档

- 贡献规范：[CONTRIBUTING_ZH_HANS.md](../docs/CONTRIBUTING_ZH_HANS.md)
- 性能文档：[PERFORMANCE_ZH_HANS.md](../docs/PERFORMANCE_ZH_HANS.md)
- English 文档中心：[README.md](../docs/README.md)
