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

package com.github.yumelira.yumebox.screen.profiles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.github.yumelira.yumebox.domain.model.OverrideConfig
import com.github.yumelira.yumebox.domain.model.ProfileBinding
import com.github.yumelira.yumebox.presentation.component.AppActionBottomSheet
import com.github.yumelira.yumebox.presentation.component.AppActionTile
import com.github.yumelira.yumebox.presentation.component.AppBottomSheetCloseAction
import com.github.yumelira.yumebox.presentation.component.AppBottomSheetConfirmAction
import com.github.yumelira.yumebox.presentation.component.AppDialog
import com.github.yumelira.yumebox.presentation.component.DialogButtonRow
import com.github.yumelira.yumebox.presentation.component.SemanticTone
import com.github.yumelira.yumebox.presentation.icon.Yume
import com.github.yumelira.yumebox.presentation.icon.yume.`Badge-plus`
import com.github.yumelira.yumebox.presentation.icon.yume.Check
import com.github.yumelira.yumebox.presentation.icon.yume.Close
import com.github.yumelira.yumebox.presentation.icon.yume.Link
import com.github.yumelira.yumebox.presentation.icon.yume.`List-chevrons-up-down`
import com.github.yumelira.yumebox.presentation.icon.yume.`Scroll-text`
import com.github.yumelira.yumebox.presentation.icon.yume.`Settings-2`
import com.github.yumelira.yumebox.presentation.icon.yume.Share
import com.github.yumelira.yumebox.presentation.util.OverrideEditorSection
import com.github.yumelira.yumebox.service.runtime.entity.Profile
import dev.oom_wg.purejoy.mlang.MLang
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.extra.DialogDefaults
import top.yukonga.miuix.kmp.extra.SuperSwitch
import top.yukonga.miuix.kmp.theme.MiuixTheme

private const val PROFILE_SETTINGS_MIN_HEIGHT_FRACTION = 0.5f
private const val PROFILE_SETTINGS_MAX_HEIGHT_FRACTION = 0.7f
private val PROFILE_SETTINGS_LIST_MAX_HEIGHT = 420.dp
private const val SYSTEM_OVERRIDE_PREFIX = "preset-"
private const val BLANK_LOCAL_PROFILE_SOURCE = "blank://local-config"

private object ProfileDialogMetrics {
    val BottomPadding = 16.dp
    val SectionSpacing = 16.dp
}

@Composable
internal fun EditProfileNameDialog(
    show: MutableState<Boolean>,
    currentName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var editName by rememberSaveable(currentName) { mutableStateOf(currentName) }

    AppDialog(
        title = MLang.ProfilesPage.EditDialog.Title,
        show = show.value,
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TextField(
                value = editName,
                onValueChange = { editName = it },
                label = MLang.ProfilesPage.Input.ProfileName,
                modifier = Modifier.fillMaxWidth(),
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AppActionTile(
                    title = MLang.ProfilesPage.Button.Cancel,
                    imageVector = Yume.Close,
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    compact = true,
                    minHeight = 64.dp,
                    tone = SemanticTone.Neutral,
                )
                AppActionTile(
                    title = MLang.ProfilesPage.Button.Confirm,
                    imageVector = Yume.Check,
                    onClick = { onConfirm(editName) },
                    modifier = Modifier.weight(1f),
                    compact = true,
                    minHeight = 64.dp,
                    tone = SemanticTone.Brand,
                    highEmphasis = true,
                )
            }
        }
    }
}

@Composable
internal fun DeleteConfirmDialog(
    show: Boolean,
    profileName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    onDismissFinished: (() -> Unit)? = null,
) {
    AppDialog(
        show = show,
        modifier = Modifier,
        title = MLang.ProfilesPage.DeleteDialog.Title,
        titleColor = DialogDefaults.titleColor(),
        summary = MLang.ProfilesPage.DeleteDialog.Message.format(profileName),
        summaryColor = DialogDefaults.summaryColor(),
        backgroundColor = DialogDefaults.backgroundColor(),
        enableWindowDim = true,
        onDismissRequest = onDismiss,
        onDismissFinished = onDismissFinished,
        outsideMargin = DialogDefaults.outsideMargin,
        insideMargin = DialogDefaults.insideMargin,
        defaultWindowInsetsPadding = true,
        content = {
            DialogButtonRow(
                onCancel = onDismiss,
                onConfirm = onConfirm,
                cancelText = MLang.ProfilesPage.Button.Cancel,
                confirmText = MLang.ProfilesPage.DeleteDialog.Confirm,
            )
        },
    )
}

