# Development Guide

[English](DEVELOP.md) | [简体中文](DEVELOP_ZH_HANS.md)

Documentation hub: [README.md](../docs/README.md)

This document defines environment and build procedures. Style and architectural constraints are specified in [CONTRIBUTING.md](CONTRIBUTING.md).

## 1. Platform and Toolchain

Supported local platforms: Windows, macOS, Linux.

CI platform: Ubuntu (GitHub Actions).

Required toolchain:

- JDK 21
- Android SDK (compile/target 36)
- Android NDK 27.3.13750724
- Kotlin command runner in PATH (for `scripts/native-build.main.kts`)

Authoritative version file: [gradle.properties](../gradle.properties)

## 2. One-Time Local Setup

Copy templates at repository root:

| Template | Local file |
| --- | --- |
| `local.properties.example` | `local.properties` |
| `startup-gate.local.properties.example` | `startup-gate.local.properties` |
| `signing.properties.example` | `signing.properties` (release only) |

Do not commit local credentials or machine-specific paths.

## 3. Native + Gradle Build Sequence

Native outputs are preconditions for APK packaging.

Linux/macOS:

```bash
./scripts/sync-kernel.sh alpha
kotlin scripts/native-build.main.kts --all
./gradlew build
```

Windows PowerShell:

```powershell
./scripts/sync-kernel.ps1 alpha
kotlin scripts/native-build.main.kts --all
.\gradlew.bat build
```

Required native outputs:

- `build/native/cpp/obj/*/libbridge.so`
- `build/native/go/*/libclash.so`

## 4. Verification Tasks

Run from repository root:

```bash
./gradlew spotlessApply
./gradlew checkUiContracts
./gradlew checkModernizationBaseline
./gradlew test
./gradlew lint
```

Windows equivalent:

```powershell
.\gradlew.bat spotlessApply
.\gradlew.bat checkUiContracts
.\gradlew.bat checkModernizationBaseline
.\gradlew.bat test
.\gradlew.bat lint
```

Root `check` includes both `checkUiContracts` and `checkModernizationBaseline`.

## 5. Module Topology

Dependency direction:

`app -> modules/feature/* -> shared/data/runtime modules`

| Module group | Responsibility |
| --- | --- |
| `app/` | entry point, startup wiring, navigation host, Android packaging |
| `modules/feature/*` | user-facing features |
| `modules/data/*` | persistence and repository layer |
| `modules/runtime/*` | runtime API/client/service boundary |
| `modules/ui/`, `modules/locale/`, `modules/platform/`, `modules/core/` | shared foundations |

Prefer module-local changes.

## 6. APK Build Outputs

| Command | Output directory |
| --- | --- |
| `./gradlew assembleDebug` | `build/app/outputs/apk/debug/` |
| `./gradlew assembleRelease` | `build/app/outputs/apk/release/` |

## 7. Android Emulator Workflow

The Android SDK `emulator` and `adb` tools are required in PATH.

- (1) List available AVD definitions:

```sh
emulator -list-avds
```

- (2) Start a target emulator:

```sh
emulator -avd <AVD_NAME>
```

- (3) Wait for boot completion (optional but recommended in scripts):

```sh
adb devices -l
adb wait-for-device
adb shell getprop sys.boot_completed
```

- (4) Build and install Debug APK to the running emulator:

```sh
.\gradlew.bat :app:installDebug
```

- (5) Optional launch command (replace placeholders):

```sh
adb shell am start -n <applicationId>/<launcherActivity>
```

- (6) Validate runtime behavior with targeted logs:

```sh
adb logcat -v time | rg -i "clash|runtime|traffic|override"
```

- (7) Reinstall release artifact when validating packaging-specific behavior:

```sh
adb install -r build/app/outputs/apk/release/MonadBox-universal-release.apk
```

- (8) Optional cleanup for deterministic regression runs:

```sh
adb uninstall <applicationId>
```

## 8. Release Signing

Signing source priority:

1. Gradle properties
2. environment variables
3. `signing.properties`

Local signing setup:

1. Copy [signing.properties.example](../signing.properties.example) to `signing.properties`.
2. Fill values:

```properties
keystore.path=/absolute/or/relative/path/to/release.jks
keystore.password=
key.alias=
key.password=
```

CI variables:

- `YUMEBOX_KEYSTORE_PATH`
- `YUMEBOX_KEYSTORE_PASSWORD`
- `YUMEBOX_KEY_ALIAS`
- `YUMEBOX_KEY_PASSWORD`

Validation command:

```sh
.\gradlew.bat :app:assembleRelease
```

## 9. Utility Scripts

| Script | Function |
| --- | --- |
| `scripts/sync-kernel.{sh,ps1,bat}` | kernel source/channel sync |
| `kotlin scripts/native-build.main.kts --all` | native artifact build |
| `scripts/repo-health.{sh,ps1}` | repository health checks |
| `scripts/setup-release-signing.{sh,ps1}` | release signing bootstrap |
| `scripts/llm-runtime-fuzz.{sh,ps1}` | runtime fuzz helper |

## 10. Troubleshooting

| Symptom | Action |
| --- | --- |
| Release build fails before packaging | verify native outputs, then rerun native build script |
| Gradle daemon instability | inspect JVM settings in `gradle.properties`, retry with `--no-configuration-cache` for diagnosis |
| UI contract failure | update `config/ui-capability-registry.txt` and align with code |

## 11. Related Docs

- Contribution rules: [CONTRIBUTING.md](../docs/CONTRIBUTING.md)
- Performance workflow: [PERFORMANCE.md](../docs/PERFORMANCE.md)
- Chinese documentation hub: [README_ZH_HANS.md](../docs/README_ZH_HANS.md)
