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

rootProject.name = "MonadBox"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

buildCache { local { directory = file("$rootDir/.gradle/build-cache") } }

include(
    ":core",
    ":platform",
    ":locale",
    ":ui",
    ":app",
    ":feature:proxy",
    ":feature:override",
    ":feature:editor",
    ":feature:meta",
    ":data:log",
    ":data:settings",
    ":data:proxy",
    ":runtime:api",
    ":runtime:client",
    ":runtime:service",
    ":performance:baselineprofile",
)

project(":core").projectDir = file("modules/core")

project(":platform").projectDir = file("modules/platform")

project(":locale").projectDir = file("modules/locale")

project(":ui").projectDir = file("modules/ui")

project(":feature").projectDir = file("modules/feature")

project(":feature:proxy").projectDir = file("modules/feature/proxy")

project(":feature:override").projectDir = file("modules/feature/override")

project(":feature:editor").projectDir = file("modules/feature/editor")

project(":feature:meta").projectDir = file("modules/feature/meta")

project(":data").projectDir = file("modules/data")

project(":data:log").projectDir = file("modules/data/log")

project(":data:settings").projectDir = file("modules/data/settings")

project(":data:proxy").projectDir = file("modules/data/proxy")

project(":runtime").projectDir = file("modules/runtime")

project(":runtime:api").projectDir = file("modules/runtime/api")

project(":runtime:client").projectDir = file("modules/runtime/client")

project(":runtime:service").projectDir = file("modules/runtime/service")

project(":performance").projectDir = file("modules/performance")

project(":performance:baselineprofile").projectDir = file("modules/performance/baselineprofile")
