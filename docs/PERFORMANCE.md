# Performance Guide

[English](PERFORMANCE.md) | [简体中文](PERFORMANCE_ZH_HANS.md)

This document describes the current release-performance workflow for MonadBox.

## Scope

The repository currently provides:

- a dedicated instrumentation performance module: `:performance:baselineprofile`
- startup baseline-profile generation journeys
- startup macrobenchmark comparisons
- `androidx.profileinstaller` integration in `:app` for runtime verification and release use

## Module Layout

- Gradle module: [`modules/performance/baselineprofile`](../modules/performance/baselineprofile)
- Build file: [`modules/performance/baselineprofile/build.gradle.kts`](../modules/performance/baselineprofile/build.gradle.kts)
- Baseline profile generator: [`BaselineProfileGenerator.kt`](../modules/performance/baselineprofile/src/main/kotlin/com/github/yumelira/yumebox/performance/BaselineProfileGenerator.kt)
- Startup benchmark: [`StartupBenchmarks.kt`](../modules/performance/baselineprofile/src/main/kotlin/com/github/yumelira/yumebox/performance/StartupBenchmarks.kt)
- Shared journeys: [`BenchmarkJourneys.kt`](../modules/performance/baselineprofile/src/main/kotlin/com/github/yumelira/yumebox/performance/BenchmarkJourneys.kt)

## Commands

Baseline-profile collection on a connected device:

```bash
./gradlew :performance:baselineprofile:connectedCheck \
  -Pandroid.testInstrumentationRunnerArguments.androidx.benchmark.enabledRules=BaselineProfile
```

Macrobenchmark execution on a connected device:

```bash
./gradlew :performance:baselineprofile:connectedCheck \
  -Pandroid.testInstrumentationRunnerArguments.androidx.benchmark.enabledRules=Macrobenchmark
```

Compile the performance module:

```bash
./gradlew :performance:baselineprofile:assemble --no-configuration-cache
```

## Current Limitation

The repository does not currently use the `androidx.baselineprofile` Gradle plugin end-to-end for the app module.

Reason:

- the current `:app` module uses a custom Android source-set layout
- that layout is not compatible with the baseline-profile plugin stack used with the current AGP 9 setup

So the project currently uses a test-module-based workflow instead of plugin-driven automatic baseline-profile export.

## Recommended Devices

- Android 13+ physical device
- Or a manually started emulator / connected test device

## Next Step

If you want fully automated release-time baseline-profile export, the app module layout or plugin integration strategy will need another compatibility pass.
