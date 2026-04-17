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

package com.github.nomadboxlab.monadbox.screen.onboarding

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.nomadboxlab.monadbox.common.runtime.StartupGate
import com.github.nomadboxlab.monadbox.data.repository.AppSettingsRepository
import com.github.nomadboxlab.monadbox.feature.onboarding.R
import com.github.nomadboxlab.monadbox.presentation.theme.MonadTheme
import com.github.nomadboxlab.monadbox.presentation.theme.ProvideAndroidPlatformTheme
import com.github.nomadboxlab.monadbox.presentation.theme.rememberAdaptiveSpacing
import com.github.nomadboxlab.monadbox.presentation.theme.rememberAvailableWindowAdaptiveInfo
import org.koin.android.ext.android.inject
import org.koin.compose.koinInject
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.theme.MiuixTheme

internal abstract class OnboardingBaseActivity : ComponentActivity() {

    private val completionHandler: OnboardingCompletionHandler by inject()

    protected val previewMode: Boolean
        get() = OnboardingLauncher.isPreviewMode(intent)

    override fun onCreate(savedInstanceState: Bundle?) {
        StartupGate.loadPrimary()
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        super.onCreate(savedInstanceState)
    }

    protected fun setOnboardingContent(content: @Composable () -> Unit) {
        setContent { OnboardingActivityTheme { content() } }
    }

    protected fun buildOnboardingIntent(
        target: Class<out ComponentActivity>,
        resetPrivacy: Boolean = false,
    ): Intent {
        return OnboardingLauncher.createIntent(
            context = this,
            target = target,
            previewMode = previewMode,
            resetPrivacy = resetPrivacy,
        )
    }

    protected fun navigateForwardTo(target: Class<out ComponentActivity>) {
        startActivity(buildOnboardingIntent(target))
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(
                OVERRIDE_TRANSITION_OPEN,
                R.anim.onboarding_slide_in_right,
                R.anim.onboarding_slide_out_left,
            )
        } else {
            overridePendingTransition(
                R.anim.onboarding_slide_in_right,
                R.anim.onboarding_slide_out_left,
            )
        }
        finish()
    }

    protected fun navigateBackwardTo(target: Class<out ComponentActivity>) {
        startActivity(buildOnboardingIntent(target))
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(
                OVERRIDE_TRANSITION_OPEN,
                R.anim.onboarding_slide_in_left,
                R.anim.onboarding_slide_out_right,
            )
        } else {
            overridePendingTransition(
                R.anim.onboarding_slide_in_left,
                R.anim.onboarding_slide_out_right,
            )
        }
        finish()
    }

    protected fun finishOnboarding() {
        if (previewMode) {
            finish()
            return
        }
        completionHandler.finishOnboarding(this)
    }
}

@Composable
private fun OnboardingActivityTheme(content: @Composable () -> Unit) {
    val appSettings = koinInject<AppSettingsRepository>()
    val themeMode by appSettings.themeMode.state.collectAsStateWithLifecycle()
    val themeSeedColorArgb by appSettings.themeSeedColorArgb.state.collectAsStateWithLifecycle()
    val pageScale by appSettings.pageScale.state.collectAsStateWithLifecycle()

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
                val windowAdaptiveInfo = rememberAvailableWindowAdaptiveInfo(maxWidth, maxHeight)
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
                    Scaffold { _ ->
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MiuixTheme.colorScheme.surface,
                            content = content,
                        )
                    }
                }
            }
        }
    }
}
