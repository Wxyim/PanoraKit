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

package com.github.nomadboxlab.monadbox

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemGestures
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.github.nomadboxlab.monadbox.common.runtime.StartupGate
import com.github.nomadboxlab.monadbox.common.util.IntentController
import com.github.nomadboxlab.monadbox.common.util.ProfilesNavigationMetrics
import com.github.nomadboxlab.monadbox.core.StoreIds
import com.github.nomadboxlab.monadbox.domain.deeplink.DeepLinkBus
import com.github.nomadboxlab.monadbox.feature.home.HomeRoute
import com.github.nomadboxlab.monadbox.feature.profiles.ProfilesPagerBody
import com.github.nomadboxlab.monadbox.feature.settings.AppSettingsViewModel
import com.github.nomadboxlab.monadbox.presentation.component.BottomBarContent
import com.github.nomadboxlab.monadbox.presentation.component.BottomBarLayoutDefaults
import com.github.nomadboxlab.monadbox.presentation.component.LocalBottomBarLiquidState
import com.github.nomadboxlab.monadbox.presentation.component.LocalBottomBarOverlayPadding
import com.github.nomadboxlab.monadbox.presentation.component.LocalBottomBarScrollBehavior
import com.github.nomadboxlab.monadbox.presentation.component.LocalHandlePageChange
import com.github.nomadboxlab.monadbox.presentation.component.LocalNavigator
import com.github.nomadboxlab.monadbox.presentation.component.LocalPagerState
import com.github.nomadboxlab.monadbox.presentation.component.LocalTopBarHazeState
import com.github.nomadboxlab.monadbox.presentation.component.LocalTopBarHazeStyle
import com.github.nomadboxlab.monadbox.presentation.component.RuntimeFailureDialogEffect
import com.github.nomadboxlab.monadbox.presentation.component.SideRailContent
import com.github.nomadboxlab.monadbox.presentation.component.ToastDialogHost
import com.github.nomadboxlab.monadbox.presentation.component.rememberBottomBarScrollBehavior
import com.github.nomadboxlab.monadbox.presentation.runtime.VpnPermissionCoordinator
import com.github.nomadboxlab.monadbox.presentation.runtime.VpnPermissionHost
import com.github.nomadboxlab.monadbox.presentation.screen.ProxyPager
import com.github.nomadboxlab.monadbox.presentation.theme.LocalWindowAdaptiveInfo
import com.github.nomadboxlab.monadbox.presentation.theme.MonadTheme
import com.github.nomadboxlab.monadbox.presentation.theme.NavigationTransitions
import com.github.nomadboxlab.monadbox.presentation.theme.ProvideAndroidPlatformTheme
import com.github.nomadboxlab.monadbox.presentation.theme.rememberAdaptiveSpacing
import com.github.nomadboxlab.monadbox.presentation.theme.rememberAvailableWindowAdaptiveInfo
import com.github.nomadboxlab.monadbox.runtime.client.usecase.AutoStartProxyUseCase
import com.github.nomadboxlab.monadbox.runtime.contract.RuntimeFailurePresenter
import com.github.nomadboxlab.monadbox.screen.onboarding.OnboardingLauncher
import com.github.nomadboxlab.monadbox.screen.settings.SettingPager
import com.github.nomadboxlab.monadbox.startup.StartupConfigRefreshCoordinator
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.LocalProfileConfigEditRouteDestination
import com.ramcosta.composedestinations.generated.destinations.OverrideConfigPreviewRouteDestination
import com.ramcosta.composedestinations.generated.destinations.ProvidersScreenDestination
import com.ramcosta.composedestinations.generated.destinations.TrafficStatisticsScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.tencent.mmkv.MMKV
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeSource
import io.github.fletchmckee.liquid.liquefiable
import io.github.fletchmckee.liquid.rememberLiquidState
import kotlin.math.max
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.qualifier.named
import timber.log.Timber
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.theme.MiuixTheme

class MainActivity : ComponentActivity() {

