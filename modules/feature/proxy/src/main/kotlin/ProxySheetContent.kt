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

package com.github.nomadboxlab.monadbox

import androidx.compose.animation.*
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.nomadboxlab.monadbox.common.util.toast
import com.github.nomadboxlab.monadbox.core.model.Proxy
import com.github.nomadboxlab.monadbox.presentation.component.AppBottomSheetAction
import com.github.nomadboxlab.monadbox.presentation.component.AppBottomSheetIconAction
import com.github.nomadboxlab.monadbox.presentation.icon.MonadIcons
import com.github.nomadboxlab.monadbox.presentation.icon.monad.`List-chevrons-up-down`
import com.github.nomadboxlab.monadbox.presentation.icon.monad.Speed
import com.github.nomadboxlab.monadbox.presentation.screen.node.NodeGroupSheetContent
import com.github.nomadboxlab.monadbox.presentation.screen.node.NodeSheetContent
import com.github.nomadboxlab.monadbox.presentation.screen.node.NodeSortPopup
import com.github.nomadboxlab.monadbox.presentation.theme.AnimationSpecs
import com.github.nomadboxlab.monadbox.presentation.theme.LocalWindowAdaptiveInfo
import com.github.nomadboxlab.monadbox.presentation.util.WindowBlurEffect
import com.github.nomadboxlab.monadbox.presentation.util.resolveAdaptiveProxyDisplayMode
import com.github.nomadboxlab.monadbox.presentation.viewmodel.ProxyViewModel
import dev.oom_wg.purejoy.mlang.MLang
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.extra.WindowBottomSheet
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.theme.MiuixTheme

private const val POPUP_ANIMATION_DURATION_MS = 320

