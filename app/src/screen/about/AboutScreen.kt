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

package com.github.yumelira.yumebox.screen.about

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.nomadboxlab.monadbox.BuildConfig
import com.github.nomadboxlab.monadbox.R
import com.github.yumelira.yumebox.presentation.component.Card
import com.github.yumelira.yumebox.presentation.component.ConfigSettingRow
import com.github.yumelira.yumebox.presentation.component.LinkItem
import com.github.yumelira.yumebox.presentation.component.NavigationBackIcon
import com.github.yumelira.yumebox.presentation.component.ScreenLazyColumn
import com.github.yumelira.yumebox.presentation.component.SmallTitle
import com.github.yumelira.yumebox.presentation.component.TopBar
import com.github.yumelira.yumebox.presentation.theme.adaptiveContentWidth
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.OpenSourceLicensesScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.oom_wg.purejoy.mlang.MLang
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.theme.MiuixTheme

private object AboutPageMetrics {
    val HeroTopSpacing = 24.dp
    val HeroIconSize = 120.dp
    val HeroIconCornerRadius = 24.dp
    val HeroSectionSpacing = 24.dp
    val HeroMetaSpacing = 8.dp
    val FooterTopPadding = 32.dp
    val FooterBottomSpacing = 32.dp
    val ContentMaxWidth = 840.dp
}

private object AboutProjectLinks {
    const val MonadBoxRepo = "https://github.com/NomadBoxLab/NomadBox"
    const val MonadBoxLatestRelease = "https://github.com/NomadBoxLab/NomadBox/releases/latest"
    const val MihomoRepo = "https://github.com/MetaCubeX/mihomo"
    const val YumeBoxUpstreamRepo = "https://github.com/YumeLira/YumeBox"
}

@Composable
@Destination<RootGraph>
fun AboutScreen(navigator: DestinationsNavigator) {
    val scrollBehavior = MiuixScrollBehavior()

    Scaffold(
        topBar = {
            TopBar(
                title = MLang.About.Title,
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
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            ScreenLazyColumn(
                modifier = Modifier.adaptiveContentWidth(AboutPageMetrics.ContentMaxWidth),
                scrollBehavior = scrollBehavior,
                innerPadding = innerPadding,
            ) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(modifier = Modifier.height(AboutPageMetrics.HeroTopSpacing))

                        Icon(
                            painter = painterResource(id = R.drawable.monadbox_about_logo),
                            contentDescription = null,
                            modifier =
                                Modifier.size(AboutPageMetrics.HeroIconSize)
                                    .clip(
                                        RoundedCornerShape(AboutPageMetrics.HeroIconCornerRadius)
                                    ),
                            tint = Color.Unspecified,
                        )

                        Spacer(modifier = Modifier.height(AboutPageMetrics.HeroSectionSpacing))

                        Text(text = MLang.About.App.Name, style = MiuixTheme.textStyles.title1)

                        Spacer(modifier = Modifier.height(AboutPageMetrics.HeroMetaSpacing))

                        Text(
                            text =
                                MLang.About.App.VersionWithMihomo.format(
                                    BuildConfig.VERSION_NAME,
                                    BuildConfig.MIHOMO_VERSION,
                                ),
                            style = MiuixTheme.textStyles.body1,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        )

                        Spacer(modifier = Modifier.height(AboutPageMetrics.FooterBottomSpacing))
                    }

                    Card {
                        BasicComponent(
                            title = MLang.About.App.Name,
                            summary = MLang.About.App.Description,
                        )
                    }

                    SmallTitle(MLang.About.Section.ProjectLinks)
                    Card {
                        AboutLinkItem(
                            title = MLang.About.Link.Repository,
                            url = AboutProjectLinks.MonadBoxRepo,
                        )
                        AboutLinkItem(
                            title = MLang.About.Link.Releases,
                            url = AboutProjectLinks.MonadBoxLatestRelease,
                        )
                        AboutLinkItem(
                            title = MLang.About.Link.Mihomo,
                            url = AboutProjectLinks.MihomoRepo,
                        )
                    }

                    SmallTitle(MLang.About.Section.Credits)
                    Card {
                        AboutLinkItem(
                            title = MLang.About.Link.Upstream,
                            url = AboutProjectLinks.YumeBoxUpstreamRepo,
                        )
                    }

                    SmallTitle(MLang.About.Section.License)
                    Card {
                        ConfigSettingRow(
                            title = MLang.About.License.Libraries,
                            summary = MLang.About.License.LibrariesSummary,
                            showDivider = false,
                            onClick = { navigator.navigate(OpenSourceLicensesScreenDestination) },
                        )
                        BasicComponent(
                            title = MLang.About.License.AgplName,
                            summary = MLang.About.License.AgplDescription,
                        )
                    }
                }

                item {
                    Column(
                        modifier =
                            Modifier.fillMaxWidth()
                                .padding(top = AboutPageMetrics.FooterTopPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(text = MLang.About.Copyright, style = MiuixTheme.textStyles.footnote1)
                    }
                    Spacer(modifier = Modifier.height(AboutPageMetrics.FooterBottomSpacing))
                }
            }
        }
    }
}

@Composable
private fun AboutLinkItem(title: String, url: String) {
    LinkItem(title = title, url = url, showArrow = true)
}
