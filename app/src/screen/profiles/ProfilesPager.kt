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

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.yumelira.yumebox.MainActivity
import com.github.yumelira.yumebox.common.util.toast
import com.github.yumelira.yumebox.domain.model.ProfileBinding
import com.github.yumelira.yumebox.feature.editor.language.LanguageScope
import com.github.yumelira.yumebox.feature.editor.screen.ConfigPreviewSaveOutcome
import com.github.yumelira.yumebox.presentation.component.*
import com.github.yumelira.yumebox.presentation.component.LocalNavigator
import com.github.yumelira.yumebox.presentation.icon.Yume
import com.github.yumelira.yumebox.presentation.icon.yume.`Circle-fading-arrow-up`
import com.github.yumelira.yumebox.presentation.icon.yume.Cloud
import com.github.yumelira.yumebox.presentation.icon.yume.Delete
import com.github.yumelira.yumebox.presentation.icon.yume.`Scroll-text`
import com.github.yumelira.yumebox.presentation.icon.yume.Share
import com.github.yumelira.yumebox.presentation.theme.LocalSpacing
import com.github.yumelira.yumebox.presentation.theme.adaptiveContentWidth
import com.github.yumelira.yumebox.presentation.theme.rememberAvailableWindowAdaptiveInfo
import com.github.yumelira.yumebox.presentation.util.OverrideStructuredEditorStore
import com.github.yumelira.yumebox.presentation.viewmodel.OverrideConfigViewModel
import com.github.yumelira.yumebox.screen.home.HomeViewModel
import com.github.yumelira.yumebox.service.runtime.entity.Profile
import com.ramcosta.composedestinations.generated.destinations.LocalProfileConfigEditRouteDestination
import com.ramcosta.composedestinations.generated.destinations.OverrideConfigPreviewRouteDestination
import dev.oom_wg.purejoy.mlang.MLang
import java.io.File
import java.util.UUID
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import top.yukonga.miuix.kmp.basic.*

private val BLANK_PROFILE_TEMPLATE =
    """
mixed-port: 7890
mode: rule
log-level: info

proxies: []
proxy-groups: []
rules: []
"""
        .trimIndent()

private data class ProfileUpdateActionUiState(val feedback: ProfileUpdateFeedbackUi? = null) {
    val isUpdating: Boolean
        get() = feedback?.state == ProfileUpdateFeedbackState.Updating
}

private fun compactProfileUpdateSuccessLabel(): String {
    return MLang.Providers.Message.UpdateSuccess.format("").trim()
}

private fun compactProfileUpdateFailedLabel(): String {
    return MLang.Providers.Message.UpdateFailed.format("")
        .substringBefore(':')
        .substringBefore('：')
        .trim()
}

