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

object MLangMetaFeature {
    init {
        RootMLangGroups
    }

    /** Meta 功能 */
    val `Title`
        get() =
            FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                it as RootMLangTags
                when (it) {
                    RootMLangTags.EN -> """Meta Features"""
                    RootMLangTags.ZH -> """Meta 功能"""
                    else -> null
                }
            } ?: """Meta 功能"""

    /** Meta 功能 */
    @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

    object `RecentRequests` {
        init {
            RootMLangGroups
        }

        /** 最近请求 */
        val `Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Recent Requests"""
                        RootMLangTags.ZH -> """最近请求"""
                        else -> null
                    }
                } ?: """最近请求"""

        /** 最近请求 */
        @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

        /** 查看最近请求与流量统计 */
        val `Summary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """View recent requests and traffic statistics"""
                        RootMLangTags.ZH -> """查看最近请求与流量统计"""
                        else -> null
                    }
                } ?: """查看最近请求与流量统计"""

        /** 查看最近请求与流量统计 */
        @Composable fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }
    }

    object `RuntimeConfig` {
        init {
            RootMLangGroups
        }

        /** 运行时配置 */
        val `Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Runtime Config"""
                        RootMLangTags.ZH -> """运行时配置"""
                        else -> null
                    }
                } ?: """运行时配置"""

        /** 运行时配置 */
        @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

        /** 查看当前 mihomo 的运行时配置快照 */
        val `Summary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """View the current mihomo runtime configuration snapshot"""
                        RootMLangTags.ZH -> """查看当前 mihomo 的运行时配置快照"""
                        else -> null
                    }
                } ?: """查看当前 mihomo 的运行时配置快照"""

        /** 查看当前 mihomo 的运行时配置快照 */
        @Composable fun `Summary`(vararg args: Any?) = FYTxtConfig.observe { `Summary`.fmt(args) }

        /** 读取运行时配置失败 */
        val `LoadFailed`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Failed to load runtime config"""
                        RootMLangTags.ZH -> """读取运行时配置失败"""
                        else -> null
                    }
                } ?: """读取运行时配置失败"""

        /** 读取运行时配置失败 */
        @Composable
        fun `LoadFailed`(vararg args: Any?) = FYTxtConfig.observe { `LoadFailed`.fmt(args) }

        /** 当前没有活动配置 */
        val `NoActiveProfile`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """No active profile"""
                        RootMLangTags.ZH -> """当前没有活动配置"""
                        else -> null
                    }
                } ?: """当前没有活动配置"""

        /** 当前没有活动配置 */
        @Composable
        fun `NoActiveProfile`(vararg args: Any?) =
            FYTxtConfig.observe { `NoActiveProfile`.fmt(args) }

        /** 未找到当前活动配置文件 */
        val `ConfigNotFound`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Active profile config file not found"""
                        RootMLangTags.ZH -> """未找到当前活动配置文件"""
                        else -> null
                    }
                } ?: """未找到当前活动配置文件"""

        /** 未找到当前活动配置文件 */
        @Composable
        fun `ConfigNotFound`(vararg args: Any?) = FYTxtConfig.observe { `ConfigNotFound`.fmt(args) }

        /** 运行时配置 */
        val `PreviewTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Runtime Config"""
                        RootMLangTags.ZH -> """运行时配置"""
                        else -> null
                    }
                } ?: """运行时配置"""

        /** 运行时配置 */
        @Composable
        fun `PreviewTitle`(vararg args: Any?) = FYTxtConfig.observe { `PreviewTitle`.fmt(args) }

        /** 运行时配置 · %s */
        val `PreviewTitleWithProfile`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Runtime Config · %s"""
                        RootMLangTags.ZH -> """运行时配置 · %s"""
                        else -> null
                    }
                } ?: """运行时配置 · %s"""

        /** 运行时配置 · %s */
        @Composable
        fun `PreviewTitleWithProfile`(vararg args: Any?) =
            FYTxtConfig.observe { `PreviewTitleWithProfile`.fmt(args) }

        /** 无法获取实时配置：%s */
        val `RuntimeConfigFetchFailed`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Unable to fetch live config: %s"""
                        RootMLangTags.ZH -> """无法获取实时配置：%s"""
                        else -> null
                    }
                } ?: """无法获取实时配置：%s"""

        /** 无法获取实时配置：%s */
        @Composable
        fun `RuntimeConfigFetchFailed`(vararg args: Any?) =
            FYTxtConfig.observe { `RuntimeConfigFetchFailed`.fmt(args) }

        /** 密钥不匹配。请尝试重启内核以同步设置。 */
        val `RuntimeConfigUnauthorized`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Mismatching secret. Please try restarting the kernel to sync settings."""
                        RootMLangTags.ZH -> """密钥不匹配。请尝试重启内核以同步设置。"""
                        else -> null
                    }
                } ?: """密钥不匹配。请尝试重启内核以同步设置。"""

        /** 密钥不匹配。请尝试重启内核以同步设置。 */
        @Composable
        fun `RuntimeConfigUnauthorized`(vararg args: Any?) =
            FYTxtConfig.observe { `RuntimeConfigUnauthorized`.fmt(args) }

        /** 无法获取实时配置：内核未运行 (Connection Refused) */
        val `RuntimeConfigNotRunning`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Unable to fetch live config: Kernel not running"""
                        RootMLangTags.ZH -> """无法获取实时配置：内核未运行 (Connection Refused)"""
                        else -> null
                    }
                } ?: """无法获取实时配置：内核未运行 (Connection Refused)"""

        /** 无法获取实时配置：内核未运行 (Connection Refused) */
        @Composable
        fun `RuntimeConfigNotRunning`(vararg args: Any?) =
            FYTxtConfig.observe { `RuntimeConfigNotRunning`.fmt(args) }
    }

    object `GeoX` {
        init {
            RootMLangGroups
        }

        /** 更新 GeoX */
        val `OnlineUpdateTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Update GeoX"""
                        RootMLangTags.ZH -> """更新 GeoX"""
                        else -> null
                    }
                } ?: """更新 GeoX"""

        /** 更新 GeoX */
        @Composable
        fun `OnlineUpdateTitle`(vararg args: Any?) =
            FYTxtConfig.observe { `OnlineUpdateTitle`.fmt(args) }

        /** 下载数据库文件 */
        val `OnlineUpdateSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Download Database"""
                        RootMLangTags.ZH -> """下载数据库文件"""
                        else -> null
                    }
                } ?: """下载数据库文件"""

        /** 下载数据库文件 */
        @Composable
        fun `OnlineUpdateSummary`(vararg args: Any?) =
            FYTxtConfig.observe { `OnlineUpdateSummary`.fmt(args) }
    }

    object `Download` {
        init {
            RootMLangGroups
        }

        /** 更新 GeoX */
        val `DialogTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Update GeoX Online"""
                        RootMLangTags.ZH -> """更新 GeoX"""
                        else -> null
                    }
                } ?: """更新 GeoX"""

        /** 更新 GeoX */
        @Composable
        fun `DialogTitle`(vararg args: Any?) = FYTxtConfig.observe { `DialogTitle`.fmt(args) }

        /** 请选择要更新的文件 */
        val `SelectFiles`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Please select files to update"""
                        RootMLangTags.ZH -> """请选择要更新的文件"""
                        else -> null
                    }
                } ?: """请选择要更新的文件"""

        /** 请选择要更新的文件 */
        @Composable
        fun `SelectFiles`(vararg args: Any?) = FYTxtConfig.observe { `SelectFiles`.fmt(args) }

        /** 下载完成：%d/%d */
        val `DownloadComplete`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Download complete: %d/%d"""
                        RootMLangTags.ZH -> """下载完成：%d/%d"""
                        else -> null
                    }
                } ?: """下载完成：%d/%d"""

        /** 下载完成：%d/%d */
        @Composable
        fun `DownloadComplete`(vararg args: Any?) =
            FYTxtConfig.observe { `DownloadComplete`.fmt(args) }
    }

    object `Dashboard` {
        init {
            RootMLangGroups
        }

        /** 网页控制面板 */
        val `Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Web Dashboard"""
                        RootMLangTags.ZH -> """网页控制面板"""
                        else -> null
                    }
                } ?: """网页控制面板"""

        /** 网页控制面板 */
        @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

        /** 控制面板端口 */
        val `Port`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Dashboard Port"""
                        RootMLangTags.ZH -> """控制面板端口"""
                        else -> null
                    }
                } ?: """控制面板端口"""

        /** 控制面板端口 */
        @Composable fun `Port`(vararg args: Any?) = FYTxtConfig.observe { `Port`.fmt(args) }

        /** 配置网页控制面板的端口 */
        val `PortSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Configure the port for the web dashboard"""
                        RootMLangTags.ZH -> """配置网页控制面板的端口"""
                        else -> null
                    }
                } ?: """配置网页控制面板的端口"""

        /** 配置网页控制面板的端口 */
        @Composable
        fun `PortSummary`(vararg args: Any?) = FYTxtConfig.observe { `PortSummary`.fmt(args) }

        /** API 密钥 */
        val `Secret`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """API Secret"""
                        RootMLangTags.ZH -> """API 密钥"""
                        else -> null
                    }
                } ?: """API 密钥"""

        /** API 密钥 */
        @Composable fun `Secret`(vararg args: Any?) = FYTxtConfig.observe { `Secret`.fmt(args) }

        /** 用于验证控制面板用户的密钥 */
        val `SecretSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """The secret used to authenticate the user for the web dashboard"""
                        RootMLangTags.ZH -> """用于验证控制面板用户的密钥"""
                        else -> null
                    }
                } ?: """用于验证控制面板用户的密钥"""

        /** 用于验证控制面板用户的密钥 */
        @Composable
        fun `SecretSummary`(vararg args: Any?) = FYTxtConfig.observe { `SecretSummary`.fmt(args) }

        /** 显示密钥 */
        val `ShowSecret`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Show Secret"""
                        RootMLangTags.ZH -> """显示密钥"""
                        else -> null
                    }
                } ?: """显示密钥"""

        /** 显示密钥 */
        @Composable
        fun `ShowSecret`(vararg args: Any?) = FYTxtConfig.observe { `ShowSecret`.fmt(args) }

        /** 下载 MetaCubeXD */
        val `Download`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Download MetaCubeXD"""
                        RootMLangTags.ZH -> """下载 MetaCubeXD"""
                        else -> null
                    }
                } ?: """下载 MetaCubeXD"""

        /** 下载 MetaCubeXD */
        @Composable fun `Download`(vararg args: Any?) = FYTxtConfig.observe { `Download`.fmt(args) }

        /** 下载 Web UI 资源并解压到数据目录 */
        val `DownloadSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Download and extract the Web UI assets into the data directory"""
                        RootMLangTags.ZH -> """下载 Web UI 资源并解压到数据目录"""
                        else -> null
                    }
                } ?: """下载 Web UI 资源并解压到数据目录"""

        /** 下载 Web UI 资源并解压到数据目录 */
        @Composable
        fun `DownloadSummary`(vararg args: Any?) =
            FYTxtConfig.observe { `DownloadSummary`.fmt(args) }

        /** 控制面板 UI 下载完成 */
        val `DownloadOk`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Dashboard UI downloaded successfully"""
                        RootMLangTags.ZH -> """控制面板 UI 下载完成"""
                        else -> null
                    }
                } ?: """控制面板 UI 下载完成"""

        /** 控制面板 UI 下载完成 */
        @Composable
        fun `DownloadOk`(vararg args: Any?) = FYTxtConfig.observe { `DownloadOk`.fmt(args) }

        /** 下载失败：%s */
        val `DownloadFailed`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Download failed: %s"""
                        RootMLangTags.ZH -> """下载失败：%s"""
                        else -> null
                    }
                } ?: """下载失败：%s"""

        /** 下载失败：%s */
        @Composable
        fun `DownloadFailed`(vararg args: Any?) = FYTxtConfig.observe { `DownloadFailed`.fmt(args) }

        /** 启动控制面板 */
        val `Launch`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Launch Dashboard"""
                        RootMLangTags.ZH -> """启动控制面板"""
                        else -> null
                    }
                } ?: """启动控制面板"""

        /** 启动控制面板 */
        @Composable fun `Launch`(vararg args: Any?) = FYTxtConfig.observe { `Launch`.fmt(args) }

        /** 全屏打开 Web UI */
        val `LaunchSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Open the Web UI in full-screen"""
                        RootMLangTags.ZH -> """全屏打开 Web UI"""
                        else -> null
                    }
                } ?: """全屏打开 Web UI"""

        /** 全屏打开 Web UI */
        @Composable
        fun `LaunchSummary`(vararg args: Any?) = FYTxtConfig.observe { `LaunchSummary`.fmt(args) }

        /** 请先下载面板 */
        val `LaunchDisabled`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Please download dashboard first"""
                        RootMLangTags.ZH -> """请先下载面板"""
                        else -> null
                    }
                } ?: """请先下载面板"""

        /** 请先下载面板 */
        @Composable
        fun `LaunchDisabled`(vararg args: Any?) = FYTxtConfig.observe { `LaunchDisabled`.fmt(args) }
    }
}
