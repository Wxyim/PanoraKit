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

plugins {
    id("com.android.library")
    kotlin("plugin.serialization")
}

android {
    namespace = "com.github.nomadboxlab.monadbox.data.proxy"
    buildFeatures { buildConfig = false }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":locale"))
    implementation(project(":data:settings"))
    implementation(project(":runtime:api"))
    implementation(project(":runtime:client"))

    implementation(libs.coroutines.android)
    implementation(libs.serialization.json)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.timber)

    val injectedAbi = findProperty("android.injected.build.abi") as? String
    val mmkv = if (injectedAbi in listOf("arm64-v8a", "x86_64")) libs.mmkv.v64 else libs.mmkv.v32
    implementation(mmkv)

    implementation(libs.koin.core)

    testImplementation(libs.junit4)
}
