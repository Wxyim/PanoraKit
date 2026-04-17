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

package com.github.nomadboxlab.monadbox.feature.settings

import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.nomadboxlab.monadbox.common.util.AppIconHelper
import com.github.nomadboxlab.monadbox.common.util.isNotificationGranted
import com.github.nomadboxlab.monadbox.common.util.openAppNotificationSettings
import com.github.nomadboxlab.monadbox.common.util.toast
import com.github.nomadboxlab.monadbox.data.model.AppLanguage
import com.github.nomadboxlab.monadbox.data.model.CleanupPolicy
import com.github.nomadboxlab.monadbox.data.model.ThemeMode
import com.github.nomadboxlab.monadbox.presentation.component.*
import com.github.nomadboxlab.monadbox.presentation.component.Card
import com.github.nomadboxlab.monadbox.presentation.component.ThemeColorPickerItem
import com.github.nomadboxlab.monadbox.presentation.theme.adaptiveContentWidth
import com.github.nomadboxlab.monadbox.presentation.theme.rememberAvailableWindowAdaptiveInfo
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.oom_wg.purejoy.mlang.MLang
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.text.toString
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.basic.SliderDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.extra.SuperSwitch
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun AppSettingsScreenBody(navigator: DestinationsNavigator) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val scope = rememberCoroutineScope()
    val scrollBehavior = MiuixScrollBehavior()
    val viewModel = koinViewModel<AppSettingsViewModel>()

    val appLanguage = viewModel.appLanguage.state.collectAsStateWithLifecycle().value
    val themeMode = viewModel.themeMode.state.collectAsStateWithLifecycle().value
    val themeSeedColorArgb = viewModel.themeSeedColorArgb.state.collectAsStateWithLifecycle().value

    val automaticRestart = viewModel.automaticRestart.state.collectAsStateWithLifecycle().value
    val autoUpdateCurrentProfileOnStart =
        viewModel.autoUpdateCurrentProfileOnStart.state.collectAsStateWithLifecycle().value
    val hideAppIcon = viewModel.hideAppIcon.state.collectAsStateWithLifecycle().value
    val excludeFromRecents = viewModel.excludeFromRecents.state.collectAsStateWithLifecycle().value
    val showTrafficNotification =
        viewModel.showTrafficNotification.state.collectAsStateWithLifecycle().value
    val autoStartLogRecording =
        viewModel.autoStartLogRecording.state.collectAsStateWithLifecycle().value
    val singleNodeTest = viewModel.singleNodeTest.state.collectAsStateWithLifecycle().value
    val bottomBarAutoHide = viewModel.bottomBarAutoHide.state.collectAsStateWithLifecycle().value
    val topBarBlurEnabled = viewModel.topBarBlurEnabled.state.collectAsStateWithLifecycle().value
    val bottomBarLiquidGlassEnabled =
        viewModel.bottomBarLiquidGlassEnabled.state.collectAsStateWithLifecycle().value
    val pageScaleState = viewModel.pageScale.state.collectAsStateWithLifecycle().value
    var pageScaleLocal by rememberSaveable(pageScaleState) { mutableFloatStateOf(pageScaleState) }

    val customUserAgent = viewModel.customUserAgent.state.collectAsStateWithLifecycle().value
    val cleanupAutoEnabled = viewModel.cleanupAutoEnabled.state.collectAsStateWithLifecycle().value
    val cleanupPolicy = viewModel.cleanupPolicy.state.collectAsStateWithLifecycle().value
    val cleanupThresholdMb = viewModel.cleanupThresholdMb.state.collectAsStateWithLifecycle().value
    val cleanupIntervalHours =
        viewModel.cleanupIntervalHours.state.collectAsStateWithLifecycle().value
    val cleanupLastRunAt = viewModel.cleanupLastRunAt.state.collectAsStateWithLifecycle().value

    val showHideIconDialog = rememberSaveable { mutableStateOf(false) }
    val showEditCustomUserAgentDialog = rememberSaveable { mutableStateOf(false) }
    val showPageScaleSheet = rememberSaveable { mutableStateOf(false) }
    val showCleanupThresholdDialog = rememberSaveable { mutableStateOf(false) }
    val showCleanupIntervalDialog = rememberSaveable { mutableStateOf(false) }

    val customUserAgentTextFieldState =
        rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(customUserAgent))
        }
    var cleanupThresholdText by
        rememberSaveable(cleanupThresholdMb) { mutableStateOf(cleanupThresholdMb.toString()) }
    var cleanupIntervalText by
        rememberSaveable(cleanupIntervalHours) { mutableStateOf(cleanupIntervalHours.toString()) }
    var pageScaleDialogText by rememberSaveable {
        mutableStateOf((pageScaleState * 100).roundToInt().toString())
    }
    val dateTimeFormatter = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
    val pageScaleLabel = "${(pageScaleState * 100).roundToInt()}%"
    val cleanupLastRunSummary =
        if (cleanupLastRunAt > 0L) {
            MLang.AppSettings.Cleanup.LastRunSummary.format(
                dateTimeFormatter.format(Date(cleanupLastRunAt))
            )
        } else {
            MLang.AppSettings.Cleanup.LastRunNever
        }
    val openPageScaleDialog = {
        pageScaleLocal = pageScaleState
        pageScaleDialogText = (pageScaleState * 100).roundToInt().toString()
        showPageScaleSheet.value = true
    }
    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
            granted ->
            if (granted) {
                viewModel.onShowTrafficNotificationChange(true)
            } else {
                viewModel.onShowTrafficNotificationChange(false)
                if (
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        activity?.shouldShowRequestPermissionRationale(
                            android.Manifest.permission.POST_NOTIFICATIONS
                        ) == false
                ) {
                    openAppNotificationSettings(context)
                } else {
                    context.toast(MLang.Onboarding.Permission.Notification.SummaryNeed)
                }
            }
        }

    Scaffold(
        topBar = {
            TopBar(
                title = MLang.AppSettings.Title,
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    NavigationBackIcon(
                        navigator = navigator,
                        contentDescription = MLang.Component.Navigation.Back,
                    )
                },
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            BoxWithConstraints(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter,
            ) {
                val adaptiveInfo = rememberAvailableWindowAdaptiveInfo(maxWidth, maxHeight)
                val contentMaxWidth = adaptiveInfo.preferredTwoPaneMaxWidth
                ScreenLazyColumn(
                    modifier = Modifier.adaptiveContentWidth(contentMaxWidth),
                    scrollBehavior = scrollBehavior,
                    innerPadding = innerPadding,
                ) {
                    item {
                        AppSettingsContent(
                            automaticRestart = automaticRestart,
                            autoUpdateCurrentProfileOnStart = autoUpdateCurrentProfileOnStart,
                            appLanguage = appLanguage,
                            themeMode = themeMode,
                            themeSeedColorArgb = themeSeedColorArgb,
                            bottomBarAutoHide = bottomBarAutoHide,
                            topBarBlurEnabled = topBarBlurEnabled,
                            bottomBarLiquidGlassEnabled = bottomBarLiquidGlassEnabled,
                            pageScaleLabel = pageScaleLabel,
                            hideAppIcon = hideAppIcon,
                            excludeFromRecents = excludeFromRecents,
                            showTrafficNotification = showTrafficNotification,
                            singleNodeTest = singleNodeTest,
                            autoStartLogRecording = autoStartLogRecording,
                            customUserAgent = customUserAgent,
                            cleanupAutoEnabled = cleanupAutoEnabled,
                            cleanupPolicy = cleanupPolicy,
                            cleanupThresholdMb = cleanupThresholdMb,
                            cleanupIntervalHours = cleanupIntervalHours,
                            cleanupLastRunSummary = cleanupLastRunSummary,
                            onAutomaticRestartChange = viewModel::onAutomaticRestartChange,
                            onAutoUpdateCurrentProfileOnStartChange =
                                viewModel::onAutoUpdateCurrentProfileOnStartChange,
                            onAppLanguageChange = {
                                viewModel.onAppLanguageChange(it)
                                AppLanguageManager.apply(it)
                                activity?.recreate()
                            },
                            onThemeModeChange = viewModel::onThemeModeChange,
                            onThemeSeedColorChange = viewModel::onThemeSeedColorChange,
                            onBottomBarAutoHideChange = viewModel::onBottomBarAutoHideChange,
                            onTopBarBlurEnabledChange = viewModel::onTopBarBlurEnabledChange,
                            onBottomBarLiquidGlassEnabledChange =
                                viewModel::onBottomBarLiquidGlassEnabledChange,
                            onOpenPageScaleDialog = openPageScaleDialog,
                            onHideAppIconChange = { checked ->
                                if (checked) {
                                    showHideIconDialog.value = true
                                } else {
                                    viewModel.onHideAppIconChange(false)
                                    AppIconHelper.toggleIcon(context, false)
                                }
                            },
                            onExcludeFromRecentsChange = viewModel::onExcludeFromRecentsChange,
                            onShowTrafficNotificationChange = { checked ->
                                if (
                                    checked &&
                                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                        !isNotificationGranted(context)
                                ) {
                                    notificationPermissionLauncher.launch(
                                        android.Manifest.permission.POST_NOTIFICATIONS
                                    )
                                } else {
                                    viewModel.onShowTrafficNotificationChange(checked)
                                }
                            },
                            onSingleNodeTestChange = viewModel::onSingleNodeTestChange,
                            onAutoStartLogRecordingChange =
                                viewModel::onAutoStartLogRecordingChange,
                            onEditCustomUserAgent = {
                                customUserAgentTextFieldState.value =
                                    TextFieldValue(customUserAgent)
                                showEditCustomUserAgentDialog.value = true
                            },
                            onCleanupAutoEnabledChange = viewModel::onCleanupAutoEnabledChange,
                            onCleanupPolicyChange = viewModel::onCleanupPolicyChange,
                            onOpenCleanupThresholdDialog = {
                                cleanupThresholdText = cleanupThresholdMb.toString()
                                showCleanupThresholdDialog.value = true
                            },
                            onOpenCleanupIntervalDialog = {
                                cleanupIntervalText = cleanupIntervalHours.toString()
                                showCleanupIntervalDialog.value = true
                            },
                            onRunCleanupNow = {
                                scope.launch {
                                    val result = viewModel.runCleanupNow()
                                    if (result.executed) {
                                        val freedMb = result.freedBytes / (1024 * 1024)
                                        val archive =
                                            result.archiveFileName
                                                ?: MLang.AppSettings.Cleanup.ArchiveSkipped
                                        context.toast(
                                            MLang.AppSettings.Cleanup.CleanupNowSuccess.format(
                                                freedMb,
                                                archive,
                                            )
                                        )
                                    } else {
                                        context.toast(MLang.AppSettings.Cleanup.CleanupNowSkipped)
                                    }
                                }
                            },
                        )
                    }
                }
            }

            WarningBottomSheet(
                show = showHideIconDialog,
                title = MLang.AppSettings.WarningDialog.Title,
                messages =
                    listOf(
                        MLang.AppSettings.WarningDialog.HideIconMsg1,
                        MLang.AppSettings.WarningDialog.HideIconMsg2,
                    ),
                onConfirm = {
                    viewModel.onHideAppIconChange(true)
                    AppIconHelper.toggleIcon(context, true)
                },
            )

            TextEditBottomSheet(
                show = showEditCustomUserAgentDialog,
                title = MLang.AppSettings.EditDialog.UserAgentTitle,
                textFieldValue = customUserAgentTextFieldState,
                onConfirm = { viewModel.applyCustomUserAgent(it) },
            )

            AppDialog(
                show = showPageScaleSheet.value,
                modifier = Modifier,
                title = MLang.AppSettings.Interface.PageScaleTitle,
                summary = MLang.AppSettings.Interface.PageScaleRange,
                onDismissRequest = { showPageScaleSheet.value = false },
                renderInRootScaffold = true,
                content = {
                    TextField(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        value = pageScaleDialogText,
                        maxLines = 1,
                        trailingIcon = {
                            Text(
                                text = "%",
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MiuixTheme.colorScheme.onSurfaceVariantActions,
                            )
                        },
                        onValueChange = { v ->
                            if (v.isEmpty() || v.all { it.isDigit() }) {
                                pageScaleDialogText = v
                            }
                        },
                    )
                    Slider(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        value = pageScaleLocal,
                        onValueChange = {
                            pageScaleLocal = it
                            pageScaleDialogText = (it * 100).roundToInt().toString()
                        },
                        valueRange = 0.8f..1.2f,
                        magnetThreshold = 0.01f,
                        hapticEffect = SliderDefaults.SliderHapticEffect.Step,
                    )
                    DialogButtonRow(
                        onCancel = { showPageScaleSheet.value = false },
                        onConfirm = {
                            val parsed = pageScaleDialogText.toFloatOrNull()
                            val clamped =
                                (parsed?.coerceIn(80f, 120f) ?: (pageScaleLocal * 100)) / 100f
                            pageScaleLocal = clamped
                            pageScaleDialogText = (clamped * 100).roundToInt().toString()
                            viewModel.onPageScaleChange(clamped)
                            showPageScaleSheet.value = false
                        },
                        cancelText = MLang.AppSettings.Button.Cancel,
                        confirmText = MLang.AppSettings.Button.Apply,
                    )
                },
            )

            AppDialog(
                show = showCleanupThresholdDialog.value,
                modifier = Modifier,
                title = MLang.AppSettings.Cleanup.ThresholdTitle,
                summary = MLang.AppSettings.Cleanup.ThresholdRange,
                onDismissRequest = { showCleanupThresholdDialog.value = false },
                renderInRootScaffold = true,
                content = {
                    TextField(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        value = cleanupThresholdText,
                        maxLines = 1,
                        trailingIcon = {
                            Text(
                                text = "MB",
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MiuixTheme.colorScheme.onSurfaceVariantActions,
                            )
                        },
                        onValueChange = { value ->
                            if (value.isEmpty() || value.all { it.isDigit() }) {
                                cleanupThresholdText = value
                            }
                        },
                    )
                    DialogButtonRow(
                        onCancel = { showCleanupThresholdDialog.value = false },
                        onConfirm = {
                            val parsed = cleanupThresholdText.toIntOrNull() ?: cleanupThresholdMb
                            viewModel.onCleanupThresholdMbChange(parsed)
                            showCleanupThresholdDialog.value = false
                        },
                        cancelText = MLang.AppSettings.Button.Cancel,
                        confirmText = MLang.AppSettings.Button.Apply,
                    )
                },
            )

            AppDialog(
                show = showCleanupIntervalDialog.value,
                modifier = Modifier,
                title = MLang.AppSettings.Cleanup.IntervalTitle,
                summary = MLang.AppSettings.Cleanup.IntervalRange,
                onDismissRequest = { showCleanupIntervalDialog.value = false },
                renderInRootScaffold = true,
                content = {
                    TextField(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        value = cleanupIntervalText,
                        maxLines = 1,
                        trailingIcon = {
                            Text(
                                text = MLang.AppSettings.Cleanup.IntervalUnit,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MiuixTheme.colorScheme.onSurfaceVariantActions,
                            )
                        },
                        onValueChange = { value ->
                            if (value.isEmpty() || value.all { it.isDigit() }) {
                                cleanupIntervalText = value
                            }
                        },
                    )
                    DialogButtonRow(
                        onCancel = { showCleanupIntervalDialog.value = false },
                        onConfirm = {
                            val parsed = cleanupIntervalText.toIntOrNull() ?: cleanupIntervalHours
                            viewModel.onCleanupIntervalHoursChange(parsed)
                            showCleanupIntervalDialog.value = false
                        },
                        cancelText = MLang.AppSettings.Button.Cancel,
                        confirmText = MLang.AppSettings.Button.Apply,
                    )
                },
            )
        }
    }
}

