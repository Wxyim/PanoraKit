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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.nomadboxlab.monadbox.BuildConfig
import com.github.nomadboxlab.monadbox.R
import com.github.yumelira.yumebox.common.util.openUrl
import com.github.yumelira.yumebox.presentation.component.Card
import com.github.yumelira.yumebox.presentation.component.ScreenLazyColumn
import com.github.yumelira.yumebox.presentation.component.SmallTitle
import com.github.yumelira.yumebox.presentation.component.TopBar
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.OpenSourceLicensesScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.oom_wg.purejoy.mlang.MLang
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.theme.MiuixTheme

private object AboutPageMetrics {
    val HeroTopSpacing = 24.dp
    val HeroIconSize = 120.dp
    val HeroIconCornerRadius = 24.dp
    val HeroSectionSpacing = 24.dp
    val HeroMetaSpacing = 8.dp
    val FooterTopPadding = 32.dp
    val FooterBottomSpacing = 32.dp
}

@Composable
@Destination<RootGraph>
fun AboutScreen(navigator: DestinationsNavigator) {
    val context = LocalContext.current
    val scrollBehavior = MiuixScrollBehavior()

    Scaffold(
        topBar = {
            TopBar(title = MLang.About.Title, scrollBehavior = scrollBehavior)
        },
    ) { innerPadding ->
        ScreenLazyColumn(
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
                        painter = painterResource(id = R.drawable.yume),
                        contentDescription = null,
                        modifier = Modifier
                            .size(AboutPageMetrics.HeroIconSize)
                            .clip(RoundedCornerShape(AboutPageMetrics.HeroIconCornerRadius)),
                        tint = Color.Unspecified,
                    )

                    Spacer(modifier = Modifier.height(AboutPageMetrics.HeroSectionSpacing))

                    Text(text = "MonadBox", style = MiuixTheme.textStyles.title1)

                    Spacer(modifier = Modifier.height(AboutPageMetrics.HeroMetaSpacing))

                    Text(
                        text = MLang.About.App.VersionWithMihomo.format(
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
                        title = "MonadBox",
                        summary = MLang.About.App.Description,
                    )
                }

                SmallTitle(MLang.About.Section.ProjectLinks)
                Card {
                    AboutLinkItem(
                        title = "Mihomo",
                        url = "https://github.com/MetaCubeX/mihomo",
                        onOpenUrl = { url -> openUrl(context, url) },
                        showArrow = false,
                    )
                }

                SmallTitle(MLang.About.Section.Credits)
                Card {
                    AboutLinkItem(
                        title = "YumeBox (Upstream)",
                        url = "https://github.com/YumeLira/YumeBox",
                        onOpenUrl = { url -> openUrl(context, url) },
                        showArrow = false,
                    )
                }

                SmallTitle(MLang.About.Section.License)
                Card {
                    SuperArrow(
                        title = MLang.About.License.Libraries,
                        summary = MLang.About.License.LibrariesSummary,
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = AboutPageMetrics.FooterTopPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = MLang.About.Copyright,
                        style = MiuixTheme.textStyles.footnote1,
                    )
                }
                Spacer(modifier = Modifier.height(AboutPageMetrics.FooterBottomSpacing))
            }
        }
    }
}

@Composable
private fun AboutLinkItem(
    title: String,
    url: String,
    onOpenUrl: (String) -> Unit,
    showArrow: Boolean,
) {
    if (showArrow) {
        SuperArrow(
            title = title,
            summary = url,
            onClick = { onOpenUrl(url) },
        )
    } else {
        BasicComponent(
            title = title,
            summary = url,
            onClick = { onOpenUrl(url) },
        )
    }
}
