# 性能文档

[简体中文](PERFORMANCE_ZH_HANS.md) | [English](PERFORMANCE.md)

本文档说明 MonadBox 当前的发布性能工程工作流。

## 范围

当前仓库已经具备：

- 独立的性能 instrumentation 模块：`:performance:baselineprofile`
- 启动基线 profile 采集路径
- 启动 macrobenchmark 对比能力
- `:app` 中的 `androidx.profileinstaller` 接入，用于运行时验证与发布使用

## 模块位置

- Gradle 模块：[`modules/performance/baselineprofile`](../modules/performance/baselineprofile)
- 构建文件：[`modules/performance/baselineprofile/build.gradle.kts`](../modules/performance/baselineprofile/build.gradle.kts)
- Baseline profile 生成器：[`BaselineProfileGenerator.kt`](../modules/performance/baselineprofile/src/main/kotlin/com/github/yumelira/yumebox/performance/BaselineProfileGenerator.kt)
- 启动 benchmark：[`StartupBenchmarks.kt`](../modules/performance/baselineprofile/src/main/kotlin/com/github/yumelira/yumebox/performance/StartupBenchmarks.kt)
- 共享交互路径：[`BenchmarkJourneys.kt`](../modules/performance/baselineprofile/src/main/kotlin/com/github/yumelira/yumebox/performance/BenchmarkJourneys.kt)

## 常用命令

在连接设备上采集 baseline profile：

```bash
./gradlew :performance:baselineprofile:connectedCheck \
  -Pandroid.testInstrumentationRunnerArguments.androidx.benchmark.enabledRules=BaselineProfile
```

在连接设备上运行 macrobenchmark：

```bash
./gradlew :performance:baselineprofile:connectedCheck \
  -Pandroid.testInstrumentationRunnerArguments.androidx.benchmark.enabledRules=Macrobenchmark
```

编译性能模块：

```bash
./gradlew :performance:baselineprofile:assemble --no-configuration-cache
```

## 当前限制

当前仓库还没有把 `androidx.baselineprofile` Gradle 插件完整接到 `:app` 模块上。

原因：

- `:app` 目前使用了自定义 Android source set 布局
- 在当前 AGP 9 组合下，这种布局与 baseline-profile 插件链路不兼容

因此项目目前采用“测试模块驱动”的性能工作流，而不是“插件自动导出 baseline profile”的方式。

## 推荐设备

- Android 13 及以上真机
- 或手动启动的模拟器 / 已连接测试设备

## 后续方向

如果要做到发布时自动导出 baseline profile，需要再做一轮 `:app` 模块布局或插件接入兼容性改造。
