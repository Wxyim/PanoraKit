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

package com.github.nomadboxlab.monadbox.data.store

import com.github.nomadboxlab.monadbox.data.model.AppColorTheme
import com.github.nomadboxlab.monadbox.data.model.AppLanguage
import com.github.nomadboxlab.monadbox.data.model.CleanupPolicy
import com.github.nomadboxlab.monadbox.data.model.ThemeMode
import com.tencent.mmkv.MMKV

/**
 * Cross-process MMKV-backed store.
 *
 * Shared between the app process and the `:runtime:service` VPN process via
 * `MMKV.MULTI_PROCESS_MODE` (see `MMKVProvider`). Must NOT be migrated to Room or single-process
 * Preferences DataStore — neither offers multi-process coherence. Any future replacement requires a
 * ContentProvider or AIDL shim. See `docs/REFACTOR_ROADMAP.md` Phase 7 step 3.
 */
class AppSettingsStorage(externalMmkv: MMKV) : MMKVPreference(externalMmkv = externalMmkv) {

    val initialSetupCompleted by boolFlow(false)
    val privacyPolicyAccepted by boolFlow(false)

    val appLanguage by enumFlow(AppLanguage.System)
    val themeMode by enumFlow(ThemeMode.Auto)
    val colorTheme by enumFlow(AppColorTheme.ClassicMonochrome)
    val themeAccentColorArgb by longFlow(0xFF138A74L)
    val automaticRestart by boolFlow(false)
    val autoUpdateCurrentProfileOnStart by boolFlow(true)
    val hideAppIcon by boolFlow(false)
    val excludeFromRecents by boolFlow(false)
    val showTrafficNotification by boolFlow(true)
    val autoStartLogRecording by boolFlow(false)
    val bottomBarAutoHide by boolFlow(false)
    val topBarBlurEnabled by boolFlow(true)
    val bottomBarLiquidGlassEnabled by boolFlow(false)
    val pageScale by floatFlow(1.0f)
    val singleNodeTest by boolFlow(true)
    val cleanupAutoEnabled by boolFlow(false)
    val cleanupPolicy by enumFlow(CleanupPolicy.Balanced)
    val cleanupThresholdMb by intFlow(512)
    val cleanupIntervalHours by intFlow(6)
    val cleanupLastRunAt by longFlow(0L)

    val customUserAgent by strFlow("")

    // Privacy: opt-in external-IP lookup. Empty = disabled. The user must tap
    // "查询" in the home screen to actually trigger the HTTP call. Non-empty
    // = manual-trigger-only; the home screen will not auto-poll this URL.
    val externalIpLookupUrl by strFlow("https://cloudflare.com/cdn-cgi/trace")
}
