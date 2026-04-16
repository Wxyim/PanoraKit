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

object MLangOnboarding {
    init {
        RootMLangGroups
    }

    object `Navigation` {
        init {
            RootMLangGroups
        }

        /** 返回 */
        val `Back`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Back"""
                        RootMLangTags.ZH -> """返回"""
                        else -> null
                    }
                } ?: """返回"""

        /** 返回 */
        @Composable fun `Back`(vararg args: Any?) = FYTxtConfig.observe { `Back`.fmt(args) }

        /** 下一步 */
        val `Next`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Next"""
                        RootMLangTags.ZH -> """下一步"""
                        else -> null
                    }
                } ?: """下一步"""

        /** 下一步 */
        @Composable fun `Next`(vararg args: Any?) = FYTxtConfig.observe { `Next`.fmt(args) }

        /** 开始设置 */
        val `Start`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Start Setup"""
                        RootMLangTags.ZH -> """开始设置"""
                        else -> null
                    }
                } ?: """开始设置"""

        /** 开始设置 */
        @Composable fun `Start`(vararg args: Any?) = FYTxtConfig.observe { `Start`.fmt(args) }

        /** 进入应用 */
        val `Enter`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Enter App"""
                        RootMLangTags.ZH -> """进入应用"""
                        else -> null
                    }
                } ?: """进入应用"""

        /** 进入应用 */
        @Composable fun `Enter`(vararg args: Any?) = FYTxtConfig.observe { `Enter`.fmt(args) }
    }

    object `Permission` {
        init {
            RootMLangGroups
        }

        /** 确认运行权限 */
        val `Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Confirm Runtime Access"""
                        RootMLangTags.ZH -> """确认运行权限"""
                        else -> null
                    }
                } ?: """确认运行权限"""

        /** 确认运行权限 */
        @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

        /** 权限会影响通知和分应用代理等功能 */
        val `Subtitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Permissions affect notifications and per-app proxy features"""
                        RootMLangTags.ZH -> """权限会影响通知和分应用代理等功能"""
                        else -> null
                    }
                } ?: """权限会影响通知和分应用代理等功能"""

        /** 权限会影响通知和分应用代理等功能 */
        @Composable fun `Subtitle`(vararg args: Any?) = FYTxtConfig.observe { `Subtitle`.fmt(args) }

        object `Common` {
            init {
                RootMLangGroups
            }

            /** 已授权 */
            val `Granted`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Granted"""
                            RootMLangTags.ZH -> """已授权"""
                            else -> null
                        }
                    } ?: """已授权"""

            /** 已授权 */
            @Composable
            fun `Granted`(vararg args: Any?) = FYTxtConfig.observe { `Granted`.fmt(args) }
        }

        object `Notification` {
            init {
                RootMLangGroups
            }

            /** 通知权限 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Notification Permission"""
                            RootMLangTags.ZH -> """通知权限"""
                            else -> null
                        }
                    } ?: """通知权限"""

            /** 通知权限 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 用于显示连接状态和流量通知 */
            val `SummaryNeed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN ->
                                """Show connection status and traffic notifications"""
                            RootMLangTags.ZH -> """用于显示连接状态和流量通知"""
                            else -> null
                        }
                    } ?: """用于显示连接状态和流量通知"""

            /** 用于显示连接状态和流量通知 */
            @Composable
            fun `SummaryNeed`(vararg args: Any?) = FYTxtConfig.observe { `SummaryNeed`.fmt(args) }

            /** 当前系统无需额外授权 */
            val `SummaryNotRequired`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Not required on your Android version"""
                            RootMLangTags.ZH -> """当前系统无需额外授权"""
                            else -> null
                        }
                    } ?: """当前系统无需额外授权"""

            /** 当前系统无需额外授权 */
            @Composable
            fun `SummaryNotRequired`(vararg args: Any?) =
                FYTxtConfig.observe { `SummaryNotRequired`.fmt(args) }
        }

        object `AppList` {
            init {
                RootMLangGroups
            }

            /** 应用列表权限 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """App List Permission"""
                            RootMLangTags.ZH -> """应用列表权限"""
                            else -> null
                        }
                    } ?: """应用列表权限"""

            /** 应用列表权限 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 用于分应用代理等功能 */
            val `SummaryNeed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Required for per-app proxy features"""
                            RootMLangTags.ZH -> """用于分应用代理等功能"""
                            else -> null
                        }
                    } ?: """用于分应用代理等功能"""

            /** 用于分应用代理等功能 */
            @Composable
            fun `SummaryNeed`(vararg args: Any?) = FYTxtConfig.observe { `SummaryNeed`.fmt(args) }

            /** 当前系统无需额外授权 */
            val `SummaryNotRequired`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Not required on your Android version"""
                            RootMLangTags.ZH -> """当前系统无需额外授权"""
                            else -> null
                        }
                    } ?: """当前系统无需额外授权"""

            /** 当前系统无需额外授权 */
            @Composable
            fun `SummaryNotRequired`(vararg args: Any?) =
                FYTxtConfig.observe { `SummaryNotRequired`.fmt(args) }
        }
    }

    object `Privacy` {
        init {
            RootMLangGroups
        }

        /** 确认隐私说明 */
        val `Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Confirm Privacy Notice"""
                        RootMLangTags.ZH -> """确认隐私说明"""
                        else -> null
                    }
                } ?: """确认隐私说明"""

        /** 确认隐私说明 */
        @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

        /** 阅读并同意隐私说明后方可继续 */
        val `Subtitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Review and accept the privacy notice to continue"""
                        RootMLangTags.ZH -> """阅读并同意隐私说明后方可继续"""
                        else -> null
                    }
                } ?: """阅读并同意隐私说明后方可继续"""

        /** 阅读并同意隐私说明后方可继续 */
        @Composable fun `Subtitle`(vararg args: Any?) = FYTxtConfig.observe { `Subtitle`.fmt(args) }

        /** 在开始使用 MonadBox 前，请先阅读并同意隐私说明。 */
        val `RichTextLead`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """You need to review and accept the privacy notice before using MonadBox."""
                        RootMLangTags.ZH -> """在开始使用 MonadBox 前，请先阅读并同意隐私说明。"""
                        else -> null
                    }
                } ?: """在开始使用 MonadBox 前，请先阅读并同意隐私说明。"""

        /** 在开始使用 MonadBox 前，请先阅读并同意隐私说明。 */
        @Composable
        fun `RichTextLead`(vararg args: Any?) = FYTxtConfig.observe { `RichTextLead`.fmt(args) }

        /** 继续前请先阅读 */
        val `RichTextPrefix`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Before continuing, please review """
                        RootMLangTags.ZH -> """继续前请先阅读 """
                        else -> null
                    }
                } ?: """继续前请先阅读 """

        /** 继续前请先阅读 */
        @Composable
        fun `RichTextPrefix`(vararg args: Any?) = FYTxtConfig.observe { `RichTextPrefix`.fmt(args) }

        /** 。 */
        val `RichTextSuffix`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """."""
                        RootMLangTags.ZH -> """。"""
                        else -> null
                    }
                } ?: """。"""

        /** 。 */
        @Composable
        fun `RichTextSuffix`(vararg args: Any?) = FYTxtConfig.observe { `RichTextSuffix`.fmt(args) }

        /** 《隐私说明》 */
        val `PolicyLink`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Privacy Notice"""
                        RootMLangTags.ZH -> """《隐私说明》"""
                        else -> null
                    }
                } ?: """《隐私说明》"""

        /** 《隐私说明》 */
        @Composable
        fun `PolicyLink`(vararg args: Any?) = FYTxtConfig.observe { `PolicyLink`.fmt(args) }

        object `Privacy` {
            init {
                RootMLangGroups
            }

            /** 隐私说明 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Privacy Notice"""
                            RootMLangTags.ZH -> """隐私说明"""
                            else -> null
                        }
                    } ?: """隐私说明"""

            /** 隐私说明 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }
        }

        object `Accept` {
            init {
                RootMLangGroups
            }

            /** 我已阅读并同意隐私说明 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """I have read and agree to the privacy notice"""
                            RootMLangTags.ZH -> """我已阅读并同意隐私说明"""
                            else -> null
                        }
                    } ?: """我已阅读并同意隐私说明"""

            /** 我已阅读并同意隐私说明 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }
        }
    }

    object `Personalize` {
        init {
            RootMLangGroups
        }

        /** 调整界面风格 */
        val `Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Tune the Interface"""
                        RootMLangTags.ZH -> """调整界面风格"""
                        else -> null
                    }
                } ?: """调整界面风格"""

        /** 调整界面风格 */
        @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

        /** 主题模式和主色可随时在设置中修改 */
        val `Subtitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Theme mode and accent color can be changed anytime in settings"""
                        RootMLangTags.ZH -> """主题模式和主色可随时在设置中修改"""
                        else -> null
                    }
                } ?: """主题模式和主色可随时在设置中修改"""

        /** 主题模式和主色可随时在设置中修改 */
        @Composable fun `Subtitle`(vararg args: Any?) = FYTxtConfig.observe { `Subtitle`.fmt(args) }
    }

    object `Finish` {
        init {
            RootMLangGroups
        }

        /** 准备完成 */
        val `Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Ready to Go"""
                        RootMLangTags.ZH -> """准备完成"""
                        else -> null
                    }
                } ?: """准备完成"""

        /** 准备完成 */
        @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

        /** 基础设置已就绪即将进入主页 */
        val `Subtitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Setup complete, entering the main app"""
                        RootMLangTags.ZH -> """基础设置已就绪即将进入主页"""
                        else -> null
                    }
                } ?: """基础设置已就绪即将进入主页"""

        /** 基础设置已就绪即将进入主页 */
        @Composable fun `Subtitle`(vararg args: Any?) = FYTxtConfig.observe { `Subtitle`.fmt(args) }
    }

    object `Project` {
        init {
            RootMLangGroups
        }

        object `Github` {
            init {
                RootMLangGroups
            }

            /** GitHub 仓库 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """GitHub Repo"""
                            RootMLangTags.ZH -> """GitHub 仓库"""
                            else -> null
                        }
                    } ?: """GitHub 仓库"""

            /** GitHub 仓库 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** https://github.com/NomadBoxLab/NomadBox */
            val `Summary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """https://github.com/NomadBoxLab/NomadBox"""
                            RootMLangTags.ZH -> """https://github.com/NomadBoxLab/NomadBox"""
                            else -> null
                        }
                    } ?: """https://github.com/NomadBoxLab/NomadBox"""

            /** https://github.com/NomadBoxLab/NomadBox */
            @Composable
            fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }
        }
    }

    object `Sheet` {
        init {
            RootMLangGroups
        }

        /** 隐私说明 */
        val `PrivacyPolicyTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Privacy Notice"""
                        RootMLangTags.ZH -> """隐私说明"""
                        else -> null
                    }
                } ?: """隐私说明"""

        /** 隐私说明 */
        @Composable
        fun `PrivacyPolicyTitle`(vararg args: Any?) =
            FYTxtConfig.observe { `PrivacyPolicyTitle`.fmt(args) }

        /** 无法加载协议内容 */
        val `LoadFailed`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Failed to load policy content"""
                        RootMLangTags.ZH -> """无法加载协议内容"""
                        else -> null
                    }
                } ?: """无法加载协议内容"""

        /** 无法加载协议内容 */
        @Composable
        fun `LoadFailed`(vararg args: Any?) = FYTxtConfig.observe { `LoadFailed`.fmt(args) }
    }
}
