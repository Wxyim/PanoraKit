@file:Suppress("UnstableApiUsage")

plugins { id("com.android.test") }

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
