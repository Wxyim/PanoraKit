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

package com.github.nomadboxlab.monadbox.feature.about

import androidx.activity.compose.BackHandler
import androidx.annotation.RawRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import com.github.nomadboxlab.monadbox.presentation.component.AppActionBottomSheet
import com.github.nomadboxlab.monadbox.presentation.component.Card
import com.github.nomadboxlab.monadbox.presentation.component.ScreenLazyColumn
import com.github.nomadboxlab.monadbox.presentation.component.TopBar
import com.github.nomadboxlab.monadbox.presentation.component.appClickable
import com.github.nomadboxlab.monadbox.presentation.theme.AppTheme
import com.github.nomadboxlab.monadbox.presentation.theme.LocalPageMetrics
import com.github.nomadboxlab.monadbox.presentation.theme.adaptiveContentWidth
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.util.strippedLicenseContent
import dev.oom_wg.purejoy.mlang.MLang
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * Body of the Open-Source Licenses screen. The `@RawRes` parameter carries the app's generated
 * `R.raw.aboutlibraries` id, which is produced by the aboutLibraries plugin applied in :app.
 * Passing the id (rather than resolving by name at runtime) is robust against R8 resource
 * shrinking/obfuscation.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OpenSourceLicensesScreenBody(
    @RawRes aboutLibrariesRawResId: Int,
    navigationIcon: @Composable () -> Unit,
    onBack: () -> Unit,
) {
    val scrollBehavior = MiuixScrollBehavior()
    val spacing = AppTheme.spacing
    val pageMetrics = LocalPageMetrics.current
    val showLicenseSheet = remember { mutableStateOf(false) }
    val selectedLibrary = remember { mutableStateOf<Library?>(null) }

    BackHandler { onBack() }

    val libraries by produceLibraries(aboutLibrariesRawResId)

    Scaffold(
        topBar = {
            TopBar(
                title = MLang.OpenSourceLicenses.Title,
                scrollBehavior = scrollBehavior,
                navigationIcon = navigationIcon,
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                ScreenLazyColumn(
                    modifier = Modifier.adaptiveContentWidth(pageMetrics.contentMaxWidth),
                    scrollBehavior = scrollBehavior,
                    innerPadding = innerPadding,
                    topPadding = spacing.xl,
                    bottomPadding = spacing.xxl,
                ) {
                    libraries?.libraries?.let { libs ->
                        items(libs.size) { index ->
                            val library = libs[index]
                            LibraryItem(
                                library = library,
                                onClick = {
                                    selectedLibrary.value = library
                                    showLicenseSheet.value = true
                                },
                            )
                        }
                    }
                }
            }

            selectedLibrary.value?.let { library ->
                LicenseBottomSheet(
                    show = showLicenseSheet,
                    library = library,
                    onDismiss = { showLicenseSheet.value = false },
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LibraryItem(library: Library, onClick: () -> Unit) {
    val spacing = AppTheme.spacing

    Card(
        modifier = Modifier.padding(bottom = spacing.md),
        insideMargin = PaddingValues(spacing.none),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().appClickable(onClick = onClick).padding(spacing.lg),
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = library.name,
                    style = MiuixTheme.textStyles.body1,
                    color = MiuixTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                library.artifactVersion?.let { version ->
                    Text(
                        text = version,
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        modifier = Modifier.padding(start = spacing.md),
                    )
                }
            }

            library.developers.firstOrNull()?.name?.let { author ->
                Text(
                    text = author,
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )
            }

            if (library.licenses.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                    verticalArrangement = Arrangement.spacedBy(spacing.xs),
                ) {
                    library.licenses.forEach { license -> LicenseChip(licenseName = license.name) }
                }
            }
        }
    }
}

@Composable
private fun LicenseChip(licenseName: String) {
    val spacing = AppTheme.spacing
    val radii = AppTheme.radii

    Box(
        modifier =
            Modifier.clip(RoundedCornerShape(radii.lg))
                .background(MiuixTheme.colorScheme.primary.copy(alpha = 0.1f))
                .padding(horizontal = spacing.sm, vertical = spacing.xs)
    ) {
        Text(
            text = licenseName,
            style = MiuixTheme.textStyles.body2,
            color = MiuixTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun LicenseBottomSheet(
    show: MutableState<Boolean>,
    library: Library,
    onDismiss: () -> Unit,
) {
    val spacing = AppTheme.spacing
    val pageMetrics = LocalPageMetrics.current
    val scrollState = rememberScrollState()
    val licenseContent =
        remember(library) { library.strippedLicenseContent.takeIf { it.isNotEmpty() } }

    AppActionBottomSheet(
        show = show.value,
        title = library.name,
        onDismissRequest = onDismiss,
        content = {
            Column(
                modifier =
                    Modifier.fillMaxWidth()
                        .heightIn(max = pageMetrics.openSourceLicenseSheetMaxHeight)
            ) {
                if (licenseContent != null) {
                    Text(
                        modifier = Modifier.verticalScroll(scrollState),
                        text = licenseContent,
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurface,
                    )
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        insideMargin = PaddingValues(spacing.lg),
                    ) {
                        Text(
                            text = MLang.OpenSourceLicenses.LicenseSheet.NoContent,
                            style = MiuixTheme.textStyles.body2,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        )
                    }
                }
            }
        },
    )
}
