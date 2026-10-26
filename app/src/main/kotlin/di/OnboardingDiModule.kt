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

package com.github.nomadboxlab.monadbox.di

import android.app.Activity
import android.content.Intent
import com.github.nomadboxlab.monadbox.MainActivity
import com.github.nomadboxlab.monadbox.data.store.AppSettingsStorage
import com.github.nomadboxlab.monadbox.screen.onboarding.OnboardingCompletionHandler
import org.koin.dsl.module

private class MainActivityOnboardingCompletionHandler(
    private val appSettingsStorage: AppSettingsStorage
) : OnboardingCompletionHandler {
    override fun finishOnboarding(activity: Activity) {
        // Keep the launcher-created task intact. Starting MainActivity with
        // NEW_TASK/CLEAR_TASK replaces that task during onboarding hand-off, so the first
        // launcher tap afterwards can get a new taskId. The restore policy would then
        // mistake a normal background/foreground cycle for a force-quit and clear the tab.
        appSettingsStorage.mainPageRestoreStage.set(
            AppSettingsStorage.MAIN_PAGE_RESTORE_ONBOARDING_HANDOFF
        )
        activity.startActivity(Intent(activity, MainActivity::class.java))
        activity.finish()
    }
}

val onboardingDiModule = module {
    single<OnboardingCompletionHandler> { MainActivityOnboardingCompletionHandler(get()) }
}
