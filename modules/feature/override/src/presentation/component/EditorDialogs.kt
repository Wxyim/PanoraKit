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

package com.github.yumelira.yumebox.presentation.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.yumelira.yumebox.presentation.util.decodeObjectFields
import com.github.yumelira.yumebox.presentation.util.encodeObjectFields
import com.github.yumelira.yumebox.presentation.util.jsonElementToEditorValue
import com.github.yumelira.yumebox.presentation.util.toOrderedJsonElementMap
import dev.oom_wg.purejoy.mlang.MLang
import kotlinx.serialization.json.JsonElement
import com.github.yumelira.yumebox.presentation.icon.Yume
import com.github.yumelira.yumebox.presentation.icon.yume.`Arrow-down-up`
import com.github.yumelira.yumebox.presentation.icon.yume.`Badge-plus`
import com.github.yumelira.yumebox.presentation.icon.yume.Copy
import com.github.yumelira.yumebox.presentation.icon.yume.Delete
import com.github.yumelira.yumebox.presentation.icon.yume.Edit
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
        MLang.Override.Dns.ProxyServerNameserverPolicy,
        -> StringMapValidationMode.DnsPolicy

        MLang.Override.Dns.Hosts -> StringMapValidationMode.Hosts
        else -> StringMapValidationMode.None
    }
}

