/*
 * This file is part of MonadBox.
 *
 * MonadBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * Copyright (c) MonadBox Contributors 2026 - Present
 */

package com.github.nomadboxlab.monadbox.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.nomadboxlab.monadbox.core.model.TunnelState
import com.github.nomadboxlab.monadbox.data.model.ProxyMode
import com.github.nomadboxlab.monadbox.domain.model.ThemeMode
import com.github.nomadboxlab.monadbox.domain.model.TrafficData
import com.github.nomadboxlab.monadbox.feature.home.HomeRuntimeVisualState
import com.github.nomadboxlab.monadbox.feature.home.TrafficDisplay
import com.github.nomadboxlab.monadbox.presentation.component.ProfileCard
import com.github.nomadboxlab.monadbox.presentation.component.TestTags
import com.github.nomadboxlab.monadbox.presentation.theme.MonadTheme
import com.github.nomadboxlab.monadbox.service.runtime.entity.Profile
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CriticalPageSemanticsInstrumentedTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun homeTrafficControls_exposeStableSemanticsAndActions() {
        var statusClicks = 0
        var modeClicks = 0

        compose.setContent {
            MonadTheme(themeMode = ThemeMode.Light) {
                TrafficDisplay(
                    trafficNow = TrafficData(upload = 2_048, download = 4_096),
                    profileName = "Demo Profile",
                    tunnelMode = TunnelState.Mode.Rule,
                    runtimeVisualState = HomeRuntimeVisualState.Running,
                    canStartProxy = true,
                    isRunning = true,
                    proxyMode = ProxyMode.Tun,
                    onStatusCapsuleClick = { statusClicks += 1 },
                    onModeBadgeClick = { modeClicks += 1 },
                )
            }
        }

        compose.onNodeWithTag(TestTags.Home.DownloadSpeed).assertIsDisplayed()
        compose.onNodeWithTag(TestTags.Home.UploadSpeed).assertIsDisplayed()
        compose
            .onNodeWithTag(TestTags.Home.StatusCapsule)
            .assertIsDisplayed()
            .assertHasClickAction()
            .assert(hasContentDescriptionPart("VPN"))
            .assert(hasNonEmptyStateDescription())
            .performClick()
        compose
            .onNodeWithTag(TestTags.Home.ProfileModeBadge)
            .assertIsDisplayed()
            .assertHasClickAction()
            .assert(hasContentDescriptionPart("Demo Profile"))
            .performClick()

        compose.runOnIdle {
            assertEquals(1, statusClicks)
            assertEquals(1, modeClicks)
        }
    }

    @Test
    fun profileCard_exposesSelectionSemanticsAndClickAction() {
        val profileId = UUID.fromString("11111111-1111-1111-1111-111111111111")
        val profile =
            Profile(
                uuid = profileId,
                name = "Instrumented Profile",
                type = Profile.Type.File,
                source = "instrumented.yaml",
                active = true,
                interval = 0,
                upload = 0,
                download = 0,
                total = 0,
                expire = 0,
                updatedAt = 0,
            )
        var selectedProfileId: UUID? = null

        compose.setContent {
            MonadTheme(themeMode = ThemeMode.Light) {
                ProfileCard(
                    profile = profile,
                    workDir = compose.activity.cacheDir,
                    isSelected = true,
                    onSelect = { selectedProfileId = it.uuid },
                    onUpdate = {},
                    onEdit = {},
                    onMoreActions = {},
                )
            }
        }

        compose
            .onNodeWithTag(TestTags.Profiles.profileCard(profileId.toString()))
            .assertIsDisplayed()
            .assertHasClickAction()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, true))
            .assert(hasContentDescriptionPart("Instrumented Profile"))
            .performClick()

        compose.runOnIdle { assertEquals(profileId, selectedProfileId) }
    }

    private fun hasContentDescriptionPart(expected: String): SemanticsMatcher {
        return SemanticsMatcher("contentDescription contains '$expected'") { node ->
            node.config
                .getOrElseNullable(SemanticsProperties.ContentDescription) { null }
                ?.any { value -> value.contains(expected) } == true
        }
    }

    private fun hasNonEmptyStateDescription(): SemanticsMatcher {
        return SemanticsMatcher("stateDescription is not blank") { node ->
            !node.config
                .getOrElseNullable(SemanticsProperties.StateDescription) { null }
                .isNullOrBlank()
        }
    }
}