@Composable
internal fun ShareOptionsDialog(
    show: MutableState<Boolean>,
    profile: Profile,
    onDismiss: () -> Unit,
    onDismissFinished: (() -> Unit)? = null,
    onShareFile: (Profile) -> Unit,
    onShareLink: (Profile) -> Unit,
) {
    AppDialog(
        show = show.value,
        modifier = Modifier,
        title = MLang.ProfilesPage.ShareDialog.Title,
        titleColor = DialogDefaults.titleColor(),
        summary = null,
        summaryColor = DialogDefaults.summaryColor(),
        backgroundColor = DialogDefaults.backgroundColor(),
        enableWindowDim = true,
        onDismissRequest = onDismiss,
        onDismissFinished = onDismissFinished,
        outsideMargin = DialogDefaults.outsideMargin,
        insideMargin = DialogDefaults.insideMargin,
        defaultWindowInsetsPadding = true,
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (profile.type == Profile.Type.Url) {
                    AppActionTile(
                        title = MLang.ProfilesPage.ShareDialog.ShareLink,
                        imageVector = Yume.Link,
                        onClick = { onShareLink(profile) },
                        modifier = Modifier.fillMaxWidth(),
                        tone = SemanticTone.Info,
                        highEmphasis = true,
                    )
                }

                AppActionTile(
                    title = MLang.ProfilesPage.ShareDialog.ShareFile,
                    imageVector = Yume.Share,
                    onClick = { onShareFile(profile) },
                    modifier = Modifier.fillMaxWidth(),
                    tone = SemanticTone.Neutral,
                )

                AppActionTile(
                    title = MLang.ProfilesPage.Button.Cancel,
                    imageVector = Yume.Close,
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    tone = SemanticTone.Neutral,
                )
            }
        },
    )
}

