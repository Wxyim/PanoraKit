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

val appAbiList =
    (providers.gradleProperty("abi.app.list").orNull ?: "armeabi-v7a,arm64-v8a,x86,x86_64")
        .split(',')
        .map { it.trim() }
        .filter { it.isNotEmpty() }

val kernelProperties =
    Properties().apply {
        val kernelPropertiesFile = rootProject.file("config/kernel.properties")
        if (kernelPropertiesFile.exists()) {
            kernelPropertiesFile.inputStream().use(::load)
        }
    }
val mihomoVersion =
    kernelProperties.getProperty("external.mihomo.branch")?.trim()?.takeIf { it.isNotEmpty() }
        ?: "unknown"

val geoFilesAssetsDir = rootProject.layout.buildDirectory.dir("generated/assets/geo")
val unifiedJniLibsDir = rootProject.layout.buildDirectory.dir("jniLibs")
val legacyJniLibsDir = rootProject.layout.projectDirectory.dir("jniLibs")
val nativeCppOutputDir = rootProject.layout.buildDirectory.dir("native/cpp/obj")
val nativeGoOutputDir = rootProject.layout.buildDirectory.dir("native/go")

android {
    namespace = providers.gradleProperty("project.namespace.base").get()
    compileSdk = providers.gradleProperty("android.compileSdk").get().toInt()

    defaultConfig {
        minSdk = providers.gradleProperty("android.minSdk").get().toInt()
        applicationId = providers.gradleProperty("project.namespace.base").get()
        targetSdk = providers.gradleProperty("android.targetSdk").get().toInt()
        versionCode = providers.gradleProperty("project.version.code").get().toInt()
        versionName = providers.gradleProperty("project.version.name").get()
        manifestPlaceholders["appName"] = providers.gradleProperty("project.name").get()
        buildConfigField("String", "MIHOMO_VERSION", "\"$mihomoVersion\"")
    }

    compileOptions {
        val javaVer =
            providers.gradleProperty("android.jvm").orNull
                ?: providers.gradleProperty("project.jvm").orNull
                ?: "17"
        sourceCompatibility = JavaVersion.toVersion(javaVer)
        targetCompatibility = JavaVersion.toVersion(javaVer)
        isCoreLibraryDesugaringEnabled = true
    }

    // Sources use AGP standard layout (src/main/kotlin, src/main/res, ...).
    // Only extra generated directories need to be registered explicitly.
    sourceSets {
        getByName("main") {
            assets.directories.add(geoFilesAssetsDir.get().asFile.invariantSeparatorsPath)
            jniLibs.directories.add(unifiedJniLibsDir.get().asFile.invariantSeparatorsPath)
        }
    }

    androidResources { generateLocaleConfig = false }

    lint {
        // Fail CI/release builds on new issues, but allow current known issues via baseline.
        // Create the baseline file by running `./gradlew :app:lintDebug`
        // (if baseline does not exist, it will be generated).
        abortOnError = true
        warningsAsErrors = false
        checkReleaseBuilds = true
        checkDependencies = true
        baseline = file("lint-baseline.xml")
        disable +=
            setOf(
                // Handled by our own checkGithubOssLicensePolicy task; lint false-positives here.
                "UnusedResources"
            )
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    testOptions { unitTests.isIncludeAndroidResources = true }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            vcsInfo.include = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    splits {
        abi {
            isEnable =
                gradle.startParameter.taskNames.none { it.contains("bundle", ignoreCase = true) }
            reset()
            include(*appAbiList.toTypedArray())
            isUniversalApk = appAbiList.size > 1
        }
    }

    packaging {
        jniLibs {
            excludes += listOf("lib/**/libjavet*.so", "lib/**/libyume.so")
            // Align with gradle.properties
            // (android.packagingOptions.jniLibs.useLegacyPackaging=false).
            // Since minSdk=26, non-legacy packaging (uncompressed + mmap) is strictly better
            // for cold start and avoids duplicating libs in device storage.
            useLegacyPackaging = false
        }
    }

    signingConfigs {
        val signingPropertiesFile = rootProject.file("signing.properties")
        val signingProperties =
            Properties().apply {
                if (signingPropertiesFile.exists()) {
                    signingPropertiesFile.inputStream().use(::load)
                }
            }

        fun readSigningValue(propertyKey: String, vararg envKeys: String): String? {
            return (project.findProperty(propertyKey) as? String)
                ?.trim()
                ?.takeIf(String::isNotBlank)
                ?: envKeys.firstNotNullOfOrNull { envKey ->
                    System.getenv(envKey)?.trim()?.takeIf(String::isNotBlank)
                }
                ?: signingProperties.getProperty(propertyKey)?.trim()?.takeIf(String::isNotBlank)
        }

        val configuredKeystorePath =
            readSigningValue("keystore.path", "MONADBOX_KEYSTORE_PATH", "YUMEBOX_KEYSTORE_PATH")
        val configuredStoreFile =
            configuredKeystorePath?.let { rawPath ->
                val asFile = file(rawPath)
                if (asFile.isAbsolute) asFile else rootProject.file(rawPath)
            } ?: rootProject.file("release.keystore").takeIf { it.exists() }

        val storePassword =
            readSigningValue(
                "keystore.password",
                "MONADBOX_KEYSTORE_PASSWORD",
                "YUMEBOX_KEYSTORE_PASSWORD",
            )
        val keyAlias = readSigningValue("key.alias", "MONADBOX_KEY_ALIAS", "YUMEBOX_KEY_ALIAS")
        val keyPassword =
            readSigningValue("key.password", "MONADBOX_KEY_PASSWORD", "YUMEBOX_KEY_PASSWORD")

        if (
            configuredStoreFile?.exists() == true &&
                storePassword != null &&
                keyAlias != null &&
                keyPassword != null
        ) {
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

    val releaseArtifactRequested =
        gradle.startParameter.taskNames.any { taskName ->
            val normalized = taskName.lowercase()
            normalized.contains("release") &&
                (normalized.contains("assemble") ||
                    normalized.contains("bundle") ||
                    normalized.contains("package"))
        }

    val requireReleaseSigning =
        (findProperty("release.signing.required") as? String)?.toBooleanStrictOrNull()
            ?: (System.getenv("CI")?.equals("true", ignoreCase = true) == true)

    if (
        releaseArtifactRequested &&
            signingConfigs.findByName("release") == null &&
            requireReleaseSigning
    ) {
        throw GradleException(
            "Release signing is not configured. Provide signing.properties or env vars " +
                "(MONADBOX_KEYSTORE_PATH, MONADBOX_KEYSTORE_PASSWORD, MONADBOX_KEY_ALIAS, MONADBOX_KEY_PASSWORD). " +
                "Legacy YUMEBOX_* env vars are still accepted."
        )
    }

    if (
        releaseArtifactRequested &&
            signingConfigs.findByName("release") == null &&
            !requireReleaseSigning
    ) {
        logger.warn(
            "Release signing is not configured, but check is relaxed for local builds. " +
                "Set -Prelease.signing.required=true to enforce it."
        )
    }

    androidComponents {
        onVariants { variant ->
            variant.outputs.forEach { output ->
                val abiName =
                    output.filters
                        .find {
                            it.filterType ==
                                com.android.build.api.variant.FilterConfiguration.FilterType.ABI
                        }
                        ?.identifier ?: "universal"
                val buildTypeName = variant.buildType ?: "release"
                output.versionName.set(providers.gradleProperty("project.version.name").get())
                (output as com.android.build.api.variant.impl.VariantOutputImpl)
                    .outputFileName
                    .set(
                        "${providers.gradleProperty("project.name").get()}-${abiName}-${buildTypeName}.apk"
                    )
            }
        }
    }
}

val syncLegacyJniLibs =
    tasks.register<Sync>("syncLegacyJniLibs") {
        from(legacyJniLibsDir)
        into(unifiedJniLibsDir)
    }

val syncBuiltNativeJniLibs =
    tasks.register<Sync>("syncBuiltNativeJniLibs") {
        val abiList = appAbiList
        val cppRoot = nativeCppOutputDir.get().asFile
        val goRoot = nativeGoOutputDir.get().asFile

        doFirst {
            val missing = mutableListOf<String>()
            abiList.forEach { abi ->
                val bridge = cppRoot.resolve("$abi/libbridge.so")
                val clash = goRoot.resolve("$abi/libclash.so")
                if (!bridge.isFile) {
                    missing += bridge.absolutePath
                }
                if (!clash.isFile) {
                    missing += clash.absolutePath
                }
            }

            if (missing.isNotEmpty()) {
                throw GradleException(
                    "Missing native runtime libraries for APK packaging:\n" +
                        missing.joinToString(separator = "\n") +
                        "\nBuild native artifacts first via scripts/native-build.main.kts."
                )
            }
        }

        from(cppRoot) { include("*/libbridge.so") }
        from(goRoot) { include("*/libclash.so") }
        into(unifiedJniLibsDir)
    }

tasks.named("preBuild") { dependsOn(syncLegacyJniLibs, syncBuiltNativeJniLibs) }

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)

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
    implementation(project(":feature:proxy:api"))
    implementation(project(":feature:proxy"))
    implementation(project(":feature:override"))
    implementation(project(":feature:editor"))
    implementation(project(":feature:meta"))
    implementation(project(":feature:about"))
    implementation(project(":feature:onboarding"))
    implementation(project(":feature:log"))
    implementation(project(":feature:connection"))
    implementation(project(":feature:traffic"))
    implementation(project(":feature:home"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:profiles"))

    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    implementation(libs.compose.runtime)
    implementation(libs.compose.animation)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3.window.size)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.activity.compose)
    debugImplementation(libs.compose.ui.tooling)

    implementation(libs.miuix)
    implementation(libs.miuix.icons)
    implementation(libs.haze)
    implementation(libs.liquid)
    implementation(libs.navigationevent.compose)

    val injectedAbi = findProperty("android.injected.build.abi") as? String
    val mmkv = if (injectedAbi in listOf("arm64-v8a", "x86_64")) libs.mmkv.v64 else libs.mmkv.v32
    implementation(mmkv)

    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.coroutines.android)
    implementation(libs.serialization.json)
    implementation(libs.snakeyaml)

    implementation(libs.compose.destinations.core)
    ksp(libs.compose.destinations.ksp)

    implementation(libs.timber)
    implementation(libs.xz)

    implementation(libs.zxing.core)

    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)
    implementation(libs.camera.core)
    implementation(libs.camera.video)

    implementation(libs.sketch4.compose)
    implementation(libs.sketch4.http)
    implementation(libs.sketch4.animated.webp)

    implementation(libs.okhttp)
    implementation(libs.reorderable)
    implementation(libs.aboutlibraries.core)
    implementation(libs.aboutlibraries.compose)
    implementation(libs.work.runtime.ktx)
    implementation(libs.profileinstaller)

    testImplementation(libs.junit4)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(composeBom)
    testImplementation(libs.compose.ui.test.junit4)
    testImplementation(libs.robolectric)
    androidTestImplementation(libs.test.ext.junit)
    androidTestImplementation(libs.test.runner)
    androidTestImplementation(composeBom)
    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.test.manifest)

    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)
}

ksp { arg("compose-destinations.defaultTransitions", "none") }
