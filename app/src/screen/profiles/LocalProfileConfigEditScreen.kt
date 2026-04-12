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

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.yumelira.yumebox.common.util.toast
import com.github.yumelira.yumebox.core.model.ConfigurationOverride
import com.github.yumelira.yumebox.data.repository.ActiveProfileOverrideReloader
import com.github.yumelira.yumebox.data.repository.ProfileBindingProvider
import com.github.yumelira.yumebox.domain.model.OverrideConfig
import com.github.yumelira.yumebox.domain.model.ProfileBinding
import com.github.yumelira.yumebox.feature.editor.screen.ConfigPreviewSaveDecision
import com.github.yumelira.yumebox.feature.editor.screen.ConfigPreviewSaveOutcome
import com.github.yumelira.yumebox.feature.editor.screen.ConfigPreviewSavePhase
import com.github.yumelira.yumebox.presentation.component.AppCommandButton
import com.github.yumelira.yumebox.presentation.component.AppDialog
import com.github.yumelira.yumebox.presentation.component.JsonTextEditorDialog
import com.github.yumelira.yumebox.presentation.component.LocalOverrideCardHorizontalPadding
import com.github.yumelira.yumebox.presentation.component.LocalProfileConfigEditContent
import com.github.yumelira.yumebox.presentation.component.OpenObjectMapEditor
import com.github.yumelira.yumebox.presentation.component.OpenRuleListEditor
import com.github.yumelira.yumebox.presentation.component.OpenStringListModifiersEditor
import com.github.yumelira.yumebox.presentation.component.OpenStructuredObjectListEditor
import com.github.yumelira.yumebox.presentation.component.OpenSubRulesEditor
import com.github.yumelira.yumebox.presentation.component.ScreenLazyColumn
import com.github.yumelira.yumebox.presentation.component.SemanticTone
import com.github.yumelira.yumebox.presentation.component.SmallTitle
import com.github.yumelira.yumebox.presentation.component.StringMapEditorDialog
import com.github.yumelira.yumebox.presentation.component.StringMapValidationMode
import com.github.yumelira.yumebox.presentation.component.TopBar
import com.github.yumelira.yumebox.presentation.component.resolveStringMapValidationMode
import com.github.yumelira.yumebox.presentation.icon.Yume
import com.github.yumelira.yumebox.presentation.icon.yume.ArrowLeft
import com.github.yumelira.yumebox.presentation.icon.yume.`Badge-plus`
import com.github.yumelira.yumebox.presentation.icon.yume.Check
import com.github.yumelira.yumebox.presentation.icon.yume.Close
import com.github.yumelira.yumebox.presentation.icon.yume.`List-chevrons-up-down`
import com.github.yumelira.yumebox.presentation.theme.rememberAvailableWindowAdaptiveInfo
import com.github.yumelira.yumebox.presentation.util.OverrideEditorSection
import com.github.yumelira.yumebox.presentation.viewmodel.OverrideConfigViewModel
import com.github.yumelira.yumebox.screen.home.HomeViewModel
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.oom_wg.purejoy.mlang.MLang
import java.util.UUID
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CircularProgressIndicator
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.SuperSwitch
import top.yukonga.miuix.kmp.theme.MiuixTheme

private object LocalProfileConfigEditMetrics {
    val MediumMaxWidth = 920.dp
    val ExpandedMaxWidth = 1220.dp
    val OverrideBindingListMaxHeight = 420.dp
}

