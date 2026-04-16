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

package com.github.nomadboxlab.monadbox.screen.settings

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.nomadboxlab.monadbox.core.model.RootTunDnsMode
import com.github.nomadboxlab.monadbox.data.model.AccessControlMode
import com.github.nomadboxlab.monadbox.data.model.ProxyMode
import com.github.nomadboxlab.monadbox.data.model.TunStack
import com.github.nomadboxlab.monadbox.presentation.component.AppDialog
import com.github.nomadboxlab.monadbox.presentation.component.Card
import com.github.nomadboxlab.monadbox.presentation.component.ConfigSettingRow
import com.github.nomadboxlab.monadbox.presentation.component.DialogButtonRow
import com.github.nomadboxlab.monadbox.presentation.component.EnumSelector
import com.github.nomadboxlab.monadbox.presentation.component.NavigationBackIcon
import com.github.nomadboxlab.monadbox.presentation.component.ScreenLazyColumn
import com.github.nomadboxlab.monadbox.presentation.component.SemanticTone
import com.github.nomadboxlab.monadbox.presentation.component.SmallTitle
import com.github.nomadboxlab.monadbox.presentation.component.TopBar
import com.github.nomadboxlab.monadbox.presentation.theme.adaptiveContentWidth
import com.github.nomadboxlab.monadbox.presentation.theme.rememberAvailableWindowAdaptiveInfo
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.AccessControlScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.oom_wg.purejoy.mlang.MLang
import org.koin.androidx.compose.koinViewModel
import top.yukonga.miuix.kmp.basic.CircularProgressIndicator
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.extra.SuperSwitch
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
@Destination<RootGraph>
fun NetworkSettingsScreen(navigator: DestinationsNavigator) {
    val scrollBehavior = MiuixScrollBehavior()
    val viewModel = koinViewModel<NetworkSettingsViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val proxyMode by viewModel.currentProxyMode.collectAsStateWithLifecycle()
    val bypassPrivateNetwork by viewModel.bypassPrivateNetwork.state.collectAsStateWithLifecycle()
    val dnsHijack by viewModel.dnsHijack.state.collectAsStateWithLifecycle()
    val allowBypass by viewModel.allowBypass.state.collectAsStateWithLifecycle()
    val enableIPv6 by viewModel.enableIPv6.state.collectAsStateWithLifecycle()
    val systemProxy by viewModel.systemProxy.state.collectAsStateWithLifecycle()
    val tunStack by viewModel.tunStack.state.collectAsStateWithLifecycle()
    val rootTunAutoRoute by viewModel.rootTunAutoRoute.state.collectAsStateWithLifecycle()
    val rootTunStrictRoute by viewModel.rootTunStrictRoute.state.collectAsStateWithLifecycle()
    val rootTunAutoRedirect by viewModel.rootTunAutoRedirect.state.collectAsStateWithLifecycle()
    val rootTunDnsMode by viewModel.rootTunDnsMode.state.collectAsStateWithLifecycle()
    val accessControlMode by viewModel.accessControlMode.state.collectAsStateWithLifecycle()

    val rootTunIfNameDraft by viewModel.rootTunIfNameDraft.collectAsStateWithLifecycle()
    val rootTunMtuDraft by viewModel.rootTunMtuDraft.collectAsStateWithLifecycle()
    val rootTunIncludeAndroidUserDraft by
        viewModel.rootTunIncludeAndroidUserDraft.collectAsStateWithLifecycle()
    val rootTunRouteExcludeAddressDraft by
        viewModel.rootTunRouteExcludeAddressDraft.collectAsStateWithLifecycle()
    val rootTunFakeIpRangeDraft by viewModel.rootTunFakeIpRangeDraft.collectAsStateWithLifecycle()
    val rootTunFakeIpRange6Draft by viewModel.rootTunFakeIpRange6Draft.collectAsStateWithLifecycle()
    var enableModeTransition by remember { mutableStateOf(false) }
    var pendingProxyModeConfirmation by rememberSaveable { mutableStateOf<ProxyMode?>(null) }

    LaunchedEffect(Unit) { enableModeTransition = true }

    val requestProxyModeChange: (ProxyMode) -> Unit = { targetMode ->
        if (
            requiresHighRiskProxyModeConfirmation(currentMode = proxyMode, targetMode = targetMode)
        ) {
            pendingProxyModeConfirmation = targetMode
        } else {
            viewModel.onProxyModeChange(targetMode)
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                title = MLang.NetworkSettings.Title,
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    NavigationBackIcon(
                        navigator = navigator,
                        contentDescription = MLang.Component.Navigation.Back,
                    )
                },
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            BoxWithConstraints(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter,
            ) {
                val adaptiveInfo = rememberAvailableWindowAdaptiveInfo(maxWidth, maxHeight)
                val contentMaxWidth = adaptiveInfo.preferredTwoPaneMaxWidth
                ScreenLazyColumn(
                    modifier = Modifier.adaptiveContentWidth(contentMaxWidth),
                    scrollBehavior = scrollBehavior,
                    innerPadding = innerPadding,
                ) {
                    item {
                        NetworkSettingsContent(
                            uiState = uiState,
                            proxyMode = proxyMode,
                            bypassPrivateNetwork = bypassPrivateNetwork,
                            dnsHijack = dnsHijack,
                            allowBypass = allowBypass,
                            enableIPv6 = enableIPv6,
                            systemProxy = systemProxy,
                            tunStack = tunStack,
                            rootTunAutoRoute = rootTunAutoRoute,
                            rootTunStrictRoute = rootTunStrictRoute,
                            rootTunAutoRedirect = rootTunAutoRedirect,
                            rootTunDnsMode = rootTunDnsMode,
                            accessControlMode = accessControlMode,
                            rootTunIfNameDraft = rootTunIfNameDraft,
                            rootTunMtuDraft = rootTunMtuDraft,
                            rootTunIncludeAndroidUserDraft = rootTunIncludeAndroidUserDraft,
                            rootTunRouteExcludeAddressDraft = rootTunRouteExcludeAddressDraft,
                            rootTunFakeIpRangeDraft = rootTunFakeIpRangeDraft,
                            rootTunFakeIpRange6Draft = rootTunFakeIpRange6Draft,
                            enableModeTransition = enableModeTransition,
                            onProxyModeChange = requestProxyModeChange,
                            onBypassPrivateNetworkChange = viewModel::onBypassPrivateNetworkChange,
                            onDnsHijackChange = viewModel::onDnsHijackChange,
                            onAllowBypassChange = viewModel::onAllowBypassChange,
                            onEnableIPv6Change = viewModel::onEnableIPv6Change,
                            onSystemProxyChange = viewModel::onSystemProxyChange,
                            onTunStackChange = viewModel::onTunStackChange,
                            onRootTunAutoRouteChange = viewModel::onRootTunAutoRouteChange,
                            onRootTunStrictRouteChange = viewModel::onRootTunStrictRouteChange,
                            onRootTunAutoRedirectChange = viewModel::onRootTunAutoRedirectChange,
                            onRootTunDnsModeChange = viewModel::onRootTunDnsModeChange,
                            onRootTunIfNameDraftChange = viewModel::onRootTunIfNameDraftChange,
                            onRootTunMtuDraftChange = viewModel::onRootTunMtuDraftChange,
                            onRootTunIncludeAndroidUserDraftChange =
                                viewModel::onRootTunIncludeAndroidUserDraftChange,
                            onRootTunRouteExcludeAddressDraftChange =
                                viewModel::onRootTunRouteExcludeAddressDraftChange,
                            onRootTunFakeIpRangeDraftChange =
                                viewModel::onRootTunFakeIpRangeDraftChange,
                            onRootTunFakeIpRange6DraftChange =
                                viewModel::onRootTunFakeIpRange6DraftChange,
                            commitRootTunIfName = viewModel::commitRootTunIfName,
                            commitRootTunMtu = viewModel::commitRootTunMtu,
                            commitRootTunIncludeAndroidUser =
                                viewModel::commitRootTunIncludeAndroidUser,
                            commitRootTunRouteExcludeAddress =
                                viewModel::commitRootTunRouteExcludeAddress,
                            commitRootTunFakeIpRange = viewModel::commitRootTunFakeIpRange,
                            commitRootTunFakeIpRange6 = viewModel::commitRootTunFakeIpRange6,
                            onAccessControlModeChange = viewModel::onAccessControlModeChange,
                            onManageAccessControl = {
                                navigator.navigate(AccessControlScreenDestination)
                            },
                        )
                    }
                }
            }

            HighRiskProxyModeDialog(
                targetMode = pendingProxyModeConfirmation,
                onDismiss = { pendingProxyModeConfirmation = null },
                onConfirm = { targetMode ->
                    pendingProxyModeConfirmation = null
                    viewModel.onProxyModeChange(targetMode)
                },
            )

            AnimatedVisibility(
                visible = uiState.isApplying,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.fillMaxSize(),
            ) {
                Box(
                    modifier =
                        Modifier.fillMaxSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {},
                            ),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

private fun requiresHighRiskProxyModeConfirmation(
    currentMode: ProxyMode,
    targetMode: ProxyMode,
): Boolean {
    return currentMode != targetMode &&
        (currentMode == ProxyMode.RootTun || targetMode == ProxyMode.RootTun)
}

@Composable
private fun HighRiskProxyModeDialog(
    targetMode: ProxyMode?,
    onDismiss: () -> Unit,
    onConfirm: (ProxyMode) -> Unit,
) {
    AppDialog(
        show = targetMode != null,
        title = "Confirm traffic routing change",
        summary =
            "This changes how MonadBox routes device traffic through the VPN tunnel. If the service is running, MonadBox will apply it with rollback on failure.",
        onDismissRequest = onDismiss,
    ) {
        DialogButtonRow(
            onCancel = onDismiss,
            onConfirm = { targetMode?.let(onConfirm) },
            confirmTone = SemanticTone.Warning,
        )
    }
}

private fun ProxyMode.toDisplayName(): String =
    when (this) {
        ProxyMode.Http -> MLang.NetworkSettings.VpnService.SystemProxy
        ProxyMode.Tun -> MLang.NetworkSettings.VpnService.VpnMode
        ProxyMode.RootTun -> MLang.NetworkSettings.VpnService.RootTunMode
    }

@Composable
private fun NetworkSettingsContent(
    uiState: NetworkSettingsUiState,
    proxyMode: ProxyMode,
    bypassPrivateNetwork: Boolean,
    dnsHijack: Boolean,
    allowBypass: Boolean,
    enableIPv6: Boolean,
    systemProxy: Boolean,
    tunStack: TunStack,
    rootTunAutoRoute: Boolean,
    rootTunStrictRoute: Boolean,
    rootTunAutoRedirect: Boolean,
    rootTunDnsMode: RootTunDnsMode,
    accessControlMode: AccessControlMode,
    rootTunIfNameDraft: String,
    rootTunMtuDraft: String,
    rootTunIncludeAndroidUserDraft: String,
    rootTunRouteExcludeAddressDraft: String,
    rootTunFakeIpRangeDraft: String,
    rootTunFakeIpRange6Draft: String,
    enableModeTransition: Boolean,
    onProxyModeChange: (ProxyMode) -> Unit,
    onBypassPrivateNetworkChange: (Boolean) -> Unit,
    onDnsHijackChange: (Boolean) -> Unit,
    onAllowBypassChange: (Boolean) -> Unit,
    onEnableIPv6Change: (Boolean) -> Unit,
    onSystemProxyChange: (Boolean) -> Unit,
    onTunStackChange: (TunStack) -> Unit,
    onRootTunAutoRouteChange: (Boolean) -> Unit,
    onRootTunStrictRouteChange: (Boolean) -> Unit,
    onRootTunAutoRedirectChange: (Boolean) -> Unit,
    onRootTunDnsModeChange: (RootTunDnsMode) -> Unit,
    onRootTunIfNameDraftChange: (String) -> Unit,
    onRootTunMtuDraftChange: (String) -> Unit,
    onRootTunIncludeAndroidUserDraftChange: (String) -> Unit,
    onRootTunRouteExcludeAddressDraftChange: (String) -> Unit,
    onRootTunFakeIpRangeDraftChange: (String) -> Unit,
    onRootTunFakeIpRange6DraftChange: (String) -> Unit,
    commitRootTunIfName: () -> Unit,
    commitRootTunMtu: () -> Unit,
    commitRootTunIncludeAndroidUser: () -> Unit,
    commitRootTunRouteExcludeAddress: () -> Unit,
    commitRootTunFakeIpRange: () -> Unit,
    commitRootTunFakeIpRange6: () -> Unit,
    onAccessControlModeChange: (AccessControlMode) -> Unit,
    onManageAccessControl: () -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val availableAdaptiveInfo = rememberAvailableWindowAdaptiveInfo(maxWidth, maxHeight)
        val isWideLayout = availableAdaptiveInfo.prefersTwoPaneContent
        val sectionSpacing = 16.dp

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(sectionSpacing),
        ) {
            if (isWideLayout) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(sectionSpacing),
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(sectionSpacing),
                    ) {
                        VpnServiceSection(
                            uiState = uiState,
                            proxyMode = proxyMode,
                            bypassPrivateNetwork = bypassPrivateNetwork,
                            dnsHijack = dnsHijack,
                            allowBypass = allowBypass,
                            enableIPv6 = enableIPv6,
                            systemProxy = systemProxy,
                            tunStack = tunStack,
                            rootTunAutoRoute = rootTunAutoRoute,
                            rootTunStrictRoute = rootTunStrictRoute,
                            rootTunAutoRedirect = rootTunAutoRedirect,
                            rootTunDnsMode = rootTunDnsMode,
                            rootTunIfNameDraft = rootTunIfNameDraft,
                            rootTunMtuDraft = rootTunMtuDraft,
                            rootTunIncludeAndroidUserDraft = rootTunIncludeAndroidUserDraft,
                            rootTunRouteExcludeAddressDraft = rootTunRouteExcludeAddressDraft,
                            rootTunFakeIpRangeDraft = rootTunFakeIpRangeDraft,
                            rootTunFakeIpRange6Draft = rootTunFakeIpRange6Draft,
                            enableModeTransition = enableModeTransition,
                            onProxyModeChange = onProxyModeChange,
                            onBypassPrivateNetworkChange = onBypassPrivateNetworkChange,
                            onDnsHijackChange = onDnsHijackChange,
                            onAllowBypassChange = onAllowBypassChange,
                            onEnableIPv6Change = onEnableIPv6Change,
                            onSystemProxyChange = onSystemProxyChange,
                            onTunStackChange = onTunStackChange,
                            onRootTunAutoRouteChange = onRootTunAutoRouteChange,
                            onRootTunStrictRouteChange = onRootTunStrictRouteChange,
                            onRootTunAutoRedirectChange = onRootTunAutoRedirectChange,
                            onRootTunDnsModeChange = onRootTunDnsModeChange,
                            onRootTunIfNameDraftChange = onRootTunIfNameDraftChange,
                            onRootTunMtuDraftChange = onRootTunMtuDraftChange,
                            onRootTunIncludeAndroidUserDraftChange =
                                onRootTunIncludeAndroidUserDraftChange,
                            onRootTunRouteExcludeAddressDraftChange =
                                onRootTunRouteExcludeAddressDraftChange,
                            onRootTunFakeIpRangeDraftChange = onRootTunFakeIpRangeDraftChange,
                            onRootTunFakeIpRange6DraftChange = onRootTunFakeIpRange6DraftChange,
                            commitRootTunIfName = commitRootTunIfName,
                            commitRootTunMtu = commitRootTunMtu,
                            commitRootTunIncludeAndroidUser = commitRootTunIncludeAndroidUser,
                            commitRootTunRouteExcludeAddress = commitRootTunRouteExcludeAddress,
                            commitRootTunFakeIpRange = commitRootTunFakeIpRange,
                            commitRootTunFakeIpRange6 = commitRootTunFakeIpRange6,
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        ProxyOptionsSection(
                            uiState = uiState,
                            accessControlMode = accessControlMode,
                            onAccessControlModeChange = onAccessControlModeChange,
                            onManageAccessControl = onManageAccessControl,
                        )
                    }
                }
            } else {
                VpnServiceSection(
                    uiState = uiState,
                    proxyMode = proxyMode,
                    bypassPrivateNetwork = bypassPrivateNetwork,
                    dnsHijack = dnsHijack,
                    allowBypass = allowBypass,
                    enableIPv6 = enableIPv6,
                    systemProxy = systemProxy,
                    tunStack = tunStack,
                    rootTunAutoRoute = rootTunAutoRoute,
                    rootTunStrictRoute = rootTunStrictRoute,
                    rootTunAutoRedirect = rootTunAutoRedirect,
                    rootTunDnsMode = rootTunDnsMode,
                    rootTunIfNameDraft = rootTunIfNameDraft,
                    rootTunMtuDraft = rootTunMtuDraft,
                    rootTunIncludeAndroidUserDraft = rootTunIncludeAndroidUserDraft,
                    rootTunRouteExcludeAddressDraft = rootTunRouteExcludeAddressDraft,
                    rootTunFakeIpRangeDraft = rootTunFakeIpRangeDraft,
                    rootTunFakeIpRange6Draft = rootTunFakeIpRange6Draft,
                    enableModeTransition = enableModeTransition,
                    onProxyModeChange = onProxyModeChange,
                    onBypassPrivateNetworkChange = onBypassPrivateNetworkChange,
                    onDnsHijackChange = onDnsHijackChange,
                    onAllowBypassChange = onAllowBypassChange,
                    onEnableIPv6Change = onEnableIPv6Change,
                    onSystemProxyChange = onSystemProxyChange,
                    onTunStackChange = onTunStackChange,
                    onRootTunAutoRouteChange = onRootTunAutoRouteChange,
                    onRootTunStrictRouteChange = onRootTunStrictRouteChange,
                    onRootTunAutoRedirectChange = onRootTunAutoRedirectChange,
                    onRootTunDnsModeChange = onRootTunDnsModeChange,
                    onRootTunIfNameDraftChange = onRootTunIfNameDraftChange,
                    onRootTunMtuDraftChange = onRootTunMtuDraftChange,
                    onRootTunIncludeAndroidUserDraftChange = onRootTunIncludeAndroidUserDraftChange,
                    onRootTunRouteExcludeAddressDraftChange =
                        onRootTunRouteExcludeAddressDraftChange,
                    onRootTunFakeIpRangeDraftChange = onRootTunFakeIpRangeDraftChange,
                    onRootTunFakeIpRange6DraftChange = onRootTunFakeIpRange6DraftChange,
                    commitRootTunIfName = commitRootTunIfName,
                    commitRootTunMtu = commitRootTunMtu,
                    commitRootTunIncludeAndroidUser = commitRootTunIncludeAndroidUser,
                    commitRootTunRouteExcludeAddress = commitRootTunRouteExcludeAddress,
                    commitRootTunFakeIpRange = commitRootTunFakeIpRange,
                    commitRootTunFakeIpRange6 = commitRootTunFakeIpRange6,
                )
                ProxyOptionsSection(
                    uiState = uiState,
                    accessControlMode = accessControlMode,
                    onAccessControlModeChange = onAccessControlModeChange,
                    onManageAccessControl = onManageAccessControl,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun VpnServiceSection(
    uiState: NetworkSettingsUiState,
    proxyMode: ProxyMode,
    bypassPrivateNetwork: Boolean,
    dnsHijack: Boolean,
    allowBypass: Boolean,
    enableIPv6: Boolean,
    systemProxy: Boolean,
    tunStack: TunStack,
    rootTunAutoRoute: Boolean,
    rootTunStrictRoute: Boolean,
    rootTunAutoRedirect: Boolean,
    rootTunDnsMode: RootTunDnsMode,
    rootTunIfNameDraft: String,
    rootTunMtuDraft: String,
    rootTunIncludeAndroidUserDraft: String,
    rootTunRouteExcludeAddressDraft: String,
    rootTunFakeIpRangeDraft: String,
    rootTunFakeIpRange6Draft: String,
    enableModeTransition: Boolean,
    onProxyModeChange: (ProxyMode) -> Unit,
    onBypassPrivateNetworkChange: (Boolean) -> Unit,
    onDnsHijackChange: (Boolean) -> Unit,
    onAllowBypassChange: (Boolean) -> Unit,
    onEnableIPv6Change: (Boolean) -> Unit,
    onSystemProxyChange: (Boolean) -> Unit,
    onTunStackChange: (TunStack) -> Unit,
    onRootTunAutoRouteChange: (Boolean) -> Unit,
    onRootTunStrictRouteChange: (Boolean) -> Unit,
    onRootTunAutoRedirectChange: (Boolean) -> Unit,
    onRootTunDnsModeChange: (RootTunDnsMode) -> Unit,
    onRootTunIfNameDraftChange: (String) -> Unit,
    onRootTunMtuDraftChange: (String) -> Unit,
    onRootTunIncludeAndroidUserDraftChange: (String) -> Unit,
    onRootTunRouteExcludeAddressDraftChange: (String) -> Unit,
    onRootTunFakeIpRangeDraftChange: (String) -> Unit,
    onRootTunFakeIpRange6DraftChange: (String) -> Unit,
    commitRootTunIfName: () -> Unit,
    commitRootTunMtu: () -> Unit,
    commitRootTunIncludeAndroidUser: () -> Unit,
    commitRootTunRouteExcludeAddress: () -> Unit,
    commitRootTunFakeIpRange: () -> Unit,
    commitRootTunFakeIpRange6: () -> Unit,
) {
    SmallTitle(MLang.NetworkSettings.Section.VpnService)
    Card {
        EnumSelector(
            title = MLang.NetworkSettings.VpnService.RouteTrafficTitle,
            summary =
                when {
                    uiState.isApplying ->
                        MLang.NetworkSettings.VpnService.RouteTrafficApplying.format(
                            uiState.effectiveMode.toDisplayName(),
                            uiState.configuredMode.toDisplayName(),
                        )
                    uiState.serviceState == ServiceState.Running ->
                        MLang.NetworkSettings.VpnService.RouteTrafficEffective.format(
                            uiState.effectiveMode.toDisplayName(),
                            uiState.configuredMode.toDisplayName(),
                        )
                    else -> MLang.NetworkSettings.VpnService.RouteTrafficSummary
                },
            currentValue = proxyMode,
            items =
                listOf(
                    MLang.NetworkSettings.VpnService.SystemProxy,
                    MLang.NetworkSettings.VpnService.VpnMode,
                    MLang.NetworkSettings.VpnService.RootTunMode,
                ),
            values = listOf(ProxyMode.Http, ProxyMode.Tun, ProxyMode.RootTun),
            onValueChange = onProxyModeChange,
        )
    }

    AnimatedVisibility(
        visible = uiState.showServiceOptions,
        enter = if (enableModeTransition) fadeIn() + expandVertically() else EnterTransition.None,
        exit = if (enableModeTransition) fadeOut() + shrinkVertically() else ExitTransition.None,
    ) {
        Column {
            SmallTitle(MLang.NetworkSettings.Section.VpnOptions)
            Card(modifier = Modifier.animateContentSize()) {
                AnimatedContent(
                    targetState = uiState.configuredMode,
                    transitionSpec = {
                        if (enableModeTransition) {
                            fadeIn() togetherWith fadeOut()
                        } else {
                            EnterTransition.None togetherWith ExitTransition.None
                        }
                    },
                    label = "network_service_options",
                ) { mode ->
                    when (mode) {
                        ProxyMode.Http -> HttpProxyInfo()
                        ProxyMode.Tun ->
                            TunServiceOptions(
                                bypassPrivateNetwork = bypassPrivateNetwork,
                                dnsHijack = dnsHijack,
                                allowBypass = allowBypass,
                                enableIPv6 = enableIPv6,
                                systemProxy = systemProxy,
                                tunStack = tunStack,
                                onBypassPrivateNetworkChange = onBypassPrivateNetworkChange,
                                onDnsHijackChange = onDnsHijackChange,
                                onAllowBypassChange = onAllowBypassChange,
                                onEnableIPv6Change = onEnableIPv6Change,
                                onSystemProxyChange = onSystemProxyChange,
                                onTunStackChange = onTunStackChange,
                            )

                        ProxyMode.RootTun ->
                            RootTunServiceOptions(
                                bypassPrivateNetwork = bypassPrivateNetwork,
                                dnsHijack = dnsHijack,
                                enableIPv6 = enableIPv6,
                                tunStack = tunStack,
                                rootTunAutoRoute = rootTunAutoRoute,
                                rootTunStrictRoute = rootTunStrictRoute,
                                rootTunAutoRedirect = rootTunAutoRedirect,
                                rootTunDnsMode = rootTunDnsMode,
                                rootTunIfNameDraft = rootTunIfNameDraft,
                                rootTunMtuDraft = rootTunMtuDraft,
                                rootTunIncludeAndroidUserDraft = rootTunIncludeAndroidUserDraft,
                                rootTunRouteExcludeAddressDraft = rootTunRouteExcludeAddressDraft,
                                rootTunFakeIpRangeDraft = rootTunFakeIpRangeDraft,
                                rootTunFakeIpRange6Draft = rootTunFakeIpRange6Draft,
                                showFakeIpRange = uiState.showFakeIpRange,
                                onBypassPrivateNetworkChange = onBypassPrivateNetworkChange,
                                onDnsHijackChange = onDnsHijackChange,
                                onEnableIPv6Change = onEnableIPv6Change,
                                onTunStackChange = onTunStackChange,
                                onRootTunAutoRouteChange = onRootTunAutoRouteChange,
                                onRootTunStrictRouteChange = onRootTunStrictRouteChange,
                                onRootTunAutoRedirectChange = onRootTunAutoRedirectChange,
                                onRootTunDnsModeChange = onRootTunDnsModeChange,
                                onRootTunIfNameDraftChange = onRootTunIfNameDraftChange,
                                onRootTunMtuDraftChange = onRootTunMtuDraftChange,
                                onRootTunIncludeAndroidUserDraftChange =
                                    onRootTunIncludeAndroidUserDraftChange,
                                onRootTunRouteExcludeAddressDraftChange =
                                    onRootTunRouteExcludeAddressDraftChange,
                                onRootTunFakeIpRangeDraftChange = onRootTunFakeIpRangeDraftChange,
                                onRootTunFakeIpRange6DraftChange = onRootTunFakeIpRange6DraftChange,
                                commitRootTunIfName = commitRootTunIfName,
                                commitRootTunMtu = commitRootTunMtu,
                                commitRootTunIncludeAndroidUser = commitRootTunIncludeAndroidUser,
                                commitRootTunRouteExcludeAddress = commitRootTunRouteExcludeAddress,
                                commitRootTunFakeIpRange = commitRootTunFakeIpRange,
                                commitRootTunFakeIpRange6 = commitRootTunFakeIpRange6,
                            )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProxyOptionsSection(
    uiState: NetworkSettingsUiState,
    accessControlMode: AccessControlMode,
    onAccessControlModeChange: (AccessControlMode) -> Unit,
    onManageAccessControl: () -> Unit,
) {
    SmallTitle(MLang.NetworkSettings.Section.ProxyOptions)
    Card {
        if (uiState.showAccessControlMode) {
            EnumSelector(
                title = MLang.NetworkSettings.ProxyOptions.AccessControlModeTitle,
                currentValue = accessControlMode,
                items =
                    listOf(
                        MLang.NetworkSettings.ProxyOptions.AllowAll,
                        MLang.NetworkSettings.ProxyOptions.AllowSelected,
                        MLang.NetworkSettings.ProxyOptions.RejectSelected,
                    ),
                values = AccessControlMode.entries,
                onValueChange = onAccessControlModeChange,
            )
        }
        ConfigSettingRow(
            title = MLang.NetworkSettings.ProxyOptions.ManageAccessControlTitle,
            summary = MLang.NetworkSettings.ProxyOptions.ManageAccessControlSummary,
            tone = SemanticTone.Info,
            showDivider = false,
            onClick = onManageAccessControl,
        )
    }
}

@Composable
private fun HttpProxyInfo() {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text(
            text = MLang.NetworkSettings.HttpMode.InfoTitle,
            style = MiuixTheme.textStyles.body1,
            color = MiuixTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = MLang.NetworkSettings.HttpMode.InfoSummary,
            style = MiuixTheme.textStyles.body2,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        )
    }
}

@Composable
private fun TunServiceOptions(
    bypassPrivateNetwork: Boolean,
    dnsHijack: Boolean,
    allowBypass: Boolean,
    enableIPv6: Boolean,
    systemProxy: Boolean,
    tunStack: TunStack,
    onBypassPrivateNetworkChange: (Boolean) -> Unit,
    onDnsHijackChange: (Boolean) -> Unit,
    onAllowBypassChange: (Boolean) -> Unit,
    onEnableIPv6Change: (Boolean) -> Unit,
    onSystemProxyChange: (Boolean) -> Unit,
    onTunStackChange: (TunStack) -> Unit,
) {
    CommonTunServiceOptions(
        bypassPrivateNetwork = bypassPrivateNetwork,
        dnsHijack = dnsHijack,
        enableIPv6 = enableIPv6,
        tunStack = tunStack,
        onBypassPrivateNetworkChange = onBypassPrivateNetworkChange,
        onDnsHijackChange = onDnsHijackChange,
        onEnableIPv6Change = onEnableIPv6Change,
        onTunStackChange = onTunStackChange,
        extraOptions = {
            SuperSwitch(
                title = MLang.NetworkSettings.VpnOptions.AllowBypassTitle,
                summary = MLang.NetworkSettings.VpnOptions.AllowBypassSummary,
                checked = allowBypass,
                onCheckedChange = onAllowBypassChange,
            )
            SuperSwitch(
                title = MLang.NetworkSettings.VpnOptions.SystemProxyTitle,
                summary = MLang.NetworkSettings.VpnOptions.SystemProxySummary,
                checked = systemProxy,
                onCheckedChange = onSystemProxyChange,
            )
        },
    )
}

@Composable
private fun RootTunServiceOptions(
    bypassPrivateNetwork: Boolean,
    dnsHijack: Boolean,
    enableIPv6: Boolean,
    tunStack: TunStack,
    rootTunAutoRoute: Boolean,
    rootTunStrictRoute: Boolean,
    rootTunAutoRedirect: Boolean,
    rootTunDnsMode: RootTunDnsMode,
    rootTunIfNameDraft: String,
    rootTunMtuDraft: String,
    rootTunIncludeAndroidUserDraft: String,
    rootTunRouteExcludeAddressDraft: String,
    rootTunFakeIpRangeDraft: String,
    rootTunFakeIpRange6Draft: String,
    showFakeIpRange: Boolean,
    onBypassPrivateNetworkChange: (Boolean) -> Unit,
    onDnsHijackChange: (Boolean) -> Unit,
    onEnableIPv6Change: (Boolean) -> Unit,
    onTunStackChange: (TunStack) -> Unit,
    onRootTunAutoRouteChange: (Boolean) -> Unit,
    onRootTunStrictRouteChange: (Boolean) -> Unit,
    onRootTunAutoRedirectChange: (Boolean) -> Unit,
    onRootTunDnsModeChange: (RootTunDnsMode) -> Unit,
    onRootTunIfNameDraftChange: (String) -> Unit,
    onRootTunMtuDraftChange: (String) -> Unit,
    onRootTunIncludeAndroidUserDraftChange: (String) -> Unit,
    onRootTunRouteExcludeAddressDraftChange: (String) -> Unit,
    onRootTunFakeIpRangeDraftChange: (String) -> Unit,
    onRootTunFakeIpRange6DraftChange: (String) -> Unit,
    commitRootTunIfName: () -> Unit,
    commitRootTunMtu: () -> Unit,
    commitRootTunIncludeAndroidUser: () -> Unit,
    commitRootTunRouteExcludeAddress: () -> Unit,
    commitRootTunFakeIpRange: () -> Unit,
    commitRootTunFakeIpRange6: () -> Unit,
) {
    CommonTunServiceOptions(
        bypassPrivateNetwork = bypassPrivateNetwork,
        dnsHijack = dnsHijack,
        enableIPv6 = enableIPv6,
        tunStack = tunStack,
        onBypassPrivateNetworkChange = onBypassPrivateNetworkChange,
        onDnsHijackChange = onDnsHijackChange,
        onEnableIPv6Change = onEnableIPv6Change,
        onTunStackChange = onTunStackChange,
        extraOptions = {
            RootTunAdvancedOptions(
                rootTunAutoRoute = rootTunAutoRoute,
                rootTunStrictRoute = rootTunStrictRoute,
                rootTunAutoRedirect = rootTunAutoRedirect,
                rootTunDnsMode = rootTunDnsMode,
                rootTunIfNameDraft = rootTunIfNameDraft,
                rootTunMtuDraft = rootTunMtuDraft,
                rootTunIncludeAndroidUserDraft = rootTunIncludeAndroidUserDraft,
                rootTunRouteExcludeAddressDraft = rootTunRouteExcludeAddressDraft,
                rootTunFakeIpRangeDraft = rootTunFakeIpRangeDraft,
                rootTunFakeIpRange6Draft = rootTunFakeIpRange6Draft,
                showFakeIpRange = showFakeIpRange,
                onRootTunAutoRouteChange = onRootTunAutoRouteChange,
                onRootTunStrictRouteChange = onRootTunStrictRouteChange,
                onRootTunAutoRedirectChange = onRootTunAutoRedirectChange,
                onRootTunDnsModeChange = onRootTunDnsModeChange,
                onRootTunIfNameDraftChange = onRootTunIfNameDraftChange,
                onRootTunMtuDraftChange = onRootTunMtuDraftChange,
                onRootTunIncludeAndroidUserDraftChange = onRootTunIncludeAndroidUserDraftChange,
                onRootTunRouteExcludeAddressDraftChange = onRootTunRouteExcludeAddressDraftChange,
                onRootTunFakeIpRangeDraftChange = onRootTunFakeIpRangeDraftChange,
                onRootTunFakeIpRange6DraftChange = onRootTunFakeIpRange6DraftChange,
                commitRootTunIfName = commitRootTunIfName,
                commitRootTunMtu = commitRootTunMtu,
                commitRootTunIncludeAndroidUser = commitRootTunIncludeAndroidUser,
                commitRootTunRouteExcludeAddress = commitRootTunRouteExcludeAddress,
                commitRootTunFakeIpRange = commitRootTunFakeIpRange,
                commitRootTunFakeIpRange6 = commitRootTunFakeIpRange6,
            )
        },
    )
}

@Composable
private fun RootTunAdvancedOptions(
    rootTunAutoRoute: Boolean,
    rootTunStrictRoute: Boolean,
    rootTunAutoRedirect: Boolean,
    rootTunDnsMode: RootTunDnsMode,
    rootTunIfNameDraft: String,
    rootTunMtuDraft: String,
    rootTunIncludeAndroidUserDraft: String,
    rootTunRouteExcludeAddressDraft: String,
    rootTunFakeIpRangeDraft: String,
    rootTunFakeIpRange6Draft: String,
    showFakeIpRange: Boolean,
    onRootTunAutoRouteChange: (Boolean) -> Unit,
    onRootTunStrictRouteChange: (Boolean) -> Unit,
    onRootTunAutoRedirectChange: (Boolean) -> Unit,
    onRootTunDnsModeChange: (RootTunDnsMode) -> Unit,
    onRootTunIfNameDraftChange: (String) -> Unit,
    onRootTunMtuDraftChange: (String) -> Unit,
    onRootTunIncludeAndroidUserDraftChange: (String) -> Unit,
    onRootTunRouteExcludeAddressDraftChange: (String) -> Unit,
    onRootTunFakeIpRangeDraftChange: (String) -> Unit,
    onRootTunFakeIpRange6DraftChange: (String) -> Unit,
    commitRootTunIfName: () -> Unit,
    commitRootTunMtu: () -> Unit,
    commitRootTunIncludeAndroidUser: () -> Unit,
    commitRootTunRouteExcludeAddress: () -> Unit,
    commitRootTunFakeIpRange: () -> Unit,
    commitRootTunFakeIpRange6: () -> Unit,
) {
    var editDialog by rememberSaveable { mutableStateOf<RootTunEditDialogState?>(null) }
    var advancedExpanded by rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
        ConfigSettingRow(
            title = MLang.NetworkSettings.Section.RootTunAdvanced,
            summary =
                if (advancedExpanded)
                    MLang.NetworkSettings.RootTun.IfNameTitle +
                        " · " +
                        MLang.NetworkSettings.RootTun.MtuTitle +
                        " …"
                else
                    MLang.NetworkSettings.RootTun.IfNameTitle +
                        " · " +
                        MLang.NetworkSettings.RootTun.MtuTitle +
                        " · " +
                        MLang.NetworkSettings.RootTun.AutoRouteTitle +
                        " …",
            tone = SemanticTone.Warning,
            onClick = { advancedExpanded = !advancedExpanded },
        )
        AnimatedVisibility(
            visible = advancedExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                ConfigSettingRow(
                    title = MLang.NetworkSettings.RootTun.IfNameTitle,
                    summary =
                        rootTunIfNameDraft.ifBlank { MLang.NetworkSettings.RootTun.IfNameSummary },
                    onClick = { editDialog = RootTunEditDialogState.IfName },
                )
                ConfigSettingRow(
                    title = MLang.NetworkSettings.RootTun.MtuTitle,
                    summary = rootTunMtuDraft.ifBlank { MLang.NetworkSettings.RootTun.MtuSummary },
                    onClick = { editDialog = RootTunEditDialogState.Mtu },
                )
                ConfigSettingRow(
                    title = MLang.NetworkSettings.RootTun.AndroidUsersTitle,
                    summary =
                        rootTunIncludeAndroidUserDraft.ifBlank {
                            MLang.NetworkSettings.RootTun.AndroidUsersPlaceholder
                        },
                    onClick = { editDialog = RootTunEditDialogState.AndroidUsers },
                )
                ConfigSettingRow(
                    title = MLang.NetworkSettings.RootTun.RouteExcludesTitle,
                    summary =
                        rootTunRouteExcludeAddressDraft.ifBlank {
                            MLang.NetworkSettings.RootTun.RouteExcludesPlaceholder
                        },
                    onClick = { editDialog = RootTunEditDialogState.RouteExcludeAddress },
                )
                SuperSwitch(
                    title = MLang.NetworkSettings.RootTun.AutoRouteTitle,
                    summary = MLang.NetworkSettings.RootTun.AutoRouteSummary,
                    checked = rootTunAutoRoute,
                    onCheckedChange = onRootTunAutoRouteChange,
                )
                SuperSwitch(
                    title = MLang.NetworkSettings.RootTun.StrictRouteTitle,
                    summary = MLang.NetworkSettings.RootTun.StrictRouteSummary,
                    checked = rootTunStrictRoute,
                    onCheckedChange = onRootTunStrictRouteChange,
                )
                SuperSwitch(
                    title = MLang.NetworkSettings.RootTun.AutoRedirectTitle,
                    summary = MLang.NetworkSettings.RootTun.AutoRedirectSummary,
                    checked = rootTunAutoRedirect,
                    onCheckedChange = onRootTunAutoRedirectChange,
                )
                EnumSelector(
                    title = MLang.NetworkSettings.RootTun.DnsModeTitle,
                    summary = MLang.NetworkSettings.RootTun.DnsModeSummary,
                    currentValue = rootTunDnsMode,
                    items =
                        listOf(
                            MLang.NetworkSettings.RootTun.DnsModeRedirHost,
                            MLang.NetworkSettings.RootTun.DnsModeFakeIp,
                        ),
                    values = RootTunDnsMode.entries,
                    onValueChange = onRootTunDnsModeChange,
                )
                AnimatedVisibility(
                    visible = showFakeIpRange,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        ConfigSettingRow(
                            title = MLang.NetworkSettings.RootTun.FakeIpRangeTitle,
                            summary =
                                rootTunFakeIpRangeDraft.ifBlank {
                                    MLang.NetworkSettings.RootTun.FakeIpRangeSummary
                                },
                            onClick = { editDialog = RootTunEditDialogState.FakeIpRange },
                        )
                        ConfigSettingRow(
                            title = MLang.NetworkSettings.RootTun.FakeIpRange6Title,
                            summary =
                                rootTunFakeIpRange6Draft.ifBlank {
                                    MLang.NetworkSettings.RootTun.FakeIpRange6Summary
                                },
                            showDivider = false,
                            onClick = { editDialog = RootTunEditDialogState.FakeIpRange6 },
                        )
                    }
                }
            }
        }
    }

    when (editDialog) {
        RootTunEditDialogState.IfName ->
            RootTunTextEditDialog(
                title = MLang.NetworkSettings.RootTun.IfNameTitle,
                value = rootTunIfNameDraft,
                onValueChange = onRootTunIfNameDraftChange,
                onDismiss = { editDialog = null },
                onCommit = {
                    commitRootTunIfName()
                    editDialog = null
                },
            )

        RootTunEditDialogState.Mtu ->
            RootTunTextEditDialog(
                title = MLang.NetworkSettings.RootTun.MtuTitle,
                value = rootTunMtuDraft,
                onValueChange = onRootTunMtuDraftChange,
                onDismiss = { editDialog = null },
                onCommit = {
                    commitRootTunMtu()
                    editDialog = null
                },
                keyboardOptions =
                    KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            )

        RootTunEditDialogState.AndroidUsers ->
            RootTunTextEditDialog(
                title = MLang.NetworkSettings.RootTun.AndroidUsersTitle,
                value = rootTunIncludeAndroidUserDraft,
                onValueChange = onRootTunIncludeAndroidUserDraftChange,
                onDismiss = { editDialog = null },
                onCommit = {
                    commitRootTunIncludeAndroidUser()
                    editDialog = null
                },
            )

        RootTunEditDialogState.RouteExcludeAddress ->
            RootTunTextEditDialog(
                title = MLang.NetworkSettings.RootTun.RouteExcludesTitle,
                value = rootTunRouteExcludeAddressDraft,
                onValueChange = onRootTunRouteExcludeAddressDraftChange,
                onDismiss = { editDialog = null },
                onCommit = {
                    commitRootTunRouteExcludeAddress()
                    editDialog = null
                },
            )

        RootTunEditDialogState.FakeIpRange ->
            RootTunTextEditDialog(
                title = MLang.NetworkSettings.RootTun.FakeIpRangeTitle,
                value = rootTunFakeIpRangeDraft,
                onValueChange = onRootTunFakeIpRangeDraftChange,
                onDismiss = { editDialog = null },
                onCommit = {
                    commitRootTunFakeIpRange()
                    editDialog = null
                },
            )

        RootTunEditDialogState.FakeIpRange6 ->
            RootTunTextEditDialog(
                title = MLang.NetworkSettings.RootTun.FakeIpRange6Title,
                value = rootTunFakeIpRange6Draft,
                onValueChange = onRootTunFakeIpRange6DraftChange,
                onDismiss = { editDialog = null },
                onCommit = {
                    commitRootTunFakeIpRange6()
                    editDialog = null
                },
            )

        null -> Unit
    }
}

@Composable
private fun CommonTunServiceOptions(
    bypassPrivateNetwork: Boolean,
    dnsHijack: Boolean,
    enableIPv6: Boolean,
    tunStack: TunStack,
    onBypassPrivateNetworkChange: (Boolean) -> Unit,
    onDnsHijackChange: (Boolean) -> Unit,
    onEnableIPv6Change: (Boolean) -> Unit,
    onTunStackChange: (TunStack) -> Unit,
    extraOptions: @Composable ColumnScope.() -> Unit = {},
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SuperSwitch(
            title = MLang.NetworkSettings.VpnOptions.BypassPrivateTitle,
            summary = MLang.NetworkSettings.VpnOptions.BypassPrivateSummary,
            checked = bypassPrivateNetwork,
            onCheckedChange = onBypassPrivateNetworkChange,
        )
        SuperSwitch(
            title = MLang.NetworkSettings.VpnOptions.DnsHijackTitle,
            summary = MLang.NetworkSettings.VpnOptions.DnsHijackSummary,
            checked = dnsHijack,
            onCheckedChange = onDnsHijackChange,
        )
        SuperSwitch(
            title = MLang.NetworkSettings.VpnOptions.EnableIpv6Title,
            summary = MLang.NetworkSettings.VpnOptions.EnableIpv6Summary,
            checked = enableIPv6,
            onCheckedChange = onEnableIPv6Change,
        )
        EnumSelector(
            title = MLang.NetworkSettings.ProxyOptions.TunStackTitle,
            currentValue = tunStack,
            items =
                listOf(
                    MLang.NetworkSettings.ProxyOptions.TunStackSystem,
                    MLang.NetworkSettings.ProxyOptions.TunStackGvisor,
                    MLang.NetworkSettings.ProxyOptions.TunStackMixed,
                ),
            values = TunStack.entries,
            onValueChange = onTunStackChange,
        )
        extraOptions()
    }
}

@Composable
private fun RootTunTextEditDialog(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onCommit: () -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
) {
    val focusManager = LocalFocusManager.current
    AppDialog(
        show = true,
        modifier = Modifier,
        title = title,
        summary = null,
        onDismissRequest = onDismiss,
        content = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = keyboardOptions,
                    keyboardActions =
                        KeyboardActions(
                            onDone = {
                                onCommit()
                                focusManager.clearFocus()
                            }
                        ),
                )
                DialogButtonRow(
                    onCancel = onDismiss,
                    onConfirm = {
                        onCommit()
                        onDismiss()
                    },
                    cancelText = MLang.ProfilesPage.Button.Cancel,
                    confirmText = MLang.ProfilesPage.Button.Confirm,
                )
            }
        },
    )
}

private enum class RootTunEditDialogState {
    IfName,
    Mtu,
    AndroidUsers,
    RouteExcludeAddress,
    FakeIpRange,
    FakeIpRange6,
}
