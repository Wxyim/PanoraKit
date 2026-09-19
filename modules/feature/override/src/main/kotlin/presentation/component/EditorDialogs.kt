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

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.nomadboxlab.monadbox.presentation.icon.MonadIcons
import com.github.nomadboxlab.monadbox.presentation.icon.monad.`Badge-plus`
import com.github.nomadboxlab.monadbox.presentation.icon.monad.Delete
import com.github.nomadboxlab.monadbox.presentation.icon.monad.Edit
import dev.oom_wg.purejoy.mlang.MLang
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.theme.MiuixTheme

enum class StringMapValidationMode {
    None,
    DnsPolicy,
    Hosts,
}

fun resolveStringMapValidationMode(title: String): StringMapValidationMode {
    return when (title) {
        MLang.Override.Dns.NameserverPolicy,
        MLang.Override.Dns.ProxyServerNameserverPolicy -> StringMapValidationMode.DnsPolicy

        MLang.Override.Dns.Hosts -> StringMapValidationMode.Hosts
        else -> StringMapValidationMode.None
    }
}

@Composable
fun JsonTextEditorDialog(
    show: MutableState<Boolean>,
    title: String,
    placeholder: String,
    value: String?,
    onValueChange: (String?) -> Unit,
) {

    com.github.nomadboxlab.monadbox.feature.editor.component.JsonEditorDialog(
        show = show.value,
        title = title,
        subtitle = MLang.Override.Edit.JsonEditHint,
        value = value,
        onValueChange = onValueChange,
        onDismiss = { show.value = false },
    )
}

@Composable
fun StringMapEditorDialog(
    show: MutableState<Boolean>,
    title: String,
    keyPlaceholder: String,
    valuePlaceholder: String,
    value: Map<String, String>?,
    validationMode: StringMapValidationMode = StringMapValidationMode.None,
    onValueChange: (Map<String, String>?) -> Unit,
) {
    val entries = remember { mutableStateListOf<Pair<String, String>>() }
    var validationError by remember(title, value, validationMode) { mutableStateOf<String?>(null) }

    LaunchedEffect(value) {
        entries.clear()
        value?.forEach { (key, mapValue) -> entries.add(key to mapValue) }
        if (entries.isEmpty()) {
            entries.add("" to "")
        }
        validationError = null
    }

    AppDialog(show = show.value, title = title, onDismissRequest = { show.value = false }) {
        Column(modifier = Modifier.padding(20.dp)) {
            LazyColumn(modifier = Modifier.heightIn(max = 320.dp)) {
                itemsIndexed(entries) { index, entry ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        TextField(
                            value = entry.first,
                            onValueChange = { updatedKey ->
                                entries[index] = updatedKey to entry.second
                                validationError = null
                            },
                            label = keyPlaceholder,
                            modifier = Modifier.weight(1f),
                        )
                        TextField(
                            value = entry.second,
                            onValueChange = { updatedValue ->
                                entries[index] = entry.first to updatedValue
                                validationError = null
                            },
                            label = valuePlaceholder,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            AppCommandButton(
                title = MLang.Override.Editor.AddItem,
                imageVector = MonadIcons.`Badge-plus`,
                tone = SemanticTone.Brand,
                onClick = {
                    entries.add("" to "")
                    validationError = null
                },
            )
            Spacer(modifier = Modifier.height(8.dp))
            AppCommandButton(
                title = MLang.Override.Editor.DeleteLastItem,
                imageVector = MonadIcons.Delete,
                tone = SemanticTone.Danger,
                onClick = {
                    if (entries.size > 1) {
                        entries.removeAt(entries.lastIndex)
                    } else {
                        entries[0] = "" to ""
                    }
                    validationError = null
                },
            )
            validationError?.let { errorMessage ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.error,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            DialogButtonRow(
                onCancel = { show.value = false },
                onConfirm = {
                    val normalizedEntries =
                        entries.map { (rawKey, rawValue) -> rawKey.trim() to rawValue.trim() }
                    val message =
                        validateStringMapEntries(
                            entries = normalizedEntries,
                            validationMode = validationMode,
                            keyPlaceholder = keyPlaceholder,
                            valuePlaceholder = valuePlaceholder,
                        )
                    if (message != null) {
                        validationError = message
                        return@DialogButtonRow
                    }

                    val map =
                        normalizedEntries
                            .filter { it.first.isNotBlank() }
                            .associate { it.first to it.second }
                    onValueChange(map.ifEmpty { null })
                    show.value = false
                },
                cancelText = MLang.Override.Dialog.Button.Cancel,
                confirmText = MLang.Override.Editor.Confirm,
            )
        }
    }
}

private fun validateStringMapEntries(
    entries: List<Pair<String, String>>,
    validationMode: StringMapValidationMode,
    keyPlaceholder: String,
    valuePlaceholder: String,
): String? {
    val seenKeys = mutableSetOf<String>()
    entries.forEach { (key, value) ->
        if (key.isBlank() && value.isBlank()) {
            return@forEach
        }
        if (key.isBlank()) {
            return MLang.Component.Editor.Error.KeyEmpty
        }
        if (value.isBlank()) {
            return MLang.Component.Editor.Error.MissingValue
        }
        if (!seenKeys.add(key)) {
            return MLang.Component.Editor.Error.DuplicateKey
        }

        when (validationMode) {
            StringMapValidationMode.None -> Unit
            StringMapValidationMode.DnsPolicy -> {
                if (key.any(Char::isWhitespace) || value.any(Char::isWhitespace)) {
                    return MLang.Component.Editor.Error.Expected.format(
                        "$keyPlaceholder / $valuePlaceholder"
                    )
                }
            }

            StringMapValidationMode.Hosts -> {
                if (!isLikelyDomainLikeKey(key) || !isLikelyIpOrHostTarget(value)) {
                    return MLang.Component.Editor.Error.Expected.format(
                        "$keyPlaceholder / $valuePlaceholder"
                    )
                }
            }
        }
    }
    return null
}

private val HostKeyRegex = Regex("^[A-Za-z0-9*._:-]+$")
private val Ipv4Regex =
    Regex("^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$")
private val HostTargetRegex =
    Regex(
        "^(?=.{1,253}$)([A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?)(\\.[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?)*$"
    )

private fun isLikelyDomainLikeKey(value: String): Boolean {
    return value.isNotBlank() && !value.contains(' ') && HostKeyRegex.matches(value)
}

private fun isLikelyIpOrHostTarget(value: String): Boolean {
    if (value.isBlank() || value.any(Char::isWhitespace)) {
        return false
    }
    if (Ipv4Regex.matches(value)) {
        return true
    }
    if (value.contains(':')) {
        // Accept IPv6-like targets and host:port forms without being over-restrictive.
        return true
    }
    return HostTargetRegex.matches(value)
}