    private val appSettingsStorage: com.github.nomadboxlab.monadbox.data.store.AppSettingsStorage by
        inject()
    private val profilesRepository:
        com.github.nomadboxlab.monadbox.runtime.client.ProfilesRepository by
        inject()
    private val runtimeControlCoordinator:
        com.github.nomadboxlab.monadbox.runtime.client.RuntimeControlCoordinator by
        inject()
    private val proxyFacade: com.github.nomadboxlab.monadbox.runtime.client.ProxyFacade by inject()
    private val startupConfigRefreshCoordinator: StartupConfigRefreshCoordinator by inject()
    private val vpnPermissionCoordinator: VpnPermissionCoordinator by inject()
    private val runtimeFailurePresenter: RuntimeFailurePresenter by inject()
    private val deepLinkBus: DeepLinkBus by inject()
    private val autoStartProxyUseCase: AutoStartProxyUseCase by inject()

    private lateinit var intentController: IntentController

    override fun onCreate(savedInstanceState: Bundle?) {
        StartupGate.loadPrimary()
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        super.onCreate(savedInstanceState)
        applyExcludeFromRecents(appSettingsStorage.excludeFromRecents.value)

        intentController = IntentController(this, lifecycleScope)
        handleIntent(intent)

        if (!appSettingsStorage.initialSetupCompleted.value) {
            OnboardingLauncher.start(this, previewMode = false)
            finish()
            return
        }

        (application as? App)?.ensureDeferredStartupInitialized()

        setContent {
            val appSettingsViewModel = koinViewModel<AppSettingsViewModel>()
            val themeMode = appSettingsViewModel.themeMode.state.collectAsStateWithLifecycle().value
            val themeSeedColorArgb =
                appSettingsViewModel.themeSeedColorArgb.state.collectAsStateWithLifecycle().value
            val excludeFromRecents =
                appSettingsViewModel.excludeFromRecents.state.collectAsStateWithLifecycle().value
            val topBarBlurEnabled =
                appSettingsViewModel.topBarBlurEnabled.state.collectAsStateWithLifecycle().value
            val pageScale = appSettingsViewModel.pageScale.state.collectAsStateWithLifecycle().value

            LaunchedEffect(excludeFromRecents) {
                this@MainActivity.applyExcludeFromRecents(excludeFromRecents)
            }

            ProvideAndroidPlatformTheme {
                val systemDensity = LocalDensity.current
                val scaledDensity =
                    remember(systemDensity, pageScale) {
                        Density(
                            density = systemDensity.density,
                            fontScale = systemDensity.fontScale * pageScale,
                        )
                    }
                CompositionLocalProvider(LocalDensity provides scaledDensity) {
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val windowAdaptiveInfo =
                            rememberAvailableWindowAdaptiveInfo(maxWidth, maxHeight)
                        val adaptiveSpacing =
                            rememberAdaptiveSpacing(
                                windowAdaptiveInfo = windowAdaptiveInfo,
                                pageScale = pageScale,
                            )
                        MonadTheme(
                            themeMode = themeMode,
                            themeSeedColorArgb = themeSeedColorArgb,
                            spacing = adaptiveSpacing,
                            windowAdaptiveInfo = windowAdaptiveInfo,
                        ) {
                            val topBarHazeState = remember { HazeState() }
                            val topBarBackground = MiuixTheme.colorScheme.surface
                            val topBarHazeStyle =
                                remember(topBarBackground) {
                                    HazeStyle(
                                        backgroundColor = topBarBackground,
                                        tint = HazeTint(topBarBackground.copy(0.8f)),
                                    )
                                }
                            val navController = rememberNavController()

                            CompositionLocalProvider(
                                LocalTopBarHazeState provides
                                    if (topBarBlurEnabled) topBarHazeState else null,
                                LocalTopBarHazeStyle provides
                                    if (topBarBlurEnabled) topBarHazeStyle else null,
                            ) {
                                Scaffold(
                                    modifier = Modifier.fillMaxSize(),
                                    containerColor = MiuixTheme.colorScheme.surface,
                                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                                ) { _ ->
                                    RuntimeFailureDialogEffect(
                                        runtimeFailureEvents = proxyFacade.runtimeFailureEvents,
                                        presenter = runtimeFailurePresenter,
                                    )
                                    VpnPermissionHost(
                                        coordinator = vpnPermissionCoordinator,
                                        runtimeFailurePresenter = runtimeFailurePresenter,
                                    )
                                    DestinationsNavHost(
                                        navGraph = NavGraphs.root,
                                        navController = navController,
                                        defaultTransitions = NavigationTransitions.defaultStyle,
                                    )
                                    ToastDialogHost()
                                }
                            }
                        }
                    }
                }
            }

            LaunchedEffect(Unit) {
                val autoUpdateOnStart = appSettingsStorage.autoUpdateCurrentProfileOnStart.value
                val preRefreshedProfile =
                    if (autoUpdateOnStart) {
                        startupConfigRefreshCoordinator.refreshProfileAndBoundOverridesOnStart()
                    } else {
                        null
                    }

                autoStartProxyUseCase.execute(
                    automaticRestartEnabled = appSettingsStorage.automaticRestart.value,
                    preRefreshedProfile = preRefreshedProfile,
                )

                if (autoUpdateOnStart) {
                    startupConfigRefreshCoordinator.refreshRuntimeProvidersIfAvailableOnStart()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        intent?.let { safeIntent ->
            safeIntent.data?.let { uri ->
                val scheme = uri.scheme
                if (scheme == "clash" || scheme == "clashmeta") {
                    val host = uri.host
                    if (host == "install-config") {
                        val configUrl = sanitizeImportUrl(uri.getQueryParameter("url"))
                        if (configUrl != null) {
                            deepLinkBus.postImportUrl(configUrl)
                        }
                    }
                }
            }

            intentController.handleIntent(safeIntent)
        }
    }

    private fun sanitizeImportUrl(rawUrl: String?): String? {
        val candidate = rawUrl?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        if (candidate.length > 2048) {
            return null
        }
        val parsed = runCatching { android.net.Uri.parse(candidate) }.getOrNull() ?: return null
        val normalizedScheme = parsed.scheme?.lowercase()
        if (normalizedScheme != "http" && normalizedScheme != "https") {
            return null
        }
        if (parsed.host.isNullOrBlank()) {
            return null
        }
        if (candidate.any { it.isISOControl() }) {
            return null
        }
        return candidate
    }

    @Suppress("DEPRECATION")
    private fun applyExcludeFromRecents(exclude: Boolean) {

        runCatching {
            val am = getSystemService(ActivityManager::class.java) ?: return@runCatching
            val currentTaskId = taskId
            val task =
                am.appTasks.firstOrNull { appTask: ActivityManager.AppTask ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        appTask.taskInfo.taskId == currentTaskId
                    } else {
                        appTask.taskInfo.id == currentTaskId
                    }
                }
            task?.setExcludeFromRecents(exclude)
        }
    }
}

@Composable
@Destination<RootGraph>
fun MainScreen(navigator: DestinationsNavigator, initialPage: Int = 0) {
    val settingsMmkv: MMKV = koinInject(qualifier = named(StoreIds.SETTINGS))
    val adaptiveInfo = LocalWindowAdaptiveInfo.current
    val useRailNavigation = adaptiveInfo.useRailNavigation
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = initialPage.coerceIn(0, 3), pageCount = { 4 })
    val hazeState = remember { HazeState() }
    val bottomBarLiquidState = if (useRailNavigation) null else rememberLiquidState()

