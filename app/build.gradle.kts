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

@file:Suppress("UnstableApiUsage")

import java.util.*
import org.gradle.api.tasks.Sync

plugins {
    id("com.android.application")
    kotlin("plugin.serialization")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
    id("com.google.devtools.ksp")
    id("com.mikepenz.aboutlibraries.plugin.android")
}


val appAbiList = (gropify.abi.app.list ?: "armeabi-v7a,arm64-v8a,x86,x86_64")
    .split(',').map { it.trim() }.filter { it.isNotEmpty() }

val kernelProperties = Properties().apply {
    val kernelPropertiesFile = rootProject.file("config/kernel.properties")
    if (kernelPropertiesFile.exists()) {
        kernelPropertiesFile.inputStream().use(::load)
    }
}
val mihomoVersion = kernelProperties.getProperty("external.mihomo.branch")
    ?.trim()
    ?.takeIf { it.isNotEmpty() }
    ?: "unknown"

val geoFilesAssetsDir = rootProject.layout.buildDirectory.dir("generated/assets/geo")
val unifiedJniLibsDir = rootProject.layout.buildDirectory.dir("jniLibs")
val legacyJniLibsDir = rootProject.layout.projectDirectory.dir("jniLibs")

android {
    namespace = gropify.project.namespace.base
    compileSdk = gropify.android.compileSdk

    defaultConfig {
        minSdk = gropify.android.minSdk
        applicationId = gropify.project.namespace.base
        targetSdk = gropify.android.targetSdk
        versionCode = gropify.project.version.code
        versionName = gropify.project.version.name
        manifestPlaceholders["appName"] = gropify.project.name
        buildConfigField("String", "MIHOMO_VERSION", "\"$mihomoVersion\"")

    }

    compileOptions {
        val javaVer = gropify.android.jvm ?: gropify.project.jvm ?: "17"
        sourceCompatibility = JavaVersion.toVersion(javaVer)
        targetCompatibility = JavaVersion.toVersion(javaVer)
        isCoreLibraryDesugaringEnabled = true
    }

    sourceSets {
        getByName("main") {
            kotlin.directories.apply {
                clear()
                add("src")
            }
            res.directories.apply {
                clear()
                add("res")
            }
            assets.directories.apply {
                clear()
                addAll(
                    listOf(
                        "assets",
                        geoFilesAssetsDir.get().asFile.invariantSeparatorsPath,
                    )
                )
            }
            aidl.directories.apply {
                clear()
                add("aidl")
            }
            resources.directories.apply {
                clear()
                add("resources")
            }
            jniLibs.directories.apply {
                clear()
                add(unifiedJniLibsDir.get().asFile.invariantSeparatorsPath)
            }
            if (project.file("AndroidManifest.xml").isFile) {
                manifest.srcFile("AndroidManifest.xml")
            }
        }
    }

    androidResources {
        generateLocaleConfig = false
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            vcsInfo.include = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    splits {
        abi {
            isEnable = gradle.startParameter.taskNames.none { it.contains("bundle", ignoreCase = true) }
            reset()
            include(*appAbiList.toTypedArray())
            isUniversalApk = true
        }
    }

    packaging {
        jniLibs {
            excludes += listOf("lib/**/libjavet*.so", "lib/**/libyume.so")
            useLegacyPackaging = true
        }
    }

    signingConfigs {
        val signingPropertiesFile = rootProject.file("signing.properties")
        val signingProperties = Properties().apply {
            if (signingPropertiesFile.exists()) {
                signingPropertiesFile.inputStream().use(::load)
            }
        }

        fun readSigningValue(propertyKey: String, envKey: String): String? {
            return (project.findProperty(propertyKey) as? String)
                ?.trim()
                ?.takeIf(String::isNotBlank)
                ?: System.getenv(envKey)
                    ?.trim()
                    ?.takeIf(String::isNotBlank)
                ?: signingProperties.getProperty(propertyKey)
                    ?.trim()
                    ?.takeIf(String::isNotBlank)
        }

        val configuredKeystorePath = readSigningValue("keystore.path", "YUMEBOX_KEYSTORE_PATH")
        val configuredStoreFile = configuredKeystorePath
            ?.let { rawPath ->
                val asFile = file(rawPath)
                if (asFile.isAbsolute) asFile else rootProject.file(rawPath)
            }
            ?: rootProject.file("release.keystore").takeIf { it.exists() }

        val storePassword = readSigningValue("keystore.password", "YUMEBOX_KEYSTORE_PASSWORD")
        val keyAlias = readSigningValue("key.alias", "YUMEBOX_KEY_ALIAS")
        val keyPassword = readSigningValue("key.password", "YUMEBOX_KEY_PASSWORD")

        if (configuredStoreFile?.exists() == true && storePassword != null && keyAlias != null && keyPassword != null) {
            create("release") {
                storeFile = configuredStoreFile
                this.storePassword = storePassword
                this.keyAlias = keyAlias
                this.keyPassword = keyPassword
            }
        }
    }

    if (signingConfigs.findByName("release") != null) {
        buildTypes.named("release").configure {
            signingConfig = signingConfigs.getByName("release")
        }
    }

    val releaseArtifactRequested = gradle.startParameter.taskNames.any { taskName ->
        val normalized = taskName.lowercase()
        normalized.contains("release") &&
            (normalized.contains("assemble") || normalized.contains("bundle") || normalized.contains("package"))
    }

    if (releaseArtifactRequested && signingConfigs.findByName("release") == null) {
        throw GradleException(
            "Release signing is not configured. Provide signing.properties or env vars " +
                "(YUMEBOX_KEYSTORE_PATH, YUMEBOX_KEYSTORE_PASSWORD, YUMEBOX_KEY_ALIAS, YUMEBOX_KEY_PASSWORD).",
        )
    }

    androidComponents {
        onVariants { variant ->
            variant.outputs.forEach { output ->
                val abiName = output.filters.find {
                    it.filterType == com.android.build.api.variant.FilterConfiguration.FilterType.ABI
                }?.identifier ?: "universal"
                val buildTypeName = variant.buildType ?: "release"
                output.versionName.set(gropify.project.version.name)
                (output as com.android.build.api.variant.impl.VariantOutputImpl).outputFileName.set(
                    "${gropify.project.name}-${abiName}-${buildTypeName}.apk"
                )
            }
        }
    }
}

val syncLegacyJniLibs = tasks.register<Sync>("syncLegacyJniLibs") {
    from(legacyJniLibsDir)
    into(unifiedJniLibsDir)
}

tasks.named("preBuild") {
    dependsOn(syncLegacyJniLibs)
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:${gropify.dep.version.desugarJdkLibs}")

    implementation(project(":core"))
    implementation(project(":platform"))
    implementation(project(":locale"))
    implementation(project(":ui"))
    implementation(project(":data:log"))
    implementation(project(":data:settings"))
    implementation(project(":data:proxy"))
    implementation(project(":runtime:api"))
    implementation(project(":runtime:client"))
    implementation(project(":runtime:service"))
    implementation(project(":feature:proxy"))
    implementation(project(":feature:override"))
    implementation(project(":feature:editor"))
    implementation(project(":feature:meta"))

    val composeBom = platform("androidx.compose:compose-bom:${gropify.dep.version.composeBom}")
    implementation(composeBom)
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.activity:activity-compose:${gropify.dep.version.activityCompose}")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("top.yukonga.miuix.kmp:miuix:${gropify.dep.version.miuix}")
    implementation("top.yukonga.miuix.kmp:miuix-icons:${gropify.dep.version.miuix}")
    implementation("dev.chrisbanes.haze:haze:${gropify.dep.version.haze}")
    implementation("io.github.fletchmckee.liquid:liquid:${gropify.dep.version.liquid}")
    implementation("androidx.navigationevent:navigationevent-compose:${gropify.dep.version.navigationevent}")

    val mmkv64 = gropify.dep.version.mmkv64
    val mmkv32 = gropify.dep.version.mmkv32
    val injectedAbi = findProperty("android.injected.build.abi") as? String
    val mmkvVersion = if (injectedAbi in listOf("arm64-v8a", "x86_64")) mmkv64 else mmkv32
    implementation("com.tencent:mmkv:$mmkvVersion")

    implementation("io.insert-koin:koin-core:${gropify.dep.version.koin}")
    implementation("io.insert-koin:koin-android:${gropify.dep.version.koin}")
    implementation("io.insert-koin:koin-androidx-compose:${gropify.dep.version.koin}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${gropify.dep.version.coroutines}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${gropify.dep.version.serializationJson}")

    implementation("io.github.raamcosta.compose-destinations:core:${gropify.dep.version.composeDestinations}")
    ksp("io.github.raamcosta.compose-destinations:ksp:${gropify.dep.version.composeDestinations}")

    implementation("com.jakewharton.timber:timber:${gropify.dep.version.timber}")
    implementation("org.tukaani:xz:1.12")

    implementation("com.google.mlkit:barcode-scanning:${gropify.dep.version.mlkitBarcodeScanning}")

    implementation("androidx.camera:camera-camera2:${gropify.dep.version.camera}")
    implementation("androidx.camera:camera-lifecycle:${gropify.dep.version.camera}")
    implementation("androidx.camera:camera-view:${gropify.dep.version.camera}")
    implementation("androidx.camera:camera-core:${gropify.dep.version.camera}")
    implementation("androidx.camera:camera-video:${gropify.dep.version.camera}")

    implementation("io.coil-kt.coil3:coil-compose:${gropify.dep.version.coil3}")
    implementation("io.coil-kt.coil3:coil-network-okhttp:${gropify.dep.version.coil3}")
    implementation("io.coil-kt.coil3:coil-svg:${gropify.dep.version.coil3}")
    implementation("io.github.panpf.sketch4:sketch-compose:${gropify.dep.version.sketch4}")
    implementation("io.github.panpf.sketch4:sketch-http:${gropify.dep.version.sketch4}")
    implementation("io.github.panpf.sketch4:sketch-animated-webp:${gropify.dep.version.sketch4}")

    implementation("sh.calvin.reorderable:reorderable:${gropify.dep.version.reorderable}")
    implementation("com.mikepenz:aboutlibraries-core:${gropify.dep.version.aboutLibraries}")
    implementation("com.mikepenz:aboutlibraries-compose:${gropify.dep.version.aboutLibraries}")
    implementation("dev.oom-wg.purejoy.fyl.fytxt:common-android:2.2")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:${gropify.dep.version.lifecycle}")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:${gropify.dep.version.lifecycle}")

}

ksp {
    arg("compose-destinations.defaultTransitions", "none")
}