@Composable
private fun AppSettingsContent(
    automaticRestart: Boolean,
    autoUpdateCurrentProfileOnStart: Boolean,
    appLanguage: AppLanguage,
    themeMode: ThemeMode,
    themeSeedColorArgb: Long,
    bottomBarAutoHide: Boolean,
    topBarBlurEnabled: Boolean,
    bottomBarLiquidGlassEnabled: Boolean,
    pageScaleLabel: String,
    hideAppIcon: Boolean,
    excludeFromRecents: Boolean,
    showTrafficNotification: Boolean,
    singleNodeTest: Boolean,
    autoStartLogRecording: Boolean,
    customUserAgent: String,
    cleanupAutoEnabled: Boolean,
    cleanupPolicy: CleanupPolicy,
    cleanupThresholdMb: Int,
    cleanupIntervalHours: Int,
    cleanupLastRunSummary: String,
    onAutomaticRestartChange: (Boolean) -> Unit,
    onAutoUpdateCurrentProfileOnStartChange: (Boolean) -> Unit,
    onAppLanguageChange: (AppLanguage) -> Unit,
    onThemeModeChange: (ThemeMode) -> Unit,
    onThemeSeedColorChange: (Long) -> Unit,
    onBottomBarAutoHideChange: (Boolean) -> Unit,
    onTopBarBlurEnabledChange: (Boolean) -> Unit,
    onBottomBarLiquidGlassEnabledChange: (Boolean) -> Unit,
    onOpenPageScaleDialog: () -> Unit,
    onHideAppIconChange: (Boolean) -> Unit,
    onExcludeFromRecentsChange: (Boolean) -> Unit,
    onShowTrafficNotificationChange: (Boolean) -> Unit,
    onSingleNodeTestChange: (Boolean) -> Unit,
    onAutoStartLogRecordingChange: (Boolean) -> Unit,
    onEditCustomUserAgent: () -> Unit,
    onCleanupAutoEnabledChange: (Boolean) -> Unit,
    onCleanupPolicyChange: (CleanupPolicy) -> Unit,
    onOpenCleanupThresholdDialog: () -> Unit,
    onOpenCleanupIntervalDialog: () -> Unit,
    onRunCleanupNow: () -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val availableAdaptiveInfo = rememberAvailableWindowAdaptiveInfo(maxWidth, maxHeight)
        val isWideLayout = availableAdaptiveInfo.prefersTwoPaneContent
        val sectionSpacing = 16.dp

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(sectionSpacing),
        ) {
            if (isWideLayout) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(sectionSpacing),
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(sectionSpacing),
                    ) {
                        BehaviorSettingsSection(
                            automaticRestart = automaticRestart,
                            autoUpdateCurrentProfileOnStart = autoUpdateCurrentProfileOnStart,
                            onAutomaticRestartChange = onAutomaticRestartChange,
                            onAutoUpdateCurrentProfileOnStartChange =
                                onAutoUpdateCurrentProfileOnStartChange,
                        )
                        InterfaceSettingsSection(
                            appLanguage = appLanguage,
                            themeMode = themeMode,
                            themeSeedColorArgb = themeSeedColorArgb,
                            bottomBarAutoHide = bottomBarAutoHide,
                            topBarBlurEnabled = topBarBlurEnabled,
                            bottomBarLiquidGlassEnabled = bottomBarLiquidGlassEnabled,
                            pageScaleLabel = pageScaleLabel,
                            hideAppIcon = hideAppIcon,
                            excludeFromRecents = excludeFromRecents,
                            onAppLanguageChange = onAppLanguageChange,
                            onThemeModeChange = onThemeModeChange,
                            onThemeSeedColorChange = onThemeSeedColorChange,
                            onBottomBarAutoHideChange = onBottomBarAutoHideChange,
                            onTopBarBlurEnabledChange = onTopBarBlurEnabledChange,
                            onBottomBarLiquidGlassEnabledChange =
                                onBottomBarLiquidGlassEnabledChange,
                            onOpenPageScaleDialog = onOpenPageScaleDialog,
                            onHideAppIconChange = onHideAppIconChange,
                            onExcludeFromRecentsChange = onExcludeFromRecentsChange,
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(sectionSpacing),
                    ) {
                        ServiceSettingsSection(
                            showTrafficNotification = showTrafficNotification,
                            singleNodeTest = singleNodeTest,
                            autoStartLogRecording = autoStartLogRecording,
                            onShowTrafficNotificationChange = onShowTrafficNotificationChange,
                            onSingleNodeTestChange = onSingleNodeTestChange,
                            onAutoStartLogRecordingChange = onAutoStartLogRecordingChange,
                        )
                        NetworkSettingsSection(
                            customUserAgent = customUserAgent,
                            onEditCustomUserAgent = onEditCustomUserAgent,
                        )
                        CleanupSettingsSection(
                            cleanupAutoEnabled = cleanupAutoEnabled,
                            cleanupPolicy = cleanupPolicy,
                            cleanupThresholdMb = cleanupThresholdMb,
                            cleanupIntervalHours = cleanupIntervalHours,
                            cleanupLastRunSummary = cleanupLastRunSummary,
                            onCleanupAutoEnabledChange = onCleanupAutoEnabledChange,
                            onCleanupPolicyChange = onCleanupPolicyChange,
                            onOpenCleanupThresholdDialog = onOpenCleanupThresholdDialog,
                            onOpenCleanupIntervalDialog = onOpenCleanupIntervalDialog,
                            onRunCleanupNow = onRunCleanupNow,
                        )
                    }
                }
            } else {
                BehaviorSettingsSection(
                    automaticRestart = automaticRestart,
                    autoUpdateCurrentProfileOnStart = autoUpdateCurrentProfileOnStart,
                    onAutomaticRestartChange = onAutomaticRestartChange,
                    onAutoUpdateCurrentProfileOnStartChange =
                        onAutoUpdateCurrentProfileOnStartChange,
                )
                InterfaceSettingsSection(
                    appLanguage = appLanguage,
                    themeMode = themeMode,
                    themeSeedColorArgb = themeSeedColorArgb,
                    bottomBarAutoHide = bottomBarAutoHide,
                    topBarBlurEnabled = topBarBlurEnabled,
                    bottomBarLiquidGlassEnabled = bottomBarLiquidGlassEnabled,
                    pageScaleLabel = pageScaleLabel,
                    hideAppIcon = hideAppIcon,
                    excludeFromRecents = excludeFromRecents,
                    onAppLanguageChange = onAppLanguageChange,
                    onThemeModeChange = onThemeModeChange,
                    onThemeSeedColorChange = onThemeSeedColorChange,
                    onBottomBarAutoHideChange = onBottomBarAutoHideChange,
                    onTopBarBlurEnabledChange = onTopBarBlurEnabledChange,
                    onBottomBarLiquidGlassEnabledChange = onBottomBarLiquidGlassEnabledChange,
                    onOpenPageScaleDialog = onOpenPageScaleDialog,
                    onHideAppIconChange = onHideAppIconChange,
                    onExcludeFromRecentsChange = onExcludeFromRecentsChange,
                )
                ServiceSettingsSection(
                    showTrafficNotification = showTrafficNotification,
                    singleNodeTest = singleNodeTest,
                    autoStartLogRecording = autoStartLogRecording,
                    onShowTrafficNotificationChange = onShowTrafficNotificationChange,
                    onSingleNodeTestChange = onSingleNodeTestChange,
                    onAutoStartLogRecordingChange = onAutoStartLogRecordingChange,
                )
                NetworkSettingsSection(
                    customUserAgent = customUserAgent,
                    onEditCustomUserAgent = onEditCustomUserAgent,
                )
                CleanupSettingsSection(
                    cleanupAutoEnabled = cleanupAutoEnabled,
                    cleanupPolicy = cleanupPolicy,
                    cleanupThresholdMb = cleanupThresholdMb,
                    cleanupIntervalHours = cleanupIntervalHours,
                    cleanupLastRunSummary = cleanupLastRunSummary,
                    onCleanupAutoEnabledChange = onCleanupAutoEnabledChange,
                    onCleanupPolicyChange = onCleanupPolicyChange,
                    onOpenCleanupThresholdDialog = onOpenCleanupThresholdDialog,
                    onOpenCleanupIntervalDialog = onOpenCleanupIntervalDialog,
                    onRunCleanupNow = onRunCleanupNow,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun BehaviorSettingsSection(
    automaticRestart: Boolean,
    autoUpdateCurrentProfileOnStart: Boolean,
    onAutomaticRestartChange: (Boolean) -> Unit,
    onAutoUpdateCurrentProfileOnStartChange: (Boolean) -> Unit,
) {
    SmallTitle(MLang.AppSettings.Section.Behavior)
    Card {
        SuperSwitch(
            title = MLang.AppSettings.Behavior.AutoStartTitle,
            summary = MLang.AppSettings.Behavior.AutoStartSummary,
            checked = automaticRestart,
            onCheckedChange = onAutomaticRestartChange,
        )
        SuperSwitch(
            title = MLang.AppSettings.Behavior.AutoUpdateOnStartTitle,
            summary = MLang.AppSettings.Behavior.AutoUpdateOnStartSummary,
            checked = autoUpdateCurrentProfileOnStart,
            onCheckedChange = onAutoUpdateCurrentProfileOnStartChange,
        )
    }
}

@Composable
private fun InterfaceSettingsSection(
    appLanguage: AppLanguage,
    themeMode: ThemeMode,
    themeSeedColorArgb: Long,
    bottomBarAutoHide: Boolean,
    topBarBlurEnabled: Boolean,
    bottomBarLiquidGlassEnabled: Boolean,
    pageScaleLabel: String,
    hideAppIcon: Boolean,
    excludeFromRecents: Boolean,
    onAppLanguageChange: (AppLanguage) -> Unit,
    onThemeModeChange: (ThemeMode) -> Unit,
    onThemeSeedColorChange: (Long) -> Unit,
    onBottomBarAutoHideChange: (Boolean) -> Unit,
    onTopBarBlurEnabledChange: (Boolean) -> Unit,
    onBottomBarLiquidGlassEnabledChange: (Boolean) -> Unit,
    onOpenPageScaleDialog: () -> Unit,
    onHideAppIconChange: (Boolean) -> Unit,
    onExcludeFromRecentsChange: (Boolean) -> Unit,
) {
    SmallTitle(MLang.AppSettings.Section.Interface)
    Card {
        EnumSelector(
            title = MLang.AppSettings.Interface.LanguageTitle,
            summary = MLang.AppSettings.Interface.LanguageSummary,
            currentValue = appLanguage,
            items =
                listOf(
                    MLang.AppSettings.Interface.LanguageSystem,
                    MLang.AppSettings.Interface.LanguageChinese,
                    MLang.AppSettings.Interface.LanguageEnglish,
                ),
            values = AppLanguage.entries,
            onValueChange = onAppLanguageChange,
        )
        EnumSelector(
            title = MLang.AppSettings.Interface.ThemeModeTitle,
            summary = MLang.AppSettings.Interface.ThemeModeSummary,
            currentValue = themeMode,
            items =
                listOf(
                    MLang.AppSettings.Interface.ThemeModeSystem,
                    MLang.AppSettings.Interface.ThemeModeLight,
                    MLang.AppSettings.Interface.ThemeModeDark,
                ),
            values = ThemeMode.entries,
            onValueChange = onThemeModeChange,
        )
        ThemeColorPickerItem(
            themeSeedColorArgb = themeSeedColorArgb,
            onThemeSeedColorChange = onThemeSeedColorChange,
        )
        SuperSwitch(
            title = MLang.AppSettings.Interface.AutoHideNavbarTitle,
            summary = MLang.AppSettings.Interface.AutoHideNavbarSummary,
            checked = bottomBarAutoHide,
            onCheckedChange = onBottomBarAutoHideChange,
        )
        SuperSwitch(
            title = MLang.AppSettings.Interface.TopBarBlurTitle,
            summary = MLang.AppSettings.Interface.TopBarBlurSummary,
            checked = topBarBlurEnabled,
            onCheckedChange = onTopBarBlurEnabledChange,
        )
        SuperSwitch(
            title = MLang.AppSettings.Interface.BottomBarLiquidGlassTitle,
            summary = MLang.AppSettings.Interface.BottomBarLiquidGlassSummary,
            checked = bottomBarLiquidGlassEnabled,
            onCheckedChange = onBottomBarLiquidGlassEnabledChange,
        )
        ConfigSettingRow(
            title = MLang.AppSettings.Interface.PageScaleTitle,
            summary = MLang.AppSettings.Interface.PageScaleSummary,
            valueLabel = pageScaleLabel,
            tone = SemanticTone.Info,
            onClick = onOpenPageScaleDialog,
        )
        SuperSwitch(
            title = MLang.AppSettings.Interface.HideIconTitle,
            summary = MLang.AppSettings.Interface.HideIconSummary,
            checked = hideAppIcon,
            onCheckedChange = onHideAppIconChange,
        )
        SuperSwitch(
            title = MLang.AppSettings.Interface.HideFromRecentsTitle,
            summary = MLang.AppSettings.Interface.HideFromRecentsSummary,
            checked = excludeFromRecents,
            onCheckedChange = onExcludeFromRecentsChange,
        )
    }
}

@Composable
private fun ServiceSettingsSection(
    showTrafficNotification: Boolean,
    singleNodeTest: Boolean,
    autoStartLogRecording: Boolean,
    onShowTrafficNotificationChange: (Boolean) -> Unit,
    onSingleNodeTestChange: (Boolean) -> Unit,
    onAutoStartLogRecordingChange: (Boolean) -> Unit,
) {
    SmallTitle(MLang.AppSettings.Section.Service)
    Card {
        SuperSwitch(
            title = MLang.AppSettings.ServiceSection.TrafficNotificationTitle,
            summary = MLang.AppSettings.ServiceSection.TrafficNotificationSummary,
            checked = showTrafficNotification,
            onCheckedChange = onShowTrafficNotificationChange,
        )
        SuperSwitch(
            title = MLang.AppSettings.ServiceSection.SingleNodeTestTitle,
            summary = MLang.AppSettings.ServiceSection.SingleNodeTestSummary,
            checked = singleNodeTest,
            onCheckedChange = onSingleNodeTestChange,
        )
        SuperSwitch(
            title = MLang.AppSettings.ServiceSection.AutoStartLogRecordingTitle,
            summary = MLang.AppSettings.ServiceSection.AutoStartLogRecordingSummary,
            checked = autoStartLogRecording,
            onCheckedChange = onAutoStartLogRecordingChange,
        )
    }
}

@Composable
private fun NetworkSettingsSection(customUserAgent: String, onEditCustomUserAgent: () -> Unit) {
    SmallTitle(MLang.AppSettings.Section.Network)
    Card {
        ConfigSettingRow(
            title = MLang.AppSettings.Network.CustomUserAgentTitle,
            summary =
                customUserAgent.ifEmpty { MLang.AppSettings.Network.CustomUserAgentSummaryDefault },
            showDivider = false,
            onClick = onEditCustomUserAgent,
        )
    }
}

@Composable
private fun CleanupSettingsSection(
    cleanupAutoEnabled: Boolean,
    cleanupPolicy: CleanupPolicy,
    cleanupThresholdMb: Int,
    cleanupIntervalHours: Int,
    cleanupLastRunSummary: String,
    onCleanupAutoEnabledChange: (Boolean) -> Unit,
    onCleanupPolicyChange: (CleanupPolicy) -> Unit,
    onOpenCleanupThresholdDialog: () -> Unit,
    onOpenCleanupIntervalDialog: () -> Unit,
    onRunCleanupNow: () -> Unit,
) {
    SmallTitle(MLang.AppSettings.Section.Cleanup)
    Card {
        SuperSwitch(
            title = MLang.AppSettings.Cleanup.AutoEnabledTitle,
            summary = MLang.AppSettings.Cleanup.AutoEnabledSummary,
            checked = cleanupAutoEnabled,
            onCheckedChange = onCleanupAutoEnabledChange,
        )
        EnumSelector(
            title = MLang.AppSettings.Cleanup.PolicyTitle,
            summary = MLang.AppSettings.Cleanup.PolicySummary,
            currentValue = cleanupPolicy,
            items =
                listOf(
                    MLang.AppSettings.Cleanup.PolicyAggressive,
                    MLang.AppSettings.Cleanup.PolicyBalanced,
                    MLang.AppSettings.Cleanup.PolicyConservative,
                ),
            values = CleanupPolicy.entries,
            onValueChange = onCleanupPolicyChange,
        )
        ConfigSettingRow(
            title = MLang.AppSettings.Cleanup.ThresholdTitle,
            summary = MLang.AppSettings.Cleanup.ThresholdSummary.format(cleanupThresholdMb),
            onClick = onOpenCleanupThresholdDialog,
        )
        ConfigSettingRow(
            title = MLang.AppSettings.Cleanup.IntervalTitle,
            summary = MLang.AppSettings.Cleanup.IntervalSummary.format(cleanupIntervalHours),
            onClick = onOpenCleanupIntervalDialog,
        )
        ConfigSettingRow(
            title = MLang.AppSettings.Cleanup.CleanupNowTitle,
            summary = cleanupLastRunSummary,
            tone = SemanticTone.Warning,
            showDivider = false,
            onClick = onRunCleanupNow,
        )
    }
}
