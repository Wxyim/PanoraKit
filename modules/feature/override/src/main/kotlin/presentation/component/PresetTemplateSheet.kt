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

package com.github.nomadboxlab.monadbox.presentation.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.nomadboxlab.monadbox.data.util.OverridePresetItem
import com.github.nomadboxlab.monadbox.data.util.OverridePresetRegion
import com.github.nomadboxlab.monadbox.data.util.OverridePresetTemplateSelection
import com.github.nomadboxlab.monadbox.presentation.theme.AppTheme
import dev.oom_wg.purejoy.mlang.MLang
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.SuperSwitch
import top.yukonga.miuix.kmp.theme.MiuixTheme

private object PresetTemplateSheetMetrics {
    val BottomPadding = 12.dp
    val SectionSpacing = 8.dp
    val IntroHorizontalPadding = 16.dp
    val IntroVerticalPadding = 14.dp
    val SectionTitlePadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
}

@Composable
fun OverridePresetTemplateSheet(
    show: Boolean,
    initialSelection: OverridePresetTemplateSelection = OverridePresetTemplateSelection(),
    onDismiss: () -> Unit,
    onConfirm: (OverridePresetTemplateSelection) -> Unit,
) {
    val pageMetrics = AppTheme.pageMetrics
    val selectedRegions = remember(show) { mutableStateListOf<OverridePresetRegion>() }
    val enabledItems = remember(show) { mutableStateListOf<OverridePresetItem>() }
    val templateId = initialSelection.templateId

    LaunchedEffect(show, initialSelection) {
        selectedRegions.clear()
        selectedRegions.addAll(
            initialSelection.regions.toList().sortedBy(OverridePresetRegion::ordinal)
        )
        enabledItems.clear()
        enabledItems.addAll(
            initialSelection.enabledItems.toList().sortedBy(OverridePresetItem::ordinal)
        )
    }

    AppActionBottomSheet(
        show = show,
        modifier = Modifier,
        title = MLang.Override.Draft.PresetTemplate,
        enableNestedScroll = false,
        dragHandleColor = Color.Transparent,
        startAction = { AppBottomSheetCloseAction(onClick = onDismiss) },
        endAction = {
            AppBottomSheetConfirmAction(
                contentDescription = MLang.Override.Draft.Apply,
                onClick = {
                    onConfirm(
                        OverridePresetTemplateSelection(
                            templateId = templateId,
                            regions = selectedRegions.toSet(),
                            enabledItems = enabledItems.toSet(),
                        )
                    )
                },
            )
        },
        onDismissRequest = onDismiss,
    ) {
        LazyColumn(
            modifier =
                Modifier.fillMaxWidth()
                    .heightIn(max = pageMetrics.overridePresetTemplateListMaxHeight)
                    .padding(bottom = PresetTemplateSheetMetrics.BottomPadding),
            verticalArrangement = Arrangement.spacedBy(PresetTemplateSheetMetrics.SectionSpacing),
        ) {
            item(key = "preset-template-intro") {
                Card(applyHorizontalPadding = false) {
                    Column(
                        modifier =
                            Modifier.fillMaxWidth()
                                .padding(
                                    horizontal = PresetTemplateSheetMetrics.IntroHorizontalPadding,
                                    vertical = PresetTemplateSheetMetrics.IntroVerticalPadding,
                                ),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = MLang.Override.Draft.OfficialMrsSummary,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            style = MiuixTheme.textStyles.body2,
                        )
                    }
                }
            }

            presetSwitchCard(
                key = "preset-regions",
                title = MLang.Override.Draft.RegionalAutoGroup,
                items = OverridePresetRegion.entries.toList(),
                isChecked = { region -> region in selectedRegions },
                onCheckedChange = { region, checked ->
                    if (checked) {
                        if (region !in selectedRegions) {
                            selectedRegions.add(region)
                        }
                    } else {
                        selectedRegions.remove(region)
                    }
                },
                itemTitle = ::localizedRegionTitle,
            )

            presetSwitchCard(
                key = "preset-base-items",
                title = MLang.Override.Draft.BasicRouting,
                items = OverridePresetItem.entries.filterNot(OverridePresetItem::isService),
                isChecked = { item -> item in enabledItems },
                onCheckedChange = { item, checked ->
                    togglePresetItem(enabledItems, item, checked)
                },
                itemTitle = ::localizedItemTitle,
            )

            presetSwitchCard(
                key = "preset-service-items",
                title = MLang.Override.Draft.ServiceRouting,
                items = OverridePresetItem.entries.filter(OverridePresetItem::isService),
                isChecked = { item -> item in enabledItems },
                onCheckedChange = { item, checked ->
                    togglePresetItem(enabledItems, item, checked)
                },
                itemTitle = ::localizedItemTitle,
            )
        }
    }
}

