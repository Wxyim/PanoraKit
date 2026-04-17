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

import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.nomadboxlab.monadbox.common.util.InstalledAppsAccess
import com.github.nomadboxlab.monadbox.common.util.InstalledAppsAccessMode
import com.github.nomadboxlab.monadbox.common.util.isNotificationGranted
import com.github.nomadboxlab.monadbox.common.util.openAppDetailsSettings
import com.github.nomadboxlab.monadbox.common.util.openAppNotificationSettings
import com.github.nomadboxlab.monadbox.data.model.ThemeMode
import com.github.nomadboxlab.monadbox.data.repository.AppSettingsRepository

internal data class PrivacyAcceptedState(
    val accepted: Boolean,
    val onAcceptedChange: (Boolean) -> Unit,
)

internal data class ThemeCustomizationState(
    val themeMode: ThemeMode,
    val onThemeModeChange: (ThemeMode) -> Unit,
    val themeSeedColorArgb: Long,
    val onThemeSeedColorChange: (Long) -> Unit,
)

@Composable
internal fun rememberPermissionState(
    context: Context,
    lifecycleOwner: LifecycleOwner,
): PermissionState {
    val miuiDynamicSupported = remember { isMiuiGetInstalledAppsDynamicSupported(context) }

    var notificationGranted by remember { mutableStateOf(isNotificationGranted(context)) }
    var appListGranted by remember {
        mutableStateOf(isAppListPermissionGranted(context, miuiDynamicSupported))
    }

    val requestNotificationPermission =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { granted ->
                notificationGranted = granted
                if (!granted) {
                    openAppNotificationSettings(context)
                }
            },
        )

    val requestMiuiGetInstalledAppsPermission =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { granted ->
                appListGranted = granted
                if (!granted) {
                    openAppDetailsSettings(context)
                }
            },
        )

    DisposableEffect(lifecycleOwner, context, miuiDynamicSupported) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                notificationGranted = isNotificationGranted(context)
                appListGranted = isAppListPermissionGranted(context, miuiDynamicSupported)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    return PermissionState(
        notificationGranted = notificationGranted,
        appListGranted = appListGranted,
        miuiDynamicSupported = miuiDynamicSupported,
        onRequestNotification = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestNotificationPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            } else {
                openAppNotificationSettings(context)
            }
        },
        onRequestAppList = {
            if (miuiDynamicSupported) {
                requestMiuiGetInstalledAppsPermission.launch(InstalledAppsAccess.MiuiPermission)
            } else {
                openAppDetailsSettings(context)
            }
        },
    )
}

@Composable
internal fun rememberPrivacyAcceptedState(
    appSettings: AppSettingsRepository
): PrivacyAcceptedState {
    val accepted by appSettings.privacyPolicyAccepted.state.collectAsStateWithLifecycle()
    return PrivacyAcceptedState(
        accepted = accepted,
        onAcceptedChange = appSettings::setPrivacyPolicyAccepted,
    )
}

@Composable
internal fun rememberThemeCustomizationState(
    appSettings: AppSettingsRepository
): ThemeCustomizationState {
    val themeMode by appSettings.themeMode.state.collectAsStateWithLifecycle()
    val themeSeedColorArgb by appSettings.themeSeedColorArgb.state.collectAsStateWithLifecycle()
    return ThemeCustomizationState(
        themeMode = themeMode,
        onThemeModeChange = appSettings::onThemeModeChange,
        themeSeedColorArgb = themeSeedColorArgb,
        onThemeSeedColorChange = appSettings::onThemeSeedColorChange,
    )
}

internal fun isMiuiGetInstalledAppsDynamicSupported(context: Context): Boolean {
    return runCatching {
            val permissionInfo =
                context.packageManager.getPermissionInfo(InstalledAppsAccess.MiuiPermission, 0)
            permissionInfo.packageName == "com.lbe.security.miui"
        }
        .getOrDefault(false)
}

internal fun isAppListPermissionGranted(context: Context, miuiDynamicSupported: Boolean): Boolean {
    return when (InstalledAppsAccess.resolve(context).mode) {
        InstalledAppsAccessMode.Full -> true
        InstalledAppsAccessMode.PermissionRequired -> false
        InstalledAppsAccessMode.ManualOnly -> !miuiDynamicSupported
    }
}
