/*
 * This file is part of MonadBox.
 *
 * MonadBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * Copyright (c) MonadBox Contributors 2026 - Present
 */

plugins {
    id("com.android.library")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.github.nomadboxlab.monadbox.data.persistence"
    buildFeatures { buildConfig = false }

    sourceSets {
        getByName("androidTest") {
            assets.directories.add(
                project.layout.projectDirectory.dir("schemas").asFile.invariantSeparatorsPath
            )
        }
    }
}

dependencies {
    api(libs.coroutines.core)
    api(libs.room.runtime)
    api(libs.room.ktx)
    ksp(libs.room.compiler)

    androidTestImplementation(libs.junit4)
    androidTestImplementation(libs.test.ext.junit)
    androidTestImplementation(libs.test.runner)
    androidTestImplementation(libs.room.testing)
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
}
