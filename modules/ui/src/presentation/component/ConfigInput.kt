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

package com.github.nomadboxlab.monadbox.presentation.component

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.github.nomadboxlab.monadbox.presentation.icon.MonadIcons
import com.github.nomadboxlab.monadbox.presentation.icon.monad.Check
import com.github.nomadboxlab.monadbox.presentation.icon.monad.Delete
import com.github.nomadboxlab.monadbox.presentation.icon.monad.Edit
import com.github.nomadboxlab.monadbox.presentation.icon.monad.`Git-merge`
import com.github.nomadboxlab.monadbox.presentation.icon.monad.List
import com.github.nomadboxlab.monadbox.presentation.icon.monad.`Wifi-cog`
import com.github.nomadboxlab.monadbox.presentation.theme.AppTheme
import dev.oom_wg.purejoy.mlang.MLang
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.theme.MiuixTheme

typealias OpenStringListModifiersEditor =
    (
        title: String,
        placeholder: String,
        replaceValue: List<String>?,
        startValue: List<String>?,
        endValue: List<String>?,
        onReplaceChange: (List<String>?) -> Unit,
        onStartChange: (List<String>?) -> Unit,
        onEndChange: (List<String>?) -> Unit,
    ) -> Unit

@Composable
fun PortInputContent(
    title: String,
    value: Int?,
    onValueChange: (Int?) -> Unit,
    imageVector: ImageVector = MonadIcons.`Wifi-cog`,
    unsetLabel: String = MLang.Component.Selector.UseDefault,
) {
    val showDialog = remember { mutableStateOf(false) }
    var textValue by remember { mutableStateOf(value?.toString() ?: "") }
    var inputValue by remember { mutableStateOf("") }

    ConfigSettingRow(
        title = title,
        valueLabel = value?.toString() ?: unsetLabel,
        imageVector = imageVector,
        tone = if (value == null) SemanticTone.Neutral else SemanticTone.Info,
        badgeTone = if (value == null) SemanticTone.Neutral else SemanticTone.Info,
        onClick = {
            textValue = value?.toString() ?: ""
            inputValue = textValue
            showDialog.value = true
        },
    )

    ConfigTextInputDialog(
        show = showDialog,
        title = title,
        textValue = inputValue,
        label = MLang.Component.ConfigInput.PortLabel,
        onTextValueChange = { input -> inputValue = input },
        onClear = { onValueChange(null) },
        onConfirm = {
            val port = inputValue.filter(Char::isDigit).toIntOrNull()
            if (port == null || (port in 1..65535)) {
                onValueChange(port)
            }
            textValue = port?.toString() ?: ""
        },
        onDismiss = { textValue = value?.toString() ?: "" },
    )
}

@Composable
fun StringInputContent(
    title: String,
    value: String?,
    placeholder: String = "",
    imageVector: ImageVector = MonadIcons.Edit,
    unsetLabel: String = MLang.Component.Selector.UseDefault,
    onValueChange: (String?) -> Unit,
) {
    val showDialog = remember { mutableStateOf(false) }
    var textValue by remember { mutableStateOf(value ?: "") }
    val displayValue = value?.takeIf { it.isNotEmpty() }
    val compactValue = displayValue?.takeIf { it.length <= 12 }

    ConfigSettingRow(
        title = title,
        summary = displayValue?.takeIf { compactValue == null },
        valueLabel = compactValue ?: if (displayValue == null) unsetLabel else null,
        imageVector = imageVector,
        tone = if (displayValue == null) SemanticTone.Neutral else SemanticTone.Info,
        badgeTone = if (displayValue == null) SemanticTone.Neutral else SemanticTone.Info,
        onClick = {
            textValue = value ?: ""
            showDialog.value = true
        },
    )

    ConfigTextInputDialog(
        show = showDialog,
        title = title,
        textValue = textValue,
        label = placeholder,
        onTextValueChange = { textValue = it },
        onClear = { onValueChange(null) },
        onConfirm = { onValueChange(textValue.takeIf { it.isNotEmpty() }) },
    )
}

