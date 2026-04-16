/*
 * This file is part of MonadBox.
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
 * Copyright (c) MonadBox Contributors 2026 - Present
 *
 */

package com.github.nomadboxlab.monadbox.screen.home

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.nomadboxlab.monadbox.common.util.toast
import com.github.nomadboxlab.monadbox.core.model.TunnelState
import com.github.nomadboxlab.monadbox.domain.model.TrafficData
import com.github.nomadboxlab.monadbox.presentation.viewmodel.ProxyViewModel
import dev.oom_wg.purejoy.mlang.MLang
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeRoute(mainInnerPadding: PaddingValues, isActive: Boolean) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val homeViewModel = koinViewModel<HomeViewModel>()
    val proxyViewModel = koinViewModel<ProxyViewModel>()

    val screenState by homeViewModel.screenState.collectAsStateWithLifecycle()
    val proxyUiState by proxyViewModel.uiState.collectAsStateWithLifecycle()
    val currentTunnelMode by proxyViewModel.currentMode.collectAsStateWithLifecycle()

    var showQuickModePanel by remember { mutableStateOf(false) }
    var modeBadgeBounds by remember { mutableStateOf<Rect?>(null) }

    LaunchedEffect(Unit) { homeViewModel.refreshProxyMode() }

    LaunchedEffect(isActive) { homeViewModel.setScreenActive(isActive) }

    DisposableEffect(lifecycleOwner, homeViewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                homeViewModel.refreshProxyMode()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val requestProxyToggle:
        (
            Boolean,
            com.github.nomadboxlab.monadbox.service.runtime.entity.Profile?,
            com.github.nomadboxlab.monadbox.data.model.ProxyMode,
        ) -> Unit =
        remember(context, coroutineScope, screenState) {
            proxyToggleRequest@{
                isRunning: Boolean,
                profile: com.github.nomadboxlab.monadbox.service.runtime.entity.Profile?,
                proxyMode: com.github.nomadboxlab.monadbox.data.model.ProxyMode ->
                if (!screenState.profilesLoaded || screenState.isToggling) return@proxyToggleRequest
                if (
                    !screenState.hasEnabledProfile ||
                        screenState.profiles.isEmpty() ||
                        profile == null
                ) {
                    context.toast(MLang.ProfilesVM.Error.ProfileNotExist, Toast.LENGTH_LONG)
                    return@proxyToggleRequest
                }
                if (!isRunning) {
                    homeViewModel.startProxy(profileId = profile.uuid.toString(), mode = proxyMode)
                } else {
                    coroutineScope.launch { homeViewModel.stopProxy() }
                }
            }
        }

    Box(modifier = Modifier.fillMaxSize()) {
        HomePager(
            mainInnerPadding = mainInnerPadding,
            trafficNow = TrafficData.from(screenState.trafficNow),
            runtimeVisualState = screenState.runtimeVisualState,
            displayRunning = screenState.displayRunning,
            isToggling = screenState.isToggling,
            profilesLoaded = screenState.profilesLoaded,
            hasProfiles = screenState.profiles.isNotEmpty(),
            hasEnabledProfile = screenState.hasEnabledProfile,
            recommendedProfile = screenState.recommendedProfile,
            currentProfileName = screenState.currentProfile?.name,
            currentTunnelMode = currentTunnelMode,
            selectedServer = screenState.selectedServer,
            ipMonitoringState = screenState.ipMonitoringState,
            speedHistory = screenState.speedHistory,
            proxyMode = screenState.proxyMode,
            uiError = screenState.ui.error ?: proxyUiState.error,
            uiMessage = screenState.ui.message ?: proxyUiState.message,
            onConsumeError = {
                homeViewModel.consumeError()
                proxyViewModel.clearError()
            },
            onConsumeMessage = {
                homeViewModel.consumeMessage()
                proxyViewModel.clearMessage()
            },
            onProxyToggleRequest = requestProxyToggle,
            onModeSwitchRequest = { showQuickModePanel = true },
            onModeBadgeBoundsChanged = { bounds -> modeBadgeBounds = bounds },
        )

        HomeModeSwitchOverlay(
            visible = showQuickModePanel,
            currentMode = currentTunnelMode,
            anchorBounds = modeBadgeBounds,
            onDismiss = { showQuickModePanel = false },
            onSelectMode = { mode: TunnelState.Mode ->
                showQuickModePanel = false
                if (mode != currentTunnelMode) {
                    proxyViewModel.patchMode(mode)
                }
            },
        )
    }
}
