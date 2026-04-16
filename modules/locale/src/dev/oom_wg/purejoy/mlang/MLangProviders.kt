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

object MLangProviders {
    init {
        RootMLangGroups
    }

    /** 外部资源 */
    val `Title`
        get() =
            FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                it as RootMLangTags
                when (it) {
                    RootMLangTags.EN -> """External Resources"""
                    RootMLangTags.ZH -> """外部资源"""
                    else -> null
                }
            } ?: """外部资源"""

    /** 外部资源 */
    @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

    object `Action` {
        init {
            RootMLangGroups
        }

        /** 更新全部 */
        val `UpdateAll`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Update All"""
                        RootMLangTags.ZH -> """更新全部"""
                        else -> null
                    }
                } ?: """更新全部"""

        /** 更新全部 */
        @Composable
        fun `UpdateAll`(vararg args: Any?) = FYTxtConfig.observe { `UpdateAll`.fmt(args) }

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

        /** 上传 */
        val `Upload`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Upload"""
                        RootMLangTags.ZH -> """上传"""
                        else -> null
                    }
                } ?: """上传"""

        /** 上传 */
        @Composable fun `Upload`(vararg args: Any?) = FYTxtConfig.observe { `Upload`.fmt(args) }

        /** 操作 */
        val `Operation`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Operation"""
                        RootMLangTags.ZH -> """操作"""
                        else -> null
                    }
                } ?: """操作"""

        /** 操作 */
        @Composable
        fun `Operation`(vararg args: Any?) = FYTxtConfig.observe { `Operation`.fmt(args) }
    }

    object `Empty` {
        init {
            RootMLangGroups
        }

        /** 代理未启动 */
        val `NotRunning`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Proxy not running"""
                        RootMLangTags.ZH -> """代理未启动"""
                        else -> null
                    }
                } ?: """代理未启动"""

        /** 代理未启动 */
        @Composable
        fun `NotRunning`(vararg args: Any?) = FYTxtConfig.observe { `NotRunning`.fmt(args) }

        /** 请先启动代理服务以查看外部资源 */
        val `NotRunningHint`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Please start proxy service to view external resources"""
                        RootMLangTags.ZH -> """请先启动代理服务以查看外部资源"""
                        else -> null
                    }
                } ?: """请先启动代理服务以查看外部资源"""

        /** 请先启动代理服务以查看外部资源 */
        @Composable
        fun `NotRunningHint`(vararg args: Any?) = FYTxtConfig.observe { `NotRunningHint`.fmt(args) }

        /** 暂无外部资源 */
        val `NoProviders`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """No external resources"""
                        RootMLangTags.ZH -> """暂无外部资源"""
                        else -> null
                    }
                } ?: """暂无外部资源"""

        /** 暂无外部资源 */
        @Composable
        fun `NoProviders`(vararg args: Any?) = FYTxtConfig.observe { `NoProviders`.fmt(args) }

        /** 当前配置未包含外部资源 */
        val `NoProvidersHint`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Current profile doesn't contain external resources"""
                        RootMLangTags.ZH -> """当前配置未包含外部资源"""
                        else -> null
                    }
                } ?: """当前配置未包含外部资源"""

        /** 当前配置未包含外部资源 */
        @Composable
        fun `NoProvidersHint`(vararg args: Any?) =
            FYTxtConfig.observe { `NoProvidersHint`.fmt(args) }
    }

    object `Type` {
        init {
            RootMLangGroups
        }

        /** 代理提供者 (%d) */
        val `ProxyProviders`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Proxy Providers (%d)"""
                        RootMLangTags.ZH -> """代理提供者 (%d)"""
                        else -> null
                    }
                } ?: """代理提供者 (%d)"""

        /** 代理提供者 (%d) */
        @Composable
        fun `ProxyProviders`(vararg args: Any?) = FYTxtConfig.observe { `ProxyProviders`.fmt(args) }

        /** 规则提供者 (%d) */
        val `RuleProviders`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Rule Providers (%d)"""
                        RootMLangTags.ZH -> """规则提供者 (%d)"""
                        else -> null
                    }
                } ?: """规则提供者 (%d)"""

        /** 规则提供者 (%d) */
        @Composable
        fun `RuleProviders`(vararg args: Any?) = FYTxtConfig.observe { `RuleProviders`.fmt(args) }

        /** 覆写外部资源 (%d) */
        val `OverrideResources`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Override Remote Resources (%d)"""
                        RootMLangTags.ZH -> """覆写外部资源 (%d)"""
                        else -> null
                    }
                } ?: """覆写外部资源 (%d)"""

        /** 覆写外部资源 (%d) */
        @Composable
        fun `OverrideResources`(vararg args: Any?) =
            FYTxtConfig.observe { `OverrideResources`.fmt(args) }
    }

    object `Transport` {
        init {
            RootMLangGroups
        }

        /** HTTP */
        val `Http`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """HTTP"""
                        RootMLangTags.ZH -> """HTTP"""
                        else -> null
                    }
                } ?: """HTTP"""

        /** HTTP */
        @Composable fun `Http`(vararg args: Any?) = FYTxtConfig.observe { `Http`.fmt(args) }

        /** 本地文件 */
        val `File`
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
        @Composable fun `File`(vararg args: Any?) = FYTxtConfig.observe { `File`.fmt(args) }

        /** 内联 */
        val `Inline`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Inline"""
                        RootMLangTags.ZH -> """内联"""
                        else -> null
                    }
                } ?: """内联"""

        /** 内联 */
        @Composable fun `Inline`(vararg args: Any?) = FYTxtConfig.observe { `Inline`.fmt(args) }

        /** 兼容 */
        val `Compatible`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Compatible"""
                        RootMLangTags.ZH -> """兼容"""
                        else -> null
                    }
                } ?: """兼容"""

        /** 兼容 */
        @Composable
        fun `Compatible`(vararg args: Any?) = FYTxtConfig.observe { `Compatible`.fmt(args) }
    }

    object `Summary` {
        init {
            RootMLangGroups
        }

        /** 更新间隔 %d 秒 · 规则 %d 条 */
        val `OverrideIntervalAndCount`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Update every %d s · %d rules"""
                        RootMLangTags.ZH -> """更新间隔 %d 秒 · 规则 %d 条"""
                        else -> null
                    }
                } ?: """更新间隔 %d 秒 · 规则 %d 条"""

        /** 更新间隔 %d 秒 · 规则 %d 条 */
        @Composable
        fun `OverrideIntervalAndCount`(vararg args: Any?) =
            FYTxtConfig.observe { `OverrideIntervalAndCount`.fmt(args) }

        /** %d 项 */
        val `ItemCount`
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
        fun `ItemCount`(vararg args: Any?) = FYTxtConfig.observe { `ItemCount`.fmt(args) }
    }

    object `Message` {
        init {
            RootMLangGroups
        }

        /** 获取外部资源失败: %s */
        val `FetchFailed`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Failed to fetch resources: %s"""
                        RootMLangTags.ZH -> """获取外部资源失败: %s"""
                        else -> null
                    }
                } ?: """获取外部资源失败: %s"""

        /** 获取外部资源失败: %s */
        @Composable
        fun `FetchFailed`(vararg args: Any?) = FYTxtConfig.observe { `FetchFailed`.fmt(args) }

        /** %s 更新成功 */
        val `UpdateSuccess`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """%s updated successfully"""
                        RootMLangTags.ZH -> """%s 更新成功"""
                        else -> null
                    }
                } ?: """%s 更新成功"""

        /** %s 更新成功 */
        @Composable
        fun `UpdateSuccess`(vararg args: Any?) = FYTxtConfig.observe { `UpdateSuccess`.fmt(args) }

        /** 更新失败: %s */
        val `UpdateFailed`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Update failed: %s"""
                        RootMLangTags.ZH -> """更新失败: %s"""
                        else -> null
                    }
                } ?: """更新失败: %s"""

        /** 更新失败: %s */
        @Composable
        fun `UpdateFailed`(vararg args: Any?) = FYTxtConfig.observe { `UpdateFailed`.fmt(args) }

        /** 以下资源更新失败: %s */
        val `UpdateFailedResources`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Failed resources: %s"""
                        RootMLangTags.ZH -> """以下资源更新失败: %s"""
                        else -> null
                    }
                } ?: """以下资源更新失败: %s"""

        /** 以下资源更新失败: %s */
        @Composable
        fun `UpdateFailedResources`(vararg args: Any?) =
            FYTxtConfig.observe { `UpdateFailedResources`.fmt(args) }

        /** 全部更新完成 */
        val `AllUpdated`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """All updated"""
                        RootMLangTags.ZH -> """全部更新完成"""
                        else -> null
                    }
                } ?: """全部更新完成"""

        /** 全部更新完成 */
        @Composable
        fun `AllUpdated`(vararg args: Any?) = FYTxtConfig.observe { `AllUpdated`.fmt(args) }

        /** %s 上传成功 */
        val `UploadSuccess`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """%s uploaded successfully"""
                        RootMLangTags.ZH -> """%s 上传成功"""
                        else -> null
                    }
                } ?: """%s 上传成功"""

        /** %s 上传成功 */
        @Composable
        fun `UploadSuccess`(vararg args: Any?) = FYTxtConfig.observe { `UploadSuccess`.fmt(args) }

        /** 上传失败: %s */
        val `UploadFailed`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Upload failed: %s"""
                        RootMLangTags.ZH -> """上传失败: %s"""
                        else -> null
                    }
                } ?: """上传失败: %s"""

        /** 上传失败: %s */
        @Composable
        fun `UploadFailed`(vararg args: Any?) = FYTxtConfig.observe { `UploadFailed`.fmt(args) }

        /** 无法读取文件: %s */
        val `ReadFileFailed`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Cannot read file: %s"""
                        RootMLangTags.ZH -> """无法读取文件: %s"""
                        else -> null
                    }
                } ?: """无法读取文件: %s"""

        /** 无法读取文件: %s */
        @Composable
        fun `ReadFileFailed`(vararg args: Any?) = FYTxtConfig.observe { `ReadFileFailed`.fmt(args) }

        /** 文件超过 %dMB 限制 */
        val `UploadSizeExceeded`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """File exceeds %dMB limit"""
                        RootMLangTags.ZH -> """文件超过 %dMB 限制"""
                        else -> null
                    }
                } ?: """文件超过 %dMB 限制"""

        /** 文件超过 %dMB 限制 */
        @Composable
        fun `UploadSizeExceeded`(vararg args: Any?) =
            FYTxtConfig.observe { `UploadSizeExceeded`.fmt(args) }

        /** 未知错误 */
        val `UnknownError`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Unknown error"""
                        RootMLangTags.ZH -> """未知错误"""
                        else -> null
                    }
                } ?: """未知错误"""

        /** 未知错误 */
        @Composable
        fun `UnknownError`(vararg args: Any?) = FYTxtConfig.observe { `UnknownError`.fmt(args) }
    }
}