@Composable
fun StringListInputContent(
    title: String,
    value: List<String>?,
    onClick: () -> Unit,
    imageVector: ImageVector = MonadIcons.List,
    unsetLabel: String = MLang.Component.Selector.UseDefault,
) {
    val itemCount = value?.size ?: 0
    ConfigSettingRow(
        title = title,
        summary = value?.firstOrNull(),
        valueLabel =
            if (itemCount > 0) {
                MLang.Component.ConfigInput.CountItems.format(itemCount)
            } else {
                unsetLabel
            },
        imageVector = imageVector,
        tone = if (itemCount > 0) SemanticTone.Info else SemanticTone.Neutral,
        badgeTone = if (itemCount > 0) SemanticTone.Info else SemanticTone.Neutral,
        onClick = onClick,
    )
}

@Composable
fun StringMapInputContent(
    title: String,
    value: Map<String, String>?,
    onClick: () -> Unit,
    imageVector: ImageVector = MonadIcons.`Git-merge`,
    unsetLabel: String = MLang.Component.Selector.UseDefault,
) {
    val itemCount = value?.size ?: 0
    ConfigSettingRow(
        title = title,
        summary = value?.entries?.firstOrNull()?.key,
        valueLabel =
            if (itemCount > 0) {
                MLang.Component.ConfigInput.CountItems.format(itemCount)
            } else {
                unsetLabel
            },
        imageVector = imageVector,
        tone = if (itemCount > 0) SemanticTone.Warning else SemanticTone.Neutral,
        badgeTone = if (itemCount > 0) SemanticTone.Warning else SemanticTone.Neutral,
        onClick = onClick,
    )
}

@Composable
private fun ConfigTextInputDialog(
    show: MutableState<Boolean>,
    title: String,
    textValue: String,
    label: String,
    onTextValueChange: (String) -> Unit,
    onClear: () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: (() -> Unit)? = null,
) {
    if (!show.value) return
    AppDialog(
        show = show.value,
        title = title,
        onDismissRequest = {
            onDismiss?.invoke()
            show.value = false
        },
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TextField(
                value = textValue,
                onValueChange = onTextValueChange,
                label = label,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AppCommandButton(
                    title = MLang.Component.Button.Clear,
                    imageVector = MonadIcons.Delete,
                    onClick = {
                        onClear()
                        show.value = false
                    },
                    modifier = Modifier.weight(1f),
                    tone = SemanticTone.Danger,
                )
                AppCommandButton(
                    title = MLang.Component.Button.Confirm,
                    imageVector = MonadIcons.Check,
                    onClick = {
                        onConfirm()
                        show.value = false
                    },
                    modifier = Modifier.weight(1f),
                    tone = SemanticTone.Brand,
                    highEmphasis = true,
                )
            }
        }
    }
}

@Composable
fun StringListWithModifiersInput(
    title: String,
    replaceValue: List<String>?,
    startValue: List<String>?,
    endValue: List<String>?,
    placeholder: String = "",
    imageVector: ImageVector = MonadIcons.List,
    unsetLabel: String = MLang.Component.Selector.NotModify,
    onReplaceChange: (List<String>?) -> Unit,
    onStartChange: (List<String>?) -> Unit,
    onEndChange: (List<String>?) -> Unit,
    onEditListGroup: OpenStringListModifiersEditor,
) {
    val summary =
        remember(replaceValue, startValue, endValue) {
            val replaceCount = replaceValue?.size ?: 0
            val startCount = startValue?.size ?: 0
            val endCount = endValue?.size ?: 0
            val totalCount = replaceCount + startCount + endCount
            val activeModes = listOf(replaceCount, startCount, endCount).count { it > 0 }

            when {
                totalCount == 0 -> unsetLabel
                activeModes == 1 -> MLang.Component.ConfigInput.CountItems.format(totalCount)
                else ->
                    buildList {
                            if (replaceCount > 0) {
                                add(
                                    "${MLang.Component.Selector.Replace} ${MLang.Component.ConfigInput.CountItems.format(replaceCount)}"
                                )
                            }
                            if (startCount > 0) {
                                add(
                                    "${MLang.Component.Selector.Prepend} ${MLang.Component.ConfigInput.CountItems.format(startCount)}"
                                )
                            }
                            if (endCount > 0) {
                                add(
                                    "${MLang.Component.Selector.Append} ${MLang.Component.ConfigInput.CountItems.format(endCount)}"
                                )
                            }
                        }
                        .joinToString(" · ")
            }
        }

    ConfigSettingRow(
        title = title,
        summary = summary.takeIf { it != unsetLabel },
        valueLabel = if (summary == unsetLabel) summary else null,
        imageVector = imageVector,
        tone = if (summary == unsetLabel) SemanticTone.Neutral else SemanticTone.Info,
        badgeTone = if (summary == unsetLabel) SemanticTone.Neutral else SemanticTone.Info,
        onClick = {
            onEditListGroup(
                title,
                placeholder,
                replaceValue,
                startValue,
                endValue,
                onReplaceChange,
                onStartChange,
                onEndChange,
            )
        },
    )
}

