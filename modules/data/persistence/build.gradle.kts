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
}

dependencies {
    api(libs.coroutines.core)
    api(libs.room.runtime)
    api(libs.room.ktx)
    ksp(libs.room.compiler)
}
