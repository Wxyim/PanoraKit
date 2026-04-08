# Contributing to YumeBox/NomadBox

[English](CONTRIBUTING.md) | [简体中文](CONTRIBUTING_ZH_HANS.md)

This document defines contribution constraints. Environment bootstrap, native build pipeline, and release packaging procedures are documented in [DEVELOP.md](DEVELOP.md).

## 1. Normative Scope

The repository enforces three classes of constraints:

- static style constraints
- architectural constraints
- regression-prevention constraints

All constraints are machine-checked through Gradle tasks and are treated as merge requirements.

## 2. Code Style

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

## 3. UI Contract Validation

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

## 4. Compose Lifecycle Collection

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

## 5. Application Startup Constraints

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

## 6. Modernization Baseline

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

## 7. Submission Checklist

1. `.\gradlew.bat spotlessApply`
2. `.\gradlew.bat check`
3. `.\gradlew.bat test`
4. Update `config/ui-capability-registry.txt` for Destination additions or renames.

## 8. License

NomadBox is licensed under [AGPLv3](https://www.gnu.org/licenses/agpl-3.0.html). Preserve existing license headers.
