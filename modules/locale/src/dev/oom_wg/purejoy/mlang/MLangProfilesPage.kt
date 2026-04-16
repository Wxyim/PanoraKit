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

object MLangProfilesPage {
    init {
        RootMLangGroups
    }

    /** 配置 */
    val `Title`
        get() =
            FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                it as RootMLangTags
                when (it) {
                    RootMLangTags.EN -> """Profiles"""
                    RootMLangTags.ZH -> """配置"""
                    else -> null
                }
            } ?: """配置"""

    /** 配置 */
    @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

    object `Action` {
        init {
            RootMLangGroups
        }

        /** 一键更新所有 */
        val `UpdateAll`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Update All"""
                        RootMLangTags.ZH -> """一键更新所有"""
                        else -> null
                    }
                } ?: """一键更新所有"""

        /** 一键更新所有 */
        @Composable
        fun `UpdateAll`(vararg args: Any?) = FYTxtConfig.observe { `UpdateAll`.fmt(args) }

        /** 添加配置 */
        val `AddProfile`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Add Profile"""
                        RootMLangTags.ZH -> """添加配置"""
                        else -> null
                    }
                } ?: """添加配置"""

        /** 添加配置 */
        @Composable
        fun `AddProfile`(vararg args: Any?) = FYTxtConfig.observe { `AddProfile`.fmt(args) }
    }

    object `Empty` {
        init {
            RootMLangGroups
        }

        /** 暂无配置文件 */
        val `NoProfiles`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """No profiles"""
                        RootMLangTags.ZH -> """暂无配置文件"""
                        else -> null
                    }
                } ?: """暂无配置文件"""

        /** 暂无配置文件 */
        @Composable
        fun `NoProfiles`(vararg args: Any?) = FYTxtConfig.observe { `NoProfiles`.fmt(args) }

        /** 点击右上角添加配置 */
        val `Hint`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Click top-right to add profile"""
                        RootMLangTags.ZH -> """点击右上角添加配置"""
                        else -> null
                    }
                } ?: """点击右上角添加配置"""

        /** 点击右上角添加配置 */
        @Composable fun `Hint`(vararg args: Any?) = FYTxtConfig.observe { `Hint`.fmt(args) }
    }

    object `Sheet` {
        init {
            RootMLangGroups
        }

        /** 添加配置文件 */
        val `AddTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Add Profile"""
                        RootMLangTags.ZH -> """添加配置文件"""
                        else -> null
                    }
                } ?: """添加配置文件"""

        /** 添加配置文件 */
        @Composable fun `AddTitle`(vararg args: Any?) = FYTxtConfig.observe { `AddTitle`.fmt(args) }

        /** 编辑配置文件 */
        val `EditTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Edit Profile"""
                        RootMLangTags.ZH -> """编辑配置文件"""
                        else -> null
                    }
                } ?: """编辑配置文件"""

        /** 编辑配置文件 */
        @Composable
        fun `EditTitle`(vararg args: Any?) = FYTxtConfig.observe { `EditTitle`.fmt(args) }
    }

    object `Type` {
        init {
            RootMLangGroups
        }

        /** 配置类型 */
        val `Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Profile Type"""
                        RootMLangTags.ZH -> """配置类型"""
                        else -> null
                    }
                } ?: """配置类型"""

        /** 配置类型 */
        @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

        /** 订阅链接 */
        val `Subscription`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Subscription URL"""
                        RootMLangTags.ZH -> """订阅链接"""
                        else -> null
                    }
                } ?: """订阅链接"""

        /** 订阅链接 */
        @Composable
        fun `Subscription`(vararg args: Any?) = FYTxtConfig.observe { `Subscription`.fmt(args) }

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

        /** 扫码添加 */
        val `QrScan`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Scan QR Code"""
                        RootMLangTags.ZH -> """扫码添加"""
                        else -> null
                    }
                } ?: """扫码添加"""

        /** 扫码添加 */
        @Composable fun `QrScan`(vararg args: Any?) = FYTxtConfig.observe { `QrScan`.fmt(args) }

        /** 空白配置 */
        val `BlankConfig`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Blank Config"""
                        RootMLangTags.ZH -> """空白配置"""
                        else -> null
                    }
                } ?: """空白配置"""

        /** 空白配置 */
        @Composable
        fun `BlankConfig`(vararg args: Any?) = FYTxtConfig.observe { `BlankConfig`.fmt(args) }
    }

    object `Input` {
        init {
            RootMLangGroups
        }

        /** 配置名称 */
        val `ProfileName`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Profile Name"""
                        RootMLangTags.ZH -> """配置名称"""
                        else -> null
                    }
                } ?: """配置名称"""

        /** 配置名称 */
        @Composable
        fun `ProfileName`(vararg args: Any?) = FYTxtConfig.observe { `ProfileName`.fmt(args) }

        /** 订阅链接 (HTTP/HTTPS) */
        val `SubscriptionUrl`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Subscription URL (HTTP/HTTPS)"""
                        RootMLangTags.ZH -> """订阅链接 (HTTP/HTTPS)"""
                        else -> null
                    }
                } ?: """订阅链接 (HTTP/HTTPS)"""

        /** 订阅链接 (HTTP/HTTPS) */
        @Composable
        fun `SubscriptionUrl`(vararg args: Any?) =
            FYTxtConfig.observe { `SubscriptionUrl`.fmt(args) }

        /** 点击选择文件 */
        val `SelectFile`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Click to select file"""
                        RootMLangTags.ZH -> """点击选择文件"""
                        else -> null
                    }
                } ?: """点击选择文件"""

        /** 点击选择文件 */
        @Composable
        fun `SelectFile`(vararg args: Any?) = FYTxtConfig.observe { `SelectFile`.fmt(args) }

        /** 新配置 */
        val `NewProfile`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """New Profile"""
                        RootMLangTags.ZH -> """新配置"""
                        else -> null
                    }
                } ?: """新配置"""

        /** 新配置 */
        @Composable
        fun `NewProfile`(vararg args: Any?) = FYTxtConfig.observe { `NewProfile`.fmt(args) }

        /** 创建一个本地配置模板，并立即在编辑器中打开。 */
        val `BlankConfigHint`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Create a local profile from the built-in basic template and open it in the editor immediately."""
                        RootMLangTags.ZH -> """基于内置基础模板创建本地配置，并立即在编辑器中打开。"""
                        else -> null
                    }
                } ?: """基于内置基础模板创建本地配置，并立即在编辑器中打开。"""

        /** 基于内置基础模板创建本地配置，并立即在编辑器中打开。 */
        @Composable
        fun `BlankConfigHint`(vararg args: Any?) =
            FYTxtConfig.observe { `BlankConfigHint`.fmt(args) }
    }

    object `QrScanner` {
        init {
            RootMLangGroups
        }

        /** 需要相机权限 */
        val `NeedPermission`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Camera permission required"""
                        RootMLangTags.ZH -> """需要相机权限"""
                        else -> null
                    }
                } ?: """需要相机权限"""

        /** 需要相机权限 */
        @Composable
        fun `NeedPermission`(vararg args: Any?) = FYTxtConfig.observe { `NeedPermission`.fmt(args) }

        /** 需要相机权限才能扫码 */
        val `NeedCamera`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Camera permission required for scanning"""
                        RootMLangTags.ZH -> """需要相机权限才能扫码"""
                        else -> null
                    }
                } ?: """需要相机权限才能扫码"""

        /** 需要相机权限才能扫码 */
        @Composable
        fun `NeedCamera`(vararg args: Any?) = FYTxtConfig.observe { `NeedCamera`.fmt(args) }

        /** 从相册选择二维码图片 */
        val `SelectFromAlbum`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Select QR code from album"""
                        RootMLangTags.ZH -> """从相册选择二维码图片"""
                        else -> null
                    }
                } ?: """从相册选择二维码图片"""

        /** 从相册选择二维码图片 */
        @Composable
        fun `SelectFromAlbum`(vararg args: Any?) =
            FYTxtConfig.observe { `SelectFromAlbum`.fmt(args) }

        /** 扫描成功 */
        val `ScanSuccess`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Scan successful"""
                        RootMLangTags.ZH -> """扫描成功"""
                        else -> null
                    }
                } ?: """扫描成功"""

        /** 扫描成功 */
        @Composable
        fun `ScanSuccess`(vararg args: Any?) = FYTxtConfig.observe { `ScanSuccess`.fmt(args) }

        /** 识别成功 */
        val `RecognizeSuccess`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Recognition successful"""
                        RootMLangTags.ZH -> """识别成功"""
                        else -> null
                    }
                } ?: """识别成功"""

        /** 识别成功 */
        @Composable
        fun `RecognizeSuccess`(vararg args: Any?) =
            FYTxtConfig.observe { `RecognizeSuccess`.fmt(args) }

        /** 未能识别到二维码 */
        val `RecognizeFailed`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Failed to recognize QR code"""
                        RootMLangTags.ZH -> """未能识别到二维码"""
                        else -> null
                    }
                } ?: """未能识别到二维码"""

        /** 未能识别到二维码 */
        @Composable
        fun `RecognizeFailed`(vararg args: Any?) =
            FYTxtConfig.observe { `RecognizeFailed`.fmt(args) }

        /** 识别失败：%s */
        val `RecognizeError`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Recognition failed: %s"""
                        RootMLangTags.ZH -> """识别失败：%s"""
                        else -> null
                    }
                } ?: """识别失败：%s"""

        /** 识别失败：%s */
        @Composable
        fun `RecognizeError`(vararg args: Any?) = FYTxtConfig.observe { `RecognizeError`.fmt(args) }
    }

    object `Message` {
        init {
            RootMLangGroups
        }

        /** 未知文件 */
        val `UnknownFile`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Unknown file"""
                        RootMLangTags.ZH -> """未知文件"""
                        else -> null
                    }
                } ?: """未知文件"""

        /** 未知文件 */
        @Composable
        fun `UnknownFile`(vararg args: Any?) = FYTxtConfig.observe { `UnknownFile`.fmt(args) }

        /** 读取配置失败 */
        val `ReadProfileFailed`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Failed to read profile"""
                        RootMLangTags.ZH -> """读取配置失败"""
                        else -> null
                    }
                } ?: """读取配置失败"""

        /** 读取配置失败 */
        @Composable
        fun `ReadProfileFailed`(vararg args: Any?) =
            FYTxtConfig.observe { `ReadProfileFailed`.fmt(args) }

        /** 配置文件不存在 */
        val `ProfileFileNotExist`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Profile file does not exist"""
                        RootMLangTags.ZH -> """配置文件不存在"""
                        else -> null
                    }
                } ?: """配置文件不存在"""

        /** 配置文件不存在 */
        @Composable
        fun `ProfileFileNotExist`(vararg args: Any?) =
            FYTxtConfig.observe { `ProfileFileNotExist`.fmt(args) }

        /** 分享失败 */
        val `ShareFailed`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Share failed"""
                        RootMLangTags.ZH -> """分享失败"""
                        else -> null
                    }
                } ?: """分享失败"""

        /** 分享失败 */
        @Composable
        fun `ShareFailed`(vararg args: Any?) = FYTxtConfig.observe { `ShareFailed`.fmt(args) }
    }

    object `Validation` {
        init {
            RootMLangGroups
        }

        /** 请输入链接 */
        val `EnterUrl`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Please enter URL"""
                        RootMLangTags.ZH -> """请输入链接"""
                        else -> null
                    }
                } ?: """请输入链接"""

        /** 请输入链接 */
        @Composable fun `EnterUrl`(vararg args: Any?) = FYTxtConfig.observe { `EnterUrl`.fmt(args) }

        /** 请选择文件 */
        val `SelectFile`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Please select a file"""
                        RootMLangTags.ZH -> """请选择文件"""
                        else -> null
                    }
                } ?: """请选择文件"""

        /** 请选择文件 */
        @Composable
        fun `SelectFile`(vararg args: Any?) = FYTxtConfig.observe { `SelectFile`.fmt(args) }

        /** 仅支持 .yaml 或 .yml 格式的配置文件 */
        val `YamlOnly`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Only .yaml or .yml format supported"""
                        RootMLangTags.ZH -> """仅支持 .yaml 或 .yml 格式的配置文件"""
                        else -> null
                    }
                } ?: """仅支持 .yaml 或 .yml 格式的配置文件"""

        /** 仅支持 .yaml 或 .yml 格式的配置文件 */
        @Composable fun `YamlOnly`(vararg args: Any?) = FYTxtConfig.observe { `YamlOnly`.fmt(args) }
    }

    object `Progress` {
        init {
            RootMLangGroups
        }

        /** 下载中... */
        val `Downloading`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Downloading..."""
                        RootMLangTags.ZH -> """下载中..."""
                        else -> null
                    }
                } ?: """下载中..."""

        /** 下载中... */
        @Composable
        fun `Downloading`(vararg args: Any?) = FYTxtConfig.observe { `Downloading`.fmt(args) }
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
    }

    object `DeleteDialog` {
        init {
            RootMLangGroups
        }

        /** 删除配置 */
        val `Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Delete Profile"""
                        RootMLangTags.ZH -> """删除配置"""
                        else -> null
                    }
                } ?: """删除配置"""

        /** 删除配置 */
        @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

        /** 确定要删除「%s」吗？ */
        val `Message`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Are you sure you want to delete '%s'?"""
                        RootMLangTags.ZH -> """确定要删除「%s」吗？"""
                        else -> null
                    }
                } ?: """确定要删除「%s」吗？"""

        /** 确定要删除「%s」吗？ */
        @Composable fun `Message`(vararg args: Any?) = FYTxtConfig.observe { `Message`.fmt(args) }

        /** 删除 */
        val `Confirm`
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
        @Composable fun `Confirm`(vararg args: Any?) = FYTxtConfig.observe { `Confirm`.fmt(args) }
    }

    object `EditDialog` {
        init {
            RootMLangGroups
        }

        /** 编辑配置名称 */
        val `Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Edit Profile Name"""
                        RootMLangTags.ZH -> """编辑配置名称"""
                        else -> null
                    }
                } ?: """编辑配置名称"""

        /** 编辑配置名称 */
        @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }
    }

    object `LinkSettings` {
        init {
            RootMLangGroups
        }

        /** 链接设置 */
        val `Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Link Settings"""
                        RootMLangTags.ZH -> """链接设置"""
                        else -> null
                    }
                } ?: """链接设置"""

        /** 链接设置 */
        @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

        /** 打开方式 */
        val `OpenMode`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Open Mode"""
                        RootMLangTags.ZH -> """打开方式"""
                        else -> null
                    }
                } ?: """打开方式"""

        /** 打开方式 */
        @Composable fun `OpenMode`(vararg args: Any?) = FYTxtConfig.observe { `OpenMode`.fmt(args) }

        /** App 内打开 */
        val `OpenModeInApp`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """In App"""
                        RootMLangTags.ZH -> """App 内打开"""
                        else -> null
                    }
                } ?: """App 内打开"""

        /** App 内打开 */
        @Composable
        fun `OpenModeInApp`(vararg args: Any?) = FYTxtConfig.observe { `OpenModeInApp`.fmt(args) }

        /** 外部浏览器 */
        val `OpenModeExternal`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """External Browser"""
                        RootMLangTags.ZH -> """外部浏览器"""
                        else -> null
                    }
                } ?: """外部浏览器"""

        /** 外部浏览器 */
        @Composable
        fun `OpenModeExternal`(vararg args: Any?) =
            FYTxtConfig.observe { `OpenModeExternal`.fmt(args) }

        /** 默认链接 */
        val `DefaultLink`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Default Link"""
                        RootMLangTags.ZH -> """默认链接"""
                        else -> null
                    }
                } ?: """默认链接"""

        /** 默认链接 */
        @Composable
        fun `DefaultLink`(vararg args: Any?) = FYTxtConfig.observe { `DefaultLink`.fmt(args) }

        /** 点击左上角快捷按钮时打开的链接 */
        val `DefaultLinkSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Link opened when clicking top-left shortcut button"""
                        RootMLangTags.ZH -> """点击左上角快捷按钮时打开的链接"""
                        else -> null
                    }
                } ?: """点击左上角快捷按钮时打开的链接"""

        /** 点击左上角快捷按钮时打开的链接 */
        @Composable
        fun `DefaultLinkSummary`(vararg args: Any?) =
            FYTxtConfig.observe { `DefaultLinkSummary`.fmt(args) }

        /** 添加链接 */
        val `AddLink`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Add Link"""
                        RootMLangTags.ZH -> """添加链接"""
                        else -> null
                    }
                } ?: """添加链接"""

        /** 添加链接 */
        @Composable fun `AddLink`(vararg args: Any?) = FYTxtConfig.observe { `AddLink`.fmt(args) }

        /** 编辑链接 */
        val `EditLink`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Edit Link"""
                        RootMLangTags.ZH -> """编辑链接"""
                        else -> null
                    }
                } ?: """编辑链接"""

        /** 编辑链接 */
        @Composable fun `EditLink`(vararg args: Any?) = FYTxtConfig.observe { `EditLink`.fmt(args) }

        /** 名称 */
        val `Name`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Name"""
                        RootMLangTags.ZH -> """名称"""
                        else -> null
                    }
                } ?: """名称"""

        /** 名称 */
        @Composable fun `Name`(vararg args: Any?) = FYTxtConfig.observe { `Name`.fmt(args) }

        /** 链接 */
        val `Url`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """URL"""
                        RootMLangTags.ZH -> """链接"""
                        else -> null
                    }
                } ?: """链接"""

        /** 链接 */
        @Composable fun `Url`(vararg args: Any?) = FYTxtConfig.observe { `Url`.fmt(args) }

        /** 关闭 */
        val `Close`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Close"""
                        RootMLangTags.ZH -> """关闭"""
                        else -> null
                    }
                } ?: """关闭"""

        /** 关闭 */
        @Composable fun `Close`(vararg args: Any?) = FYTxtConfig.observe { `Close`.fmt(args) }

        object `Validation` {
            init {
                RootMLangGroups
            }

            /** 请输入名称 */
            val `EnterName`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Please enter name"""
                            RootMLangTags.ZH -> """请输入名称"""
                            else -> null
                        }
                    } ?: """请输入名称"""

            /** 请输入名称 */
            @Composable
            fun `EnterName`(vararg args: Any?) = FYTxtConfig.observe { `EnterName`.fmt(args) }

            /** 请输入链接 */
            val `EnterUrl`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Please enter URL"""
                            RootMLangTags.ZH -> """请输入链接"""
                            else -> null
                        }
                    } ?: """请输入链接"""

            /** 请输入链接 */
            @Composable
            fun `EnterUrl`(vararg args: Any?) = FYTxtConfig.observe { `EnterUrl`.fmt(args) }

            /** 请输入有效的链接 */
            val `InvalidUrl`
                get() =
                    FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                        it as RootMLangTags
                        when (it) {
                            RootMLangTags.EN -> """Please enter a valid URL"""
                            RootMLangTags.ZH -> """请输入有效的链接"""
                            else -> null
                        }
                    } ?: """请输入有效的链接"""

            /** 请输入有效的链接 */
            @Composable
            fun `InvalidUrl`(vararg args: Any?) = FYTxtConfig.observe { `InvalidUrl`.fmt(args) }
        }
    }

    object `ShareDialog` {
        init {
            RootMLangGroups
        }

        /** 分享配置文件 */
        val `ShareFile`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Share Config File"""
                        RootMLangTags.ZH -> """分享配置文件"""
                        else -> null
                    }
                } ?: """分享配置文件"""

        /** 分享配置文件 */
        @Composable
        fun `ShareFile`(vararg args: Any?) = FYTxtConfig.observe { `ShareFile`.fmt(args) }
    }

    object `Misc` {
        init {
            RootMLangGroups
        }

        /** 完成 */
        val `Complete`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Complete"""
                        RootMLangTags.ZH -> """完成"""
                        else -> null
                    }
                } ?: """完成"""

        /** 完成 */
        @Composable fun `Complete`(vararg args: Any?) = FYTxtConfig.observe { `Complete`.fmt(args) }

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
    }

    object `SettingsDialog` {
        init {
            RootMLangGroups
        }

        /** 订阅设置 */
        val `Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Subscription Settings"""
                        RootMLangTags.ZH -> """订阅设置"""
                        else -> null
                    }
                } ?: """订阅设置"""

        /** 订阅设置 */
        @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

        /** 更改订阅链接 */
        val `ChangeLink`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Change Subscription Link"""
                        RootMLangTags.ZH -> """更改订阅链接"""
                        else -> null
                    }
                } ?: """更改订阅链接"""

        /** 更改订阅链接 */
        @Composable
        fun `ChangeLink`(vararg args: Any?) = FYTxtConfig.observe { `ChangeLink`.fmt(args) }

        /** 预设覆写 */
        val `SystemPreset`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """System Preset Override"""
                        RootMLangTags.ZH -> """预设覆写"""
                        else -> null
                    }
                } ?: """预设覆写"""

        /** 预设覆写 */
        @Composable
        fun `SystemPreset`(vararg args: Any?) = FYTxtConfig.observe { `SystemPreset`.fmt(args) }

        /** 如果不知道什么是分流，推荐打开 */
        val `SystemPresetSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Enable the built-in default override preset if you want a usable routing baseline without configuring rules yourself"""
                        RootMLangTags.ZH -> """如果想先得到一套可用的默认分流基础、又不打算自己配置规则，可以直接开启"""
                        else -> null
                    }
                } ?: """如果想先得到一套可用的默认分流基础、又不打算自己配置规则，可以直接开启"""

        /** 如果想先得到一套可用的默认分流基础、又不打算自己配置规则，可以直接开启 */
        @Composable
        fun `SystemPresetSummary`(vararg args: Any?) =
            FYTxtConfig.observe { `SystemPresetSummary`.fmt(args) }

        /** 未设置说明 */
        val `NoDescription`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """No description set"""
                        RootMLangTags.ZH -> """未设置说明"""
                        else -> null
                    }
                } ?: """未设置说明"""

        /** 未设置说明 */
        @Composable
        fun `NoDescription`(vararg args: Any?) = FYTxtConfig.observe { `NoDescription`.fmt(args) }

        /** 本地配置编辑器 */
        val `LocalConfigEditorSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Open the raw YAML content of the current local config for direct editing"""
                        RootMLangTags.ZH -> """打开当前本地配置的原始 YAML 内容进行直接编辑"""
                        else -> null
                    }
                } ?: """打开当前本地配置的原始 YAML 内容进行直接编辑"""

        /** 打开当前本地配置的原始 YAML 内容进行直接编辑 */
        @Composable
        fun `LocalConfigEditorSummary`(vararg args: Any?) =
            FYTxtConfig.observe { `LocalConfigEditorSummary`.fmt(args) }

        /** 配置来源 */
        val `LocalSource`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Config Source"""
                        RootMLangTags.ZH -> """配置来源"""
                        else -> null
                    }
                } ?: """配置来源"""

        /** 配置来源 */
        @Composable
        fun `LocalSource`(vararg args: Any?) = FYTxtConfig.observe { `LocalSource`.fmt(args) }

        /** 空白配置 */
        val `LocalSourceBlank`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Built-in Basic Template"""
                        RootMLangTags.ZH -> """内置基础模板"""
                        else -> null
                    }
                } ?: """内置基础模板"""

        /** 内置基础模板 */
        @Composable
        fun `LocalSourceBlank`(vararg args: Any?) =
            FYTxtConfig.observe { `LocalSourceBlank`.fmt(args) }

        /** 导入文件 */
        val `LocalSourceImported`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Imported Local File"""
                        RootMLangTags.ZH -> """已导入本地文件"""
                        else -> null
                    }
                } ?: """已导入本地文件"""

        /** 已导入本地文件 */
        @Composable
        fun `LocalSourceImported`(vararg args: Any?) =
            FYTxtConfig.observe { `LocalSourceImported`.fmt(args) }

        /** 编辑配置 */
        val `EditProfile`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Edit Profile"""
                        RootMLangTags.ZH -> """编辑配置"""
                        else -> null
                    }
                } ?: """编辑配置"""

        /** 编辑配置 */
        @Composable
        fun `EditProfile`(vararg args: Any?) = FYTxtConfig.observe { `EditProfile`.fmt(args) }

        /** 打开配置 */
        val `OpenConfig`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Edit Text"""
                        RootMLangTags.ZH -> """编辑文本"""
                        else -> null
                    }
                } ?: """编辑文本"""

        /** 打开配置 */
        @Composable
        fun `OpenConfig`(vararg args: Any?) = FYTxtConfig.observe { `OpenConfig`.fmt(args) }

        /** 编辑设置 */
        val `EditSettings`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Profile Options"""
                        RootMLangTags.ZH -> """配置选项"""
                        else -> null
                    }
                } ?: """配置选项"""

        /** 编辑设置 */
        @Composable
        fun `EditSettings`(vararg args: Any?) = FYTxtConfig.observe { `EditSettings`.fmt(args) }

        /** 保存配置失败 */
        val `SaveFailed`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Failed to save profile"""
                        RootMLangTags.ZH -> """保存配置失败"""
                        else -> null
                    }
                } ?: """保存配置失败"""

        /** 保存配置失败 */
        @Composable
        fun `SaveFailed`(vararg args: Any?) = FYTxtConfig.observe { `SaveFailed`.fmt(args) }

        /** 规则复写 */
        val `RuleOverrides`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Rule Overrides"""
                        RootMLangTags.ZH -> """规则复写"""
                        else -> null
                    }
                } ?: """规则复写"""

        /** 规则复写 */
        @Composable
        fun `RuleOverrides`(vararg args: Any?) = FYTxtConfig.observe { `RuleOverrides`.fmt(args) }

        /** 越靠上优先级越高，新增项会插入最前 */
        val `RuleOverridesPriorityHint`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Overrides are applied from top to bottom.
Higher items take precedence, and new items are inserted at the top."""
                        RootMLangTags.ZH ->
                            """规则复写会按从上到下的顺序应用。
越靠上优先级越高，新项会插入最前。"""
                        else -> null
                    }
                }
                    ?: """规则复写会按从上到下的顺序应用。
越靠上优先级越高，新项会插入最前。"""

        /** 规则复写会按从上到下的顺序应用。 越靠上优先级越高，新项会插入最前。 */
        @Composable
        fun `RuleOverridesPriorityHint`(vararg args: Any?) =
            FYTxtConfig.observe { `RuleOverridesPriorityHint`.fmt(args) }
    }
}