private fun localizedRegionTitle(region: OverridePresetRegion): String {
    return when (region) {
        OverridePresetRegion.HK -> MLang.Override.Draft.RegionHongKong
        OverridePresetRegion.TW -> MLang.Override.Draft.RegionTaiwan
        OverridePresetRegion.JP -> MLang.Override.Draft.RegionJapan
        OverridePresetRegion.SG -> MLang.Override.Draft.RegionSingapore
        OverridePresetRegion.US -> MLang.Override.Draft.RegionUnitedStates
    }
}

private fun localizedItemTitle(item: OverridePresetItem): String {
    return when (item) {
        OverridePresetItem.Ads -> MLang.Override.Draft.ItemAds
        OverridePresetItem.Private -> MLang.Override.Draft.ItemPrivate
        OverridePresetItem.Google -> MLang.Override.Draft.ItemGoogle
        OverridePresetItem.Telegram -> MLang.Override.Draft.ItemTelegram
        OverridePresetItem.GitHub -> MLang.Override.Draft.ItemGitHub
        OverridePresetItem.Microsoft -> MLang.Override.Draft.ItemMicrosoft
        OverridePresetItem.Apple -> MLang.Override.Draft.ItemApple
        OverridePresetItem.YouTube -> MLang.Override.Draft.ItemYouTube
        OverridePresetItem.Netflix -> MLang.Override.Draft.ItemNetflix
        OverridePresetItem.Spotify -> MLang.Override.Draft.ItemSpotify
        OverridePresetItem.OpenAI -> MLang.Override.Draft.ItemOpenAI
        OverridePresetItem.Steam -> MLang.Override.Draft.ItemSteam
        OverridePresetItem.Cn -> MLang.Override.Draft.ItemCn
        OverridePresetItem.Proxy -> MLang.Override.Draft.ItemProxy
        OverridePresetItem.GeolocationNotCn -> MLang.Override.Draft.ItemGeolocationNotCn
        OverridePresetItem.Match -> MLang.Override.Draft.ItemMatch
    }
}

private fun togglePresetItem(
    enabledItems: MutableList<OverridePresetItem>,
    item: OverridePresetItem,
    checked: Boolean,
) {
    if (checked) {
        if (item !in enabledItems) {
            enabledItems.add(item)
        }
    } else {
        enabledItems.remove(item)
    }
}

private fun <T> LazyListScope.presetSwitchCard(
    key: String,
    title: String,
    items: List<T>,
    isChecked: (T) -> Boolean,
    onCheckedChange: (T, Boolean) -> Unit,
    itemTitle: (T) -> String,
    itemSummary: ((T) -> String)? = null,
) {
    item(key = key) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            SmallTitle(text = title, insideMargin = PresetTemplateSheetMetrics.SectionTitlePadding)
            Card(applyHorizontalPadding = false) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    items.forEach { item ->
                        SuperSwitch(
                            title = itemTitle(item),
                            summary = itemSummary?.invoke(item),
                            checked = isChecked(item),
                            onCheckedChange = { checked -> onCheckedChange(item, checked) },
                        )
                    }
                }
            }
        }
    }
}
