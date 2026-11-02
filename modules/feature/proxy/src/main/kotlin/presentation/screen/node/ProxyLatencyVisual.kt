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
 */

package com.github.nomadboxlab.monadbox.presentation.screen.node

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.github.nomadboxlab.monadbox.domain.model.ProxyLatencyState
import com.github.nomadboxlab.monadbox.domain.model.toProxyLatencyState
import com.github.nomadboxlab.monadbox.presentation.theme.LocalSemanticColors
import com.github.nomadboxlab.monadbox.presentation.theme.latencyForeground
import dev.oom_wg.purejoy.mlang.MLang
import top.yukonga.miuix.kmp.theme.MiuixTheme

internal data class ProxyLatencyVisual(val label: String, val color: Color)

@Composable
internal fun proxyLatencyVisual(delay: Int?, isTesting: Boolean): ProxyLatencyVisual {
    val semanticColors = LocalSemanticColors.current
    if (isTesting) {
        return ProxyLatencyVisual(
            label = MLang.Proxy.Testing.InProgress,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        )
    }

    val state = delay?.toProxyLatencyState() ?: ProxyLatencyState.Unknown

    return when (state) {
        is ProxyLatencyState.Available ->
            ProxyLatencyVisual(
                label = "${state.delayMs}ms",
                color = semanticColors.latencyForeground(state.delayMs),
            )

        ProxyLatencyState.Timeout ->
            ProxyLatencyVisual(
                label = MLang.Proxy.Selection.Timeout,
                color = semanticColors.danger.foreground,
            )

        ProxyLatencyState.Unknown ->
            ProxyLatencyVisual(
                label = MLang.Proxy.Selection.UnknownLatency,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            )
    }
}
