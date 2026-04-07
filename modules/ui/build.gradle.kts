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

plugins {
    id("com.android.library")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
}

android {
    namespace = "com.github.yumelira.yumebox.core.ui"
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
                add("assets")
            }
            aidl.directories.apply {
                clear()
                add("aidl")
            }
            resources.directories.apply {
                clear()
                add("resources")
            }
            if (project.file("AndroidManifest.xml").isFile) {
                manifest.srcFile("AndroidManifest.xml")
            }
        }
        getByName("test") {
            kotlin.directories.clear()
            resources.directories.clear()
            assets.directories.clear()
        }
        getByName("androidTest") {
            kotlin.directories.clear()
            res.directories.clear()
            assets.directories.clear()
            aidl.directories.clear()
            resources.directories.clear()
        }
    }

    buildFeatures {
        compose = true
        buildConfig = false
    }
}

dependencies {
    implementation(project(":platform"))
    implementation(project(":locale"))
    implementation(project(":data:settings"))
    implementation(project(":runtime:api"))

    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.ui:ui")
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.compose.destinations.core)
    implementation(libs.sketch4.compose)
    implementation(libs.haze)
    implementation(libs.liquid)
    implementation(libs.kyant.shapes)
    implementation(libs.miuix)
    implementation(libs.miuix.icons)
}
