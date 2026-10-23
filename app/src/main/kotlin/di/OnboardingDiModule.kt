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
import com.github.nomadboxlab.monadbox.screen.onboarding.OnboardingCompletionHandler
import org.koin.dsl.module

private class MainActivityOnboardingCompletionHandler : OnboardingCompletionHandler {
    override fun finishOnboarding(activity: Activity) {
        // Enter through the launcher entry point, not an explicit MainActivity intent.
        // The task's base intent must match the one used by the home-screen icon; otherwise
        // the first launcher tap after onboarding resets the task (and its taskId), which
        // the restore logic correctly interprets as a force-quit and clears the last tab.
        val intent =
            activity.packageManager.getLaunchIntentForPackage(activity.packageName)
                ?: Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                    setClass(activity, MainActivity::class.java)
                }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        activity.startActivity(intent)
        activity.finish()
    }
}

val onboardingDiModule = module {
    single<OnboardingCompletionHandler> { MainActivityOnboardingCompletionHandler() }
}
