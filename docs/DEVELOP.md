# Development Guide

This document describes the local development setup for MonadBox/YumeBox.

## 1. Platform Scope

- GitHub Actions CI is Ubuntu-only.
- Local development is supported on Windows, macOS, and Linux.

## 2. Prerequisites

- JDK 21
- Android SDK (compile/target SDK 36)
- Android NDK `27.3.13750724`
- Kotlin command runner available in PATH for `scripts/native-build.main.kts`

Project-wide versions are defined in [gradle.properties](../gradle.properties).

## 3. Local Configuration Templates

Copy and fill the following templates at repository root:

- [local.properties.example](../local.properties.example) -> `local.properties`
- [startup-gate.local.properties.example](../startup-gate.local.properties.example) -> `startup-gate.local.properties`
- [signing.properties.example](../signing.properties.example) -> `signing.properties` (only when signing is required)

## 4. Build And Test

Run from repository root:

```bash
./gradlew build
./gradlew test
./gradlew lint
./gradlew spotlessApply
```

On Windows PowerShell, use `./gradlew.bat`.

## 5. APK Build Outputs

Common APK build commands (run from repository root):

```bash
./gradlew assembleDebug
./gradlew assembleRelease
```

If you only need the app module:

```bash
./gradlew :app:assembleDebug
./gradlew :app:assembleRelease
```

Output paths:

- Debug APK: `app/build/outputs/apk/debug/`
- Raw Debug/Release outputs: `app/build/outputs/apk/`
- Release APK: `app/build/outputs/apk/release/`

On Windows PowerShell, replace the commands with `./gradlew.bat ...`.

## 6. Release Signing Setup

The project supports these signing sources (highest priority first):

- Gradle properties (for example `-Pkeystore.path=...`)
- Environment variables
- `signing.properties`

### 6.1 Option 1: Use signing.properties (simplest for local dev)

1. Copy [signing.properties.example](../signing.properties.example) to `signing.properties`
2. Fill your signing values:

```properties
keystore.path=/absolute/or/relative/path/to/release.jks
keystore.password=YOUR_KEYSTORE_PASSWORD
key.alias=YOUR_KEY_ALIAS
key.password=YOUR_KEY_PASSWORD
```

Notes:

- `keystore.path` can be absolute, or relative to repository root.
- `signing.properties` is local-only and should not be committed.

### 6.2 Option 2: Use environment variables (recommended for CI)

```text
YUMEBOX_KEYSTORE_PATH
YUMEBOX_KEYSTORE_PASSWORD
YUMEBOX_KEY_ALIAS
YUMEBOX_KEY_PASSWORD
```

### 6.3 Validate Signing

```bash
./gradlew :app:assembleRelease
```

If signing is incomplete, the release build fails with a signing configuration error.

### 6.4 One-Command Bootstrap Script (Windows/macOS/Linux)

The repository provides cross-platform bootstrap scripts that can create a keystore, write `signing.properties` and `startup-gate.local.properties`, and validate a release build:

```powershell
./scripts/setup-release-signing.ps1
```

```bash
./scripts/setup-release-signing.sh
```

Common options:

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

Note: release startup enforces StartupGate checks. If the local signing fingerprint is missing in `startup-gate.local.properties`, the app can exit immediately on launch. The bootstrap scripts generate this fingerprint config automatically.

## 7. Kernel Sync Helper

Pick one based on your shell:

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

Channels: `alpha`, `meta`, `smart`.

## 8. Native Build

```bash
kotlin scripts/native-build.main.kts --all
```

## 9. Development Helper Scripts

Repository health checks:

- Linux/macOS:

```bash
./scripts/repo-health.sh
```

- Windows PowerShell:

```powershell
./scripts/repo-health.ps1
```

Runtime fuzz helper:

- Linux/macOS:

```bash
./scripts/llm-runtime-fuzz.sh --rounds 5 --events-per-round 200
```

- Windows PowerShell:

```powershell
./scripts/llm-runtime-fuzz.ps1 -Rounds 5 -EventsPerRound 200
```

Release signing bootstrap script (Windows/macOS/Linux):

```powershell
./scripts/setup-release-signing.ps1
```

```bash
./scripts/setup-release-signing.sh
```

## 10. Script Executable Bit (macOS/Linux)

If cloned on Unix-like systems, ensure shell scripts are executable:

```bash
chmod +x gradlew scripts/*.sh
```

## 11. Related Docs

- [Contributing](./CONTRIBUTING.md)
- [Chinese Project Overview](./README_ZH_HANS.md)
- [Third-party dependencies](./ThirdParty.md)
