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

package com.github.nomadboxlab.monadbox.screen.onboarding

import android.os.Bundle
import com.github.nomadboxlab.monadbox.data.store.AppSettingsStorage
import org.koin.android.ext.android.inject

internal class OnboardingStartupActivity : OnboardingBaseActivity() {

    private val appSettingsStorage: AppSettingsStorage by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!previewMode && OnboardingLauncher.shouldResetPrivacy(intent)) {
            appSettingsStorage.privacyPolicyAccepted.set(false)
        }

        setOnboardingContent {
            StartupHeroShell(enabled = true) {
                navigateForwardTo(OnboardingPermissionsActivity::class.java)
            }
        }
    }
}
