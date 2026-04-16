/*
 * This file is part of MonadBox.
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
 * Copyright (c) MonadBox Contributors 2026 - Present
 */

@file:Suppress(
    "PackageDirectoryMismatch",
    "PackageName",
    "ClassName",
    "ObjectPropertyName",
    "PropertyName",
    "FunctionName",
    "NonAsciiCharacters",
    "RemoveRedundantBackticks",
    "REDUNDANT_ELSE_IN_WHEN",
    "UnusedExpression",
    "unused",
)

package dev.oom_wg.purejoy.mlang

import androidx.compose.runtime.Composable
import dev.oom_wg.purejoy.fyl.fytxt.FYTxtConfig
import dev.oom_wg.purejoy.fyl.fytxt.compose.observe
import dev.oom_wg.purejoy.fyl.fytxt.strfmt.fmt
import dev.oom_wg.purejoy.mlang.MLang.`MLangGroups` as RootMLangGroups
import dev.oom_wg.purejoy.mlang.MLang.`MLangTags` as RootMLangTags

object MLangAbout {
    init {
        RootMLangGroups
    }

    /** 关于 */
    val `Title`
        get() =
            FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                it as RootMLangTags
                when (it) {
                    RootMLangTags.EN -> """About"""
                    RootMLangTags.ZH -> """关于"""
                    else -> null
                }
            } ?: """关于"""

    /** 关于 */
    @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

    object `App` {
        init {
            RootMLangGroups
        }

        /** 一个基于 mihomo 的定制化 Android 客户端 */
        val `Description`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """A customized Android client based on mihomo"""
                        RootMLangTags.ZH -> """一个基于 mihomo 的定制化 Android 客户端"""
                        else -> null
                    }
                } ?: """一个基于 mihomo 的定制化 Android 客户端"""

        /** 一个基于 mihomo 的定制化 Android 客户端 */
        @Composable
        fun `Description`(vararg args: Any?) = FYTxtConfig.observe { `Description`.fmt(args) }

        /** 加载中... */
        val `VersionLoading`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Loading..."""
                        RootMLangTags.ZH -> """加载中..."""
                        else -> null
                    }
                } ?: """加载中..."""

        /** 加载中... */
        @Composable
        fun `VersionLoading`(vararg args: Any?) = FYTxtConfig.observe { `VersionLoading`.fmt(args) }

        /** 加载失败 */
        val `VersionFailed`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Failed to load"""
                        RootMLangTags.ZH -> """加载失败"""
                        else -> null
                    }
                } ?: """加载失败"""

        /** 加载失败 */
        @Composable
        fun `VersionFailed`(vararg args: Any?) = FYTxtConfig.observe { `VersionFailed`.fmt(args) }

        /** %s · mihomo %s */
        val `VersionWithMihomo`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """%s · mihomo %s"""
                        RootMLangTags.ZH -> """%s · mihomo %s"""
                        else -> null
                    }
                } ?: """%s · mihomo %s"""

        /** %s · mihomo %s */
        @Composable
        fun `VersionWithMihomo`(vararg args: Any?) =
            FYTxtConfig.observe { `VersionWithMihomo`.fmt(args) }

        /** MonadBox */
        val `Name`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """MonadBox"""
                        RootMLangTags.ZH -> """MonadBox"""
                        else -> null
                    }
                } ?: """MonadBox"""

        /** MonadBox */
        @Composable fun `Name`(vararg args: Any?) = FYTxtConfig.observe { `Name`.fmt(args) }
    }

    object `Section` {
        init {
            RootMLangGroups
        }

        /** 项目链接 */
        val `ProjectLinks`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Project Links"""
                        RootMLangTags.ZH -> """项目链接"""
                        else -> null
                    }
                } ?: """项目链接"""

        /** 项目链接 */
        @Composable
        fun `ProjectLinks`(vararg args: Any?) = FYTxtConfig.observe { `ProjectLinks`.fmt(args) }

        /** 鸣谢 */
        val `Credits`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Credits"""
                        RootMLangTags.ZH -> """鸣谢"""
                        else -> null
                    }
                } ?: """鸣谢"""

        /** 鸣谢 */
        @Composable fun `Credits`(vararg args: Any?) = FYTxtConfig.observe { `Credits`.fmt(args) }

        /** 更多 */
        val `More`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """More"""
                        RootMLangTags.ZH -> """更多"""
                        else -> null
                    }
                } ?: """更多"""

        /** 更多 */
        @Composable fun `More`(vararg args: Any?) = FYTxtConfig.observe { `More`.fmt(args) }

        /** 许可证 */
        val `License`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """License"""
                        RootMLangTags.ZH -> """许可证"""
                        else -> null
                    }
                } ?: """许可证"""

        /** 许可证 */
        @Composable fun `License`(vararg args: Any?) = FYTxtConfig.observe { `License`.fmt(args) }
    }

    object `Link` {
        init {
            RootMLangGroups
        }

        /** MonadBox（GitHub） */
        val `Repository`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """MonadBox (GitHub)"""
                        RootMLangTags.ZH -> """MonadBox（GitHub）"""
                        else -> null
                    }
                } ?: """MonadBox（GitHub）"""

        /** MonadBox（GitHub） */
        @Composable
        fun `Repository`(vararg args: Any?) = FYTxtConfig.observe { `Repository`.fmt(args) }

        /** MonadBox Releases（更新源） */
        val `Releases`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """MonadBox Releases (Update Source)"""
                        RootMLangTags.ZH -> """MonadBox Releases（更新源）"""
                        else -> null
                    }
                } ?: """MonadBox Releases（更新源）"""

        /** MonadBox Releases（更新源） */
        @Composable fun `Releases`(vararg args: Any?) = FYTxtConfig.observe { `Releases`.fmt(args) }

        /** mihomo */
        val `Mihomo`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """mihomo"""
                        RootMLangTags.ZH -> """mihomo"""
                        else -> null
                    }
                } ?: """mihomo"""

        /** mihomo */
        @Composable fun `Mihomo`(vararg args: Any?) = FYTxtConfig.observe { `Mihomo`.fmt(args) }

        /** YumeBox（上游） */
        val `Upstream`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """YumeBox (Upstream)"""
                        RootMLangTags.ZH -> """YumeBox（上游）"""
                        else -> null
                    }
                } ?: """YumeBox（上游）"""

        /** YumeBox（上游） */
        @Composable fun `Upstream`(vararg args: Any?) = FYTxtConfig.observe { `Upstream`.fmt(args) }

        /** Telegram 群组 */
        val `TelegramGroup`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Telegram Group"""
                        RootMLangTags.ZH -> """Telegram 群组"""
                        else -> null
                    }
                } ?: """Telegram 群组"""

        /** Telegram 群组 */
        @Composable
        fun `TelegramGroup`(vararg args: Any?) = FYTxtConfig.observe { `TelegramGroup`.fmt(args) }

        /** Telegram 频道 */
        val `TelegramChannel`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Telegram Channel"""
                        RootMLangTags.ZH -> """Telegram 频道"""
                        else -> null
                    }
                } ?: """Telegram 频道"""

        /** Telegram 频道 */
        @Composable
        fun `TelegramChannel`(vararg args: Any?) =
            FYTxtConfig.observe { `TelegramChannel`.fmt(args) }
    }

    object `License` {
        init {
            RootMLangGroups
        }

        /** 检查更新 */
        val `CheckUpdate`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Check for Updates"""
                        RootMLangTags.ZH -> """检查更新"""
                        else -> null
                    }
                } ?: """检查更新"""

        /** 检查更新 */
        @Composable
        fun `CheckUpdate`(vararg args: Any?) = FYTxtConfig.observe { `CheckUpdate`.fmt(args) }

        /** 打开 GitHub Releases 页面查看新版本 */
        val `CheckUpdateSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Open the GitHub releases page"""
                        RootMLangTags.ZH -> """打开 GitHub Releases 页面查看新版本"""
                        else -> null
                    }
                } ?: """打开 GitHub Releases 页面查看新版本"""

        /** 打开 GitHub Releases 页面查看新版本 */
        @Composable
        fun `CheckUpdateSummary`(vararg args: Any?) =
            FYTxtConfig.observe { `CheckUpdateSummary`.fmt(args) }

        /** 引用库 */
        val `Libraries`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Libraries"""
                        RootMLangTags.ZH -> """引用库"""
                        else -> null
                    }
                } ?: """引用库"""

        /** 引用库 */
        @Composable
        fun `Libraries`(vararg args: Any?) = FYTxtConfig.observe { `Libraries`.fmt(args) }

        /** 查看本应用使用的第三方库 */
        val `LibrariesSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """View all third-party libraries used in this app"""
                        RootMLangTags.ZH -> """查看本应用使用的第三方库"""
                        else -> null
                    }
                } ?: """查看本应用使用的第三方库"""

        /** 查看本应用使用的第三方库 */
        @Composable
        fun `LibrariesSummary`(vararg args: Any?) =
            FYTxtConfig.observe { `LibrariesSummary`.fmt(args) }

        /** GNU Affero General Public License v3.0 */
        val `AgplName`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """GNU Affero General Public License v3.0"""
                        RootMLangTags.ZH -> """GNU Affero General Public License v3.0"""
                        else -> null
                    }
                } ?: """GNU Affero General Public License v3.0"""

        /** GNU Affero General Public License v3.0 */
        @Composable fun `AgplName`(vararg args: Any?) = FYTxtConfig.observe { `AgplName`.fmt(args) }

        /** 本程序是自由软件；您可以在 GNU Affero General Public License 的条款下重新分发和修改它。 */
        val `AgplDescription`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License."""
                        RootMLangTags.ZH ->
                            """本程序是自由软件；您可以在 GNU Affero General Public License 的条款下重新分发和修改它。"""
                        else -> null
                    }
                } ?: """本程序是自由软件；您可以在 GNU Affero General Public License 的条款下重新分发和修改它。"""

        /** 本程序是自由软件；您可以在 GNU Affero General Public License 的条款下重新分发和修改它。 */
        @Composable
        fun `AgplDescription`(vararg args: Any?) =
            FYTxtConfig.observe { `AgplDescription`.fmt(args) }
    }

    /** © 2025 YumeLira，2026 – 至今 MonadBox */
    val `Copyright`
        get() =
            FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                it as RootMLangTags
                when (it) {
                    RootMLangTags.EN -> """© 2025 YumeLira, 2026 – Present MonadBox"""
                    RootMLangTags.ZH -> """© 2025 YumeLira，2026 – 至今 MonadBox"""
                    else -> null
                }
            } ?: """© 2025 YumeLira，2026 – 至今 MonadBox"""

    /** © 2025 YumeLira，2026 – 至今 MonadBox */
    @Composable fun `Copyright`(vararg args: Any?) = FYTxtConfig.observe { `Copyright`.fmt(args) }
}
