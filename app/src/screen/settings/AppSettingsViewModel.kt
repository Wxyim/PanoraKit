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

package com.github.yumelira.yumebox.screen.settings

import androidx.lifecycle.ViewModel
import com.github.yumelira.yumebox.common.util.StorageCleanupManager
import com.github.yumelira.yumebox.data.model.AppColorTheme
import com.github.yumelira.yumebox.data.model.AppLanguage
import com.github.yumelira.yumebox.data.model.CleanupPolicy
import com.github.yumelira.yumebox.data.model.ThemeMode
import com.github.yumelira.yumebox.data.repository.AppSettingsRepository
import com.github.yumelira.yumebox.data.store.Preference

class AppSettingsViewModel(
    private val repository: AppSettingsRepository,
    private val cleanupManager: StorageCleanupManager,
) : ViewModel() {

    val initialSetupCompleted: Preference<Boolean> = repository.initialSetupCompleted
    val privacyPolicyAccepted: Preference<Boolean> = repository.privacyPolicyAccepted

    val appLanguage: Preference<AppLanguage> = repository.appLanguage
    val themeMode: Preference<ThemeMode> = repository.themeMode
    val colorTheme: Preference<AppColorTheme> = repository.colorTheme
    val themeSeedColorArgb: Preference<Long> = repository.themeSeedColorArgb
    val automaticRestart: Preference<Boolean> = repository.automaticRestart
    val autoUpdateCurrentProfileOnStart: Preference<Boolean> =
        repository.autoUpdateCurrentProfileOnStart
    val hideAppIcon: Preference<Boolean> = repository.hideAppIcon
    val excludeFromRecents: Preference<Boolean> = repository.excludeFromRecents
    val showTrafficNotification: Preference<Boolean> = repository.showTrafficNotification
    val bottomBarAutoHide: Preference<Boolean> = repository.bottomBarAutoHide
    val topBarBlurEnabled: Preference<Boolean> = repository.topBarBlurEnabled
    val bottomBarLiquidGlassEnabled: Preference<Boolean> = repository.bottomBarLiquidGlassEnabled
    val pageScale: Preference<Float> = repository.pageScale
    val singleNodeTest: Preference<Boolean> = repository.singleNodeTest
    val cleanupAutoEnabled: Preference<Boolean> = repository.cleanupAutoEnabled
    val cleanupPolicy: Preference<CleanupPolicy> = repository.cleanupPolicy
    val cleanupThresholdMb: Preference<Int> = repository.cleanupThresholdMb
    val cleanupIntervalHours: Preference<Int> = repository.cleanupIntervalHours
    val cleanupLastRunAt: Preference<Long> = repository.cleanupLastRunAt

    val customUserAgent: Preference<String> = repository.customUserAgent

    fun onAppLanguageChange(language: AppLanguage) = repository.onAppLanguageChange(language)

    fun onThemeModeChange(mode: ThemeMode) = themeMode.set(mode)

    fun onColorThemeChange(theme: AppColorTheme) = colorTheme.set(theme)

    fun onThemeSeedColorChange(argb: Long) = themeSeedColorArgb.set(argb)

    fun resetThemeSeedColor() = themeSeedColorArgb.set(0xFF138A74L)

    fun onBottomBarAutoHideChange(enabled: Boolean) = bottomBarAutoHide.set(enabled)

    fun onTopBarBlurEnabledChange(enabled: Boolean) = topBarBlurEnabled.set(enabled)

    fun onBottomBarLiquidGlassEnabledChange(enabled: Boolean) =
        bottomBarLiquidGlassEnabled.set(enabled)

    fun onPageScaleChange(scale: Float) = pageScale.set(scale)

    fun onAutomaticRestartChange(enabled: Boolean) = automaticRestart.set(enabled)

    fun onAutoUpdateCurrentProfileOnStartChange(enabled: Boolean) =
        autoUpdateCurrentProfileOnStart.set(enabled)

    fun onHideAppIconChange(hide: Boolean) = hideAppIcon.set(hide)

    fun onExcludeFromRecentsChange(exclude: Boolean) = excludeFromRecents.set(exclude)

    fun onShowTrafficNotificationChange(show: Boolean) = showTrafficNotification.set(show)

    fun onSingleNodeTestChange(enabled: Boolean) = singleNodeTest.set(enabled)

    fun onCleanupAutoEnabledChange(enabled: Boolean) = cleanupAutoEnabled.set(enabled)

    fun onCleanupPolicyChange(policy: CleanupPolicy) = cleanupPolicy.set(policy)

    fun onCleanupThresholdMbChange(value: Int) = cleanupThresholdMb.set(value.coerceIn(64, 4096))

    fun onCleanupIntervalHoursChange(value: Int) = cleanupIntervalHours.set(value.coerceIn(1, 48))

    fun applyCustomUserAgent(userAgent: String) = repository.applyCustomUserAgent(userAgent)

    suspend fun runCleanupNow(): StorageCleanupManager.CleanupResult {
        return cleanupManager.runCleanupNow()
    }

    fun setInitialSetupCompleted(completed: Boolean) = initialSetupCompleted.set(completed)

    fun setPrivacyPolicyAccepted(accepted: Boolean) = privacyPolicyAccepted.set(accepted)
}
