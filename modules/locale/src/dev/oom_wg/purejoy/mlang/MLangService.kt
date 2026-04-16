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

object MLangService {
    init {
        RootMLangGroups
    }

    object `Notification` {
        init {
            RootMLangGroups
        }

        /** 运行中 */
        val `Running`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Running"""
                        RootMLangTags.ZH -> """运行中"""
                        else -> null
                    }
                } ?: """运行中"""

        /** 运行中 */
        @Composable fun `Running`(vararg args: Any?) = FYTxtConfig.observe { `Running`.fmt(args) }

        /** 总计：%s */
        val `TrafficFormat`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Total: %s"""
                        RootMLangTags.ZH -> """总计：%s"""
                        else -> null
                    }
                } ?: """总计：%s"""

        /** 总计：%s */
        @Composable
        fun `TrafficFormat`(vararg args: Any?) = FYTxtConfig.observe { `TrafficFormat`.fmt(args) }

        /** 未知配置 */
        val `UnknownProfile`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Unknown profile"""
                        RootMLangTags.ZH -> """未知配置"""
                        else -> null
                    }
                } ?: """未知配置"""

        /** 未知配置 */
        @Composable
        fun `UnknownProfile`(vararg args: Any?) = FYTxtConfig.observe { `UnknownProfile`.fmt(args) }
    }

    object `Tile` {
        init {
            RootMLangGroups
        }

        /** 点击打开应用 */
        val `ClickToOpen`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Click to open app"""
                        RootMLangTags.ZH -> """点击打开应用"""
                        else -> null
                    }
                } ?: """点击打开应用"""

        /** 点击打开应用 */
        @Composable
        fun `ClickToOpen`(vararg args: Any?) = FYTxtConfig.observe { `ClickToOpen`.fmt(args) }

        /** 启动代理 */
        val `ClickToStartProxy`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Start proxy"""
                        RootMLangTags.ZH -> """启动代理"""
                        else -> null
                    }
                } ?: """启动代理"""

        /** 启动代理 */
        @Composable
        fun `ClickToStartProxy`(vararg args: Any?) =
            FYTxtConfig.observe { `ClickToStartProxy`.fmt(args) }

        /** 停止代理 */
        val `ClickToStopProxy`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Stop proxy"""
                        RootMLangTags.ZH -> """停止代理"""
                        else -> null
                    }
                } ?: """停止代理"""

        /** 停止代理 */
        @Composable
        fun `ClickToStopProxy`(vararg args: Any?) =
            FYTxtConfig.observe { `ClickToStopProxy`.fmt(args) }

        /** 正在连接... */
        val `Connecting`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Connecting..."""
                        RootMLangTags.ZH -> """正在连接..."""
                        else -> null
                    }
                } ?: """正在连接..."""

        /** 正在连接... */
        @Composable
        fun `Connecting`(vararg args: Any?) = FYTxtConfig.observe { `Connecting`.fmt(args) }

        /** 正在断开... */
        val `Disconnecting`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Disconnecting..."""
                        RootMLangTags.ZH -> """正在断开..."""
                        else -> null
                    }
                } ?: """正在断开..."""

        /** 正在断开... */
        @Composable
        fun `Disconnecting`(vararg args: Any?) = FYTxtConfig.observe { `Disconnecting`.fmt(args) }
    }

    object `AutoRestart` {
        init {
            RootMLangGroups
        }

        /** 自动重启服务 */
        val `ChannelName`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Auto Restart Service"""
                        RootMLangTags.ZH -> """自动重启服务"""
                        else -> null
                    }
                } ?: """自动重启服务"""

        /** 自动重启服务 */
        @Composable
        fun `ChannelName`(vararg args: Any?) = FYTxtConfig.observe { `ChannelName`.fmt(args) }

        /** 用于自动重启代理服务 */
        val `ChannelDescription`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Used to restart proxy service automatically"""
                        RootMLangTags.ZH -> """用于自动重启代理服务"""
                        else -> null
                    }
                } ?: """用于自动重启代理服务"""

        /** 用于自动重启代理服务 */
        @Composable
        fun `ChannelDescription`(vararg args: Any?) =
            FYTxtConfig.observe { `ChannelDescription`.fmt(args) }

        /** 正在检查自动启动... */
        val `Checking`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Checking auto-start..."""
                        RootMLangTags.ZH -> """正在检查自动启动..."""
                        else -> null
                    }
                } ?: """正在检查自动启动..."""

        /** 正在检查自动启动... */
        @Composable fun `Checking`(vararg args: Any?) = FYTxtConfig.observe { `Checking`.fmt(args) }
    }

    object `LogRecord` {
        init {
            RootMLangGroups
        }

        /** 日志录制 */
        val `ChannelName`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Log Recording"""
                        RootMLangTags.ZH -> """日志录制"""
                        else -> null
                    }
                } ?: """日志录制"""

        /** 日志录制 */
        @Composable
        fun `ChannelName`(vararg args: Any?) = FYTxtConfig.observe { `ChannelName`.fmt(args) }

        /** 日志录制服务通知 */
        val `ChannelDescription`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Notification for log recording service"""
                        RootMLangTags.ZH -> """日志录制服务通知"""
                        else -> null
                    }
                } ?: """日志录制服务通知"""

        /** 日志录制服务通知 */
        @Composable
        fun `ChannelDescription`(vararg args: Any?) =
            FYTxtConfig.observe { `ChannelDescription`.fmt(args) }

        /** 正在录制日志 */
        val `NotificationTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Recording logs"""
                        RootMLangTags.ZH -> """正在录制日志"""
                        else -> null
                    }
                } ?: """正在录制日志"""

        /** 正在录制日志 */
        @Composable
        fun `NotificationTitle`(vararg args: Any?) =
            FYTxtConfig.observe { `NotificationTitle`.fmt(args) }

        /** 正在录制日志... */
        val `NotificationContent`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Recording logs..."""
                        RootMLangTags.ZH -> """正在录制日志..."""
                        else -> null
                    }
                } ?: """正在录制日志..."""

        /** 正在录制日志... */
        @Composable
        fun `NotificationContent`(vararg args: Any?) =
            FYTxtConfig.observe { `NotificationContent`.fmt(args) }

        /** 停止 */
        val `ActionStop`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Stop"""
                        RootMLangTags.ZH -> """停止"""
                        else -> null
                    }
                } ?: """停止"""

        /** 停止 */
        @Composable
        fun `ActionStop`(vararg args: Any?) = FYTxtConfig.observe { `ActionStop`.fmt(args) }
    }
}
