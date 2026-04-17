/*
 * This file is part of MonadBox - A customized edition of YumeBox.
 *
 * MonadBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) YumeLira 2025 - 2026
 * Copyright (c) MonadBox Contributors 2026 - Present
 *
 */

import java.util.Properties

plugins {
    id("com.android.library")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
}

val startupGateLocalProperties =
    Properties().apply {
        val file = rootProject.file("startup-gate.local.properties")
        if (file.exists()) {
            file.inputStream().use { input -> load(input) }
        }
    }

val androidLocalProperties =
    Properties().apply {
        val file = rootProject.file("local.properties")
        if (file.exists()) {
            file.inputStream().use { input -> load(input) }
        }
    }

fun resolveStartupGateProperty(key: String): String? {
    return providers.gradleProperty(key).orNull?.trim()?.takeIf(String::isNotBlank)
        ?: startupGateLocalProperties.getProperty(key)?.trim()?.takeIf(String::isNotBlank)
        ?: androidLocalProperties.getProperty(key)?.trim()?.takeIf(String::isNotBlank)
}

fun resolveStartupGateBoolean(key: String, defaultValue: Boolean = false): Boolean {
    return resolveStartupGateProperty(key)?.toBooleanStrictOrNull() ?: defaultValue
}

android {
    namespace = "com.github.nomadboxlab.monadbox.core.android"
    val expectedPackage =
        resolveStartupGateProperty("startup.gate.expectedPackage")
            ?: providers.gradleProperty("project.namespace.base").get()
    val expectedAppClass =
        resolveStartupGateProperty("startup.gate.expectedAppClass")
            ?: "com.github.nomadboxlab.monadbox.App"
    val expectedAppParent =
        resolveStartupGateProperty("startup.gate.expectedAppParent") ?: "android.app.Application"
    val releaseFingerprint = resolveStartupGateProperty("startup.gate.releaseFingerprint").orEmpty()
    val enforceSigner =
        resolveStartupGateBoolean("startup.gate.enforceSigner", defaultValue = false)
    val enforceApkV2 = resolveStartupGateBoolean("startup.gate.enforceApkV2", defaultValue = false)
    defaultConfig {
        buildConfigField("String", "STARTUP_GATE_EXPECTED_PACKAGE", "\"$expectedPackage\"")
        buildConfigField("String", "STARTUP_GATE_EXPECTED_APP_CLASS", "\"$expectedAppClass\"")
        buildConfigField("String", "STARTUP_GATE_EXPECTED_APP_PARENT", "\"$expectedAppParent\"")
        buildConfigField("String", "STARTUP_GATE_RELEASE_FINGERPRINT", "\"$releaseFingerprint\"")
        buildConfigField("boolean", "STARTUP_GATE_ENFORCE_SIGNER", enforceSigner.toString())
        buildConfigField("boolean", "STARTUP_GATE_ENFORCE_APK_V2", enforceApkV2.toString())
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    buildTypes {
        getByName("debug") { buildConfigField("boolean", "STARTUP_GATE_STRICT", "false") }
        getByName("release") { buildConfigField("boolean", "STARTUP_GATE_STRICT", "true") }
    }
}

dependencies {
    implementation(project(":locale"))
    implementation(project(":core"))

    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    implementation(libs.compose.runtime)
    implementation(libs.compose.ui)
    implementation(libs.core.ktx)
    implementation(libs.apksig)
    implementation(libs.timber)

    val injectedAbi = findProperty("android.injected.build.abi") as? String
    val mmkv = if (injectedAbi in listOf("arm64-v8a", "x86_64")) libs.mmkv.v64 else libs.mmkv.v32
    implementation(mmkv)
}