@Composable
fun LocalProfileConfigEditScreen(
    navigator: DestinationsNavigator,
    profileUuid: String,
    onOpenStringListEditor: OpenStringListModifiersEditor,
    onOpenRuleListEditor: OpenRuleListEditor,
    onOpenObjectListEditor: OpenStructuredObjectListEditor,
    onOpenObjectMapEditor: OpenObjectMapEditor,
    onOpenSubRulesEditor: OpenSubRulesEditor,
) {
    val profilesViewModel = koinViewModel<ProfilesViewModel>()
    val homeViewModel = koinViewModel<HomeViewModel>()
    val overrideConfigViewModel = koinViewModel<OverrideConfigViewModel>()
    val bindingProvider: ProfileBindingProvider = koinInject()
    val activeProfileOverrideReloader: ActiveProfileOverrideReloader = koinInject()
    val isRuntimeRunning by homeViewModel.isRunning.collectAsStateWithLifecycle()
    val systemPresets by overrideConfigViewModel.systemPresets.collectAsStateWithLifecycle()
    val userConfigs by overrideConfigViewModel.userConfigs.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollBehavior = MiuixScrollBehavior()
    val editorListState = rememberLazyListState()
    val profileId = remember(profileUuid) { UUID.fromString(profileUuid) }

    var originalConfig by remember(profileUuid) { mutableStateOf<ConfigurationOverride?>(null) }
    var currentConfig by remember(profileUuid) { mutableStateOf<ConfigurationOverride?>(null) }
    var loadError by remember(profileUuid) { mutableStateOf<String?>(null) }
    var isLoading by remember(profileUuid) { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var savePhase by remember { mutableStateOf<ConfigPreviewSavePhase?>(null) }
    var saveDecision by remember { mutableStateOf(ConfigPreviewSaveDecision.Continue) }
    var showRuntimeStoppedDialog by remember { mutableStateOf(false) }
    val showStringMapEditor = remember { mutableStateOf(false) }
    val showJsonEditor = remember { mutableStateOf(false) }
    var expandedSectionNames by rememberSaveable { mutableStateOf(setOf<String>()) }
    var currentMapEditorCallback by remember { mutableStateOf<(Map<String, String>?) -> Unit>({}) }
    var currentJsonEditorCallback by remember { mutableStateOf<(String?) -> Unit>({}) }
    var currentMapEditorTitle by remember { mutableStateOf("") }
    var currentMapEditorKeyPlaceholder by remember { mutableStateOf("") }
    var currentMapEditorValuePlaceholder by remember { mutableStateOf("") }
    var currentMapEditorValue by remember { mutableStateOf<Map<String, String>?>(null) }
    var currentMapEditorValidationMode by remember { mutableStateOf(StringMapValidationMode.None) }
    var currentJsonEditorTitle by remember { mutableStateOf("") }
    var currentJsonEditorPlaceholder by remember { mutableStateOf("") }
    var currentJsonEditorValue by remember { mutableStateOf<String?>(null) }
    var bindingSystemPresetEnabled by remember(profileUuid) { mutableStateOf(false) }
    var bindingSelectedOverrideIds by remember(profileUuid) { mutableStateOf(emptyList<String>()) }

    val expandedSections =
        remember(expandedSectionNames) {
            expandedSectionNames
                .mapNotNull { sectionName ->
                    OverrideEditorSection.entries.firstOrNull { it.name == sectionName }
                }
                .toSet()
        }

    LaunchedEffect(profileUuid) {
        isLoading = true
        loadError = null
        runCatching { profilesViewModel.loadProfileConfigForGui(profileId) }
            .onSuccess { loadedConfig ->
                originalConfig = loadedConfig
                currentConfig = loadedConfig
            }
            .onFailure { error ->
                loadError = error.message ?: MLang.ProfilesPage.Message.ReadProfileFailed
            }
        runCatching { bindingProvider.getBinding(profileUuid) }
            .onSuccess { binding ->
                bindingSystemPresetEnabled = binding?.enabled ?: false
                bindingSelectedOverrideIds =
                    binding?.overrideIds.orEmpty().filterNot { it.startsWith("preset-") }
            }
        isLoading = false
    }

    fun applyBindingChange(newSelectedIds: List<String>, newSystemPresetEnabled: Boolean) {
        scope.launch {
            val normalizedIds = newSelectedIds.distinct()
            val currentBinding = bindingProvider.getBinding(profileUuid)
            val updatedBinding =
                currentBinding?.copy(overrideIds = normalizedIds, enabled = newSystemPresetEnabled)
                    ?: ProfileBinding(
                        profileId = profileUuid,
                        overrideIds = normalizedIds,
                        enabled = newSystemPresetEnabled,
                    )
            bindingProvider.setBinding(updatedBinding)
            activeProfileOverrideReloader.reapplyIfActiveProfile(profileUuid)
        }
    }

    fun requestExit() {
        when {
            showStringMapEditor.value -> showStringMapEditor.value = false
            showJsonEditor.value -> showJsonEditor.value = false
            isLoading || isSaving -> Unit
            else -> navigator.navigateUp()
        }
    }

    fun applyConfigChange(updated: ConfigurationOverride) {
        val savedConfig = originalConfig
        currentConfig = updated
        if (isSaving) return
        if (savedConfig == null) {
            return
        }
        if (!profilesViewModel.hasProfileGuiConfigChanges(savedConfig, updated)) {
            return
        }

        isSaving = true
        savePhase = ConfigPreviewSavePhase.LocalSaving
        saveDecision = ConfigPreviewSaveDecision.Continue
        scope.launch {
            var lastPhase: ConfigPreviewSavePhase? = ConfigPreviewSavePhase.LocalSaving
            var stoppedRunningProfile = false
            val isActiveRunningProfile =
                isRuntimeRunning && homeViewModel.isCurrentProfile(profileId)

            suspend fun saveWithDecision(fixedDecision: ConfigPreviewSaveDecision? = null) =
                profilesViewModel.saveProfileConfigGuiContent(
                    uuid = profileId,
                    originalConfig = savedConfig,
                    updatedConfig = updated,
                    onPhaseChanged = { phase ->
                        lastPhase = phase
                        savePhase = phase
                    },
                    decisionProvider = { fixedDecision ?: saveDecision },
                    stopRuntime = {
                        if (isActiveRunningProfile) {
                            stoppedRunningProfile = true
                            homeViewModel.stopProxy()
                        }
                    },
                )

            runCatching { saveWithDecision() }
                .recoverCatching { error ->
                    if (lastPhase != ConfigPreviewSavePhase.FetchingRemoteResources) {
                        throw error
                    }
                    saveWithDecision(ConfigPreviewSaveDecision.SaveLocally)
                }
                .onSuccess { outcome ->
                    when (outcome) {
                        ConfigPreviewSaveOutcome.Saved -> {
                            originalConfig = updated
                            currentConfig = updated
                        }

                        ConfigPreviewSaveOutcome.SavedLocally -> {
                            originalConfig = updated
                            currentConfig = updated
                            if (stoppedRunningProfile) {
                                showRuntimeStoppedDialog = true
                            } else {
                                context.toast(MLang.Component.Editor.Action.SaveLocally)
                            }
                        }

                        ConfigPreviewSaveOutcome.ResumeEditing -> Unit
                    }
                }
                .onFailure { error ->
                    currentConfig = savedConfig
                    context.toast(error.message ?: MLang.Component.Editor.Error.SaveFailed)
                }

            isSaving = false
            savePhase = null
            saveDecision = ConfigPreviewSaveDecision.Continue
        }
    }

    BackHandler(enabled = !isSaving) { requestExit() }

    Scaffold(
        topBar = {
            TopBar(
                title = MLang.Override.Edit.TitleEdit,
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(
                        modifier = Modifier.padding(start = 24.dp),
                        enabled = !isSaving,
                        onClick = { requestExit() },
                    ) {
                        Icon(Yume.ArrowLeft, contentDescription = MLang.Component.Navigation.Back)
                    }
                },
                actions = {},
            )
        }
    ) { paddingValues ->
        Box(
            modifier =
                Modifier.fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors =
                                listOf(
                                    lerp(
                                        MiuixTheme.colorScheme.surface,
                                        MiuixTheme.colorScheme.surfaceVariant,
                                        0.12f,
                                    ),
                                    MiuixTheme.colorScheme.surface,
                                    lerp(
                                        MiuixTheme.colorScheme.surface,
                                        MiuixTheme.colorScheme.surfaceVariant,
                                        0.06f,
                                    ),
                                )
                        )
                    )
                    .padding(paddingValues)
                    .imePadding()
        ) {
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                loadError != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = loadError.orEmpty(),
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            style = MiuixTheme.textStyles.body2,
                        )
                    }
                }

                currentConfig != null -> {
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val adaptiveInfo = rememberAvailableWindowAdaptiveInfo(maxWidth, maxHeight)
                        val contentMaxWidth =
                            when {
                                adaptiveInfo.isExpandedWidth ->
                                    LocalProfileConfigEditMetrics.ExpandedMaxWidth
                                adaptiveInfo.isMediumWidth ->
                                    LocalProfileConfigEditMetrics.MediumMaxWidth
                                else -> Dp.Unspecified
                            }
                        val useWideLayout =
                            adaptiveInfo.isMediumWidth || adaptiveInfo.isExpandedWidth

                        CompositionLocalProvider(
                            LocalOverrideCardHorizontalPadding provides !useWideLayout
                        ) {
                            ScreenLazyColumn(
                                scrollBehavior = scrollBehavior,
                                innerPadding = PaddingValues(0.dp),
                                lazyListState = editorListState,
                            ) {
                                item(key = "override-binding") {
                                    LocalProfileOverrideBindingSection(
                                        systemPreset = systemPresets.firstOrNull(),
                                        userConfigs = userConfigs,
                                        systemPresetEnabled = bindingSystemPresetEnabled,
                                        selectedOverrideIds = bindingSelectedOverrideIds,
                                        contentMaxWidth = contentMaxWidth,
                                        onSystemPresetToggle = { enabled ->
                                            bindingSystemPresetEnabled = enabled
                                            applyBindingChange(bindingSelectedOverrideIds, enabled)
                                        },
                                        onOverrideAdded = { id ->
                                            val newIds =
                                                listOf(id) +
                                                    bindingSelectedOverrideIds.filterNot {
                                                        it == id
                                                    }
                                            bindingSelectedOverrideIds = newIds
                                            applyBindingChange(newIds, bindingSystemPresetEnabled)
                                        },
                                        onOverrideRemoved = { id ->
                                            val newIds = bindingSelectedOverrideIds - id
                                            bindingSelectedOverrideIds = newIds
                                            applyBindingChange(newIds, bindingSystemPresetEnabled)
                                        },
                                        onOverrideMoved = { fromIndex, toIndex ->
                                            val newIds =
                                                bindingSelectedOverrideIds.toMutableList().apply {
                                                    add(toIndex, removeAt(fromIndex))
                                                }
                                            bindingSelectedOverrideIds = newIds
                                            applyBindingChange(newIds, bindingSystemPresetEnabled)
                                        },
                                    )
                                }
                                LocalProfileConfigEditContent(
                                    config = currentConfig ?: ConfigurationOverride(),
                                    currentConfigProvider = {
                                        currentConfig ?: originalConfig ?: ConfigurationOverride()
                                    },
                                    expandedSections = expandedSections,
                                    contentMaxWidth = contentMaxWidth,
                                    onConfigChange = ::applyConfigChange,
                                    onSectionToggle = { section ->
                                        expandedSectionNames =
                                            if (section.name in expandedSectionNames) {
                                                expandedSectionNames - section.name
                                            } else {
                                                expandedSectionNames + section.name
                                            }
                                    },
                                    onEditStringList = onOpenStringListEditor,
                                    onEditRuleList = onOpenRuleListEditor,
                                    onEditStringMap = {
                                        title,
                                        keyPlaceholder,
                                        valuePlaceholder,
                                        value,
                                        callback ->
                                        currentMapEditorTitle = title
                                        currentMapEditorKeyPlaceholder = keyPlaceholder
                                        currentMapEditorValuePlaceholder = valuePlaceholder
                                        currentMapEditorValue = value
                                        currentMapEditorValidationMode =
                                            resolveStringMapValidationMode(title)
                                        currentMapEditorCallback = callback
                                        showStringMapEditor.value = true
                                    },
                                    onEditJson = { title, placeholder, value, callback ->
                                        currentJsonEditorTitle = title
                                        currentJsonEditorPlaceholder = placeholder
                                        currentJsonEditorValue = value
                                        currentJsonEditorCallback = callback
                                        showJsonEditor.value = true
                                    },
                                    onEditObjectList = onOpenObjectListEditor,
                                    onEditObjectMap = onOpenObjectMapEditor,
                                    onEditSubRules = onOpenSubRulesEditor,
                                )
                            }
                        }
                    }
                }
            }

            AppDialog(
                show = isSaving,
                title = MLang.Component.Editor.Action.Save,
                summary =
                    when (savePhase) {
                        ConfigPreviewSavePhase.LocalSaving ->
                            MLang.Component.Editor.Dialog.LocalSaving
                        ConfigPreviewSavePhase.Validating ->
                            MLang.Component.Editor.Dialog.ValidatingConfig
                        ConfigPreviewSavePhase.FetchingRemoteResources ->
                            MLang.Component.Editor.Dialog.FetchingRemoteResources
                        null -> MLang.Component.Loading.Starting
                    },
                onDismissRequest = {},
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }

                    if (savePhase == ConfigPreviewSavePhase.FetchingRemoteResources) {
                        val isActiveRunning =
                            isRuntimeRunning && homeViewModel.isCurrentProfile(profileId)
                        AppCommandButton(
                            title =
                                if (isActiveRunning) {
                                    MLang.Component.Editor.Action.SaveAndStop
                                } else {
                                    MLang.Component.Editor.Action.SaveLocally
                                },
                            imageVector = Yume.Check,
                            onClick = { saveDecision = ConfigPreviewSaveDecision.SaveLocally },
                            enabled = saveDecision == ConfigPreviewSaveDecision.Continue,
                            tone = if (isActiveRunning) SemanticTone.Danger else SemanticTone.Brand,
                            highEmphasis = true,
                        )
                    }
                }
            }

            AppDialog(
                show = showRuntimeStoppedDialog,
                title = MLang.Component.Message.Hint,
                summary = MLang.Component.Editor.Dialog.DirectSaveStoppedRuntimeSummary,
                onDismissRequest = { showRuntimeStoppedDialog = false },
            ) {
                AppCommandButton(
                    title = MLang.Component.Button.Confirm,
                    imageVector = Yume.Check,
                    onClick = { showRuntimeStoppedDialog = false },
                    tone = SemanticTone.Brand,
                    highEmphasis = true,
                )
            }

            StringMapEditorDialog(
                show = showStringMapEditor,
                title = currentMapEditorTitle,
                keyPlaceholder = currentMapEditorKeyPlaceholder,
                valuePlaceholder = currentMapEditorValuePlaceholder,
                value = currentMapEditorValue,
                validationMode = currentMapEditorValidationMode,
                onValueChange = currentMapEditorCallback,
            )

            JsonTextEditorDialog(
                show = showJsonEditor,
                title = currentJsonEditorTitle,
                placeholder = currentJsonEditorPlaceholder,
                value = currentJsonEditorValue,
                onValueChange = currentJsonEditorCallback,
            )
        }
    }
}

