# Development Guide

[English](DEVELOP.md) | [简体中文](DEVELOP_ZH_HANS.md)

This document defines environment, build procedures, and contribution constraints.

## 1. Platform and Toolchain

Supported local platforms: Windows, macOS, Linux.

CI platform: Ubuntu (GitHub Actions).

Required toolchain:

- JDK 21
- Android SDK (compile/target 36)
- Android NDK 27.3.13750724
- Kotlin command runner in PATH (for `scripts/native-build.main.kts`)

Authoritative version file: [gradle.properties](../gradle.properties)

> **AGP 9 built-in Kotlin.** This project relies on AGP 9's bundled Kotlin
> support: Android library/application modules do **not** declare
> `kotlin("android")` (or `org.jetbrains.kotlin.android`) in their `plugins {}`
> block. The `com.android.library` / `com.android.application` plugins
> activate Kotlin compilation automatically. Only opt-in Kotlin compiler
> plugins (`kotlin("plugin.compose")`, `kotlin("plugin.serialization")`)
> remain explicit. When upgrading or downgrading AGP, audit every
> `modules/**/build.gradle.kts` to keep this assumption consistent.

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

- `MONADBOX_KEYSTORE_PATH`
- `MONADBOX_KEYSTORE_PASSWORD`
- `MONADBOX_KEY_ALIAS`
- `MONADBOX_KEY_PASSWORD`

Validation command:

```sh
.\gradlew.bat :app:assembleRelease
```

Notes:

- Signed release builds automatically derive `startup.gate.releaseFingerprint` from the configured release keystore.
- If `startup.gate.releaseFingerprint` is also set manually and does not match the signing certificate SHA-256, the build fails.
- Signed release builds must keep startup gate strict mode enabled; `startup.gate.strict=false` is only valid for unsigned local troubleshooting builds.

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

## 11. Performance Workflow

Current performance engineering path is test-module-driven and centered on
`:performance:baselineprofile`.

Module location:

- Gradle module: `modules/performance/baselineprofile`
- Baseline profile generator: `BaselineProfileGenerator.kt`
- Startup benchmark: `StartupBenchmarks.kt`
- Shared journeys: `BenchmarkJourneys.kt`

Common commands:

```bash
./gradlew :performance:baselineprofile:connectedCheck \
  -Pandroid.testInstrumentationRunnerArguments.androidx.benchmark.enabledRules=BaselineProfile

./gradlew :performance:baselineprofile:connectedCheck \
  -Pandroid.testInstrumentationRunnerArguments.androidx.benchmark.enabledRules=Macrobenchmark
```

Current limitation:

- `:app` uses a custom Android source-set layout.
- With the current AGP 9 setup, that layout is not compatible with the
  end-to-end `androidx.baselineprofile` plugin pipeline.

## 12. Normative Scope

The repository enforces three classes of constraints:

- static style constraints
- architectural constraints
- regression-prevention constraints

All constraints are machine-checked through Gradle tasks and are treated as merge requirements.

## 13. Code Style

Kotlin and C/C++ style is configured through project IDE profiles.

1. Open the project in Android Studio or IntelliJ IDEA.
2. Navigate to `File -> Settings -> Editor -> Code Style -> C/C++ and Kotlin`.
3. Select `Scheme: Project`.

Language rules:

| Language | Indent | Star imports |
| --- | --- | --- |
| Kotlin / KTS | 4 spaces | forbidden |
| Go | tabs | n/a |
| C / C++ | 2 spaces | n/a |
| Markdown | trailing whitespace preserved where needed | n/a |

Apply formatter before commit:

```sh
.\gradlew.bat spotlessApply
```

## 14. UI Contract Validation

Task: `checkUiContracts`

```sh
.\gradlew.bat checkUiContracts
```

Validation targets:

- Destination declarations under `app/src` and `modules/feature`
- registration completeness in `config/ui-capability-registry.txt`
- route reachability in navigation graphs
- `settingsSection` consistency with `app/src/screen/settings/SettingPager.kt`
- explicit registration of nested-graph Destinations in addition to `RootGraph`

Registry format (pipe-separated, 8 columns):

```text
# destination|screenFile|capabilityId|ownerModule|uiType|entryMode|settingsSection|implementationFiles
SomeScreen|path/to/SomeScreen.kt|some-id|feature/module|top-level|navigation||path/to/SomeScreen.kt
AnotherScreen|path/to/AnotherScreen.kt|another-id|app|top-level|navigation|ui-settings|path/to/AnotherScreen.kt
```

Column definitions: `destination` (route class name), `screenFile`, `capabilityId` (kebab-case), `ownerModule` (module subdirectory path), `uiType` (`system`, `top-level`, `detail`, `editor`), `entryMode` (`start`, `navigation`, `implicit`), `settingsSection` (`ui-settings`, `more`, or empty), `implementationFiles` (semicolon-separated paths).

Failure handling:

1. Inspect report files under `build/reports/ui-contracts/`.
2. Add missing registry entries for Destinations.
3. Align `settingsSection` with `SettingPager.kt`.

## 15. Compose Lifecycle Collection

Mandatory API for UI state in Compose:

```kotlin
val state by viewModel.uiState.collectAsStateWithLifecycle()
```

Mandatory helper for one-shot effects (navigation, toast, transient messages):

```kotlin
CollectFlowWithLifecycle(viewModel.navigationEvents) { event ->
    navController.navigate(event.route)
}
```

Reference implementation: `app/src/presentation/component/CollectFlowWithLifecycle.kt`

Disallowed patterns in Compose screens:

- `collectAsState()`
- `LaunchedEffect(Unit) { flow.collect { ... } }`

## 16. Application Startup Constraints

`app/src/App.kt` is limited to process-wide initialization. Deferred and runtime-sensitive startup logic belongs to `app/src/startup/AppStartupCoordinator.kt`.

```text
App.onCreate()
  -> process-wide init (DI, locale)
  -> ensureDeferredStartupInitialized()
       -> AppStartupCoordinator
```

Additional gate: `AppTrafficStatisticsCollector` initialization depends on `ProxyFacade.isRunning == true`.

Contribution rule:

- do not add deferred startup logic to `App.kt`
- register new startup steps via coordinator classes

## 17. Modernization Baseline

Task: `checkModernizationBaseline`

```sh
.\gradlew.bat checkModernizationBaseline
```

Invariants:

| Invariant | Scope |
| --- | --- |
| `collectAsState()` absent | app/feature Compose sources |
| raw `LaunchedEffect { flow.collect }` absent | app/feature Compose sources |
| `App.kt` remains thin | app module |
| `lifecycle-runtime-compose` retained | key feature modules |

This task is normative. Inline suppressions intended to bypass the rule set are not accepted.

## 18. Submission Checklist

1. `.\gradlew.bat spotlessApply`
2. `.\gradlew.bat check`
3. `.\gradlew.bat test`
4. Update `config/ui-capability-registry.txt` for Destination additions or renames.

## 19. License

Repository-owned and fork-derived MonadBox source files are licensed under [AGPL-3.0-only](https://www.gnu.org/licenses/agpl-3.0.html). Preserve existing license headers.

The root `LICENSE` file does not relicense third-party dependencies or synced upstream source. Review [LICENSING.md](../docs/LICENSING.md) before adding new dependencies or publishing binaries.

## 20. Related Docs

- Documentation hub: [README.md](../README.md)
- Licensing and third-party inventory: [LICENSING.md](../docs/LICENSING.md)
