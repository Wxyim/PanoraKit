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

package com.github.yumelira.yumebox.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.github.yumelira.yumebox.presentation.component.*
import com.github.yumelira.yumebox.presentation.component.Card
import com.github.yumelira.yumebox.presentation.icon.Yume
import com.github.yumelira.yumebox.presentation.icon.yume.`Badge-plus`
import com.github.yumelira.yumebox.presentation.icon.yume.Delete
import com.github.yumelira.yumebox.presentation.icon.yume.List
import com.github.yumelira.yumebox.presentation.icon.yume.Undo
import com.github.yumelira.yumebox.presentation.util.OverrideEditorSemantics
import com.github.yumelira.yumebox.presentation.util.OverrideListEditorMode
import com.github.yumelira.yumebox.presentation.util.OverrideListModeValues
import com.github.yumelira.yumebox.presentation.util.OverrideStructuredEditorStore
import com.github.yumelira.yumebox.presentation.util.modeTitle
import com.github.yumelira.yumebox.presentation.util.reorderDraftList
import com.github.yumelira.yumebox.presentation.util.resolveLabel
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.oom_wg.purejoy.mlang.MLang
import java.util.ArrayDeque
import java.util.UUID
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.theme.MiuixTheme

private object OverrideStringListMetrics {
    val EntryTopPadding = 12.dp
    val RowHorizontalPadding = 16.dp
    val RowVerticalPadding = 14.dp
    val IndexWidth = 40.dp
    val ValueEndPadding = 8.dp
}

private data class StringListUiEntry(val uiId: String, val value: String)