    val appSettingsViewModel = koinViewModel<AppSettingsViewModel>()
    val bottomBarAutoHideEnabled by
        appSettingsViewModel.bottomBarAutoHide.state.collectAsStateWithLifecycle()
    val bottomBarLiquidGlassEnabled by
        appSettingsViewModel.bottomBarLiquidGlassEnabled.state.collectAsStateWithLifecycle()

    val bottomBarScrollBehavior =
        if (useRailNavigation) {
            null
        } else {
            rememberBottomBarScrollBehavior(autoHideEnabled = bottomBarAutoHideEnabled)
        }

    LaunchedEffect(useRailNavigation, pagerState.currentPage, bottomBarScrollBehavior) {
        if (!useRailNavigation) {
            bottomBarScrollBehavior?.showBottomBar(force = true)
        }
    }

    androidx.lifecycle.compose.LifecycleResumeEffect(bottomBarScrollBehavior) {
        if (!useRailNavigation) {
            bottomBarScrollBehavior?.showBottomBar(force = true)
        }
        onPauseOrDispose {}
    }

    var pageChangeJob by remember { mutableStateOf<Job?>(null) }

    val handlePageChange: (Int) -> Unit =
        remember(pagerState, coroutineScope) {
            { targetPage ->
                val boundedTargetPage = targetPage.coerceIn(0, 3)
                if (pagerState.isScrollInProgress) return@remember
                if (boundedTargetPage == pagerState.currentPage) return@remember

                pageChangeJob?.cancel()
                pageChangeJob =
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(page = boundedTargetPage)
                    }
            }
        }

    CompositionLocalProvider(
        LocalPagerState provides pagerState,
        LocalHandlePageChange provides handlePageChange,
        LocalNavigator provides navigator,
        LocalBottomBarScrollBehavior provides bottomBarScrollBehavior,
        LocalBottomBarOverlayPadding provides
            BottomBarLayoutDefaults.overlayPadding(useRailNavigation),
        LocalBottomBarLiquidState provides
            if (!useRailNavigation && bottomBarLiquidGlassEnabled) bottomBarLiquidState else null,
    ) {
        Scaffold { innerPadding ->
            val density = LocalDensity.current
            val layoutDirection = LocalLayoutDirection.current
            val safeDrawingPadding = WindowInsets.safeDrawing.asPaddingValues()
            val safeTopInset = safeDrawingPadding.calculateTopPadding()
            val safeStartInset = safeDrawingPadding.calculateStartPadding(layoutDirection)
            val safeEndInset = safeDrawingPadding.calculateEndPadding(layoutDirection)
            val systemBottomInset =
                with(density) {
                    val navBottom = WindowInsets.navigationBars.getBottom(this)
                    val gestureBottom = WindowInsets.systemGestures.getBottom(this)
                    max(navBottom, gestureBottom).toDp()
                }
            val safeBottomInset =
                if (safeDrawingPadding.calculateBottomPadding() > systemBottomInset) {
                    safeDrawingPadding.calculateBottomPadding()
                } else {
                    systemBottomInset
                }
            val bottomBarReservedHeight = LocalBottomBarOverlayPadding.current
            val safeMainPadding =
                PaddingValues(
                    top = innerPadding.calculateTopPadding(),
                    bottom =
                        innerPadding.calculateBottomPadding() +
                            bottomBarReservedHeight +
                            safeBottomInset,
                    start = if (useRailNavigation) 0.dp else safeStartInset,
                    end = safeEndInset,
                )
            val pagerModifier =
                Modifier.fillMaxSize()
                    .hazeSource(state = hazeState)
                    .then(
                        if (bottomBarLiquidState != null && bottomBarLiquidGlassEnabled) {
                            Modifier.liquefiable(bottomBarLiquidState)
                        } else {
                            Modifier
                        }
                    )

            val pagerContent: @Composable (Modifier) -> Unit = { modifier ->
                val pageContent: @Composable (Int) -> Unit = { page ->
                    when (page) {
                        0 ->
                            HomeRoute(
                                mainInnerPadding = safeMainPadding,
                                isActive = page == pagerState.currentPage,
                                onNavigateToTrafficStatistics = {
                                    navigator.navigate(TrafficStatisticsScreenDestination) {
                                        launchSingleTop = true
                                    }
                                },
                            )
                        1 ->
                            ProxyPager(
                                mainInnerPadding = safeMainPadding,
                                onNavigateToProviders = {
                                    navigator.navigate(ProvidersScreenDestination) {
                                        launchSingleTop = true
                                    }
                                },
                                isActive = page == pagerState.currentPage,
                            )

                        2 ->
                            ProfilesPagerBody(
                                mainInnerPadding = safeMainPadding,
                                onNavigateToLocalProfileEdit = { profileUuid ->
                                    ProfilesNavigationMetrics.onGuiEditClick(settingsMmkv)
                                    Timber.tag("ProfilesNav")
                                        .d(
                                            "event=profiles_local_gui_edit_click profileUuid=%s",
                                            profileUuid,
                                        )
                                    runCatching {
                                            navigator.navigate(
                                                LocalProfileConfigEditRouteDestination(
                                                    profileUuid = profileUuid
                                                )
                                            ) {
                                                launchSingleTop = true
                                            }
                                        }
                                        .onSuccess {
                                            ProfilesNavigationMetrics.onGuiEditNavigationDispatched(
                                                settingsMmkv
                                            )
                                            Timber.tag("ProfilesNav")
                                                .i(
                                                    "event=profiles_local_gui_edit_nav_dispatch_success profileUuid=%s",
                                                    profileUuid,
                                                )
                                        }
                                        .onFailure { error ->
                                            ProfilesNavigationMetrics
                                                .onGuiEditNavigationDispatchFailure(
                                                    settingsMmkv,
                                                    error,
                                                )
                                            Timber.tag("ProfilesNav")
                                                .w(
                                                    error,
                                                    "event=profiles_local_gui_edit_nav_dispatch_failed profileUuid=%s",
                                                    profileUuid,
                                                )
                                        }
                                },
                                onNavigateToOverrideConfigPreview = {
                                    ProfilesNavigationMetrics.onTextEditorClick(settingsMmkv)
                                    Timber.tag("ProfilesNav").d("event=profiles_text_editor_click")
                                    runCatching {
                                            navigator.navigate(
                                                OverrideConfigPreviewRouteDestination
                                            ) {
                                                launchSingleTop = true
                                            }
                                        }
                                        .onSuccess {
                                            ProfilesNavigationMetrics
                                                .onTextEditorNavigationDispatched(settingsMmkv)
                                            Timber.tag("ProfilesNav")
                                                .i(
                                                    "event=profiles_text_editor_nav_dispatch_success"
                                                )
                                        }
                                        .onFailure { error ->
                                            ProfilesNavigationMetrics
                                                .onTextEditorNavigationDispatchFailure(
                                                    settingsMmkv,
                                                    error,
                                                )
                                            Timber.tag("ProfilesNav")
                                                .w(
                                                    error,
                                                    "event=profiles_text_editor_nav_dispatch_failed",
                                                )
                                        }
                                },
                            )
                        3 -> SettingPager(safeMainPadding)
                    }
                }

                if (adaptiveInfo.useVerticalPageSwitch) {
                    VerticalPager(
                        modifier = modifier,
                        state = pagerState,
                        beyondViewportPageCount = 0,
                        userScrollEnabled = false,
                        pageNestedScrollConnection =
                            PagerDefaults.pageNestedScrollConnection(
                                state = pagerState,
                                orientation =
                                    androidx.compose.foundation.gestures.Orientation.Vertical,
                            ),
                    ) { page ->
                        pageContent(page)
                    }
                } else {
                    HorizontalPager(
                        modifier = modifier,
                        state = pagerState,
                        beyondViewportPageCount = 0,
                        userScrollEnabled = true,
                        pageNestedScrollConnection =
                            PagerDefaults.pageNestedScrollConnection(
                                state = pagerState,
                                orientation =
                                    androidx.compose.foundation.gestures.Orientation.Horizontal,
                            ),
                    ) { page ->
                        pageContent(page)
                    }
                }
            }

            if (useRailNavigation) {
                Row(Modifier.fillMaxSize()) {
                    Box(
                        modifier =
                            Modifier.padding(
                                    start = safeStartInset + 12.dp,
                                    top = safeTopInset + 12.dp,
                                    end = 12.dp,
                                    bottom = safeBottomInset + 12.dp,
                                )
                                .fillMaxHeight(),
                        contentAlignment = Alignment.Center,
                    ) {
                        SideRailContent()
                    }
                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        pagerContent(pagerModifier)
                    }
                }
            } else {
                Box(Modifier.fillMaxSize()) {
                    pagerContent(pagerModifier)

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                        BottomBarContent()
                    }
                }
            }
        }
    }
}

private tailrec fun Context.findActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