@Composable
fun StringListEditorDialog(
    show: MutableState<Boolean>,
    title: String,
    placeholder: String,
    value: List<String>?,
    onValueChange: (List<String>?) -> Unit,
) {
    var editText by remember(title, value) { mutableStateOf(value?.joinToString("\n") ?: "") }

    AppDialog(show = show.value, title = title, onDismissRequest = { show.value = false }) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = MLang.Override.Editor.OneItemPerLine,
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.outline,
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextField(
                value = editText,
                onValueChange = { editText = it },
                label = placeholder,
                modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                maxLines = 20,
            )
            Spacer(modifier = Modifier.height(24.dp))
            DialogButtonRow(
                onCancel = { show.value = false },
                onConfirm = {
                    val lines = editText.lines().filter { it.isNotBlank() }
                    onValueChange(lines.ifEmpty { null })
                    show.value = false
                },
                cancelText = MLang.Override.Dialog.Button.Cancel,
                confirmText = MLang.Override.Editor.Confirm,
            )
        }
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

    com.github.yumelira.yumebox.feature.editor.component.JsonEditorDialog(
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
                imageVector = Yume.`Badge-plus`,
                tone = SemanticTone.Brand,
                onClick = {
                    entries.add("" to "")
                    validationError = null
                },
            )
            Spacer(modifier = Modifier.height(8.dp))
            AppCommandButton(
                title = MLang.Override.Editor.DeleteLastItem,
                imageVector = Yume.Delete,
                tone = SemanticTone.Danger,
                onClick = {
                    if (entries.size > 1) {
                        entries.removeLast()
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
                    return MLang.Component.Editor.Error.Expected.format("$keyPlaceholder / $valuePlaceholder")
                }
            }

            StringMapValidationMode.Hosts -> {
                if (!isLikelyDomainLikeKey(key) || !isLikelyIpOrHostTarget(value)) {
                    return MLang.Component.Editor.Error.Expected.format("$keyPlaceholder / $valuePlaceholder")
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
    Regex("^(?=.{1,253}$)([A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?)(\\.[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?)*$")

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

@Composable
fun JsonObjectListEditorDialog(
    show: MutableState<Boolean>,
    title: String,
    value: List<Map<String, JsonElement>>?,
    onValueChange: (List<Map<String, JsonElement>>?) -> Unit,
) {
    val drafts = remember { mutableStateListOf<Map<String, JsonElement>>() }
    var editingIndex by remember { mutableIntStateOf(-1) }
    val showItemEditor = remember { mutableStateOf(false) }

    LaunchedEffect(value) {
        drafts.clear()
        value?.forEach { drafts.add(it) }
    }

    AppDialog(show = show.value, title = title, onDismissRequest = { show.value = false }) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = MLang.Override.Edit.StructuredObjectListHint,
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.outline,
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(
                modifier = Modifier.heightIn(max = 360.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                itemsIndexed(drafts) { index, fields ->
                    Card(insideMargin = androidx.compose.foundation.layout.PaddingValues(12.dp)) {
                        Text(
                            text =
                                objectCardTitle(
                                    fields,
                                    "${MLang.Override.Draft.Object} ${index + 1}",
                                ),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                        )
                        Text(
                            text = objectCardSubtitle(fields),
                            fontSize = 13.sp,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AppActionTile(
                                title = MLang.Override.Editor.Edit,
                                imageVector = Yume.Edit,
                                compact = true,
                                tone = SemanticTone.Neutral,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    editingIndex = index
                                    showItemEditor.value = true
                                },
                            )
                            AppActionTile(
                                title = MLang.Override.Editor.Copy,
                                imageVector = Yume.Copy,
                                compact = true,
                                tone = SemanticTone.Info,
                                modifier = Modifier.weight(1f),
                                onClick = { drafts.add(index + 1, toOrderedJsonElementMap(fields)) },
                            )
                            AppActionTile(
                                title = MLang.Override.Card.Delete,
                                imageVector = Yume.Delete,
                                compact = true,
                                tone = SemanticTone.Danger,
                                modifier = Modifier.weight(1f),
                                onClick = { drafts.removeAt(index) },
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AppActionTile(
                                title = MLang.Override.Editor.MoveUp,
                                imageVector = Yume.`Arrow-down-up`,
                                compact = true,
                                enabled = index > 0,
                                modifier = Modifier.weight(1f),
                                onClick = { moveItem(drafts, index, index - 1) },
                            )
                            AppActionTile(
                                title = MLang.Override.Editor.MoveDown,
                                imageVector = Yume.`Arrow-down-up`,
                                compact = true,
                                enabled = index < drafts.lastIndex,
                                modifier = Modifier.weight(1f),
                                onClick = { moveItem(drafts, index, index + 1) },
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            AppCommandButton(
                title = MLang.Override.Editor.AddObject,
                imageVector = Yume.`Badge-plus`,
                tone = SemanticTone.Brand,
                highEmphasis = true,
                onClick = {
                    drafts.add(emptyMap())
                    editingIndex = drafts.lastIndex
                    showItemEditor.value = true
                },
            )
            Spacer(modifier = Modifier.height(24.dp))
            DialogButtonRow(
                onCancel = { show.value = false },
                onConfirm = {
                    onValueChange(drafts.toList().ifEmpty { null })
                    show.value = false
                },
                cancelText = MLang.Override.Dialog.Button.Cancel,
                confirmText = MLang.Override.Editor.Confirm,
            )
        }
    }

    JsonObjectFieldsDialog(
        show = showItemEditor,
        title = title,
        initialKey = null,
        initialFields = drafts.getOrNull(editingIndex),
        onConfirm = { _, fields ->
            if (editingIndex in drafts.indices) {
                drafts[editingIndex] = fields
            }
        },
    )
}

@Composable
fun JsonObjectMapEditorDialog(
    show: MutableState<Boolean>,
    title: String,
    value: Map<String, Map<String, JsonElement>>?,
    onValueChange: (Map<String, Map<String, JsonElement>>?) -> Unit,
) {
    val drafts = remember { mutableStateListOf<Pair<String, Map<String, JsonElement>>>() }
    var editingIndex by remember { mutableIntStateOf(-1) }
    val showItemEditor = remember { mutableStateOf(false) }

    LaunchedEffect(value) {
        drafts.clear()
        value?.forEach { (key, fields) -> drafts.add(key to fields) }
    }

    AppDialog(show = show.value, title = title, onDismissRequest = { show.value = false }) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = MLang.Override.Edit.StructuredProviderDictHint,
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.outline,
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(
                modifier = Modifier.heightIn(max = 360.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                itemsIndexed(drafts) { index, draft ->
                    Card(insideMargin = androidx.compose.foundation.layout.PaddingValues(12.dp)) {
                        Text(
                            text =
                                draft.first.ifBlank {
                                    MLang.Override.Editor.Unnamed.format(
                                        MLang.Override.Structured.ProxyProviders.ItemLabel
                                    )
                                },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                        )
                        Text(
                            text = objectCardSubtitle(draft.second),
                            fontSize = 13.sp,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AppActionTile(
                                title = MLang.Override.Editor.Edit,
                                imageVector = Yume.Edit,
                                compact = true,
                                tone = SemanticTone.Neutral,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    editingIndex = index
                                    showItemEditor.value = true
                                },
                            )
                            AppActionTile(
                                title = MLang.Override.Card.Delete,
                                imageVector = Yume.Delete,
                                compact = true,
                                tone = SemanticTone.Danger,
                                modifier = Modifier.weight(1f),
                                onClick = { drafts.removeAt(index) },
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            AppCommandButton(
                title = MLang.Proxy.Action.AddProvider,
                imageVector = Yume.`Badge-plus`,
                tone = SemanticTone.Brand,
                highEmphasis = true,
                onClick = {
                    drafts.add("" to emptyMap())
                    editingIndex = drafts.lastIndex
                    showItemEditor.value = true
                },
            )
            Spacer(modifier = Modifier.height(24.dp))
            DialogButtonRow(
                onCancel = { show.value = false },
                onConfirm = {
                    val mappedValue =
                        drafts.filter { it.first.isNotBlank() }.associate { it.first to it.second }
                    onValueChange(mappedValue.ifEmpty { null })
                    show.value = false
                },
                cancelText = MLang.Override.Dialog.Button.Cancel,
                confirmText = MLang.Override.Editor.Confirm,
            )
        }
    }

    JsonObjectFieldsDialog(
        show = showItemEditor,
        title = title,
        initialKey = drafts.getOrNull(editingIndex)?.first,
        initialFields = drafts.getOrNull(editingIndex)?.second,
        onConfirm = { key, fields ->
            if (editingIndex in drafts.indices && key != null) {
                drafts[editingIndex] = key to fields
            }
        },
    )
}

@Composable
fun SubRulesEditorDialog(
    show: MutableState<Boolean>,
    title: String,
    value: Map<String, List<String>>?,
    onValueChange: (Map<String, List<String>>?) -> Unit,
) {
    val drafts = remember { mutableStateListOf<Pair<String, List<String>>>() }
    val showRulesEditor = remember { mutableStateOf(false) }
    var editingIndex by remember { mutableIntStateOf(-1) }

    LaunchedEffect(value) {
        drafts.clear()
        value?.forEach { (key, rules) -> drafts.add(key to rules) }
        if (drafts.isEmpty()) {
            drafts.add("" to emptyList())
        }
    }

    AppDialog(show = show.value, title = title, onDismissRequest = { show.value = false }) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = MLang.Override.Edit.SubRuleGroupHint,
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.outline,
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(
                modifier = Modifier.heightIn(max = 360.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                itemsIndexed(drafts) { index, draft ->
                    Card(insideMargin = androidx.compose.foundation.layout.PaddingValues(12.dp)) {
                        TextField(
                            value = draft.first,
                            onValueChange = { updatedKey ->
                                drafts[index] = updatedKey to draft.second
                            },
                            label = MLang.Override.Editor.SubRuleName,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text =
                                if (draft.second.isEmpty()) MLang.Override.Draft.NoRules
                                else
                                    MLang.Override.Editor.RulesConfiguredInline.format(
                                        draft.second.size
                                    ),
                            fontSize = 13.sp,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AppActionTile(
                                title = MLang.Override.Editor.EditRule,
                                imageVector = Yume.Edit,
                                compact = true,
                                tone = SemanticTone.Neutral,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    editingIndex = index
                                    showRulesEditor.value = true
                                },
                            )
                            AppActionTile(
                                title = MLang.Override.Card.Delete,
                                imageVector = Yume.Delete,
                                compact = true,
                                tone = SemanticTone.Danger,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    if (drafts.size > 1) {
                                        drafts.removeAt(index)
                                    } else {
                                        drafts[0] = "" to emptyList()
                                    }
                                },
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            AppCommandButton(
                title = MLang.Override.Editor.AddSubRuleGroup,
                imageVector = Yume.`Badge-plus`,
                tone = SemanticTone.Brand,
                highEmphasis = true,
                onClick = { drafts.add("" to emptyList()) },
            )
            Spacer(modifier = Modifier.height(24.dp))
            DialogButtonRow(
                onCancel = { show.value = false },
                onConfirm = {
                    val mappedValue =
                        drafts.filter { it.first.isNotBlank() }.associate { it.first to it.second }
                    onValueChange(mappedValue.ifEmpty { null })
                    show.value = false
                },
                cancelText = MLang.Override.Dialog.Button.Cancel,
                confirmText = MLang.Override.Editor.Confirm,
            )
        }
    }

    StringListEditorDialog(
        show = showRulesEditor,
        title =
            drafts.getOrNull(editingIndex)?.first?.ifBlank { MLang.Override.Editor.EditSubRule }
                ?: MLang.Override.Editor.EditSubRule,
        placeholder = "DOMAIN-SUFFIX,example.com,DIRECT",
        value = drafts.getOrNull(editingIndex)?.second,
        onValueChange = { updatedRules ->
            if (editingIndex in drafts.indices) {
                drafts[editingIndex] = drafts[editingIndex].first to updatedRules.orEmpty()
            }
        },
    )
}

@Composable
private fun JsonObjectFieldsDialog(
    show: MutableState<Boolean>,
    title: String,
    initialKey: String?,
    initialFields: Map<String, JsonElement>?,
    onConfirm: (String?, Map<String, JsonElement>) -> Unit,
) {
    var keyText by remember(initialKey, initialFields) { mutableStateOf(initialKey ?: "") }
    var rawJson by
        remember(initialKey, initialFields) {
            mutableStateOf(encodeObjectFields(initialFields) ?: "{}")
        }

    AppDialog(show = show.value, title = title, onDismissRequest = { show.value = false }) {
        Column(modifier = Modifier.padding(20.dp)) {
            if (initialKey != null) {
                TextField(
                    value = keyText,
                    onValueChange = { keyText = it },
                    label = MLang.Override.Editor.KeyName,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            Text(
                text = MLang.Override.Edit.JsonKeyValueHint,
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.outline,
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextField(
                value = rawJson,
                onValueChange = { rawJson = it },
                label = "{ \"name\": \"proxy\", \"type\": \"ss\" }",
                modifier = Modifier.fillMaxWidth().heightIn(min = 220.dp),
                maxLines = 30,
            )
            Spacer(modifier = Modifier.height(24.dp))
            DialogButtonRow(
                onCancel = { show.value = false },
                onConfirm = {
                    val fields = decodeObjectFields(rawJson).orEmpty()
                    onConfirm(initialKey?.let { keyText.takeIf(String::isNotBlank) ?: "" }, fields)
                    show.value = false
                },
                cancelText = MLang.Override.Dialog.Button.Cancel,
                confirmText = MLang.Override.Editor.Confirm,
            )
        }
    }
}

private fun objectCardTitle(fields: Map<String, JsonElement>, fallbackTitle: String): String {
    val nameField = fields["name"]?.let(::jsonElementToEditorValue)
    return nameField?.takeIf(String::isNotBlank) ?: fallbackTitle
}

private fun objectCardSubtitle(fields: Map<String, JsonElement>): String {
    val typeField = fields["type"]?.let(::jsonElementToEditorValue)?.takeIf(String::isNotBlank)
    val keyCountText = MLang.Override.Form.ItemsConfigured.format(fields.size)
    return if (typeField != null) {
        "$typeField · $keyCountText"
    } else {
        keyCountText
    }
}

private fun moveItem(
    drafts: androidx.compose.runtime.snapshots.SnapshotStateList<Map<String, JsonElement>>,
    fromIndex: Int,
    toIndex: Int,
) {
    val item = drafts.removeAt(fromIndex)
    drafts.add(toIndex, item)
}
