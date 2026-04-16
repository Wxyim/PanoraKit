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

object MLangComponent {
    init {
        RootMLangGroups
    }

    object `ProfileCard` {
        init {
            RootMLangGroups
        }

        /** 分享 */
        val `Export`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Share"""
                        RootMLangTags.ZH -> """分享"""
                        else -> null
                    }
                } ?: """分享"""

        /** 分享 */
        @Composable fun `Export`(vararg args: Any?) = FYTxtConfig.observe { `Export`.fmt(args) }

        /** 编辑 */
        val `Edit`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Edit"""
                        RootMLangTags.ZH -> """编辑"""
                        else -> null
                    }
                } ?: """编辑"""

        /** 编辑 */
        @Composable fun `Edit`(vararg args: Any?) = FYTxtConfig.observe { `Edit`.fmt(args) }

        /** 更新 */
        val `Update`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Update"""
                        RootMLangTags.ZH -> """更新"""
                        else -> null
                    }
                } ?: """更新"""

        /** 更新 */
        @Composable fun `Update`(vararg args: Any?) = FYTxtConfig.observe { `Update`.fmt(args) }

        /** 删除 */
        val `Delete`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Delete"""
                        RootMLangTags.ZH -> """删除"""
                        else -> null
                    }
                } ?: """删除"""

        /** 删除 */
        @Composable fun `Delete`(vararg args: Any?) = FYTxtConfig.observe { `Delete`.fmt(args) }

        /** 远程订阅 */
        val `RemoteSubscription`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Remote Subscription"""
                        RootMLangTags.ZH -> """远程订阅"""
                        else -> null
                    }
                } ?: """远程订阅"""

        /** 远程订阅 */
        @Composable
        fun `RemoteSubscription`(vararg args: Any?) =
            FYTxtConfig.observe { `RemoteSubscription`.fmt(args) }

        /** 本地文件 */
        val `LocalFile`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Local File"""
                        RootMLangTags.ZH -> """本地文件"""
                        else -> null
                    }
                } ?: """本地文件"""

        /** 本地文件 */
        @Composable
        fun `LocalFile`(vararg args: Any?) = FYTxtConfig.observe { `LocalFile`.fmt(args) }

        /** 本地配置文件 */
        val `LocalConfig`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Local Configuration"""
                        RootMLangTags.ZH -> """本地配置文件"""
                        else -> null
                    }
                } ?: """本地配置文件"""

        /** 本地配置文件 */
        @Composable
        fun `LocalConfig`(vararg args: Any?) = FYTxtConfig.observe { `LocalConfig`.fmt(args) }

        /** 点击更新获取订阅信息 */
        val `ClickToUpdate`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Click update to get subscription info"""
                        RootMLangTags.ZH -> """点击更新获取订阅信息"""
                        else -> null
                    }
                } ?: """点击更新获取订阅信息"""

        /** 点击更新获取订阅信息 */
        @Composable
        fun `ClickToUpdate`(vararg args: Any?) = FYTxtConfig.observe { `ClickToUpdate`.fmt(args) }

        /** 流量：%s / %s (%d%%) */
        val `Traffic`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Traffic: %s / %s (%d%%)"""
                        RootMLangTags.ZH -> """流量：%s / %s (%d%%)"""
                        else -> null
                    }
                } ?: """流量：%s / %s (%d%%)"""

        /** 流量：%s / %s (%d%%) */
        @Composable fun `Traffic`(vararg args: Any?) = FYTxtConfig.observe { `Traffic`.fmt(args) }

        /** 已用：%s */
        val `UsedTraffic`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Used: %s"""
                        RootMLangTags.ZH -> """已用：%s"""
                        else -> null
                    }
                } ?: """已用：%s"""

        /** 已用：%s */
        @Composable
        fun `UsedTraffic`(vararg args: Any?) = FYTxtConfig.observe { `UsedTraffic`.fmt(args) }

        /** 到期：%s (剩余%d 天) */
        val `ExpireAt`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Expires: %s (%d days left)"""
                        RootMLangTags.ZH -> """到期：%s (剩余%d 天)"""
                        else -> null
                    }
                } ?: """到期：%s (剩余%d 天)"""

        /** 到期：%s (剩余%d 天) */
        @Composable fun `ExpireAt`(vararg args: Any?) = FYTxtConfig.observe { `ExpireAt`.fmt(args) }

        /** 到期：今天 */
        val `ExpireToday`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Expires: Today"""
                        RootMLangTags.ZH -> """到期：今天"""
                        else -> null
                    }
                } ?: """到期：今天"""

        /** 到期：今天 */
        @Composable
        fun `ExpireToday`(vararg args: Any?) = FYTxtConfig.observe { `ExpireToday`.fmt(args) }

        /** 已过期：%s */
        val `Expired`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Expired: %s"""
                        RootMLangTags.ZH -> """已过期：%s"""
                        else -> null
                    }
                } ?: """已过期：%s"""

        /** 已过期：%s */
        @Composable fun `Expired`(vararg args: Any?) = FYTxtConfig.observe { `Expired`.fmt(args) }

        /** 刚刚 */
        val `JustNow`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Just now"""
                        RootMLangTags.ZH -> """刚刚"""
                        else -> null
                    }
                } ?: """刚刚"""

        /** 刚刚 */
        @Composable fun `JustNow`(vararg args: Any?) = FYTxtConfig.observe { `JustNow`.fmt(args) }

        /** %d 分钟前 */
        val `MinutesAgo`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """%d min ago"""
                        RootMLangTags.ZH -> """%d 分钟前"""
                        else -> null
                    }
                } ?: """%d 分钟前"""

        /** %d 分钟前 */
        @Composable
        fun `MinutesAgo`(vararg args: Any?) = FYTxtConfig.observe { `MinutesAgo`.fmt(args) }

        /** %d 小时前 */
        val `HoursAgo`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """%d hours ago"""
                        RootMLangTags.ZH -> """%d 小时前"""
                        else -> null
                    }
                } ?: """%d 小时前"""

        /** %d 小时前 */
        @Composable fun `HoursAgo`(vararg args: Any?) = FYTxtConfig.observe { `HoursAgo`.fmt(args) }

        /** %d 天前 */
        val `DaysAgo`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """%d days ago"""
                        RootMLangTags.ZH -> """%d 天前"""
                        else -> null
                    }
                } ?: """%d 天前"""

        /** %d 天前 */
        @Composable fun `DaysAgo`(vararg args: Any?) = FYTxtConfig.observe { `DaysAgo`.fmt(args) }
    }

    object `WebView` {
        init {
            RootMLangGroups
        }

        /** 无效的 URL */
        val `InvalidUrl`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Invalid URL"""
                        RootMLangTags.ZH -> """无效的 URL"""
                        else -> null
                    }
                } ?: """无效的 URL"""

        /** 无效的 URL */
        @Composable
        fun `InvalidUrl`(vararg args: Any?) = FYTxtConfig.observe { `InvalidUrl`.fmt(args) }
    }

    object `Selector` {
        init {
            RootMLangGroups
        }

        /** 不修改 */
        val `NotModify`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Don't Modify"""
                        RootMLangTags.ZH -> """不修改"""
                        else -> null
                    }
                } ?: """不修改"""

        /** 不修改 */
        @Composable
        fun `NotModify`(vararg args: Any?) = FYTxtConfig.observe { `NotModify`.fmt(args) }

        /** 使用默认 */
        val `UseDefault`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Use Default"""
                        RootMLangTags.ZH -> """使用默认"""
                        else -> null
                    }
                } ?: """使用默认"""

        /** 使用默认 */
        @Composable
        fun `UseDefault`(vararg args: Any?) = FYTxtConfig.observe { `UseDefault`.fmt(args) }

        /** 启用 */
        val `Enable`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Enable"""
                        RootMLangTags.ZH -> """启用"""
                        else -> null
                    }
                } ?: """启用"""

        /** 启用 */
        @Composable fun `Enable`(vararg args: Any?) = FYTxtConfig.observe { `Enable`.fmt(args) }

        /** 禁用 */
        val `Disable`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Disable"""
                        RootMLangTags.ZH -> """禁用"""
                        else -> null
                    }
                } ?: """禁用"""

        /** 禁用 */
        @Composable fun `Disable`(vararg args: Any?) = FYTxtConfig.observe { `Disable`.fmt(args) }

        /** 替换 */
        val `Replace`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Replace"""
                        RootMLangTags.ZH -> """替换"""
                        else -> null
                    }
                } ?: """替换"""

        /** 替换 */
        @Composable fun `Replace`(vararg args: Any?) = FYTxtConfig.observe { `Replace`.fmt(args) }

        /** 前置 */
        val `Prepend`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Prepend"""
                        RootMLangTags.ZH -> """前置"""
                        else -> null
                    }
                } ?: """前置"""

        /** 前置 */
        @Composable fun `Prepend`(vararg args: Any?) = FYTxtConfig.observe { `Prepend`.fmt(args) }

        /** 后置 */
        val `Append`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Append"""
                        RootMLangTags.ZH -> """后置"""
                        else -> null
                    }
                } ?: """后置"""

        /** 后置 */
        @Composable fun `Append`(vararg args: Any?) = FYTxtConfig.observe { `Append`.fmt(args) }

        /** 合并 */
        val `Merge`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Merge"""
                        RootMLangTags.ZH -> """合并"""
                        else -> null
                    }
                } ?: """合并"""

        /** 合并 */
        @Composable fun `Merge`(vararg args: Any?) = FYTxtConfig.observe { `Merge`.fmt(args) }
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

        /** 刷新 */
        val `Refresh`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Refresh"""
                        RootMLangTags.ZH -> """刷新"""
                        else -> null
                    }
                } ?: """刷新"""

        /** 刷新 */
        @Composable fun `Refresh`(vararg args: Any?) = FYTxtConfig.observe { `Refresh`.fmt(args) }

        /** 搜索 */
        val `Search`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Search"""
                        RootMLangTags.ZH -> """搜索"""
                        else -> null
                    }
                } ?: """搜索"""

        /** 搜索 */
        @Composable fun `Search`(vararg args: Any?) = FYTxtConfig.observe { `Search`.fmt(args) }

        /** 排序 */
        val `Sort`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Sort"""
                        RootMLangTags.ZH -> """排序"""
                        else -> null
                    }
                } ?: """排序"""

        /** 排序 */
        @Composable fun `Sort`(vararg args: Any?) = FYTxtConfig.observe { `Sort`.fmt(args) }

        /** 设置 */
        val `Settings`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Settings"""
                        RootMLangTags.ZH -> """设置"""
                        else -> null
                    }
                } ?: """设置"""

        /** 设置 */
        @Composable fun `Settings`(vararg args: Any?) = FYTxtConfig.observe { `Settings`.fmt(args) }
    }

    object `Message` {
        init {
            RootMLangGroups
        }

        /** 确定 */
        val `Confirm`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Confirm"""
                        RootMLangTags.ZH -> """确定"""
                        else -> null
                    }
                } ?: """确定"""

        /** 确定 */
        @Composable fun `Confirm`(vararg args: Any?) = FYTxtConfig.observe { `Confirm`.fmt(args) }

        /** 提示 */
        val `Hint`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Hint"""
                        RootMLangTags.ZH -> """提示"""
                        else -> null
                    }
                } ?: """提示"""

        /** 提示 */
        @Composable fun `Hint`(vararg args: Any?) = FYTxtConfig.observe { `Hint`.fmt(args) }

        /** 错误 */
        val `Error`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Error"""
                        RootMLangTags.ZH -> """错误"""
                        else -> null
                    }
                } ?: """错误"""

        /** 错误 */
        @Composable fun `Error`(vararg args: Any?) = FYTxtConfig.observe { `Error`.fmt(args) }

        /** 成功 */
        val `Success`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Success"""
                        RootMLangTags.ZH -> """成功"""
                        else -> null
                    }
                } ?: """成功"""

        /** 成功 */
        @Composable fun `Success`(vararg args: Any?) = FYTxtConfig.observe { `Success`.fmt(args) }
    }

    object `Button` {
        init {
            RootMLangGroups
        }

        /** 取消 */
        val `Cancel`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Cancel"""
                        RootMLangTags.ZH -> """取消"""
                        else -> null
                    }
                } ?: """取消"""

        /** 取消 */
        @Composable fun `Cancel`(vararg args: Any?) = FYTxtConfig.observe { `Cancel`.fmt(args) }

        /** 确定 */
        val `Confirm`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Confirm"""
                        RootMLangTags.ZH -> """确定"""
                        else -> null
                    }
                } ?: """确定"""

        /** 确定 */
        @Composable fun `Confirm`(vararg args: Any?) = FYTxtConfig.observe { `Confirm`.fmt(args) }

        /** 清除 */
        val `Clear`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Clear"""
                        RootMLangTags.ZH -> """清除"""
                        else -> null
                    }
                } ?: """清除"""

        /** 清除 */
        @Composable fun `Clear`(vararg args: Any?) = FYTxtConfig.observe { `Clear`.fmt(args) }

        /** 编辑 */
        val `Edit`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Edit"""
                        RootMLangTags.ZH -> """编辑"""
                        else -> null
                    }
                } ?: """编辑"""

        /** 编辑 */
        @Composable fun `Edit`(vararg args: Any?) = FYTxtConfig.observe { `Edit`.fmt(args) }

        /** 开始 */
        val `Start`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Start"""
                        RootMLangTags.ZH -> """开始"""
                        else -> null
                    }
                } ?: """开始"""

        /** 开始 */
        @Composable fun `Start`(vararg args: Any?) = FYTxtConfig.observe { `Start`.fmt(args) }
    }

    object `Loading` {
        init {
            RootMLangGroups
        }

        /** 启动中... */
        val `Starting`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Starting..."""
                        RootMLangTags.ZH -> """启动中..."""
                        else -> null
                    }
                } ?: """启动中..."""

        /** 启动中... */
        @Composable fun `Starting`(vararg args: Any?) = FYTxtConfig.observe { `Starting`.fmt(args) }
    }

    object `Update` {
        init {
            RootMLangGroups
        }

        object `Title` {
            init {
                RootMLangGroups
            }

            /** 发现新版本 */
            val `Available`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """New Version Available"""
                            RootMLangTags.ZH -> """发现新版本"""
                            else -> null
                        }
                    } ?: """发现新版本"""

            /** 发现新版本 */
            @Composable
            fun `Available`(vararg args: Any?) = FYTxtConfig.observe { `Available`.fmt(args) }

            /** 强制更新提示 */
            val `ForceCancel`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Mandatory Update"""
                            RootMLangTags.ZH -> """强制更新提示"""
                            else -> null
                        }
                    } ?: """强制更新提示"""

            /** 强制更新提示 */
            @Composable
            fun `ForceCancel`(vararg args: Any?) = FYTxtConfig.observe { `ForceCancel`.fmt(args) }

            /** 安装更新 */
            val `Install`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Install Update"""
                            RootMLangTags.ZH -> """安装更新"""
                            else -> null
                        }
                    } ?: """安装更新"""

            /** 安装更新 */
            @Composable
            fun `Install`(vararg args: Any?) = FYTxtConfig.observe { `Install`.fmt(args) }
        }

        object `Message` {
            init {
                RootMLangGroups
            }

            /** 检测到可用更新 */
            val `Available`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """A new update is available"""
                            RootMLangTags.ZH -> """检测到可用更新"""
                            else -> null
                        }
                    } ?: """检测到可用更新"""

            /** 检测到可用更新 */
            @Composable
            fun `Available`(vararg args: Any?) = FYTxtConfig.observe { `Available`.fmt(args) }

            /** 更新封面 */
            val `CoverDesc`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Update cover"""
                            RootMLangTags.ZH -> """更新封面"""
                            else -> null
                        }
                    } ?: """更新封面"""

            /** 更新封面 */
            @Composable
            fun `CoverDesc`(vararg args: Any?) = FYTxtConfig.observe { `CoverDesc`.fmt(args) }

            /** 当前版本 */
            val `CurrentVersion`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Current Version"""
                            RootMLangTags.ZH -> """当前版本"""
                            else -> null
                        }
                    } ?: """当前版本"""

            /** 当前版本 */
            @Composable
            fun `CurrentVersion`(vararg args: Any?) =
                FYTxtConfig.observe { `CurrentVersion`.fmt(args) }

            /** 推送版本 */
            val `RemoteVersion`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Target Version"""
                            RootMLangTags.ZH -> """推送版本"""
                            else -> null
                        }
                    } ?: """推送版本"""

            /** 推送版本 */
            @Composable
            fun `RemoteVersion`(vararg args: Any?) =
                FYTxtConfig.observe { `RemoteVersion`.fmt(args) }

            /** 正在更新 */
            val `Updating`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Updating"""
                            RootMLangTags.ZH -> """正在更新"""
                            else -> null
                        }
                    } ?: """正在更新"""

            /** 正在更新 */
            @Composable
            fun `Updating`(vararg args: Any?) = FYTxtConfig.observe { `Updating`.fmt(args) }

            /** 正在准备下载... */
            val `Preparing`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Preparing download..."""
                            RootMLangTags.ZH -> """正在准备下载..."""
                            else -> null
                        }
                    } ?: """正在准备下载..."""

            /** 正在准备下载... */
            @Composable
            fun `Preparing`(vararg args: Any?) = FYTxtConfig.observe { `Preparing`.fmt(args) }

            /** 正在下载更新包 %d%% */
            val `DownloadingWithProgress`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Downloading update package %d%%"""
                            RootMLangTags.ZH -> """正在下载更新包 %d%%"""
                            else -> null
                        }
                    } ?: """正在下载更新包 %d%%"""

            /** 正在下载更新包 %d%% */
            @Composable
            fun `DownloadingWithProgress`(vararg args: Any?) =
                FYTxtConfig.observe { `DownloadingWithProgress`.fmt(args) }

            /** 正在校验安装包... */
            val `Verifying`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Verifying update package..."""
                            RootMLangTags.ZH -> """正在校验安装包..."""
                            else -> null
                        }
                    } ?: """正在校验安装包..."""

            /** 正在校验安装包... */
            @Composable
            fun `Verifying`(vararg args: Any?) = FYTxtConfig.observe { `Verifying`.fmt(args) }

            /** 下载完成，等待安装确认 */
            val `Finished`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN ->
                                """Download complete, waiting for install confirmation"""
                            RootMLangTags.ZH -> """下载完成，等待安装确认"""
                            else -> null
                        }
                    } ?: """下载完成，等待安装确认"""

            /** 下载完成，等待安装确认 */
            @Composable
            fun `Finished`(vararg args: Any?) = FYTxtConfig.observe { `Finished`.fmt(args) }

            /** 正在下载更新包... */
            val `Downloading`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Downloading update package..."""
                            RootMLangTags.ZH -> """正在下载更新包..."""
                            else -> null
                        }
                    } ?: """正在下载更新包..."""

            /** 正在下载更新包... */
            @Composable
            fun `Downloading`(vararg args: Any?) = FYTxtConfig.observe { `Downloading`.fmt(args) }

            /** 下载完成，准备安装 */
            val `DownloadReady`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Download complete, ready to install"""
                            RootMLangTags.ZH -> """下载完成，准备安装"""
                            else -> null
                        }
                    } ?: """下载完成，准备安装"""

            /** 下载完成，准备安装 */
            @Composable
            fun `DownloadReady`(vararg args: Any?) =
                FYTxtConfig.observe { `DownloadReady`.fmt(args) }

            /** 安装包校验失败，请重试 */
            val `VerifyFailed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Package verification failed, please retry"""
                            RootMLangTags.ZH -> """安装包校验失败，请重试"""
                            else -> null
                        }
                    } ?: """安装包校验失败，请重试"""

            /** 安装包校验失败，请重试 */
            @Composable
            fun `VerifyFailed`(vararg args: Any?) = FYTxtConfig.observe { `VerifyFailed`.fmt(args) }

            /** 下载失败 (%d): %s */
            val `DownloadErrorWithCode`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Download failed (%d): %s"""
                            RootMLangTags.ZH -> """下载失败 (%d): %s"""
                            else -> null
                        }
                    } ?: """下载失败 (%d): %s"""

            /** 下载失败 (%d): %s */
            @Composable
            fun `DownloadErrorWithCode`(vararg args: Any?) =
                FYTxtConfig.observe { `DownloadErrorWithCode`.fmt(args) }

            /** 下载失败，请稍后重试 */
            val `Error`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Download failed, please try again later"""
                            RootMLangTags.ZH -> """下载失败，请稍后重试"""
                            else -> null
                        }
                    } ?: """下载失败，请稍后重试"""

            /** 下载失败，请稍后重试 */
            @Composable fun `Error`(vararg args: Any?) = FYTxtConfig.observe { `Error`.fmt(args) }

            /** 知道了 */
            val `Close`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Got it"""
                            RootMLangTags.ZH -> """知道了"""
                            else -> null
                        }
                    } ?: """知道了"""

            /** 知道了 */
            @Composable fun `Close`(vararg args: Any?) = FYTxtConfig.observe { `Close`.fmt(args) }

            /** 等待下载开始... */
            val `Waiting`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Waiting for download..."""
                            RootMLangTags.ZH -> """等待下载开始..."""
                            else -> null
                        }
                    } ?: """等待下载开始..."""

            /** 等待下载开始... */
            @Composable
            fun `Waiting`(vararg args: Any?) = FYTxtConfig.observe { `Waiting`.fmt(args) }
        }
    }

    object `ConfigInput` {
        init {
            RootMLangGroups
        }

        /** 端口号 (留空表示不修改) */
        val `PortLabel`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Port (leave empty to not modify)"""
                        RootMLangTags.ZH -> """端口号 (留空表示不修改)"""
                        else -> null
                    }
                } ?: """端口号 (留空表示不修改)"""

        /** 端口号 (留空表示不修改) */
        @Composable
        fun `PortLabel`(vararg args: Any?) = FYTxtConfig.observe { `PortLabel`.fmt(args) }

        /** %d 项 */
        val `CountItems`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """%d items"""
                        RootMLangTags.ZH -> """%d 项"""
                        else -> null
                    }
                } ?: """%d 项"""

        /** %d 项 */
        @Composable
        fun `CountItems`(vararg args: Any?) = FYTxtConfig.observe { `CountItems`.fmt(args) }

        /** 替换整个字典 */
        val `ReplaceHelper`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """After saving, this field is replaced by the complete dictionary edited here. Existing keys not listed here will be removed."""
                        RootMLangTags.ZH -> """保存后会用这里编辑的完整字典替换当前字段；这里未填写的旧键不会保留。"""
                        else -> null
                    }
                } ?: """保存后会用这里编辑的完整字典替换当前字段；这里未填写的旧键不会保留。"""

        /** 保存后会用这里编辑的完整字典替换当前字段；这里未填写的旧键不会保留。 */
        @Composable
        fun `ReplaceHelper`(vararg args: Any?) = FYTxtConfig.observe { `ReplaceHelper`.fmt(args) }

        /** 覆盖匹配键对应的值 */
        val `MergeHelper`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """After saving, only the keys edited here are merged into the current dictionary"""
                        RootMLangTags.ZH -> """保存后只会把这里编辑的键合并到当前字典。"""
                        else -> null
                    }
                } ?: """保存后只会把这里编辑的键合并到当前字典。"""

        /** 保存后只会把这里编辑的键合并到当前字典。 */
        @Composable
        fun `MergeHelper`(vararg args: Any?) = FYTxtConfig.observe { `MergeHelper`.fmt(args) }

        /** 合并模式仅修改指定键，未指定键保持不变。 */
        val `MergeNotice`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """In merge mode, keys with the same name are overwritten, and keys not provided here keep their current values."""
                        RootMLangTags.ZH -> """合并模式下，同名键会被覆盖；这里未填写的键保持当前值。"""
                        else -> null
                    }
                } ?: """合并模式下，同名键会被覆盖；这里未填写的键保持当前值。"""

        /** 合并模式下，同名键会被覆盖；这里未填写的键保持当前值。 */
        @Composable
        fun `MergeNotice`(vararg args: Any?) = FYTxtConfig.observe { `MergeNotice`.fmt(args) }
    }

    object `Accessibility` {
        init {
            RootMLangGroups
        }

        /** %s 旗帜 */
        val `CountryFlag`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """%s flag"""
                        RootMLangTags.ZH -> """%s 旗帜"""
                        else -> null
                    }
                } ?: """%s 旗帜"""

        /** %s 旗帜 */
        @Composable
        fun `CountryFlag`(vararg args: Any?) = FYTxtConfig.observe { `CountryFlag`.fmt(args) }
    }

    object `BottomBar` {
        init {
            RootMLangGroups
        }

        /** 首页 */
        val `Home`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Home"""
                        RootMLangTags.ZH -> """首页"""
                        else -> null
                    }
                } ?: """首页"""

        /** 首页 */
        @Composable fun `Home`(vararg args: Any?) = FYTxtConfig.observe { `Home`.fmt(args) }

        /** 代理 */
        val `Proxy`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Proxy"""
                        RootMLangTags.ZH -> """代理"""
                        else -> null
                    }
                } ?: """代理"""

        /** 代理 */
        @Composable fun `Proxy`(vararg args: Any?) = FYTxtConfig.observe { `Proxy`.fmt(args) }

        /** 配置 */
        val `Config`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Config"""
                        RootMLangTags.ZH -> """配置"""
                        else -> null
                    }
                } ?: """配置"""

        /** 配置 */
        @Composable fun `Config`(vararg args: Any?) = FYTxtConfig.observe { `Config`.fmt(args) }

        /** 设置 */
        val `Setting`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Settings"""
                        RootMLangTags.ZH -> """设置"""
                        else -> null
                    }
                } ?: """设置"""

        /** 设置 */
        @Composable fun `Setting`(vararg args: Any?) = FYTxtConfig.observe { `Setting`.fmt(args) }
    }

    object `Editor` {
        init {
            RootMLangGroups
        }

        /** 共 %d 项 */
        val `CountItems`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Total %d items"""
                        RootMLangTags.ZH -> """共 %d 项"""
                        else -> null
                    }
                } ?: """共 %d 项"""

        /** 共 %d 项 */
        @Composable
        fun `CountItems`(vararg args: Any?) = FYTxtConfig.observe { `CountItems`.fmt(args) }

        object `Action` {
            init {
                RootMLangGroups
            }

            /** 重置 */
            val `Reset`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Reset"""
                            RootMLangTags.ZH -> """重置"""
                            else -> null
                        }
                    } ?: """重置"""

            /** 重置 */
            @Composable fun `Reset`(vararg args: Any?) = FYTxtConfig.observe { `Reset`.fmt(args) }

            /** 添加 */
            val `Add`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Add"""
                            RootMLangTags.ZH -> """添加"""
                            else -> null
                        }
                    } ?: """添加"""

            /** 添加 */
            @Composable fun `Add`(vararg args: Any?) = FYTxtConfig.observe { `Add`.fmt(args) }

            /** 删除 */
            val `Delete`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Delete"""
                            RootMLangTags.ZH -> """删除"""
                            else -> null
                        }
                    } ?: """删除"""

            /** 删除 */
            @Composable fun `Delete`(vararg args: Any?) = FYTxtConfig.observe { `Delete`.fmt(args) }

            /** 撤销 */
            val `Undo`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Undo"""
                            RootMLangTags.ZH -> """撤销"""
                            else -> null
                        }
                    } ?: """撤销"""

            /** 撤销 */
            @Composable fun `Undo`(vararg args: Any?) = FYTxtConfig.observe { `Undo`.fmt(args) }

            /** 重做 */
            val `Redo`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Redo"""
                            RootMLangTags.ZH -> """重做"""
                            else -> null
                        }
                    } ?: """重做"""

            /** 重做 */
            @Composable fun `Redo`(vararg args: Any?) = FYTxtConfig.observe { `Redo`.fmt(args) }

            /** 格式化 */
            val `Format`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Format"""
                            RootMLangTags.ZH -> """格式化"""
                            else -> null
                        }
                    } ?: """格式化"""

            /** 格式化 */
            @Composable fun `Format`(vararg args: Any?) = FYTxtConfig.observe { `Format`.fmt(args) }

            /** 保存 */
            val `Save`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Save"""
                            RootMLangTags.ZH -> """保存"""
                            else -> null
                        }
                    } ?: """保存"""

            /** 保存 */
            @Composable fun `Save`(vararg args: Any?) = FYTxtConfig.observe { `Save`.fmt(args) }

            /** 保存并退出 */
            val `SaveAndExit`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Save & Exit"""
                            RootMLangTags.ZH -> """保存并退出"""
                            else -> null
                        }
                    } ?: """保存并退出"""

            /** 保存并退出 */
            @Composable
            fun `SaveAndExit`(vararg args: Any?) = FYTxtConfig.observe { `SaveAndExit`.fmt(args) }

            /** 直接保存 */
            val `SaveLocally`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Save Directly"""
                            RootMLangTags.ZH -> """直接保存"""
                            else -> null
                        }
                    } ?: """直接保存"""

            /** 直接保存 */
            @Composable
            fun `SaveLocally`(vararg args: Any?) = FYTxtConfig.observe { `SaveLocally`.fmt(args) }

            /** 直接保存并停止 */
            val `SaveAndStop`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Save Directly & Stop"""
                            RootMLangTags.ZH -> """直接保存并停止"""
                            else -> null
                        }
                    } ?: """直接保存并停止"""

            /** 直接保存并停止 */
            @Composable
            fun `SaveAndStop`(vararg args: Any?) = FYTxtConfig.observe { `SaveAndStop`.fmt(args) }

            /** 继续编辑 */
            val `ContinueEditing`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Continue Editing"""
                            RootMLangTags.ZH -> """继续编辑"""
                            else -> null
                        }
                    } ?: """继续编辑"""

            /** 继续编辑 */
            @Composable
            fun `ContinueEditing`(vararg args: Any?) =
                FYTxtConfig.observe { `ContinueEditing`.fmt(args) }

            /** 放弃修改 */
            val `Discard`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Discard"""
                            RootMLangTags.ZH -> """放弃修改"""
                            else -> null
                        }
                    } ?: """放弃修改"""

            /** 放弃修改 */
            @Composable
            fun `Discard`(vararg args: Any?) = FYTxtConfig.observe { `Discard`.fmt(args) }

            /** 校验 */
            val `Check`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Check"""
                            RootMLangTags.ZH -> """校验"""
                            else -> null
                        }
                    } ?: """校验"""

            /** 校验 */
            @Composable fun `Check`(vararg args: Any?) = FYTxtConfig.observe { `Check`.fmt(args) }
        }

        object `Dialog` {
            init {
                RootMLangGroups
            }

            /** 添加条目 */
            val `AddTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Add Entry"""
                            RootMLangTags.ZH -> """添加条目"""
                            else -> null
                        }
                    } ?: """添加条目"""

            /** 添加条目 */
            @Composable
            fun `AddTitle`(vararg args: Any?) = FYTxtConfig.observe { `AddTitle`.fmt(args) }

            /** 编辑条目 */
            val `EditTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Edit Entry"""
                            RootMLangTags.ZH -> """编辑条目"""
                            else -> null
                        }
                    } ?: """编辑条目"""

            /** 编辑条目 */
            @Composable
            fun `EditTitle`(vararg args: Any?) = FYTxtConfig.observe { `EditTitle`.fmt(args) }

            /** 重置确认 */
            val `ResetTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Confirm Reset"""
                            RootMLangTags.ZH -> """重置确认"""
                            else -> null
                        }
                    } ?: """重置确认"""

            /** 重置确认 */
            @Composable
            fun `ResetTitle`(vararg args: Any?) = FYTxtConfig.observe { `ResetTitle`.fmt(args) }

            /** 清空所有条目并恢复为不修改状态？ */
            val `ResetMessage`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN ->
                                """Clear all entries in this editor and restore this field to the unmodified state?"""
                            RootMLangTags.ZH -> """清空这个编辑器中的全部条目，并将该字段恢复为不修改状态？"""
                            else -> null
                        }
                    } ?: """清空这个编辑器中的全部条目，并将该字段恢复为不修改状态？"""

            /** 清空这个编辑器中的全部条目，并将该字段恢复为不修改状态？ */
            @Composable
            fun `ResetMessage`(vararg args: Any?) = FYTxtConfig.observe { `ResetMessage`.fmt(args) }

            /** 未保存的修改 */
            val `DiscardTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Unsaved Changes"""
                            RootMLangTags.ZH -> """未保存的修改"""
                            else -> null
                        }
                    } ?: """未保存的修改"""

            /** 未保存的修改 */
            @Composable
            fun `DiscardTitle`(vararg args: Any?) = FYTxtConfig.observe { `DiscardTitle`.fmt(args) }

            /** 当前有未保存的修改，你想怎么处理？ */
            val `DiscardMessage`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN ->
                                """Leave without saving? Current changes in the editor will be lost."""
                            RootMLangTags.ZH -> """要不保存直接离开吗？当前编辑器中的修改会丢失。"""
                            else -> null
                        }
                    } ?: """要不保存直接离开吗？当前编辑器中的修改会丢失。"""

            /** 要不保存直接离开吗？当前编辑器中的修改会丢失。 */
            @Composable
            fun `DiscardMessage`(vararg args: Any?) =
                FYTxtConfig.observe { `DiscardMessage`.fmt(args) }

            /** 使用 JSON 格式编辑 */
            val `JsonSubtitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Edit the raw JSON content directly"""
                            RootMLangTags.ZH -> """直接编辑原始 JSON 内容"""
                            else -> null
                        }
                    } ?: """直接编辑原始 JSON 内容"""

            /** 直接编辑原始 JSON 内容 */
            @Composable
            fun `JsonSubtitle`(vararg args: Any?) = FYTxtConfig.observe { `JsonSubtitle`.fmt(args) }

            /** 配置预览 */
            val `ConfigPreviewTitle`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Config Preview"""
                            RootMLangTags.ZH -> """配置预览"""
                            else -> null
                        }
                    } ?: """配置预览"""

            /** 配置预览 */
            @Composable
            fun `ConfigPreviewTitle`(vararg args: Any?) =
                FYTxtConfig.observe { `ConfigPreviewTitle`.fmt(args) }

            /** 正在保存到本地... */
            val `LocalSaving`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN ->
                                """Writing the edited result to the local config file..."""
                            RootMLangTags.ZH -> """正在把编辑结果写入本地配置文件..."""
                            else -> null
                        }
                    } ?: """正在把编辑结果写入本地配置文件..."""

            /** 正在把编辑结果写入本地配置文件... */
            @Composable
            fun `LocalSaving`(vararg args: Any?) = FYTxtConfig.observe { `LocalSaving`.fmt(args) }

            /** 正在校验配置... */
            val `ValidatingConfig`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Validating the edited configuration..."""
                            RootMLangTags.ZH -> """正在校验编辑后的配置内容..."""
                            else -> null
                        }
                    } ?: """正在校验编辑后的配置内容..."""

            /** 正在校验编辑后的配置内容... */
            @Composable
            fun `ValidatingConfig`(vararg args: Any?) =
                FYTxtConfig.observe { `ValidatingConfig`.fmt(args) }

            /** 配置校验通过 */
            val `ValidationPassed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Configuration check passed"""
                            RootMLangTags.ZH -> """配置校验通过"""
                            else -> null
                        }
                    } ?: """配置校验通过"""

            /** 配置校验通过 */
            @Composable
            fun `ValidationPassed`(vararg args: Any?) =
                FYTxtConfig.observe { `ValidationPassed`.fmt(args) }

            /** 正在获取远程资源... */
            val `FetchingRemoteResources`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN ->
                                """Fetching remote resources referenced by the configuration and finishing final validation..."""
                            RootMLangTags.ZH -> """正在拉取配置引用的远程资源，并完成最终校验..."""
                            else -> null
                        }
                    } ?: """正在拉取配置引用的远程资源，并完成最终校验..."""

            /** 正在拉取配置引用的远程资源，并完成最终校验... */
            @Composable
            fun `FetchingRemoteResources`(vararg args: Any?) =
                FYTxtConfig.observe { `FetchingRemoteResources`.fmt(args) }

            /** 正在把已保存的配置应用到当前运行时... */
            val `ApplyingRuntime`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN ->
                                """Applying the saved configuration to the current runtime..."""
                            RootMLangTags.ZH -> """正在把已保存的配置应用到当前运行时..."""
                            else -> null
                        }
                    } ?: """正在把已保存的配置应用到当前运行时..."""

            /** 正在把已保存的配置应用到当前运行时... */
            @Composable
            fun `ApplyingRuntime`(vararg args: Any?) =
                FYTxtConfig.observe { `ApplyingRuntime`.fmt(args) }

            /** 保存耗时比预期更长。你可以撤销本次保存并继续编辑。 */
            val `LongRunningUndoSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN ->
                                """This is taking longer than expected. You can undo this save and return to editing."""
                            RootMLangTags.ZH -> """保存耗时比预期更长。你可以撤销本次保存并继续编辑。"""
                            else -> null
                        }
                    } ?: """保存耗时比预期更长。你可以撤销本次保存并继续编辑。"""

            /** 保存耗时比预期更长。你可以撤销本次保存并继续编辑。 */
            @Composable
            fun `LongRunningUndoSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `LongRunningUndoSummary`.fmt(args) }

            /** 远程资源获取耗时比预期更长。你可以继续等待，也可以直接保存当前本地结果并跳过剩余远程资源获取。 */
            val `LongRunningRemoteInterruptionSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN ->
                                """Fetching remote resources is taking longer than expected. You can keep waiting, or save the current local result now and skip the remaining remote fetch."""
                            RootMLangTags.ZH ->
                                """远程资源获取耗时比预期更长。你可以继续等待，也可以直接保存当前本地结果并跳过剩余远程资源获取。"""
                            else -> null
                        }
                    } ?: """远程资源获取耗时比预期更长。你可以继续等待，也可以直接保存当前本地结果并跳过剩余远程资源获取。"""

            /** 远程资源获取耗时比预期更长。你可以继续等待，也可以直接保存当前本地结果并跳过剩余远程资源获取。 */
            @Composable
            fun `LongRunningRemoteInterruptionSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `LongRunningRemoteInterruptionSummary`.fmt(args) }

            /** 已中断远程资源获取并直接保存。当前选中配置正在运行，mihomo 已停止，请按需重新启动。 */
            val `DirectSaveStoppedRuntimeSummary`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN ->
                                """Remote resource fetching was interrupted and the current file was saved locally. Because the selected profile was running, mihomo has been stopped. Restart it if you still want the profile to take effect."""
                            RootMLangTags.ZH ->
                                """已中断远程资源获取，并把当前文件直接保存到本地。由于当前选中的配置当时正在运行，mihomo 已停止；如需继续生效，请重新启动。"""
                            else -> null
                        }
                    } ?: """已中断远程资源获取，并把当前文件直接保存到本地。由于当前选中的配置当时正在运行，mihomo 已停止；如需继续生效，请重新启动。"""

            /** 已中断远程资源获取，并把当前文件直接保存到本地。由于当前选中的配置当时正在运行，mihomo 已停止；如需继续生效，请重新启动。 */
            @Composable
            fun `DirectSaveStoppedRuntimeSummary`(vararg args: Any?) =
                FYTxtConfig.observe { `DirectSaveStoppedRuntimeSummary`.fmt(args) }
        }

        object `Empty` {
            init {
                RootMLangGroups
            }

            /** 暂无条目 */
            val `Title`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """No Entries"""
                            RootMLangTags.ZH -> """暂无条目"""
                            else -> null
                        }
                    } ?: """暂无条目"""

            /** 暂无条目 */
            @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

            /** 点击右上角按钮添加 */
            val `Hint`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN ->
                                """Use the add action in the top-right corner to create the first entry"""
                            RootMLangTags.ZH -> """使用右上角的新增按钮创建第一条条目"""
                            else -> null
                        }
                    } ?: """使用右上角的新增按钮创建第一条条目"""

            /** 使用右上角的新增按钮创建第一条条目 */
            @Composable fun `Hint`(vararg args: Any?) = FYTxtConfig.observe { `Hint`.fmt(args) }
        }

        object `Error` {
            init {
                RootMLangGroups
            }

            /** 键不能为空 */
            val `KeyEmpty`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Key cannot be empty"""
                            RootMLangTags.ZH -> """键不能为空"""
                            else -> null
                        }
                    } ?: """键不能为空"""

            /** 键不能为空 */
            @Composable
            fun `KeyEmpty`(vararg args: Any?) = FYTxtConfig.observe { `KeyEmpty`.fmt(args) }

            /** 键已存在 */
            val `KeyExists`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Key already exists"""
                            RootMLangTags.ZH -> """键已存在"""
                            else -> null
                        }
                    } ?: """键已存在"""

            /** 键已存在 */
            @Composable
            fun `KeyExists`(vararg args: Any?) = FYTxtConfig.observe { `KeyExists`.fmt(args) }

            /** 保存内容失败 */
            val `SaveFailed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Failed to save content"""
                            RootMLangTags.ZH -> """保存内容失败"""
                            else -> null
                        }
                    } ?: """保存内容失败"""

            /** 保存内容失败 */
            @Composable
            fun `SaveFailed`(vararg args: Any?) = FYTxtConfig.observe { `SaveFailed`.fmt(args) }

            /** JSON 语法错误 */
            val `JsonSyntaxError`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """JSON syntax error"""
                            RootMLangTags.ZH -> """JSON 语法错误"""
                            else -> null
                        }
                    } ?: """JSON 语法错误"""

            /** JSON 语法错误 */
            @Composable
            fun `JsonSyntaxError`(vararg args: Any?) =
                FYTxtConfig.observe { `JsonSyntaxError`.fmt(args) }

            /** 配置语法校验失败：%s */
            val `ValidationFailed`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Configuration syntax validation failed: %s"""
                            RootMLangTags.ZH -> """配置语法校验失败：%s"""
                            else -> null
                        }
                    } ?: """配置语法校验失败：%s"""

            /** 配置语法校验失败：%s */
            @Composable
            fun `ValidationFailed`(vararg args: Any?) =
                FYTxtConfig.observe { `ValidationFailed`.fmt(args) }

            /** JSON 必须以对象或数组开头 */
            val `JsonRootExpected`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """JSON must start with an object or array"""
                            RootMLangTags.ZH -> """JSON 必须以对象或数组开头"""
                            else -> null
                        }
                    } ?: """JSON 必须以对象或数组开头"""

            /** JSON 必须以对象或数组开头 */
            @Composable
            fun `JsonRootExpected`(vararg args: Any?) =
                FYTxtConfig.observe { `JsonRootExpected`.fmt(args) }

            /** YAML 语法错误 */
            val `YamlSyntaxError`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """YAML syntax error"""
                            RootMLangTags.ZH -> """YAML 语法错误"""
                            else -> null
                        }
                    } ?: """YAML 语法错误"""

            /** YAML 语法错误 */
            @Composable
            fun `YamlSyntaxError`(vararg args: Any?) =
                FYTxtConfig.observe { `YamlSyntaxError`.fmt(args) }

            /** 未终止的字符串或对象 */
            val `Unterminated`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Unterminated string or object"""
                            RootMLangTags.ZH -> """未终止的字符串或对象"""
                            else -> null
                        }
                    } ?: """未终止的字符串或对象"""

            /** 未终止的字符串或对象 */
            @Composable
            fun `Unterminated`(vararg args: Any?) = FYTxtConfig.observe { `Unterminated`.fmt(args) }

            /** 期望 %s */
            val `Expected`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Expected %s"""
                            RootMLangTags.ZH -> """期望 %s"""
                            else -> null
                        }
                    } ?: """期望 %s"""

            /** 期望 %s */
            @Composable
            fun `Expected`(vararg args: Any?) = FYTxtConfig.observe { `Expected`.fmt(args) }

            /** 未知 */
            val `Unknown`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Unknown"""
                            RootMLangTags.ZH -> """未知"""
                            else -> null
                        }
                    } ?: """未知"""

            /** 未知 */
            @Composable
            fun `Unknown`(vararg args: Any?) = FYTxtConfig.observe { `Unknown`.fmt(args) }

            /** 缺少值 */
            val `MissingValue`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Missing value"""
                            RootMLangTags.ZH -> """缺少值"""
                            else -> null
                        }
                    } ?: """缺少值"""

            /** 缺少值 */
            @Composable
            fun `MissingValue`(vararg args: Any?) = FYTxtConfig.observe { `MissingValue`.fmt(args) }

            /** 重复的键 */
            val `DuplicateKey`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Duplicate key"""
                            RootMLangTags.ZH -> """重复的键"""
                            else -> null
                        }
                    } ?: """重复的键"""

            /** 重复的键 */
            @Composable
            fun `DuplicateKey`(vararg args: Any?) = FYTxtConfig.observe { `DuplicateKey`.fmt(args) }
        }

        object `Rule` {
            init {
                RootMLangGroups
            }

            /** 规则类型 */
            val `Type`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Rule Type"""
                            RootMLangTags.ZH -> """规则类型"""
                            else -> null
                        }
                    } ?: """规则类型"""

            /** 规则类型 */
            @Composable fun `Type`(vararg args: Any?) = FYTxtConfig.observe { `Type`.fmt(args) }

            /** 目标 */
            val `Target`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Target"""
                            RootMLangTags.ZH -> """目标"""
                            else -> null
                        }
                    } ?: """目标"""

            /** 目标 */
            @Composable fun `Target`(vararg args: Any?) = FYTxtConfig.observe { `Target`.fmt(args) }

            /** 规则内容 */
            val `Content`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Rule Content"""
                            RootMLangTags.ZH -> """规则内容"""
                            else -> null
                        }
                    } ?: """规则内容"""

            /** 规则内容 */
            @Composable
            fun `Content`(vararg args: Any?) = FYTxtConfig.observe { `Content`.fmt(args) }

            /** 源 IP */
            val `Src`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Src IP"""
                            RootMLangTags.ZH -> """源 IP"""
                            else -> null
                        }
                    } ?: """源 IP"""

            /** 源 IP */
            @Composable fun `Src`(vararg args: Any?) = FYTxtConfig.observe { `Src`.fmt(args) }

            /** 不解析 */
            val `NoResolve`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """No Resolve"""
                            RootMLangTags.ZH -> """不解析"""
                            else -> null
                        }
                    } ?: """不解析"""

            /** 不解析 */
            @Composable
            fun `NoResolve`(vararg args: Any?) = FYTxtConfig.observe { `NoResolve`.fmt(args) }

            /** REJECT */
            val `TargetReject`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """REJECT"""
                            RootMLangTags.ZH -> """REJECT"""
                            else -> null
                        }
                    } ?: """REJECT"""

            /** REJECT */
            @Composable
            fun `TargetReject`(vararg args: Any?) = FYTxtConfig.observe { `TargetReject`.fmt(args) }

            /** DIRECT */
            val `TargetDirect`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """DIRECT"""
                            RootMLangTags.ZH -> """DIRECT"""
                            else -> null
                        }
                    } ?: """DIRECT"""

            /** DIRECT */
            @Composable
            fun `TargetDirect`(vararg args: Any?) = FYTxtConfig.observe { `TargetDirect`.fmt(args) }

            /** MATCH */
            val `TargetMatch`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """MATCH"""
                            RootMLangTags.ZH -> """MATCH"""
                            else -> null
                        }
                    } ?: """MATCH"""

            /** MATCH */
            @Composable
            fun `TargetMatch`(vararg args: Any?) = FYTxtConfig.observe { `TargetMatch`.fmt(args) }

            /** 请选择目标 */
            val `ErrorTargetRequired`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Please select target"""
                            RootMLangTags.ZH -> """请选择目标"""
                            else -> null
                        }
                    } ?: """请选择目标"""

            /** 请选择目标 */
            @Composable
            fun `ErrorTargetRequired`(vararg args: Any?) =
                FYTxtConfig.observe { `ErrorTargetRequired`.fmt(args) }

            /** 规则内容不能为空 */
            val `ErrorContentRequired`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Rule content cannot be empty"""
                            RootMLangTags.ZH -> """规则内容不能为空"""
                            else -> null
                        }
                    } ?: """规则内容不能为空"""

            /** 规则内容不能为空 */
            @Composable
            fun `ErrorContentRequired`(vararg args: Any?) =
                FYTxtConfig.observe { `ErrorContentRequired`.fmt(args) }
        }
    }
}
