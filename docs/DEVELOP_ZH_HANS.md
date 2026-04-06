# 开发指南

本文档用于说明 MonadBox/YumeBox 的本地开发环境与常用流程。

## 1. 平台范围

- GitHub Actions CI 仅运行在 Ubuntu。
- 本地开发支持 Windows、macOS、Linux。

## 2. 前置依赖

- JDK 21
- Android SDK（compile/target SDK 36）
- Android NDK `27.3.13750724`
- PATH 中可用 Kotlin 命令（用于执行 `scripts/native-build.main.kts`）

项目统一版本定义在 [gradle.properties](../gradle.properties)。

## 3. 本地配置模板

在仓库根目录复制并填写以下模板：

- [local.properties.example](../local.properties.example) -> `local.properties`
- [startup-gate.local.properties.example](../startup-gate.local.properties.example) -> `startup-gate.local.properties`
- [signing.properties.example](../signing.properties.example) -> `signing.properties`（仅在需要签名时）

## 4. 构建与测试

在仓库根目录执行：

```bash
./gradlew build
./gradlew test
./gradlew lint
./gradlew spotlessApply
```

Windows PowerShell 下使用 `./gradlew.bat`。

## 5. APK 构建与产物

常用 APK 构建命令（仓库根目录）：

```bash
./gradlew assembleDebug
./gradlew assembleRelease
```

仅构建 app 模块时可使用：

```bash
./gradlew :app:assembleDebug
./gradlew :app:assembleRelease
```

产物路径：

- Debug APK：`app/build/outputs/apk/debug/`
- Debug/Release 原始输出目录：`app/build/outputs/apk/`
- Release APK：`app/build/outputs/apk/release/`

在 Windows PowerShell 中，以上命令可替换为 `./gradlew.bat ...`。

## 6. Release 签名配置

项目支持以下签名配置来源（优先级从高到低）：

- Gradle 参数（`-Pkeystore.path=...` 等）
- 环境变量
- `signing.properties`

### 6.1 方式一：使用 signing.properties（本地最简单）

1. 复制 [signing.properties.example](../signing.properties.example) 为 `signing.properties`
2. 填写你自己的签名信息：

```properties
keystore.path=/absolute/or/relative/path/to/release.jks
keystore.password=YOUR_KEYSTORE_PASSWORD
key.alias=YOUR_KEY_ALIAS
key.password=YOUR_KEY_PASSWORD
```

说明：

- `keystore.path` 可填写绝对路径，也可填写相对仓库根目录路径。
- `signing.properties` 仅用于本地，不应提交到仓库。

### 6.2 方式二：使用环境变量（CI 推荐）

```text
YUMEBOX_KEYSTORE_PATH
YUMEBOX_KEYSTORE_PASSWORD
YUMEBOX_KEY_ALIAS
YUMEBOX_KEY_PASSWORD
```

### 6.3 验证签名配置

```bash
./gradlew :app:assembleRelease
```

如签名未配置完整，构建会失败并提示缺少签名信息。

### 6.4 一键初始化脚本（Windows/macOS/Linux）

仓库内提供了跨平台初始化脚本，可生成 keystore，写入 `signing.properties` 与 `startup-gate.local.properties`，并验证 release 构建：

```powershell
./scripts/setup-release-signing.ps1
```

```bash
./scripts/setup-release-signing.sh
```

常用参数：

```powershell
./scripts/setup-release-signing.ps1 -KeystorePath "D:\keystore\monadbox-release.jks" -Alias "monadbox-release"
./scripts/setup-release-signing.ps1 -SkipBuild
./scripts/setup-release-signing.ps1 -ForceRegenerate
./scripts/setup-release-signing.ps1 -DryRun
```

```bash
./scripts/setup-release-signing.sh --keystore-path "$HOME/.android/keystore/monadbox-release.jks" --alias "monadbox-release"
./scripts/setup-release-signing.sh --skip-build
./scripts/setup-release-signing.sh --force-regenerate
./scripts/setup-release-signing.sh --dry-run
```

说明：release 启动时会执行 StartupGate 校验；如果本地签名指纹未配置到 `startup-gate.local.properties`，应用会在启动时直接退出。使用上述脚本可自动生成正确指纹配置。

## 7. 内核同步脚本

按终端环境选择：

- Linux/macOS:

```bash
./scripts/sync-kernel.sh alpha
```

- Windows PowerShell:

```powershell
./scripts/sync-kernel.ps1 alpha
```

- Windows CMD:

```bat
scripts\sync-kernel.bat alpha
```

可选 channel：`alpha`、`meta`、`smart`。

## 8. Native 构建

```bash
kotlin scripts/native-build.main.kts --all
```

## 9. 开发辅助脚本

仓库健康检查：

- Linux/macOS:

```bash
./scripts/repo-health.sh
```

- Windows PowerShell:

```powershell
./scripts/repo-health.ps1
```

运行时模糊测试辅助脚本：

- Linux/macOS:

```bash
./scripts/llm-runtime-fuzz.sh --rounds 5 --events-per-round 200
```

- Windows PowerShell:

```powershell
./scripts/llm-runtime-fuzz.ps1 -Rounds 5 -EventsPerRound 200
```

Release 签名初始化脚本（Windows/macOS/Linux）：

```powershell
./scripts/setup-release-signing.ps1
```

```bash
./scripts/setup-release-signing.sh
```

## 10. 脚本可执行位（macOS/Linux）

如克隆后脚本不可执行，可在仓库根目录运行：

```bash
chmod +x gradlew scripts/*.sh
```

## 11. 相关文档

- [贡献说明](./CONTRIBUTING.md)
- [项目中文概览](./README_ZH_HANS.md)
- [第三方依赖](./ThirdParty.md)