@Composable
private fun LocalProfileOverrideBindingSection(
    systemPreset: OverrideConfig?,
    userConfigs: List<OverrideConfig>,
    systemPresetEnabled: Boolean,
    selectedOverrideIds: List<String>,
    contentMaxWidth: Dp = Dp.Unspecified,
    onSystemPresetToggle: (Boolean) -> Unit,
    onOverrideAdded: (String) -> Unit,
    onOverrideRemoved: (String) -> Unit,
    onOverrideMoved: (fromIndex: Int, toIndex: Int) -> Unit,
) {
    if (systemPreset == null && userConfigs.isEmpty()) return

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier =
                Modifier.fillMaxWidth().padding(horizontal = 20.dp).let { mod ->
                    if (contentMaxWidth != Dp.Unspecified) mod.widthIn(max = contentMaxWidth)
                    else mod
                },
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Spacer(Modifier.height(8.dp))
            SmallTitle(MLang.ProfilesPage.SettingsDialog.RuleOverrides)
            Text(
                text = MLang.ProfilesPage.SettingsDialog.RuleOverridesPriorityHint,
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                modifier = Modifier.padding(start = 4.dp, end = 4.dp),
            )

            if (systemPreset != null) {
                Card {
                    SuperSwitch(
                        title = MLang.ProfilesPage.SettingsDialog.SystemPreset,
                        summary = MLang.ProfilesPage.SettingsDialog.SystemPresetSummary,
                        checked = systemPresetEnabled,
                        onCheckedChange = onSystemPresetToggle,
                    )
                }
            }

            if (userConfigs.isNotEmpty()) {
                val selectedConfigs =
                    selectedOverrideIds.mapNotNull { id -> userConfigs.firstOrNull { it.id == id } }
                val unselectedConfigs = userConfigs.filter { it.id !in selectedOverrideIds }
                val overridesListState = rememberLazyListState()
                val reorderState =
                    rememberReorderableLazyListState(overridesListState) { from, to ->
                        val fromKey = from.key as? String ?: return@rememberReorderableLazyListState
                        val toKey = to.key as? String ?: return@rememberReorderableLazyListState
                        if (fromKey.startsWith("sel-") && toKey.startsWith("sel-")) {
                            val fromId = fromKey.removePrefix("sel-")
                            val toId = toKey.removePrefix("sel-")
                            val fromIndex = selectedConfigs.indexOfFirst { it.id == fromId }
                            val toIndex = selectedConfigs.indexOfFirst { it.id == toId }
                            if (fromIndex >= 0 && toIndex >= 0 && fromIndex != toIndex) {
                                onOverrideMoved(fromIndex, toIndex)
                            }
                        }
                    }

                Card {
                    LazyColumn(
                        state = overridesListState,
                        modifier =
                            Modifier.fillMaxWidth()
                                .heightIn(
                                    max = LocalProfileConfigEditMetrics.OverrideBindingListMaxHeight
                                ),
                    ) {
                        items(items = selectedConfigs, key = { "sel-${it.id}" }) { config ->
                            ReorderableItem(reorderState, key = "sel-${config.id}") { isDragging ->
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
                                            tint = MiuixTheme.colorScheme.onSurfaceVariantActions,
                                            contentDescription = null,
                                        )
                                    },
                                    endActions = {
                                        IconButton(
                                            onClick = { onOverrideRemoved(config.id) },
                                            minHeight = 32.dp,
                                            minWidth = 32.dp,
                                        ) {
                                            Icon(
                                                imageVector = Yume.Close,
                                                tint =
                                                    MiuixTheme.colorScheme.onSurfaceVariantActions,
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

                        items(items = unselectedConfigs, key = { "avail-${it.id}" }) { config ->
                            BasicComponent(
                                title = config.name,
                                summary =
                                    config.description?.takeIf { it.isNotBlank() }
                                        ?: MLang.ProfilesPage.SettingsDialog.NoDescription,
                                endActions = {
                                    IconButton(
                                        onClick = { onOverrideAdded(config.id) },
                                        minHeight = 36.dp,
                                        minWidth = 36.dp,
                                    ) {
                                        Icon(
                                            modifier = Modifier.size(18.dp),
                                            imageVector = Yume.`Badge-plus`,
                                            tint = MiuixTheme.colorScheme.primary,
                                            contentDescription = null,
                                        )
                                    }
                                },
                                onClick = { onOverrideAdded(config.id) },
                            )
                            if (unselectedConfigs.indexOf(config) < unselectedConfigs.lastIndex) {
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
