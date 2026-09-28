@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.test")
    // On a com.android.test module the baseline profile plugin takes its "producer" role: it
    // registers the profile collection tasks and the consumable variant that the app resolves
    // through `baselineProfile(project(":performance:baselineprofile"))`. Without it the app's
    // per-build-type configuration has no matching variant and the task graph fails to resolve.
    id("androidx.baselineprofile")
}

android {
    namespace = "${providers.gradleProperty("project.namespace.base").get()}.baselineprofile"
    compileSdk = providers.gradleProperty("android.compileSdk").get().toInt()
    targetProjectPath = ":app"
    experimentalProperties["android.experimental.self-instrumenting"] = true

    defaultConfig {
        minSdk = 28
        targetSdk = providers.gradleProperty("android.targetSdk").get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["androidx.benchmark.suppressErrors"] =
            "EMULATOR,LOW-BATTERY"
    }

    buildTypes { create("benchmark") { isDebuggable = true } }

    testOptions { animationsDisabled = true }
}

dependencies {
    implementation(libs.benchmark.macro.junit4)
    implementation(libs.test.ext.junit)
    implementation(libs.test.runner)
    implementation(libs.test.uiautomator)
}
