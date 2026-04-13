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

package com.github.yumelira.yumebox.presentation.screen.node

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import com.github.yumelira.yumebox.core.model.Proxy
import com.github.yumelira.yumebox.domain.model.ProxyDisplayMode
import com.github.yumelira.yumebox.presentation.theme.DefaultSpacing
import com.github.yumelira.yumebox.presentation.theme.ProxyNodeGridLayoutDefaults
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

internal fun LazyListScope.nodeGridItems(
    proxies: List<Proxy>,
    selectedProxyName: String,
    onProxyClick: ((String) -> Unit)? = null,
    isDelayTesting: Boolean = false,
    testingProxyNames: Set<String> = emptySet(),
    onSingleNodeTestClick: ((String) -> Unit)? = null,
    outerHorizontalPadding: Dp = DefaultSpacing.none,
    itemVerticalPadding: Dp = DefaultSpacing.none,
    singleNodeTestEnabled: Boolean = true,
    interactionRole: Role = Role.RadioButton,
    onProxyClickLabel: String? = null,
    actionChipLabel: String? = null,
) {
    items(items = proxies, key = { it.name }, contentType = { "NodeCard1" }) { proxy ->
        NodeCard(
            proxy = proxy,
            isSelected = proxy.name == selectedProxyName,
            onClick = onProxyClick,
            isDelayTesting = isDelayTesting,
            isThisProxyTesting = proxy.name in testingProxyNames,
            onSingleNodeTestClick = onSingleNodeTestClick?.let { { it(proxy.name) } },
            showCountryFlag = true,
            singleNodeTestEnabled = singleNodeTestEnabled,
            interactionRole = interactionRole,
            onClickLabel = onProxyClickLabel,
            actionChipLabel = actionChipLabel,
            modifier =
                Modifier.animateItem()
                    .padding(horizontal = outerHorizontalPadding, vertical = itemVerticalPadding),
        )
    }
}

internal fun LazyListScope.adaptiveNodeGridItems(
    proxies: List<Proxy>,
    columns: Int,
    selectedProxyName: String,
    onProxyClick: ((String) -> Unit)? = null,
    isDelayTesting: Boolean = false,
    testingProxyNames: Set<String> = emptySet(),
    onSingleNodeTestClick: ((String) -> Unit)? = null,
    outerHorizontalPadding: Dp = DefaultSpacing.none,
    itemVerticalPadding: Dp = DefaultSpacing.none,
    singleNodeTestEnabled: Boolean = true,
    interactionRole: Role = Role.RadioButton,
    onProxyClickLabel: String? = null,
    actionChipLabel: String? = null,
) {
    if (columns <= 1) {
        nodeGridItems(
            proxies = proxies,
            selectedProxyName = selectedProxyName,
            onProxyClick = onProxyClick,
            isDelayTesting = isDelayTesting,
            testingProxyNames = testingProxyNames,
            onSingleNodeTestClick = onSingleNodeTestClick,
            outerHorizontalPadding = outerHorizontalPadding,
            itemVerticalPadding = itemVerticalPadding,
            singleNodeTestEnabled = singleNodeTestEnabled,
            interactionRole = interactionRole,
            onProxyClickLabel = onProxyClickLabel,
            actionChipLabel = actionChipLabel,
        )
        return
    }

    val rows = proxies.chunked(columns)
    items(
        items = rows,
        key = { row -> row.joinToString(separator = "|") { proxy -> proxy.name } },
        contentType = { "AdaptiveNodeCardRow" },
    ) { row ->
        Row(
            modifier =
                Modifier.fillMaxWidth()
                    .animateItem()
                    .padding(horizontal = outerHorizontalPadding, vertical = itemVerticalPadding),
            horizontalArrangement = Arrangement.spacedBy(ProxyNodeGridLayoutDefaults.ColumnSpacing),
        ) {
            row.forEach { proxy ->
                NodeCard(
                    proxy = proxy,
                    isSelected = proxy.name == selectedProxyName,
                    onClick = onProxyClick,
                    isDelayTesting = isDelayTesting,
                    isThisProxyTesting = proxy.name in testingProxyNames,
                    onSingleNodeTestClick = onSingleNodeTestClick?.let { { it(proxy.name) } },
                    showCountryFlag = true,
                    singleNodeTestEnabled = singleNodeTestEnabled,
                    interactionRole = interactionRole,
                    onClickLabel = onProxyClickLabel,
                    actionChipLabel = actionChipLabel,
                    modifier = Modifier.weight(1f),
                )
            }
            repeat(columns - row.size) { Spacer(modifier = Modifier.weight(1f)) }
        }
    }
}

@Composable
internal fun rememberAdaptiveNodeGridColumns(maxWidth: Dp, displayMode: ProxyDisplayMode): Int {
    return remember(maxWidth, displayMode) {
        val adaptiveColumns =
            when {
                maxWidth >= ProxyNodeGridLayoutDefaults.AdaptiveThreeColumnMinWidth -> 3
                maxWidth >= ProxyNodeGridLayoutDefaults.AdaptiveTwoColumnMinWidth -> 2
                else -> 1
            }
        val requestedMinimumColumns = if (displayMode.isSingleColumn) 1 else 2
        adaptiveColumns.coerceAtLeast(requestedMinimumColumns)
    }
}

@Composable
internal fun NodeGrid(
    proxies: List<Proxy>,
    selectedProxyName: String,
    displayMode: ProxyDisplayMode,
    onProxyClick: ((String) -> Unit)? = null,
    isDelayTesting: Boolean = false,
    testingProxyNames: Set<String> = emptySet(),
    onSingleNodeTestClick: ((String) -> Unit)? = null,
    listStateKey: String? = null,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(ProxyNodeGridLayoutDefaults.ContentPadding),
    singleNodeTestEnabled: Boolean = true,
    interactionRole: Role = Role.RadioButton,
    onProxyClickLabel: String? = null,
    actionChipLabel: String? = null,
) {
    val listState = rememberSaveable(listStateKey, saver = LazyListState.Saver) { LazyListState() }
    BoxWithConstraints(modifier = modifier) {
        val columns =
            rememberAdaptiveNodeGridColumns(maxWidth = maxWidth, displayMode = displayMode)

        LazyColumn(
            modifier =
                Modifier.fillMaxWidth()
                    .heightIn(max = maxHeight)
                    .scrollEndHaptic()
                    .overScrollVertical(),
            state = listState,
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(ProxyNodeGridLayoutDefaults.ItemSpacing),
            overscrollEffect = null,
        ) {
            adaptiveNodeGridItems(
                proxies = proxies,
                columns = columns,
                selectedProxyName = selectedProxyName,
                onProxyClick = onProxyClick,
                isDelayTesting = isDelayTesting,
                testingProxyNames = testingProxyNames,
                onSingleNodeTestClick = onSingleNodeTestClick,
                singleNodeTestEnabled = singleNodeTestEnabled,
                interactionRole = interactionRole,
                onProxyClickLabel = onProxyClickLabel,
                actionChipLabel = actionChipLabel,
            )
        }
    }
}
