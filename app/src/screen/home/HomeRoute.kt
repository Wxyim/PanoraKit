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

package com.github.yumelira.yumebox.screen.home

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.github.yumelira.yumebox.common.util.toast
import com.github.yumelira.yumebox.core.model.TunnelState
import com.github.yumelira.yumebox.domain.model.TrafficData
import com.github.yumelira.yumebox.presentation.component.CollectFlowWithLifecycle
import com.github.yumelira.yumebox.presentation.viewmodel.ProxyViewModel
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

    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val proxyUiState by proxyViewModel.uiState.collectAsStateWithLifecycle()
    val trafficNow by homeViewModel.trafficNow.collectAsStateWithLifecycle()
    val displayRunning by homeViewModel.displayRunning.collectAsStateWithLifecycle()
    val isToggling by homeViewModel.isToggling.collectAsStateWithLifecycle()
    val profilesLoaded by homeViewModel.profilesLoaded.collectAsStateWithLifecycle()
    val profiles by homeViewModel.profiles.collectAsStateWithLifecycle()
    val hasEnabledProfile by
        homeViewModel.hasEnabledProfile.collectAsStateWithLifecycle(initialValue = false)
    val recommendedProfile by homeViewModel.recommendedProfile.collectAsStateWithLifecycle()
    val currentProfile by homeViewModel.currentProfile.collectAsStateWithLifecycle()
    val selectedServer by homeViewModel.selectedServer.collectAsStateWithLifecycle()
    val ipMonitoringState by homeViewModel.ipMonitoringState.collectAsStateWithLifecycle()
    val speedHistory by homeViewModel.speedHistory.collectAsStateWithLifecycle()
    val currentProxyMode by homeViewModel.proxyMode.collectAsStateWithLifecycle()
    val currentTunnelMode by proxyViewModel.currentMode.collectAsStateWithLifecycle()

    var pendingProfileId by remember { mutableStateOf<String?>(null) }
    var pendingProxyMode by remember {
        mutableStateOf<com.github.yumelira.yumebox.data.model.ProxyMode?>(null)
    }
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

    val vpnPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                pendingProfileId?.let { profileId ->
                    homeViewModel.startProxy(profileId, mode = pendingProxyMode)
                }
            }
            pendingProfileId = null
            pendingProxyMode = null
        }

    CollectFlowWithLifecycle(flow = homeViewModel.vpnPrepareIntent) { intent ->
        vpnPermissionLauncher.launch(intent)
    }

    val requestProxyToggle:
        (
            Boolean,
            com.github.yumelira.yumebox.service.runtime.entity.Profile?,
            com.github.yumelira.yumebox.data.model.ProxyMode,
        ) -> Unit =
        remember(
            context,
            coroutineScope,
            displayRunning,
            hasEnabledProfile,
            isToggling,
            profilesLoaded,
            profiles,
            recommendedProfile,
        ) {
            proxyToggleRequest@{
                isRunning: Boolean,
                profile: com.github.yumelira.yumebox.service.runtime.entity.Profile?,
                proxyMode: com.github.yumelira.yumebox.data.model.ProxyMode ->
                if (!profilesLoaded || isToggling) return@proxyToggleRequest
                if (!hasEnabledProfile || profiles.isEmpty() || profile == null) {
                    context.toast(MLang.ProfilesVM.Error.ProfileNotExist, Toast.LENGTH_LONG)
                    return@proxyToggleRequest
                }
                if (!isRunning) {
                    pendingProfileId = profile.uuid.toString()
                    pendingProxyMode = proxyMode
                    homeViewModel.startProxy(profileId = profile.uuid.toString(), mode = null)
                } else {
                    coroutineScope.launch { homeViewModel.stopProxy() }
                }
            }
        }

    Box(modifier = Modifier.fillMaxSize()) {
        HomePager(
            mainInnerPadding = mainInnerPadding,
            trafficNow = TrafficData.from(trafficNow),
            displayRunning = displayRunning,
            isToggling = isToggling,
            profilesLoaded = profilesLoaded,
            hasProfiles = profiles.isNotEmpty(),
            hasEnabledProfile = hasEnabledProfile,
            recommendedProfile = recommendedProfile,
            currentProfileName = currentProfile?.name,
            currentTunnelMode = currentTunnelMode,
            selectedServer = selectedServer,
            ipMonitoringState = ipMonitoringState,
            speedHistory = speedHistory,
            proxyMode = currentProxyMode,
            uiError = uiState.error ?: proxyUiState.error,
            uiMessage = uiState.message ?: proxyUiState.message,
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
