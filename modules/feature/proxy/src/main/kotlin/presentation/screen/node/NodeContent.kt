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

package com.github.nomadboxlab.monadbox.presentation.screen.node

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.nomadboxlab.monadbox.core.model.Proxy
import com.github.nomadboxlab.monadbox.domain.model.ProxyDisplayMode
import com.github.nomadboxlab.monadbox.domain.model.ProxyGroupInfo
import com.github.nomadboxlab.monadbox.presentation.theme.AppTheme
import com.github.nomadboxlab.monadbox.presentation.theme.LocalWindowAdaptiveInfo
import dev.oom_wg.purejoy.mlang.MLang
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical

val NodeSheetContentPadding = PaddingValues(start = 0.dp, end = 0.dp, top = 8.dp, bottom = 16.dp)

@Composable
internal fun rememberNodeSheetHeight(sheetHeightFraction: Float): Dp {
    val normalized = sheetHeightFraction.coerceIn(0.5f, 0.8f)
    val windowHeight = LocalWindowAdaptiveInfo.current.windowHeight.takeIf { it > 0.dp } ?: 640.dp
    return remember(windowHeight, normalized) { windowHeight * normalized }
}

@Composable
internal fun NodeGroupSheetContent(
    groups: List<ProxyGroupInfo>,
    testingGroupNames: Set<String>,
    onGroupClick: (ProxyGroupInfo) -> Unit,
) {
    val sheetHeightFraction = AppTheme.pageMetrics.proxyNotificationSheetHeightFraction
    val sheetHeight = rememberNodeSheetHeight(sheetHeightFraction)

    LazyColumn(
        modifier = Modifier.fillMaxWidth().height(sheetHeight).overScrollVertical(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = NodeSheetContentPadding,
        overscrollEffect = null,
    ) {
        nodeGroupItems(
            groups = groups,
            onGroupClick = onGroupClick,
            testingGroupNames = testingGroupNames,
            itemVerticalPadding = 0.dp,
        )
    }
}

@Composable
fun NodeSheetContent(
    group: ProxyGroupInfo,
    displayMode: ProxyDisplayMode,
    onSelectProxy: (String) -> Unit,
    isDelayTesting: Boolean,
    testingProxyNames: Set<String>,
    onTestDelay: () -> Unit,
    onTestProxyDelay: (String) -> Unit,
    singleNodeTestEnabled: Boolean = true,
) {
    val sheetHeightFraction = AppTheme.pageMetrics.proxyNotificationSheetHeightFraction
    val sheetHeight = rememberNodeSheetHeight(sheetHeightFraction)

    BoxWithConstraints(modifier = Modifier.fillMaxWidth().height(sheetHeight)) {
        val columns =
            rememberAdaptiveNodeGridColumns(maxWidth = maxWidth, displayMode = displayMode)

        LazyColumn(
            modifier = Modifier.fillMaxWidth().heightIn(max = maxHeight).overScrollVertical(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = NodeSheetContentPadding,
            overscrollEffect = null,
        ) {
            item(key = "__refresh_indicator__") {
                AnimatedVisibility(
                    visible = isDelayTesting,
                    enter =
                        expandVertically(
                            animationSpec = tween(durationMillis = 200),
                            expandFrom = Alignment.Top,
                        ) + fadeIn(animationSpec = tween(durationMillis = 150)),
                    exit =
                        shrinkVertically(
                            animationSpec = tween(durationMillis = 200),
                            shrinkTowards = Alignment.Top,
                        ) + fadeOut(animationSpec = tween(durationMillis = 150)),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        InfiniteProgressIndicator(modifier = Modifier.size(24.dp))
                        Text(
                            text = MLang.Proxy.Testing.InProgress,
                            style = MiuixTheme.textStyles.footnote1,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        )
                    }
                }
            }

            adaptiveNodeGridItems(
                proxies = group.proxies,
                columns = columns,
                selectedProxyName = group.now,
                onProxyClick = { proxyName ->
                    if (group.type == Proxy.Type.Selector) {
                        onSelectProxy(proxyName)
                    } else {
                        onTestDelay()
                    }
                },
                isDelayTesting = isDelayTesting,
                testingProxyNames = testingProxyNames,
                onSingleNodeTestClick = onTestProxyDelay,
                singleNodeTestEnabled = singleNodeTestEnabled,
            )
        }
    }
}