@SuppressLint("UseKtx")
@Composable
fun ProfilesPager(mainInnerPadding: PaddingValues) {
    val navigator = LocalNavigator.current
    val spacing = LocalSpacing.current
    val profilesViewModel = koinViewModel<ProfilesViewModel>()
    val homeViewModel = koinViewModel<HomeViewModel>()
    val profiles by profilesViewModel.profiles.collectAsStateWithLifecycle()
    val activeProfile by profilesViewModel.activeProfile.collectAsStateWithLifecycle()
    val profilesUiState by profilesViewModel.uiState.collectAsStateWithLifecycle()
    val isRunning by homeViewModel.isRunning.collectAsStateWithLifecycle()

    val overrideConfigViewModel = koinViewModel<OverrideConfigViewModel>()
    val systemPresets by overrideConfigViewModel.systemPresets.collectAsStateWithLifecycle()
    val userConfigs by overrideConfigViewModel.userConfigs.collectAsStateWithLifecycle()
    val showAddBottomSheet = rememberSaveable { mutableStateOf(false) }
    var isDeleteDialogVisible by rememberSaveable { mutableStateOf(false) }
    var deleteDialogProfileId by rememberSaveable { mutableStateOf<String?>(null) }
    val showSettingsDialog = rememberSaveable { mutableStateOf(false) }
    var moreActionsProfileId by rememberSaveable { mutableStateOf<String?>(null) }
    var settingsProfileId by rememberSaveable { mutableStateOf<String?>(null) }
    var profileBinding by remember { mutableStateOf<ProfileBinding?>(null) }
    val profileUpdateStates = remember { mutableStateMapOf<String, ProfileUpdateActionUiState>() }
    var isUpdatingAllProfiles by rememberSaveable { mutableStateOf(false) }
    var pendingRuntimeReloadProfileId by remember { mutableStateOf<UUID?>(null) }
    var pendingLocalProfileConfigEditorProfileId by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    var pendingOpenTextEditorProfileId by rememberSaveable { mutableStateOf<String?>(null) }

    var importUrlFromScheme by rememberSaveable { mutableStateOf<String?>(null) }
    val pendingImportUrl by MainActivity.pendingImportUrl.collectAsStateWithLifecycle()

    var scannedUrl by rememberSaveable { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val deleteDialogProfile =
        remember(deleteDialogProfileId, profiles) {
            profiles.firstOrNull { it.uuid.toString() == deleteDialogProfileId }
        }
    val settingsProfile =
        remember(settingsProfileId, profiles) {
            profiles.firstOrNull { it.uuid.toString() == settingsProfileId }
        }
    val moreActionsProfile =
        remember(moreActionsProfileId, profiles) {
            profiles.firstOrNull { it.uuid.toString() == moreActionsProfileId }
        }
    val anyProfileUpdating by remember {
        derivedStateOf { profileUpdateStates.values.any(ProfileUpdateActionUiState::isUpdating) }
    }

    fun profileUpdateState(profileId: UUID): ProfileUpdateActionUiState {
        return profileUpdateStates[profileId.toString()] ?: ProfileUpdateActionUiState()
    }

    suspend fun updateProfileWithFeedback(profile: Profile): Boolean {
        val profileId = profile.uuid.toString()
        profileUpdateStates[profileId] =
            ProfileUpdateActionUiState(
                feedback =
                    ProfileUpdateFeedbackUi(
                        state = ProfileUpdateFeedbackState.Updating,
                        label = MLang.Providers.Action.Update,
                    )
            )

        return runCatching { profilesViewModel.updateProfileNow(profile.uuid) }
            .fold(
                onSuccess = { updatedProfile ->
                    profileUpdateStates[profileId] =
                        ProfileUpdateActionUiState(
                            feedback =
                                ProfileUpdateFeedbackUi(
                                    state = ProfileUpdateFeedbackState.Success,
                                    label = compactProfileUpdateSuccessLabel(),
                                    timestampMillis =
                                        updatedProfile.updatedAt.takeIf { it > 0L }
                                            ?: System.currentTimeMillis(),
                                )
                        )
                    scope.launch {
                        delay(3_000)
                        val currentFeedback = profileUpdateStates[profileId]?.feedback
                        if (currentFeedback?.state == ProfileUpdateFeedbackState.Success) {
                            profileUpdateStates[profileId] = ProfileUpdateActionUiState()
                        }
                    }
                    true
                },
                onFailure = { error ->
                    val reason = error.message ?: MLang.ProfilesVM.Error.Unknown
                    profileUpdateStates[profileId] =
                        ProfileUpdateActionUiState(
                            feedback =
                                ProfileUpdateFeedbackUi(
                                    state = ProfileUpdateFeedbackState.Failure,
                                    label = compactProfileUpdateFailedLabel(),
                                )
                        )
                    com.github.yumelira.yumebox.App.instance.toast(
                        MLang.ProfilesVM.Message.UpdateFailed.format(reason)
                    )
                    false
                },
            )
    }

    LaunchedEffect(profiles) {
        val validProfileIds = profiles.mapTo(mutableSetOf()) { it.uuid.toString() }
        profileUpdateStates.keys.toList().forEach { profileId ->
            if (profileId !in validProfileIds) {
                profileUpdateStates.remove(profileId)
            }
        }
    }
    LaunchedEffect(pendingImportUrl) {
        if (pendingImportUrl != null) {
            importUrlFromScheme = pendingImportUrl
            settingsProfileId = null
            showAddBottomSheet.value = true
            MainActivity.clearPendingImportUrl()
        }
    }

    LaunchedEffect(deleteDialogProfileId, settingsProfileId, moreActionsProfileId, profiles) {
        if (deleteDialogProfileId != null && deleteDialogProfile == null) {
            deleteDialogProfileId = null
            isDeleteDialogVisible = false
        }
        if (settingsProfileId != null && settingsProfile == null) {
            settingsProfileId = null
            showSettingsDialog.value = false
            profileBinding = null
        }
        if (moreActionsProfileId != null && moreActionsProfile == null) {
            moreActionsProfileId = null
        }
    }

    LaunchedEffect(showSettingsDialog.value) {
        if (showSettingsDialog.value) {
            overrideConfigViewModel.refresh()
        }
    }

    LaunchedEffect(showSettingsDialog.value, pendingLocalProfileConfigEditorProfileId) {
        if (!showSettingsDialog.value) {
            pendingLocalProfileConfigEditorProfileId?.let { profileId ->
                pendingLocalProfileConfigEditorProfileId = null
                navigator.navigate(LocalProfileConfigEditRouteDestination(profileUuid = profileId))
            }
        }
    }

    val scrollBehavior = MiuixScrollBehavior()
    val addFabController = rememberOverrideFabController()
    val updatableProfiles = remember(profiles) { profiles.filter { it.type == Profile.Type.Url } }
    val showUpdateAllAction = updatableProfiles.size > 1
    val showAddFab =
        !showAddBottomSheet.value &&
            !showSettingsDialog.value &&
            !isDeleteDialogVisible &&
            moreActionsProfileId == null
    LaunchedEffect(profilesUiState.message, pendingRuntimeReloadProfileId) {
        pendingRuntimeReloadProfileId ?: return@LaunchedEffect
        if (profilesUiState.message != null) {
            pendingRuntimeReloadProfileId = null
        }
    }

    LaunchedEffect(profiles.isEmpty()) {
        if (profiles.isEmpty()) {
            addFabController.onScrollDirectionChanged(false)
        }
    }

    val openProfileEditor: (UUID, String) -> Unit = openProfileEditor@{ profileUuid, profileName ->
        val isEditingRuntimeProfile = homeViewModel.isCurrentProfile(profileUuid)
        val shouldOfferStopRuntime = isRunning && isEditingRuntimeProfile
        val configFile = resolveProfileConfigFile(profileUuid)
        val configContent =
            runCatching {
                    if (!configFile.exists()) {
                        configFile.parentFile?.mkdirs()
                        configFile.writeText(BLANK_PROFILE_TEMPLATE)
                    }
                    configFile.readText()
                }
                .getOrElse {
                    com.github.yumelira.yumebox.App.instance.toast(
                        it.message ?: MLang.ProfilesPage.Message.ReadProfileFailed
                    )
                    return@openProfileEditor
                }

        OverrideStructuredEditorStore.setupConfigPreview(
            title = profileName,
            content = configContent,
            language = LanguageScope.Yaml,
            runtimeRunning = shouldOfferStopRuntime,
            callback = { updatedContent, onPhaseChanged, decisionProvider ->
                if (updatedContent == configContent) {
                    return@setupConfigPreview Result.success(ConfigPreviewSaveOutcome.Saved)
                }
                runCatching {
                        profilesViewModel.saveProfileConfigContent(
                            uuid = profileUuid,
                            content = updatedContent,
                            onPhaseChanged = onPhaseChanged,
                            decisionProvider = decisionProvider,
                            stopRuntime = {
                                val isSavingRuntimeProfile =
                                    isRunning && homeViewModel.isCurrentProfile(profileUuid)
                                if (isSavingRuntimeProfile) {
                                    homeViewModel.stopProxy()
                                }
                            },
                        )
                    }
                    .fold(
                        onSuccess = { outcome -> Result.success(outcome) },
                        onFailure = { error -> Result.failure(error) },
                    )
            },
        )
        navigator.navigate(OverrideConfigPreviewRouteDestination)
    }

    val openLocalProfileConfigEditor: (UUID) -> Unit = { profileUuid ->
        navigator.navigate(
            LocalProfileConfigEditRouteDestination(profileUuid = profileUuid.toString())
        )
    }

    LaunchedEffect(showSettingsDialog.value, pendingOpenTextEditorProfileId, profiles) {
        if (!showSettingsDialog.value) {
            pendingOpenTextEditorProfileId?.let { profileId ->
                pendingOpenTextEditorProfileId = null
                profiles
                    .firstOrNull { it.uuid.toString() == profileId }
                    ?.let { profile -> openProfileEditor(profile.uuid, profile.name) }
            }
        }
    }

    val openAddProfileSheet = {
        settingsProfileId = null
        showAddBottomSheet.value = true
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val availableAdaptiveInfo = rememberAvailableWindowAdaptiveInfo(maxWidth, maxHeight)
        val useDockedAddAction = availableAdaptiveInfo.prefersDockedPrimaryAction

        Scaffold(
            floatingActionButton = {
                if (!useDockedAddAction) {
                    OverrideAnimatedFab(
                        controller = addFabController,
                        visible = showAddFab,
                        imageVector = Yume.Cloud,
                        contentDescription = MLang.ProfilesPage.Action.AddProfile,
                        label = MLang.ProfilesPage.Action.AddProfile,
                        supportingText =
                            "${MLang.ProfilesPage.Type.Subscription} / ${MLang.ProfilesPage.Type.LocalFile}",
                        onClick = openAddProfileSheet,
                    )
                }
            },
            topBar = {
                TopBar(
                    title = MLang.ProfilesPage.Title,
                    scrollBehavior = scrollBehavior,
                    actions = {
                        if (showUpdateAllAction || (useDockedAddAction && showAddFab)) {
                            Row(
                                modifier = Modifier.padding(end = LocalSpacing.current.xxl),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                if (showUpdateAllAction) {
                                    ProfileUpdateAllChip(
                                        enabled = !anyProfileUpdating && !isUpdatingAllProfiles,
                                        onClick = {
                                            if (!anyProfileUpdating && !isUpdatingAllProfiles) {
                                                isUpdatingAllProfiles = true
                                                scope.launch {
                                                    try {
                                                        updatableProfiles.forEach { p ->
                                                            updateProfileWithFeedback(p)
                                                        }
                                                    } finally {
                                                        isUpdatingAllProfiles = false
                                                    }
                                                }
                                            }
                                        },
                                    )
                                }

                                if (useDockedAddAction && showAddFab) {
                                    ProfileAddTopBarChip(onClick = openAddProfileSheet)
                                }
                            }
                        }
                    },
                )
            },
        ) { innerPadding ->
            if (profiles.isEmpty()) {
                Box(
                    modifier =
                        Modifier.fillMaxSize()
                            .padding(combinePaddingValues(innerPadding, mainInnerPadding))
                ) {
                    CenteredText(
                        firstLine = MLang.ProfilesPage.Empty.NoProfiles,
                        secondLine = MLang.ProfilesPage.Empty.Hint,
                    )
                }
            } else {
                val lazyListState = rememberLazyListState()
                val reorderableLazyListState =
                    rememberReorderableLazyListState(lazyListState) { from, to ->
                        profilesViewModel.reorderProfiles(from.index, to.index)
                    }

                val profileContentMaxWidth = availableAdaptiveInfo.preferredSinglePaneMaxWidth
                ScreenLazyColumn(
                    lazyListState = lazyListState,
                    scrollBehavior = scrollBehavior,
                    innerPadding = combinePaddingValues(innerPadding, mainInnerPadding),
                    topPadding = 20.dp,
                    modifier =
                        Modifier.adaptiveContentWidth(profileContentMaxWidth)
                            .padding(horizontal = spacing.gutter)
                            .align(Alignment.TopCenter),
                ) {
                    items(items = profiles, key = { it.uuid.toString() }) { profile ->
                        val cardUpdateState = profileUpdateState(profile.uuid)
                        ReorderableItem(reorderableLazyListState, key = profile.uuid.toString()) {
                            isDragging ->
                            ProfileCard(
                                profile = profile,
                                workDir =
                                    File(
                                        com.github.yumelira.yumebox.App.instance.filesDir,
                                        "imported",
                                    ),
                                isSelected = activeProfile?.uuid == profile.uuid,
                                isDownloading = cardUpdateState.isUpdating,
                                updateFeedback = cardUpdateState.feedback,
                                modifier = Modifier.alpha(if (isDragging) 0.9f else 1f),
                                dragHandleModifier = Modifier.longPressDraggableHandle(),
                                onSelect = { selectedProfile ->
                                    if (
                                        !profileUpdateState(selectedProfile.uuid).isUpdating &&
                                            activeProfile?.uuid != selectedProfile.uuid
                                    ) {
                                        scope.launch {
                                            runCatching {
                                                    profilesViewModel.setProfileEnabledNow(
                                                        uuid = selectedProfile.uuid,
                                                        enabled = true,
                                                    )
                                                }
                                                .onFailure {
                                                    com.github.yumelira.yumebox.App.instance.toast(
                                                        it.message
                                                            ?: MLang.ProfilesVM.Message.ToggleFailed
                                                                .format(
                                                                    MLang.ProfilesVM.Error.Unknown
                                                                )
                                                    )
                                                }
                                        }
                                    }
                                },
                                onUpdate = { profile ->
                                    if (!profileUpdateState(profile.uuid).isUpdating) {
                                        scope.launch { updateProfileWithFeedback(profile) }
                                    }
                                },
                                onEdit = { profile ->
                                    if (!profileUpdateState(profile.uuid).isUpdating) {
                                        if (profile.type == Profile.Type.Url) {
                                            settingsProfileId = profile.uuid.toString()
                                            scope.launch {
                                                profileBinding =
                                                    profilesViewModel.loadProfileBinding(
                                                        profile.uuid.toString()
                                                    )
                                            }
                                            showSettingsDialog.value = true
                                        } else {
                                            openLocalProfileConfigEditor(profile.uuid)
                                        }
                                    }
                                },
                                onMoreActions = { profile ->
                                    if (!profileUpdateState(profile.uuid).isUpdating) {
                                        moreActionsProfileId = profile.uuid.toString()
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
        AddProfileSheet(
            show = showAddBottomSheet,
            profileToEdit = null,
            importUrl = importUrlFromScheme ?: scannedUrl,
            onAddProfile = { name, source, type, interval, fileUri ->
                profilesViewModel.createProfile(type, name, source, interval, fileUri)
            },
            onCreateBlankProfile = { name ->
                showAddBottomSheet.value = false
                profilesViewModel.createBlankProfile(
                    name = name,
                    initialContent = BLANK_PROFILE_TEMPLATE,
                ) { uuid ->
                    openProfileEditor(uuid, name)
                }
            },
            onUpdateProfile = { uuid, name, source, interval ->
                pendingRuntimeReloadProfileId = uuid
                profilesViewModel.patchProfile(uuid, name, source, interval)
            },
            onDownloadComplete = {
                showAddBottomSheet.value = false
                profilesViewModel.clearDownloadProgress()
            },
            profilesViewModel = profilesViewModel,
        )

        deleteDialogProfile?.let { profile ->
            DeleteConfirmDialog(
                show = isDeleteDialogVisible,
                profileName = profile.name,
                onConfirm = {
                    isDeleteDialogVisible = false
                    scope.launch {
                        if (profile.active && isRunning) {
                            homeViewModel.stopProxy()
                        }
                        profilesViewModel.deleteProfile(profile.uuid)
                    }
                },
                onDismiss = { isDeleteDialogVisible = false },
                onDismissFinished = { deleteDialogProfileId = null },
            )
        }

        val currentProfileToEdit = settingsProfile
        if (currentProfileToEdit != null) {
            ProfileSettingsDialog(
                show = showSettingsDialog,
                profile = currentProfileToEdit,
                systemPreset = systemPresets.firstOrNull(),
                userConfigs = userConfigs,
                binding = profileBinding,
                onDismiss = { showSettingsDialog.value = false },
                onDismissFinished = {
                    settingsProfileId = null
                    profileBinding = null
                },
                onOpenConfigOptionsEditor = {
                    showSettingsDialog.value = false
                    pendingLocalProfileConfigEditorProfileId = currentProfileToEdit.uuid.toString()
                },
                onOpenConfigTextEditor = {
                    showSettingsDialog.value = false
                    pendingOpenTextEditorProfileId = currentProfileToEdit.uuid.toString()
                },
                onSaveProfileMeta = { newName, newSource ->
                    if (newName.isNotBlank() && newSource.isNotBlank()) {
                        scope.launch {
                            runCatching {
                                    profilesViewModel.patchProfileNow(
                                        currentProfileToEdit.uuid,
                                        newName,
                                        newSource,
                                        currentProfileToEdit.interval,
                                    )
                                }
                                .onFailure {
                                    com.github.yumelira.yumebox.App.instance.toast(
                                        it.message ?: MLang.ProfilesPage.SettingsDialog.SaveFailed
                                    )
                                }
                        }
                    }
                },
                onSaveOverrideSettings = { systemPresetEnabled, selectedOverrideIds ->
                    scope.launch {
                        val profileId = currentProfileToEdit.uuid.toString()
                        val normalizedOverrideIds = selectedOverrideIds.distinct()
                        val currentBinding =
                            profileBinding ?: profilesViewModel.loadProfileBinding(profileId)
                        val updatedBinding =
                            currentBinding?.copy(
                                overrideIds = normalizedOverrideIds,
                                enabled = systemPresetEnabled,
                            )
                                ?: ProfileBinding(
                                    profileId = profileId,
                                    overrideIds = normalizedOverrideIds,
                                    enabled = systemPresetEnabled,
                                )

                        profilesViewModel.saveProfileBinding(updatedBinding)
                        profileBinding = profilesViewModel.loadProfileBinding(profileId)

                        profilesViewModel.reapplyOverrideIfActiveProfile(profileId)
                    }
                },
            )
        }

        moreActionsProfile?.let { profile ->
            ProfileMoreActionsDialog(
                show = moreActionsProfileId != null,
                profile = profile,
                onEditText = {
                    moreActionsProfileId = null
                    openProfileEditor(profile.uuid, profile.name)
                },
                onShare = {
                    moreActionsProfileId = null
                    val context = com.github.yumelira.yumebox.App.instance
                    val file =
                        File(File(context.filesDir, "imported"), "${profile.uuid}/config.yaml")
                            .takeIf { it.exists() }
                            ?: File(
                                    File(context.filesDir, "clash"),
                                    "profiles/${profile.uuid}/config.yaml",
                                )
                                .takeIf { it.exists() }
                    if (file == null) {
                        context.toast(MLang.ProfilesPage.Message.ProfileFileNotExist)
                    } else {
                        runCatching {
                                val uri =
                                    FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.fileprovider",
                                        file,
                                    )
                                val intent =
                                    Intent(Intent.ACTION_SEND).apply {
                                        type = "application/x-yaml"
                                        putExtra(Intent.EXTRA_STREAM, uri)
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                context.startActivity(
                                    Intent.createChooser(
                                            intent,
                                            MLang.ProfilesPage.ShareDialog.ShareFile,
                                        )
                                        .apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                                )
                            }
                            .onFailure {
                                context.toast(it.message ?: MLang.ProfilesPage.Message.ShareFailed)
                            }
                    }
                },
                onDelete = {
                    moreActionsProfileId = null
                    deleteDialogProfileId = profile.uuid.toString()
                    isDeleteDialogVisible = true
                },
                onDismiss = { moreActionsProfileId = null },
            )
        }
    }
}

@Composable
private fun ProfileUpdateAllChip(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val style = SemanticActionDefaults.style(SemanticTone.Info, highEmphasis = true)
    val shape = RoundedCornerShape(22.dp)

    Row(
        modifier =
            modifier
                .clip(shape)
                .background(style.containerColor, shape)
                .border(0.8.dp, style.borderColor, shape)
                .appClickable(enabled = enabled, disabledAlpha = 0.5f, onClick = onClick)
                .padding(horizontal = 13.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(16.dp),
            imageVector = Yume.`Circle-fading-arrow-up`,
            tint = style.contentColor,
            contentDescription = MLang.ProfilesPage.Action.UpdateAll,
        )

        Text(
            text = MLang.ProfilesPage.Action.UpdateAll,
            color = style.contentColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun ProfileAddTopBarChip(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val style = SemanticActionDefaults.style(SemanticTone.Brand, highEmphasis = true)
    val shape = RoundedCornerShape(22.dp)

    Row(
        modifier =
            modifier
                .clip(shape)
                .background(style.containerColor, shape)
                .border(0.8.dp, style.borderColor, shape)
                .appClickable(onClick = onClick)
                .padding(horizontal = 13.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(16.dp),
            imageVector = Yume.Cloud,
            tint = style.contentColor,
            contentDescription = MLang.ProfilesPage.Action.AddProfile,
        )

        Text(
            text = MLang.ProfilesPage.Action.AddProfile,
            color = style.contentColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

private fun resolveProfileConfigFile(profileUuid: UUID): File {
    val filesDir = com.github.yumelira.yumebox.App.instance.filesDir
    val importedFile = File(filesDir, "imported/${profileUuid}/config.yaml")
    return if (importedFile.exists()) {
        importedFile
    } else {
        File(filesDir, "clash/profiles/${profileUuid}/config.yaml")
    }
}

@Composable
private fun ProfileMoreActionsDialog(
    show: Boolean,
    profile: Profile,
    onEditText: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    AppDialog(
        show = show,
        title = profile.name,
        summary = MLang.Settings.Section.More,
        onDismissRequest = onDismiss,
        onDismissFinished = {},
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            AppActionTile(
                title = MLang.ProfilesPage.SettingsDialog.OpenConfig,
                imageVector = Yume.`Scroll-text`,
                onClick = onEditText,
                modifier = Modifier.fillMaxWidth(),
                tone = SemanticTone.Info,
                highEmphasis = true,
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AppActionTile(
                    title = MLang.Component.ProfileCard.Export,
                    imageVector = Yume.Share,
                    onClick = onShare,
                    modifier = Modifier.weight(1f),
                    compact = true,
                    tone = SemanticTone.Info,
                )

                AppActionTile(
                    title = MLang.Component.ProfileCard.Delete,
                    imageVector = Yume.Delete,
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    compact = true,
                    tone = SemanticTone.Danger,
                    highEmphasis = true,
                )
            }
        }
    }
}
