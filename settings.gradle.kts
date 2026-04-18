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

rootProject.name = "MonadBox"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io") {
            // Restrict JitPack to known groups used by this project to avoid
            // accidental group-id shadowing attacks.
            content {
                includeGroupByRegex("com\\.github\\..*")
                includeGroupByRegex("io\\.github\\..*")
            }
        }
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io") {
            content {
                includeGroupByRegex("com\\.github\\..*")
                includeGroupByRegex("io\\.github\\..*")
            }
        }
    }
}

buildCache { local { directory = file("$rootDir/.gradle/build-cache") } }

include(
    ":core",
    ":platform",
    ":locale",
    ":ui",
    ":app",
    ":feature:meta:api",
    ":feature:proxy:api",
    ":feature:proxy",
    ":feature:override:api",
    ":feature:override",
    ":feature:editor:api",
    ":feature:editor",
    ":feature:meta",
    ":feature:about",
    ":feature:onboarding",
    ":feature:log",
    ":feature:connection",
    ":feature:traffic",
    ":feature:home:api",
    ":feature:home",
    ":feature:settings",
    ":feature:profiles",
    ":data:log",
    ":data:settings",
    ":data:proxy",
    ":data:persistence",
    ":runtime:contract",
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

project(":feature:meta:api").projectDir = file("modules/feature/meta-api")

project(":feature:proxy:api").projectDir = file("modules/feature/proxy-api")

project(":feature:proxy").projectDir = file("modules/feature/proxy")

project(":feature:override:api").projectDir = file("modules/feature/override-api")

project(":feature:override").projectDir = file("modules/feature/override")

project(":feature:editor:api").projectDir = file("modules/feature/editor-api")

project(":feature:editor").projectDir = file("modules/feature/editor")

project(":feature:meta").projectDir = file("modules/feature/meta")

project(":feature:onboarding").projectDir = file("modules/feature/onboarding")

project(":feature:log").projectDir = file("modules/feature/log")

project(":feature:connection").projectDir = file("modules/feature/connection")

project(":feature:traffic").projectDir = file("modules/feature/traffic")

project(":feature:home:api").projectDir = file("modules/feature/home-api")

project(":feature:home").projectDir = file("modules/feature/home")

project(":feature:settings").projectDir = file("modules/feature/settings")

project(":feature:profiles").projectDir = file("modules/feature/profiles")

project(":feature:about").projectDir = file("modules/feature/about")

project(":data").projectDir = file("modules/data")

project(":data:log").projectDir = file("modules/data/log")

project(":data:settings").projectDir = file("modules/data/settings")

project(":data:proxy").projectDir = file("modules/data/proxy")

project(":data:persistence").projectDir = file("modules/data/persistence")

project(":runtime").projectDir = file("modules/runtime")

project(":runtime:contract").projectDir = file("modules/runtime/contract")

project(":runtime:api").projectDir = file("modules/runtime/api")

project(":runtime:client").projectDir = file("modules/runtime/client")

project(":runtime:service").projectDir = file("modules/runtime/service")

project(":performance").projectDir = file("modules/performance")

project(":performance:baselineprofile").projectDir = file("modules/performance/baselineprofile")