@Composable
fun ProxySheetContent(onDismiss: () -> Unit, proxyViewModel: ProxyViewModel = koinViewModel()) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val windowAdaptiveInfo = LocalWindowAdaptiveInfo.current
    val proxyGroups by proxyViewModel.sortedProxyGroups.collectAsStateWithLifecycle()
    val uiState by proxyViewModel.uiState.collectAsStateWithLifecycle()
    val testingGroupNames by proxyViewModel.testingGroupNames.collectAsStateWithLifecycle()
    val testingProxyNames by proxyViewModel.testingProxyNames.collectAsStateWithLifecycle()
    val sortMode by proxyViewModel.sortMode.collectAsStateWithLifecycle()
    val adaptiveDisplayMode =
        remember(windowAdaptiveInfo.windowWidth, windowAdaptiveInfo.prefersTwoPaneContent) {
            resolveAdaptiveProxyDisplayMode(
                maxWidth = windowAdaptiveInfo.windowWidth,
                prefersTwoPane = windowAdaptiveInfo.prefersTwoPaneContent,
            )
        }

    val showSheet = rememberSaveable { mutableStateOf(true) }
    val showSortPopup = rememberSaveable { mutableStateOf(false) }
    var selectedGroupName by rememberSaveable { mutableStateOf<String?>(null) }
    val blurRadius by
        animateIntAsState(
            targetValue = if (showSheet.value) 30 else 0,
            animationSpec =
                tween(durationMillis = POPUP_ANIMATION_DURATION_MS, easing = AnimationSpecs.Legacy),
            label = "notification_proxy_popup_blur",
        )

    val groupsByName = remember(proxyGroups) { proxyGroups.associateBy { it.name } }
    val selectedGroup by
        remember(selectedGroupName, groupsByName) {
            derivedStateOf {
                val name = selectedGroupName ?: return@derivedStateOf null
                groupsByName[name]
            }
        }

    DisposableEffect(Unit) {
        proxyViewModel.ensureCoreLoaded(true)
        onDispose { proxyViewModel.ensureCoreLoaded(false) }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            context.toast(error)
            proxyViewModel.clearError()
        }
    }

    LaunchedEffect(uiState.message) {
        uiState.message?.let { message ->
            context.toast(message)
            proxyViewModel.clearMessage()
        }
    }

    LaunchedEffect(proxyGroups, selectedGroupName) {
        if (selectedGroupName != null && selectedGroup == null) {
            selectedGroupName = null
        }
    }

    val dismissSheet =
        remember(onDismiss) {
            {
                showSortPopup.value = false
                showSheet.value = false
                scope.launch {
                    delay(POPUP_ANIMATION_DURATION_MS.toLong())
                    onDismiss()
                }
            }
        }

    WindowBlurEffect(useBlur = true, blurRadius = blurRadius)

    WindowBottomSheet(
        show = showSheet.value,
        title = selectedGroup?.name ?: MLang.Proxy.Title,
        backgroundColor = MiuixTheme.colorScheme.surface,
        startAction = {
            if (selectedGroup != null) {
                AppBottomSheetIconAction(
                    action =
                        AppBottomSheetAction(
                            icon = MiuixIcons.Back,
                            contentDescription = MLang.Proxy.Action.Back,
                            onClick = { selectedGroupName = null },
                        )
                )
            } else {
                Box {
                    AppBottomSheetIconAction(
                        action =
                            AppBottomSheetAction(
                                icon = MonadIcons.`List-chevrons-up-down`,
                                contentDescription = MLang.Proxy.Action.SortMode,
                                onClick = { showSortPopup.value = true },
                            )
                    )
                    NodeSortPopup(
                        show = showSortPopup,
                        onDismiss = { showSortPopup.value = false },
                        sortMode = sortMode,
                        alignment = PopupPositionProvider.Align.BottomStart,
                        onSortSelected = proxyViewModel::setSortMode,
                    )
                }
            }
        },
        endAction = {
            AppBottomSheetIconAction(
                action =
                    AppBottomSheetAction(
                        icon = MonadIcons.Speed,
                        contentDescription = MLang.Proxy.Action.Test,
                        onClick = {
                            val group = selectedGroup
                            if (group == null) {
                                proxyViewModel.testDelay()
                            } else {
                                proxyViewModel.testDelay(group.name)
                            }
                        },
                    )
            )
        },
        onDismissRequest = { dismissSheet() },
        insideMargin = DpSize(16.dp, 16.dp),
        enableNestedScroll = false,
    ) {
        AnimatedContent(
            targetState = selectedGroupName,
            transitionSpec = {
                if (targetState != null) {
                    (slideInHorizontally(
                        animationSpec =
                            tween(
                                durationMillis = AnimationSpecs.Proxy.SheetSlideInDuration,
                                easing = AnimationSpecs.Legacy,
                            ),
                        initialOffsetX = { it },
                    ) +
                        fadeIn(
                            animationSpec =
                                tween(durationMillis = AnimationSpecs.Proxy.SheetFadeInDuration)
                        )) togetherWith
                        (slideOutHorizontally(
                            animationSpec =
                                tween(
                                    durationMillis = AnimationSpecs.Proxy.SheetSlideOutDuration,
                                    easing = AnimationSpecs.Legacy,
                                ),
                            targetOffsetX = { -it / 3 },
                        ) +
                            fadeOut(
                                animationSpec =
                                    tween(
                                        durationMillis = AnimationSpecs.Proxy.SheetFadeOutDuration
                                    )
                            ))
                } else {
                    (slideInHorizontally(
                        animationSpec =
                            tween(
                                durationMillis = AnimationSpecs.Proxy.SheetSlideOutDuration,
                                easing = AnimationSpecs.Legacy,
                            ),
                        initialOffsetX = { -it / 3 },
                    ) +
                        fadeIn(
                            animationSpec =
                                tween(
                                    durationMillis = AnimationSpecs.Proxy.SheetFadeInDuration - 20
                                )
                        )) togetherWith
                        (slideOutHorizontally(
                            animationSpec =
                                tween(
                                    durationMillis = AnimationSpecs.Proxy.SheetSlideInDuration - 20,
                                    easing = AnimationSpecs.Legacy,
                                ),
                            targetOffsetX = { it },
                        ) +
                            fadeOut(
                                animationSpec =
                                    tween(
                                        durationMillis = AnimationSpecs.Proxy.SheetFadeOutDuration
                                    )
                            ))
                }
            },
            label = "notification_node_sheet_content",
        ) { targetGroupName ->
            val group = targetGroupName?.let { name -> proxyGroups.find { it.name == name } }
            if (group == null) {
                NodeGroupSheetContent(
                    groups = proxyGroups,
                    onGroupClick = { targetGroup -> selectedGroupName = targetGroup.name },
                    testingGroupNames = testingGroupNames,
                )
            } else {
                NodeSheetContent(
                    group = group,
                    displayMode = adaptiveDisplayMode,
                    isDelayTesting = testingGroupNames.contains(group.name),
                    testingProxyNames = testingProxyNames,
                    onSelectProxy = { proxyName ->
                        if (group.type == Proxy.Type.Selector) {
                            proxyViewModel.selectProxy(group.name, proxyName)
                        } else {
                            proxyViewModel.testDelay(group.name)
                        }
                    },
                    onTestDelay = { proxyViewModel.testDelay(group.name) },
                    onTestProxyDelay = { proxyName -> proxyViewModel.testProxyDelay(proxyName) },
                )
            }
        }
    }
}
