/*
 * This file is part of YumeBox.
 *
 * YumeBox is free software: you can redistribute it and/or modify
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
 * Copyright (c)  YumeLira 2025 - Present
 *
 */

import java.util.Properties

plugins {
    id("com.android.library")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
}

val startupGateLocalProperties = Properties().apply {
    val file = rootProject.file("startup-gate.local.properties")
    if (file.exists()) {
        file.inputStream().use { input -> load(input) }
    }
}

val androidLocalProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use { input -> load(input) }
    }
}

fun resolveStartupGateProperty(key: String): String? {
    return providers.gradleProperty(key).orNull
        ?.trim()
        ?.takeIf(String::isNotBlank)
        ?: startupGateLocalProperties.getProperty(key)
            ?.trim()
            ?.takeIf(String::isNotBlank)
        ?: androidLocalProperties.getProperty(key)
            ?.trim()
            ?.takeIf(String::isNotBlank)
}

fun resolveStartupGateBoolean(key: String, defaultValue: Boolean = false): Boolean {
    return resolveStartupGateProperty(key)
        ?.toBooleanStrictOrNull()
        ?: defaultValue
}

android {
    namespace = "com.github.yumelira.yumebox.core.android"
    compileSdk = gropify.android.compileSdk
    val expectedPackage = resolveStartupGateProperty("startup.gate.expectedPackage")
        ?: gropify.project.namespace.base
    val expectedAppClass = resolveStartupGateProperty("startup.gate.expectedAppClass")
        ?: "com.github.yumelira.yumebox.App"
    val expectedAppParent = resolveStartupGateProperty("startup.gate.expectedAppParent")
        ?: "android.app.Application"
    val releaseFingerprint = resolveStartupGateProperty("startup.gate.releaseFingerprint").orEmpty()
    val enforceSigner = resolveStartupGateBoolean("startup.gate.enforceSigner", defaultValue = false)
    val enforceApkV2 = resolveStartupGateBoolean("startup.gate.enforceApkV2", defaultValue = false)

    val ndkVersionValue = gropify.android.ndkVersion
    if (ndkVersionValue.isNotBlank()) {
        ndkVersion = ndkVersionValue
    }

    defaultConfig {
        minSdk = gropify.android.minSdk
        buildConfigField("String", "STARTUP_GATE_EXPECTED_PACKAGE", "\"$expectedPackage\"")
        buildConfigField("String", "STARTUP_GATE_EXPECTED_APP_CLASS", "\"$expectedAppClass\"")
        buildConfigField("String", "STARTUP_GATE_EXPECTED_APP_PARENT", "\"$expectedAppParent\"")
        buildConfigField("String", "STARTUP_GATE_RELEASE_FINGERPRINT", "\"$releaseFingerprint\"")
        buildConfigField("boolean", "STARTUP_GATE_ENFORCE_SIGNER", enforceSigner.toString())
        buildConfigField("boolean", "STARTUP_GATE_ENFORCE_APK_V2", enforceApkV2.toString())
    }

    compileOptions {
        val javaVer = gropify.android.jvm ?: gropify.project.jvm ?: "17"
        sourceCompatibility = JavaVersion.toVersion(javaVer)
        targetCompatibility = JavaVersion.toVersion(javaVer)
    }

    packaging {
        resources {
            excludes += setOf(
                "/META-INF/{AL2.0,LGPL2.1}",
                "/META-INF/*.kotlin_module",
                "DebugProbesKt.bin",
            )
        }
        jniLibs {
            useLegacyPackaging = true
        }
    }

    sourceSets {
        getByName("main") {
            kotlin.directories.apply {
                clear()
                add("src")
            }
            res.srcDirs("res")
            assets.srcDirs("assets")
            aidl.srcDirs("aidl")
            resources.srcDirs("resources")
            if (project.file("AndroidManifest.xml").isFile) {
                manifest.srcFile("AndroidManifest.xml")
            }
        }
        getByName("test") {
            kotlin.directories.clear()
            resources.setSrcDirs(emptyList<String>())
            assets.setSrcDirs(emptyList<String>())
        }
        getByName("androidTest") {
            kotlin.directories.clear()
            res.setSrcDirs(emptyList<String>())
            assets.setSrcDirs(emptyList<String>())
            aidl.setSrcDirs(emptyList<String>())
            resources.setSrcDirs(emptyList<String>())
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    buildTypes {
        getByName("debug") {
            buildConfigField("boolean", "STARTUP_GATE_STRICT", "false")
        }
        getByName("release") {
            buildConfigField("boolean", "STARTUP_GATE_STRICT", "true")
        }
    }
}

dependencies {
    implementation(project(":locale"))

    val composeBom = platform("androidx.compose:compose-bom:${gropify.dep.version.composeBom}")
    implementation(composeBom)
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.core:core-ktx:${gropify.dep.version.coreKtx}")
    implementation("com.android.tools.build:apksig:${gropify.dep.version.apksig}")
    implementation("com.jakewharton.timber:timber:${gropify.dep.version.timber}")
}