@Composable
internal fun ProfileSettingsDialog(
    show: MutableState<Boolean>,
    profile: Profile,
    systemPreset: OverrideConfig?,
    userConfigs: List<OverrideConfig>,
    binding: ProfileBinding?,
    onDismiss: () -> Unit,
    onDismissFinished: () -> Unit,
    onOpenConfigOptionsEditor: () -> Unit,
    onOpenConfigTextEditor: () -> Unit,
    onSaveProfileMeta: (String, String) -> Unit,
    onSaveOverrideSettings: (Boolean, List<String>) -> Unit,
) {
    val initialOverrideIds = binding?.overrideIds.orEmpty().filterNot(::isBuiltinPresetOverrideId)
    val initialSystemPresetEnabled = binding?.enabled ?: false
    val appliedOverrideIds = initialOverrideIds
    var editName by rememberSaveable(profile.uuid) { mutableStateOf(profile.name) }
    var editSource by rememberSaveable(profile.uuid) { mutableStateOf("") }
    var systemPresetSelected by
        rememberSaveable(profile.uuid) { mutableStateOf(initialSystemPresetEnabled) }
    var pendingSelectedUserOverrideIds by
        rememberSaveable(profile.uuid) { mutableStateOf(emptyList<String>()) }

    val overridesListState = rememberLazyListState()
    val reorderState =
        rememberReorderableLazyListState(overridesListState) { from, to ->
            val fromKey = from.key as? String ?: return@rememberReorderableLazyListState
            val toKey = to.key as? String ?: return@rememberReorderableLazyListState
            if (fromKey.startsWith("sel-") && toKey.startsWith("sel-")) {
                val fromId = fromKey.removePrefix("sel-")
                val toId = toKey.removePrefix("sel-")
                val newList = pendingSelectedUserOverrideIds.toMutableList()
                val fromIdx = newList.indexOf(fromId)
                val toIdx = newList.indexOf(toId)
                if (fromIdx >= 0 && toIdx >= 0) {
                    newList.add(toIdx, newList.removeAt(fromIdx))
                    pendingSelectedUserOverrideIds = newList
                }
            }
        }

    LaunchedEffect(show.value, profile.uuid, profile.name, binding?.overrideIds, binding?.enabled) {
        if (show.value) {
            editName = profile.name
            editSource = ""
            systemPresetSelected = initialSystemPresetEnabled
            pendingSelectedUserOverrideIds = appliedOverrideIds
        }
    }

    val toggleUserOverrideSelection: (String, Boolean) -> Unit = { overrideId, isSelected ->
        pendingSelectedUserOverrideIds =
            toggleOverrideIdSelection(pendingSelectedUserOverrideIds, overrideId, isSelected)
    }
    val guiSectionSummary = remember {
        listOf(
                OverrideEditorSection.General.title,
                OverrideEditorSection.Inbound.title,
                OverrideEditorSection.Dns.title,
                OverrideEditorSection.Tun.title,
                OverrideEditorSection.Sniffer.title,
            )
            .joinToString(" · ")
    }
    val saveSettings = {
        val trimmedName = editName.trim()
        val trimmedSource = editSource.trim()
        val targetSource =
            if (profile.type == Profile.Type.Url && trimmedSource.isNotEmpty()) {
                trimmedSource
            } else {
                profile.source
            }
        if (
            trimmedName.isNotEmpty() &&
                targetSource.isNotEmpty() &&
                (trimmedName != profile.name || targetSource != profile.source)
        ) {
            onSaveProfileMeta(trimmedName, targetSource)
        }

        val finalSelectedOverrideIds = buildFinalOverrideIds(pendingSelectedUserOverrideIds)
        onSaveOverrideSettings(systemPresetSelected, finalSelectedOverrideIds)
        onDismiss()
    }

    AppActionBottomSheet(
        show = show.value,
        modifier = Modifier,
        title = profile.settingsDialogTitle(),
        startAction = {
            AppBottomSheetCloseAction(
                onClick = onDismiss,
                contentDescription = MLang.ProfilesPage.Button.Cancel,
            )
        },
        endAction = {
            AppBottomSheetConfirmAction(
                onClick = saveSettings,
                contentDescription = MLang.ProfilesPage.Button.Confirm,
            )
        },
        onDismissRequest = onDismiss,
        onDismissFinished = onDismissFinished,
        enableNestedScroll = true,
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val minimumSheetHeight = maxHeight * PROFILE_SETTINGS_MIN_HEIGHT_FRACTION
            val maximumSheetHeight = maxHeight * PROFILE_SETTINGS_MAX_HEIGHT_FRACTION

            Column(
                modifier =
                    Modifier.fillMaxWidth()
                        .heightIn(min = minimumSheetHeight, max = maximumSheetHeight)
                        .padding(bottom = ProfileDialogMetrics.BottomPadding),
                verticalArrangement = Arrangement.spacedBy(ProfileDialogMetrics.SectionSpacing),
            ) {
                TextField(
                    value = editName,
                    onValueChange = { editName = it },
                    label = MLang.ProfilesPage.Input.ProfileName,
                    modifier = Modifier.fillMaxWidth(),
                )

                if (profile.type == Profile.Type.Url) {
                    TextField(
                        value = editSource,
                        onValueChange = { editSource = it },
                        label = MLang.ProfilesPage.SettingsDialog.ChangeLink,
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                    )
                }

                if (profile.type != Profile.Type.Url) {
                    Card {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            AppActionTile(
                                title = MLang.ProfilesPage.SettingsDialog.EditSettings,
                                imageVector = Yume.`Settings-2`,
                                summary = guiSectionSummary,
                                onClick = onOpenConfigOptionsEditor,
                                modifier = Modifier.fillMaxWidth(),
                                tone = SemanticTone.Info,
                                highEmphasis = true,
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                thickness = 0.5.dp,
                                color = MiuixTheme.colorScheme.outline.copy(alpha = 0.3f),
                            )
                            BasicComponent(
                                title = MLang.ProfilesPage.SettingsDialog.LocalSource,
                                summary = profile.localConfigSourceSummary(),
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                thickness = 0.5.dp,
                                color = MiuixTheme.colorScheme.outline.copy(alpha = 0.3f),
                            )
                            AppActionTile(
                                title = MLang.ProfilesPage.SettingsDialog.OpenConfig,
                                imageVector = Yume.`Scroll-text`,
                                summary =
                                    MLang.ProfilesPage.SettingsDialog.LocalConfigEditorSummary,
                                onClick = onOpenConfigTextEditor,
                                modifier = Modifier.fillMaxWidth(),
                                tone = SemanticTone.Neutral,
                            )
                        }
                    }
                }

                if (systemPreset != null) {
                    Card {
                        SuperSwitch(
                            title = MLang.ProfilesPage.SettingsDialog.SystemPreset,
                            summary = MLang.ProfilesPage.SettingsDialog.SystemPresetSummary,
                            checked = systemPresetSelected,
                            onCheckedChange = { systemPresetSelected = it },
                        )
                    }
                }

                if (userConfigs.isNotEmpty()) {
                    val selectedConfigs =
                        pendingSelectedUserOverrideIds.mapNotNull { id ->
                            userConfigs.firstOrNull { it.id == id }
                        }
                    val unselectedConfigs =
                        userConfigs.filter { it.id !in pendingSelectedUserOverrideIds }
                    Card {
                        LazyColumn(
                            state = overridesListState,
                            modifier =
                                Modifier.fillMaxWidth()
                                    .heightIn(max = PROFILE_SETTINGS_LIST_MAX_HEIGHT),
                        ) {
                            // Selected overrides (draggable via long-press handle)
                            items(items = selectedConfigs, key = { "sel-${it.id}" }) { config ->
                                ReorderableItem(reorderState, key = "sel-${config.id}") { isDragging
                                    ->
                                    BasicComponent(
                                        modifier =
                                            Modifier.alpha(if (isDragging) 0.82f else 1f)
                                                .longPressDraggableHandle(),
                                        title = config.name,
                                        summary =
                                            config.description?.takeIf { it.isNotBlank() }
                                                ?: MLang.ProfilesPage.SettingsDialog.NoDescription,
                                        startAction = {
                                            Icon(
                                                modifier = Modifier.size(20.dp),
                                                imageVector = Yume.`List-chevrons-up-down`,
                                                tint =
                                                    MiuixTheme.colorScheme.onSurfaceVariantActions,
                                                contentDescription = null,
                                            )
                                        },
                                        endActions = {
                                            IconButton(
                                                onClick = {
                                                    pendingSelectedUserOverrideIds =
                                                        pendingSelectedUserOverrideIds - config.id
                                                },
                                                minHeight = 32.dp,
                                                minWidth = 32.dp,
                                            ) {
                                                Icon(
                                                    imageVector = Yume.Close,
                                                    tint =
                                                        MiuixTheme.colorScheme
                                                            .onSurfaceVariantActions,
                                                    contentDescription = null,
                                                )
                                            }
                                        },
                                    )
                                }
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    thickness = 0.5.dp,
                                    color = MiuixTheme.colorScheme.outline.copy(alpha = 0.3f),
                                )
                            }
                            // Available (unselected) overrides
                            items(items = unselectedConfigs, key = { "avail-${it.id}" }) { config ->
                                BasicComponent(
                                    title = config.name,
                                    summary =
                                        config.description?.takeIf { it.isNotBlank() }
                                            ?: MLang.ProfilesPage.SettingsDialog.NoDescription,
                                    endActions = {
                                        IconButton(
                                            onClick = {
                                                pendingSelectedUserOverrideIds =
                                                    listOf(config.id) +
                                                        pendingSelectedUserOverrideIds.filterNot {
                                                            it == config.id
                                                        }
                                            },
                                            minHeight = 32.dp,
                                            minWidth = 32.dp,
                                        ) {
                                            Icon(
                                                imageVector = Yume.`Badge-plus`,
                                                tint = MiuixTheme.colorScheme.primary,
                                                contentDescription = null,
                                            )
                                        }
                                    },
                                    onClick = {
                                        pendingSelectedUserOverrideIds =
                                            listOf(config.id) +
                                                pendingSelectedUserOverrideIds.filterNot {
                                                    it == config.id
                                                }
                                    },
                                )
                                if (
                                    unselectedConfigs.indexOf(config) < unselectedConfigs.lastIndex
                                ) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        thickness = 0.5.dp,
                                        color = MiuixTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun toggleOverrideIdSelection(
    selectedOverrideIds: List<String>,
    overrideId: String,
    isSelected: Boolean,
): List<String> {
    return if (isSelected) {
        selectedOverrideIds - overrideId
    } else {
        (selectedOverrideIds + overrideId).distinct()
    }
}

private fun buildFinalOverrideIds(selectedUserOverrideIds: List<String>): List<String> {
    return selectedUserOverrideIds.filterNot(::isBuiltinPresetOverrideId).distinct()
}

private fun isBuiltinPresetOverrideId(overrideId: String): Boolean {
    return overrideId.startsWith(SYSTEM_OVERRIDE_PREFIX)
}

private fun Profile.settingsDialogTitle(): String {
    return if (type == Profile.Type.Url) {
        MLang.ProfilesPage.SettingsDialog.Title
    } else {
        MLang.Component.ProfileCard.LocalConfig
    }
}

private fun Profile.localConfigSourceSummary(): String {
    return when {
        source == BLANK_LOCAL_PROFILE_SOURCE -> MLang.ProfilesPage.SettingsDialog.LocalSourceBlank
        source.isBlank() -> MLang.Component.ProfileCard.LocalFile
        else -> MLang.ProfilesPage.SettingsDialog.LocalSourceImported
    }
}