@Composable
fun StringMapWithModifiersInput(
    title: String,
    replaceValue: Map<String, String>?,
    mergeValue: Map<String, String>?,
    keyPlaceholder: String = "",
    valuePlaceholder: String = "",
    imageVector: ImageVector = MonadIcons.`Git-merge`,
    unsetLabel: String = MLang.Component.Selector.NotModify,
    compactSingleMode: Boolean = false,
    onReplaceChange: (Map<String, String>?) -> Unit,
    onMergeChange: (Map<String, String>?) -> Unit,
    onEditMap:
        (
            mode: MapMergeStrategy,
            title: String,
            keyPlaceholder: String,
            valuePlaceholder: String,
            value: Map<String, String>?,
            onValueChange: (Map<String, String>?) -> Unit,
        ) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val localMergedValue =
        remember(replaceValue, mergeValue, compactSingleMode) {
            if (!compactSingleMode) {
                null
            } else {
                linkedMapOf<String, String>()
                    .apply {
                        mergeValue.orEmpty().forEach { (key, value) -> this[key] = value }
                        replaceValue.orEmpty().forEach { (key, value) ->
                            if (key !in this) {
                                this[key] = value
                            }
                        }
                    }
                    .takeIf { it.isNotEmpty() }
            }
        }

    if (compactSingleMode) {
        val itemCount = localMergedValue?.size ?: 0
        ConfigSettingRow(
            title = title,
            summary = localMergedValue?.entries?.firstOrNull()?.key,
            valueLabel =
                if (itemCount > 0) {
                    MLang.Component.ConfigInput.CountItems.format(itemCount)
                } else {
                    unsetLabel
                },
            imageVector = imageVector,
            tone = if (itemCount > 0) SemanticTone.Warning else SemanticTone.Neutral,
            badgeTone = if (itemCount > 0) SemanticTone.Warning else SemanticTone.Neutral,
            onClick = {
                onEditMap(
                    MapMergeStrategy.Replace,
                    title,
                    keyPlaceholder,
                    valuePlaceholder,
                    localMergedValue,
                ) { updatedValue ->
                    onReplaceChange(updatedValue)
                    onMergeChange(null)
                }
            },
        )
        return
    }

    val summary =
        remember(replaceValue, mergeValue, unsetLabel) {
            val replaceCount = replaceValue?.size ?: 0
            val mergeCount = mergeValue?.size ?: 0
            val totalCount = replaceCount + mergeCount
            val activeModes = listOf(replaceCount, mergeCount).count { it > 0 }

            when {
                totalCount == 0 -> unsetLabel
                activeModes == 1 -> MLang.Component.ConfigInput.CountItems.format(totalCount)
                else ->
                    buildList {
                            if (replaceCount > 0) {
                                add(
                                    "${MLang.Component.Selector.Replace} ${MLang.Component.ConfigInput.CountItems.format(replaceCount)}"
                                )
                            }
                            if (mergeCount > 0) {
                                add(
                                    "${MLang.Component.Selector.Merge} ${MLang.Component.ConfigInput.CountItems.format(mergeCount)}"
                                )
                            }
                        }
                        .joinToString(" · ")
            }
        }

    Column {
        ConfigSettingRow(
            title = title,
            summary = summary.takeIf { it != unsetLabel },
            valueLabel =
                if (replaceValue.isNullOrEmpty() && mergeValue.isNullOrEmpty()) {
                    unsetLabel
                } else {
                    MLang.Component.ConfigInput.CountItems.format(
                        (replaceValue?.size ?: 0) + (mergeValue?.size ?: 0)
                    )
                },
            imageVector = imageVector,
            tone =
                if (expanded || !replaceValue.isNullOrEmpty() || !mergeValue.isNullOrEmpty())
                    SemanticTone.Warning
                else SemanticTone.Neutral,
            badgeTone =
                if (expanded || !replaceValue.isNullOrEmpty() || !mergeValue.isNullOrEmpty())
                    SemanticTone.Warning
                else SemanticTone.Neutral,
            onClick = { expanded = !expanded },
        )

        AnimatedVisibility(
            visible = expanded,
            enter =
                expandVertically(
                    animationSpec = tween(durationMillis = 260),
                    expandFrom = Alignment.Top,
                ) + fadeIn(animationSpec = tween(durationMillis = 180)),
            exit =
                shrinkVertically(
                    animationSpec = tween(durationMillis = 220),
                    shrinkTowards = Alignment.Top,
                ) + fadeOut(animationSpec = tween(durationMillis = 160)),
            label = "map_modifiers_$title",
        ) {
            Column {
                HorizontalDivider()
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ModifierModeCard(
                        modifier = Modifier.weight(1f),
                        title = MLang.Component.Selector.Replace,
                        summary = buildMapModeSummary(replaceValue, unsetLabel),
                        helperText = MLang.Component.ConfigInput.ReplaceHelper,
                        tone = SemanticTone.Brand,
                        onEdit = {
                            onEditMap(
                                MapMergeStrategy.Replace,
                                "$title (${MLang.Component.Selector.Replace})",
                                keyPlaceholder,
                                valuePlaceholder,
                                replaceValue,
                                onReplaceChange,
                            )
                        },
                        onClear =
                            if (!replaceValue.isNullOrEmpty()) {
                                { onReplaceChange(null) }
                            } else {
                                null
                            },
                    )
                    ModifierModeCard(
                        modifier = Modifier.weight(1f),
                        title = MLang.Component.Selector.Merge,
                        summary = buildMapModeSummary(mergeValue, unsetLabel),
                        helperText = MLang.Component.ConfigInput.MergeHelper,
                        tone = SemanticTone.Info,
                        onEdit = {
                            onEditMap(
                                MapMergeStrategy.Merge,
                                "$title (${MLang.Component.Selector.Merge})",
                                keyPlaceholder,
                                valuePlaceholder,
                                mergeValue,
                                onMergeChange,
                            )
                        },
                        onClear =
                            if (!mergeValue.isNullOrEmpty()) {
                                { onMergeChange(null) }
                            } else {
                                null
                            },
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = MLang.Component.ConfigInput.MergeNotice,
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )
            }
        }
    }
}

