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

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    base
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.androidx.baselineprofile) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.jetbrains.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.aboutlibraries.android) apply false
    alias(libs.plugins.purejoy.fytxt) apply false
    alias(libs.plugins.spotless)
}

apply(from = "gradle/ui-contract-validation.gradle.kts")

apply(from = "gradle/modernization-baseline.gradle.kts")

extensions.configure(SpotlessExtension::class.java) {
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**", "**/generated/**")
        ktfmt("0.52").kotlinlangStyle()
    }

    kotlinGradle {
        target("**/*.kts")
        targetExclude("**/build/**")
        ktfmt("0.52").kotlinlangStyle()
    }
}

subprojects {
    val moduleOutputPath = path.removePrefix(":").replace(':', '/')
    layout.buildDirectory.set(rootProject.layout.buildDirectory.dir(moduleOutputPath))

    dependencyLocking { lockAllConfigurations() }

    val androidCompileSdk = providers.gradleProperty("android.compileSdk").get().toInt()
    val androidMinSdk = providers.gradleProperty("android.minSdk").get().toInt()
    val androidTargetSdk = providers.gradleProperty("android.targetSdk").get().toInt()
    val javaVer =
        providers.gradleProperty("android.jvm").orNull
            ?: providers.gradleProperty("project.jvm").orNull
            ?: "17"
    val ndkVersionValue = providers.gradleProperty("android.ndkVersion").orNull.orEmpty()

    pluginManager.withPlugin("com.android.library") {
        extensions.configure(LibraryExtension::class.java) {
            compileSdk = androidCompileSdk

            if (ndkVersionValue.isNotBlank()) {
                ndkVersion = ndkVersionValue
            }

            defaultConfig { minSdk = androidMinSdk }

            compileOptions {
                sourceCompatibility = JavaVersion.toVersion(javaVer)
                targetCompatibility = JavaVersion.toVersion(javaVer)
            }

            packaging {
                resources {
                    excludes +=
                        setOf(
                            "/META-INF/{AL2.0,LGPL2.1}",
                            "/META-INF/*.kotlin_module",
                            "DebugProbesKt.bin",
                        )
                }
                jniLibs { useLegacyPackaging = true }
            }
        }
    }

    pluginManager.withPlugin("com.android.application") {
        extensions.configure(ApplicationExtension::class.java) {
            compileSdk = androidCompileSdk

            if (ndkVersionValue.isNotBlank()) {
                ndkVersion = ndkVersionValue
            }

            defaultConfig {
                minSdk = androidMinSdk
                targetSdk = androidTargetSdk
            }

            compileOptions {
                sourceCompatibility = JavaVersion.toVersion(javaVer)
                targetCompatibility = JavaVersion.toVersion(javaVer)
            }
        }
    }

    tasks.withType(KotlinCompile::class.java).configureEach {
        val persistentDir =
            rootProject.findProperty("kotlin.project.persistent.dir")?.toString()?.trim().orEmpty()
        if (persistentDir.isNotEmpty()) {
            val sessionsDir = rootProject.file(persistentDir).resolve("sessions")
            doFirst { sessionsDir.mkdirs() }
        }
    }
}
