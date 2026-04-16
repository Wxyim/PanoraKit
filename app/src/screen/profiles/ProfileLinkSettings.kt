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

package com.github.nomadboxlab.monadbox.screen.profiles

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.nomadboxlab.monadbox.data.store.LinkOpenMode
import com.github.nomadboxlab.monadbox.data.store.ProfileLink
import com.github.nomadboxlab.monadbox.presentation.component.AppActionBottomSheet
import com.github.nomadboxlab.monadbox.presentation.component.EnumSelector
import com.github.nomadboxlab.monadbox.presentation.theme.AppTheme
import com.github.nomadboxlab.monadbox.presentation.theme.LocalPageMetrics
import dev.oom_wg.purejoy.mlang.MLang
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Delete
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
internal fun LinkSettingsDialog(
    show: MutableState<Boolean>,
    links: List<ProfileLink>,
    linkOpenMode: LinkOpenMode,
    defaultLinkId: String,
    onOpenModeChange: (LinkOpenMode) -> Unit,
    onDefaultLinkChange: (String) -> Unit,
    onAddLink: () -> Unit,
    onDeleteLink: (String) -> Unit,
    onOpenLink: (ProfileLink) -> Unit,
) {
    val spacing = AppTheme.spacing
    val pageMetrics = LocalPageMetrics.current
    val openModeOptions =
        listOf(
            MLang.ProfilesPage.LinkSettings.OpenModeInApp,
            MLang.ProfilesPage.LinkSettings.OpenModeExternal,
        )
    val openModeIndex =
        when (linkOpenMode) {
            LinkOpenMode.IN_APP -> 0
            LinkOpenMode.EXTERNAL_BROWSER -> 1
        }

    val defaultLinkIndex =
        if (defaultLinkId.isEmpty() || links.isEmpty()) {
            0
        } else {
            links.indexOfFirst { it.id == defaultLinkId }.let { if (it == -1) 0 else it }
        }

    AppActionBottomSheet(
        show = show.value,
        modifier = Modifier,
        title = MLang.ProfilesPage.LinkSettings.Title,
        onDismissRequest = { show.value = false },
        enableNestedScroll = true,
        content = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(bottom = spacing.lg),
                verticalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                top.yukonga.miuix.kmp.basic.Card {
                    EnumSelector(
                        title = MLang.ProfilesPage.LinkSettings.OpenMode,
                        currentValue = linkOpenMode,
                        items = openModeOptions,
                        values = listOf(LinkOpenMode.IN_APP, LinkOpenMode.EXTERNAL_BROWSER),
                        showDivider = false,
                        onValueChange = onOpenModeChange,
                    )
                }

                if (links.isNotEmpty()) {
                    top.yukonga.miuix.kmp.basic.Card {
                        EnumSelector(
                            title = MLang.ProfilesPage.LinkSettings.DefaultLink,
                            summary = MLang.ProfilesPage.LinkSettings.DefaultLinkSummary,
                            currentValue = links[defaultLinkIndex],
                            items = links.map { it.name },
                            values = links,
                            showDivider = false,
                            onValueChange = { onDefaultLinkChange(it.id) },
                        )
                    }
                }

                if (links.isNotEmpty()) {
                    top.yukonga.miuix.kmp.basic.Card {
                        Column(
                            modifier =
                                Modifier.fillMaxWidth()
                                    .heightIn(
                                        max = pageMetrics.profileLinkSettingsLinkListMaxHeight
                                    )
                        ) {
                            links.forEachIndexed { index, link ->
                                Row(
                                    modifier =
                                        Modifier.fillMaxWidth()
                                            .clickable { onOpenLink(link) }
                                            .padding(
                                                horizontal = spacing.lg,
                                                vertical = spacing.md,
                                            ),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = link.name, style = MiuixTheme.textStyles.body1)
                                        Text(
                                            text = link.url,
                                            style = MiuixTheme.textStyles.body2,
                                            color =
                                                MiuixTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }

                                    IconButton(onClick = { onDeleteLink(link.id) }) {
                                        Icon(
                                            imageVector = MiuixIcons.Delete,
                                            contentDescription =
                                                MLang.Component.Editor.Action.Delete,
                                            tint = MiuixTheme.colorScheme.error,
                                        )
                                    }
                                }

                                if (index < links.size - 1) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = spacing.lg),
                                        thickness = 0.5.dp,
                                        color = MiuixTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    )
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.md),
                ) {
                    TextButton(
                        text = MLang.ProfilesPage.LinkSettings.Close,
                        onClick = { show.value = false },
                        modifier = Modifier.weight(1f),
                    )
                    Button(
                        onClick = onAddLink,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColorsPrimary(),
                    ) {
                        Text(
                            MLang.ProfilesPage.LinkSettings.AddLink,
                            color = MiuixTheme.colorScheme.onPrimary,
                        )
                    }
                }
            }
        },
    )
}

@Composable
internal fun AddLinkDialog(
    show: MutableState<Boolean>,
    linkToEdit: ProfileLink?,
    linkName: String,
    onNameChange: (String) -> Unit,
    linkUrl: String,
    onUrlChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val spacing = AppTheme.spacing
    var error by remember { mutableStateOf("") }
    var currentName by remember { mutableStateOf(linkName) }
    var currentUrl by remember { mutableStateOf(linkUrl) }

    LaunchedEffect(show.value, linkToEdit) {
        if (show.value) {
            if (linkToEdit != null) {
                currentName = linkToEdit.name
                currentUrl = linkToEdit.url
            } else {
                currentName = ""
                currentUrl = ""
            }
            error = ""
        }
    }

    AppActionBottomSheet(
        show = show.value,
        modifier = Modifier,
        title =
            if (linkToEdit != null) MLang.ProfilesPage.LinkSettings.EditLink
            else MLang.ProfilesPage.LinkSettings.AddLink,
        onDismissRequest = onDismiss,
        enableNestedScroll = true,
        content = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(bottom = spacing.lg),
                verticalArrangement = Arrangement.spacedBy(spacing.lg),
            ) {
                TextField(
                    value = currentName,
                    onValueChange = {
                        currentName = it
                        error = ""
                    },
                    label = MLang.ProfilesPage.LinkSettings.Name,
                    modifier = Modifier.fillMaxWidth(),
                )

                TextField(
                    value = currentUrl,
                    onValueChange = {
                        currentUrl = it
                        error = ""
                    },
                    label = MLang.ProfilesPage.LinkSettings.Url,
                    modifier = Modifier.fillMaxWidth(),
                )

                if (error.isNotEmpty()) {
                    Text(
                        text = error,
                        color = MiuixTheme.colorScheme.error,
                        style = MiuixTheme.textStyles.body2,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.md),
                ) {
                    Button(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text(MLang.ProfilesPage.Button.Cancel)
                    }
                    Button(
                        onClick = {
                            when {
                                currentName.isBlank() ->
                                    error = MLang.ProfilesPage.LinkSettings.Validation.EnterName

                                currentUrl.isBlank() ->
                                    error = MLang.ProfilesPage.LinkSettings.Validation.EnterUrl

                                !currentUrl.startsWith("http", ignoreCase = true) ->
                                    error = MLang.ProfilesPage.LinkSettings.Validation.InvalidUrl

                                else -> {
                                    onNameChange(currentName)
                                    onUrlChange(currentUrl)
                                    onConfirm()
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColorsPrimary(),
                    ) {
                        Text(
                            MLang.ProfilesPage.Button.Confirm,
                            color = MiuixTheme.colorScheme.onPrimary,
                        )
                    }
                }
            }
        },
    )
}