@Composable
private fun ModifierModeCard(
    title: String,
    summary: String,
    helperText: String,
    tone: SemanticTone,
    onEdit: () -> Unit,
    onClear: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val style = SemanticActionDefaults.style(tone = tone, highEmphasis = true)
    val shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(style.containerColor, shape)
                .border(AppTheme.strokes.default, style.borderColor, shape)
                .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        StatusBadge(text = title, tone = tone, compact = true)
        Text(text = summary, color = MiuixTheme.colorScheme.onSurface)
        Text(
            text = helperText,
            style = MiuixTheme.textStyles.body2,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AppCommandButton(
                title = MLang.Component.Button.Edit,
                imageVector = MonadIcons.Edit,
                onClick = onEdit,
                modifier = Modifier.weight(1f),
                tone = tone,
                highEmphasis = true,
            )
            if (onClear != null) {
                AppCommandButton(
                    title = MLang.Component.Button.Clear,
                    imageVector = MonadIcons.Delete,
                    onClick = onClear,
                    modifier = Modifier.weight(1f),
                    tone = SemanticTone.Danger,
                )
            }
        }
    }
}

private fun buildListModeSummary(value: List<String>?): String {
    return when {
        value.isNullOrEmpty() -> MLang.Component.Selector.NotModify
        else -> "${MLang.Component.ConfigInput.CountItems.format(value.size)} · ${value.first()}"
    }
}

private fun buildMapModeSummary(value: Map<String, String>?, unsetLabel: String): String {
    return when {
        value.isNullOrEmpty() -> unsetLabel
        else ->
            "${MLang.Component.ConfigInput.CountItems.format(value.size)} · ${value.entries.first().key}"
    }
}