@Composable
fun OverrideStringListEditorScreen(navigator: DestinationsNavigator) {
    val scrollBehavior = MiuixScrollBehavior()
    val listState = rememberLazyListState()
    val title =
        OverrideStructuredEditorStore.stringListEditorTitle.ifBlank { MLang.Override.Editor.List }
    val placeholder = OverrideStructuredEditorStore.stringListEditorPlaceholder
    val availableModes = OverrideStructuredEditorStore.stringListEditorAvailableModes
    val editorSemantics = OverrideStructuredEditorStore.stringListEditorSemantics
    var showItemDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var editingIndex by remember { mutableIntStateOf(-1) }
    var currentDraftValue by remember { mutableStateOf("") }
    val addFabController = rememberOverrideFabController()
    val selectedMode = OverrideStructuredEditorStore.stringListEditorSelectedMode
    val editorValues =
        remember(OverrideStructuredEditorStore.stringListEditorValues) {
            OverrideListModeValues(
                replaceValue =
                    OverrideStructuredEditorStore.stringListEditorValues.replaceValue?.toList(),
                startValue =
                    OverrideStructuredEditorStore.stringListEditorValues.startValue?.toList(),
                endValue = OverrideStructuredEditorStore.stringListEditorValues.endValue?.toList(),
            )
        }

    val currentItems = editorValues.valueFor(selectedMode).orEmpty()
    val selectedModeIndex = availableModes.indexOf(selectedMode).coerceAtLeast(0)
    val showAddFab = !showItemDialog && !showResetDialog
    val showModeSelector = availableModes.size > 1
    val reorderHeaderCount = if (showModeSelector) 1 else 0
    val currentEntries = rememberStringListUiEntries(selectedMode, currentItems)
    val addFabLabel = MLang.Override.Editor.AddItem

    fun currentStringListValues(): OverrideListModeValues<List<String>> {
        val latestValues = OverrideStructuredEditorStore.stringListEditorValues
        return OverrideListModeValues(
            replaceValue = latestValues.replaceValue?.toList(),
            startValue = latestValues.startValue?.toList(),
            endValue = latestValues.endValue?.toList(),
        )
    }

    fun applyStringListValues(values: OverrideListModeValues<List<String>>) {
        OverrideStructuredEditorStore.applyStringListValues(
            OverrideListModeValues(
                replaceValue = values.replaceValue?.toList(),
                startValue = values.startValue?.toList(),
                endValue = values.endValue?.toList(),
            )
        )
    }

    val handleAddClick: () -> Unit = {
        editingIndex = -1
        currentDraftValue = ""
        showItemDialog = true
    }

    val reorderState =
        rememberReorderableLazyListState(listState) { from, to ->
            val fromIndex = (from.index - reorderHeaderCount).coerceAtLeast(0)
            val toIndex = (to.index - reorderHeaderCount).coerceAtLeast(0)
            val latestValues = currentStringListValues()
            val mode = OverrideStructuredEditorStore.stringListEditorSelectedMode
            val updatedValues =
                latestValues.update(
                    mode,
                    reorderDraftList(latestValues.valueFor(mode).orEmpty(), fromIndex, toIndex),
                )
            applyStringListValues(updatedValues)
        }

    Scaffold(
        floatingActionButton = {
            OverrideAnimatedFab(
                controller = addFabController,
                visible = showAddFab,
                imageVector = Yume.`Badge-plus`,
                contentDescription = MLang.Override.Editor.AddItem,
                label = addFabLabel,
                onClick = handleAddClick,
            )
        },
        topBar = {
            TopBar(
                title = title,
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(
                        onClick = { showResetDialog = true },
                        modifier = Modifier.padding(end = 24.dp),
                    ) {
                        Icon(
                            imageVector = Yume.Undo,
                            contentDescription = MLang.Override.Editor.ClearCurrentMode,
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        ScreenLazyColumn(
            scrollBehavior = scrollBehavior,
            innerPadding = innerPadding,
            bottomPadding = OverrideFloatingActionContentBottomPadding,
            modifier = Modifier.fillMaxWidth(),
            topPadding = 20.dp,
            lazyListState = listState,
            onScrollDirectionChanged = addFabController::onScrollDirectionChanged,
        ) {
            if (showModeSelector) {
                item {
                    Card {
                        EnumSelector(
                            title = editorSemantics.modeTitle(),
                            currentValue = selectedMode,
                            items = availableModes.map { it.resolveLabel(editorSemantics) },
                            values = availableModes,
                            showDivider = false,
                            onValueChange = { newMode ->
                                OverrideStructuredEditorStore.updateStringListEditorSession(
                                    selectedMode = newMode
                                )
                            },
                        )
                    }
                }
            }

            if (currentEntries.isNotEmpty()) {
                itemsIndexed(currentEntries, key = { _, entry -> entry.uiId }) { index, entry ->
                    ReorderableItem(state = reorderState, key = entry.uiId) { isDragging ->
                        StringListEntryCard(
                            index = index + 1,
                            value = entry.value,
                            isDragging = isDragging,
                            onEdit = {
                                editingIndex = index
                                currentDraftValue = entry.value
                                showItemDialog = true
                            },
                            onDelete = {
                                val mode =
                                    OverrideStructuredEditorStore.stringListEditorSelectedMode
                                val latestValues = currentStringListValues()
                                val updatedValues =
                                    latestValues.update(
                                        mode,
                                        latestValues
                                            .valueFor(mode)
                                            .orEmpty()
                                            .toMutableList()
                                            .also { items -> items.removeAt(index) },
                                    )
                                applyStringListValues(updatedValues)
                            },
                        )
                    }
                }
            } else {
                item(key = "string-list-empty") {
                    OverrideEmptyStateCard(
                        title = MLang.Override.Empty.Title,
                        hint = MLang.Override.Empty.Hint,
                        actionLabel = addFabLabel,
                        onAction = handleAddClick,
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(OverrideSectionBottomSpacing)) }
        }

        StringListEntryDialog(
            show = showItemDialog,
            title =
                if (editingIndex >= 0) MLang.Override.Editor.EditItem
                else MLang.Override.Editor.AddItem,
            placeholder = placeholder,
            initialValue = currentDraftValue,
            onConfirm = { updatedValue ->
                val normalizedValue = updatedValue.trim()
                if (normalizedValue.isBlank()) {
                    return@StringListEntryDialog
                }
                val updatedItems =
                    currentItems.toMutableList().also { items ->
                        if (editingIndex in items.indices) {
                            items[editingIndex] = normalizedValue
                        } else {
                            if (editorSemantics == OverrideEditorSemantics.LocalConfig) {
                                items.add(0, normalizedValue)
                            } else {
                                items.add(normalizedValue)
                            }
                        }
                    }
                val mode = OverrideStructuredEditorStore.stringListEditorSelectedMode
                val updatedValues = currentStringListValues().update(mode, updatedItems)
                applyStringListValues(updatedValues)
                editingIndex = -1
                currentDraftValue = ""
                showItemDialog = false
            },
            onDismiss = {
                editingIndex = -1
                currentDraftValue = ""
                showItemDialog = false
            },
        )

        AppDialog(
            show = showResetDialog,
            title = MLang.Override.Editor.ClearCurrentMode,
            summary = MLang.Override.Editor.ClearDialog.Summary.format(MLang.Override.Editor.List),
            onDismissRequest = { showResetDialog = false },
        ) {
            DialogButtonRow(
                onCancel = { showResetDialog = false },
                onConfirm = {
                    showResetDialog = false
                    val mode = OverrideStructuredEditorStore.stringListEditorSelectedMode
                    applyStringListValues(currentStringListValues().update(mode, emptyList()))
                },
                cancelText = MLang.Override.Dialog.Button.Cancel,
                confirmText = MLang.Override.Editor.Clear,
            )
        }
    }
}

@Composable
private fun rememberStringListUiEntries(
    mode: OverrideListEditorMode,
    items: List<String>,
): List<StringListUiEntry> {
    val entries = remember(mode) { mutableStateListOf<StringListUiEntry>() }

    LaunchedEffect(mode, items) {
        val reusableIds =
            entries.groupBy(StringListUiEntry::value).mapValues { (_, value) ->
                ArrayDeque(value.map(StringListUiEntry::uiId))
            }
        val updatedEntries =
            items.map { itemValue ->
                val queue = reusableIds[itemValue]
                val uiId =
                    if (queue != null && queue.isNotEmpty()) {
                        queue.removeFirst()
                    } else {
                        UUID.randomUUID().toString()
                    }
                StringListUiEntry(uiId = uiId, value = itemValue)
            }

        entries.clear()
        entries.addAll(updatedEntries)
    }

    return entries
}

@Composable
private fun ReorderableCollectionItemScope.StringListEntryCard(
    index: Int,
    value: String,
    isDragging: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier =
            Modifier.padding(top = OverrideStringListMetrics.EntryTopPadding)
                .longPressDraggableHandle()
                .alpha(if (isDragging) 0.92f else 1f)
    ) {
        Row(
            modifier =
                Modifier.fillMaxWidth()
                    .appClickable(onClick = onEdit)
                    .padding(
                        horizontal = OverrideStringListMetrics.RowHorizontalPadding,
                        vertical = OverrideStringListMetrics.RowVerticalPadding,
                    ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Yume.List,
                contentDescription = MLang.Override.Editor.DragToSort,
                tint = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            )
            Text(
                text = "$index.",
                style = MiuixTheme.textStyles.body1,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                modifier = Modifier.width(OverrideStringListMetrics.IndexWidth),
            )
            Text(
                text = value,
                style = MiuixTheme.textStyles.body1,
                modifier =
                    Modifier.weight(1f).padding(end = OverrideStringListMetrics.ValueEndPadding),
            )
            OverrideCardActionIconButton(
                imageVector = Yume.Delete,
                contentDescription = MLang.Override.Card.Delete,
                onClick = onDelete,
            )
        }
    }
}

@Composable
private fun StringListEntryDialog(
    show: Boolean,
    title: String,
    placeholder: String,
    initialValue: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    if (!show) {
        return
    }

    var draftValue by remember(show, initialValue) { mutableStateOf(initialValue) }

    AppDialog(show = show, title = title, onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TextField(
                value = draftValue,
                onValueChange = { draftValue = it },
                label = placeholder,
                modifier = Modifier.fillMaxWidth(),
            )
            DialogButtonRow(
                onCancel = onDismiss,
                onConfirm = { onConfirm(draftValue) },
                cancelText = MLang.Override.Dialog.Button.Cancel,
                confirmText = MLang.Override.Editor.Confirm,
            )
        }
    }
}
