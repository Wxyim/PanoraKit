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

object MLangProfilesVM {
    init {
        RootMLangGroups
    }

    object `Message` {
        init {
            RootMLangGroups
        }

        /** 配置已添加：%s */
        val `ProfileAdded`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Profile added: %s"""
                        RootMLangTags.ZH -> """配置已添加：%s"""
                        else -> null
                    }
                } ?: """配置已添加：%s"""

        /** 配置已添加：%s */
        @Composable
        fun `ProfileAdded`(vararg args: Any?) = FYTxtConfig.observe { `ProfileAdded`.fmt(args) }

        /** 添加配置失败：%s */
        val `AddFailed`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Add profile failed: %s"""
                        RootMLangTags.ZH -> """添加配置失败：%s"""
                        else -> null
                    }
                } ?: """添加配置失败：%s"""

        /** 添加配置失败：%s */
        @Composable
        fun `AddFailed`(vararg args: Any?) = FYTxtConfig.observe { `AddFailed`.fmt(args) }

        /** 配置已删除 */
        val `ProfileDeleted`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Profile deleted"""
                        RootMLangTags.ZH -> """配置已删除"""
                        else -> null
                    }
                } ?: """配置已删除"""

        /** 配置已删除 */
        @Composable
        fun `ProfileDeleted`(vararg args: Any?) = FYTxtConfig.observe { `ProfileDeleted`.fmt(args) }

        /** 删除配置失败：%s */
        val `DeleteFailed`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Delete profile failed: %s"""
                        RootMLangTags.ZH -> """删除配置失败：%s"""
                        else -> null
                    }
                } ?: """删除配置失败：%s"""

        /** 删除配置失败：%s */
        @Composable
        fun `DeleteFailed`(vararg args: Any?) = FYTxtConfig.observe { `DeleteFailed`.fmt(args) }

        /** 配置已更新：%s */
        val `ProfileUpdated`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Profile updated: %s"""
                        RootMLangTags.ZH -> """配置已更新：%s"""
                        else -> null
                    }
                } ?: """配置已更新：%s"""

        /** 配置已更新：%s */
        @Composable
        fun `ProfileUpdated`(vararg args: Any?) = FYTxtConfig.observe { `ProfileUpdated`.fmt(args) }

        /** 更新配置失败：%s */
        val `UpdateFailed`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Update profile failed: %s"""
                        RootMLangTags.ZH -> """更新配置失败：%s"""
                        else -> null
                    }
                } ?: """更新配置失败：%s"""

        /** 更新配置失败：%s */
        @Composable
        fun `UpdateFailed`(vararg args: Any?) = FYTxtConfig.observe { `UpdateFailed`.fmt(args) }

        /** 切换状态失败：%s */
        val `ToggleFailed`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Toggle state failed: %s"""
                        RootMLangTags.ZH -> """切换状态失败：%s"""
                        else -> null
                    }
                } ?: """切换状态失败：%s"""

        /** 切换状态失败：%s */
        @Composable
        fun `ToggleFailed`(vararg args: Any?) = FYTxtConfig.observe { `ToggleFailed`.fmt(args) }

        /** 配置已导入：%s */
        val `ProfileImported`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Profile imported: %s"""
                        RootMLangTags.ZH -> """配置已导入：%s"""
                        else -> null
                    }
                } ?: """配置已导入：%s"""

        /** 配置已导入：%s */
        @Composable
        fun `ProfileImported`(vararg args: Any?) =
            FYTxtConfig.observe { `ProfileImported`.fmt(args) }

        /** 导入配置失败：%s */
        val `ImportFailed`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Import profile failed: %s"""
                        RootMLangTags.ZH -> """导入配置失败：%s"""
                        else -> null
                    }
                } ?: """导入配置失败：%s"""

        /** 导入配置失败：%s */
        @Composable
        fun `ImportFailed`(vararg args: Any?) = FYTxtConfig.observe { `ImportFailed`.fmt(args) }
    }

    object `Progress` {
        init {
            RootMLangGroups
        }

        /** 准备下载... */
        val `Preparing`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Preparing download..."""
                        RootMLangTags.ZH -> """准备下载..."""
                        else -> null
                    }
                } ?: """准备下载..."""

        /** 准备下载... */
        @Composable
        fun `Preparing`(vararg args: Any?) = FYTxtConfig.observe { `Preparing`.fmt(args) }

        /** 准备导入文件... */
        val `ImportPreparing`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Preparing to import file..."""
                        RootMLangTags.ZH -> """准备导入文件..."""
                        else -> null
                    }
                } ?: """准备导入文件..."""

        /** 准备导入文件... */
        @Composable
        fun `ImportPreparing`(vararg args: Any?) =
            FYTxtConfig.observe { `ImportPreparing`.fmt(args) }

        /** 正在验证配置... */
        val `Verifying`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Verifying configuration..."""
                        RootMLangTags.ZH -> """正在验证配置..."""
                        else -> null
                    }
                } ?: """正在验证配置..."""

        /** 正在验证配置... */
        @Composable
        fun `Verifying`(vararg args: Any?) = FYTxtConfig.observe { `Verifying`.fmt(args) }

        /** 导入完成 */
        val `ImportComplete`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Import complete"""
                        RootMLangTags.ZH -> """导入完成"""
                        else -> null
                    }
                } ?: """导入完成"""

        /** 导入完成 */
        @Composable
        fun `ImportComplete`(vararg args: Any?) = FYTxtConfig.observe { `ImportComplete`.fmt(args) }
    }

    object `Error` {
        init {
            RootMLangGroups
        }

        /** 配置不存在 */
        val `ProfileNotExist`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Profile does not exist"""
                        RootMLangTags.ZH -> """配置不存在"""
                        else -> null
                    }
                } ?: """配置不存在"""

        /** 配置不存在 */
        @Composable
        fun `ProfileNotExist`(vararg args: Any?) =
            FYTxtConfig.observe { `ProfileNotExist`.fmt(args) }

        /** 未知错误 */
        val `Unknown`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Unknown"""
                        RootMLangTags.ZH -> """未知错误"""
                        else -> null
                    }
                } ?: """未知错误"""

        /** 未知错误 */
        @Composable fun `Unknown`(vararg args: Any?) = FYTxtConfig.observe { `Unknown`.fmt(args) }
    }
}
