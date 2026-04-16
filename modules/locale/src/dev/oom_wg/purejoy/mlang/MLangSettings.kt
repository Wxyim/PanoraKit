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

object MLangSettings {
    init {
        RootMLangGroups
    }

    /** 设置 */
    val `Title`
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
    @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

    object `Section` {
        init {
            RootMLangGroups
        }

        /** 界面设置 */
        val `UiSettings`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """UI Settings"""
                        RootMLangTags.ZH -> """界面设置"""
                        else -> null
                    }
                } ?: """界面设置"""

        /** 界面设置 */
        @Composable
        fun `UiSettings`(vararg args: Any?) = FYTxtConfig.observe { `UiSettings`.fmt(args) }

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

        /** 高级 */
        val `Advanced`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Advanced"""
                        RootMLangTags.ZH -> """高级"""
                        else -> null
                    }
                } ?: """高级"""

        /** 高级 */
        @Composable fun `Advanced`(vararg args: Any?) = FYTxtConfig.observe { `Advanced`.fmt(args) }
    }

    object `UiSettings` {
        init {
            RootMLangGroups
        }

        /** 应用 */
        val `App`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """App"""
                        RootMLangTags.ZH -> """应用"""
                        else -> null
                    }
                } ?: """应用"""

        /** 应用 */
        @Composable fun `App`(vararg args: Any?) = FYTxtConfig.observe { `App`.fmt(args) }

        /** 外观 · 语言 · 主题 */
        val `AppSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Appearance · Language · Theme"""
                        RootMLangTags.ZH -> """外观 · 语言 · 主题"""
                        else -> null
                    }
                } ?: """外观 · 语言 · 主题"""

        /** 外观 · 语言 · 主题 */
        @Composable
        fun `AppSummary`(vararg args: Any?) = FYTxtConfig.observe { `AppSummary`.fmt(args) }

        /** 网络 */
        val `Network`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Network"""
                        RootMLangTags.ZH -> """网络"""
                        else -> null
                    }
                } ?: """网络"""

        /** 网络 */
        @Composable fun `Network`(vararg args: Any?) = FYTxtConfig.observe { `Network`.fmt(args) }

        /** DNS · 端口 · 入站 */
        val `NetworkSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """DNS · Port · Inbound"""
                        RootMLangTags.ZH -> """DNS · 端口 · 入站"""
                        else -> null
                    }
                } ?: """DNS · 端口 · 入站"""

        /** DNS · 端口 · 入站 */
        @Composable
        fun `NetworkSummary`(vararg args: Any?) = FYTxtConfig.observe { `NetworkSummary`.fmt(args) }

        /** 覆写 */
        val `Override`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Override"""
                        RootMLangTags.ZH -> """覆写"""
                        else -> null
                    }
                } ?: """覆写"""

        /** 覆写 */
        @Composable fun `Override`(vararg args: Any?) = FYTxtConfig.observe { `Override`.fmt(args) }

        /** 规则覆写 */
        val `OverrideSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Rule Override"""
                        RootMLangTags.ZH -> """规则覆写"""
                        else -> null
                    }
                } ?: """规则覆写"""

        /** 规则覆写 */
        @Composable
        fun `OverrideSummary`(vararg args: Any?) =
            FYTxtConfig.observe { `OverrideSummary`.fmt(args) }

        /** Meta 功能 */
        val `MetaFeatures`
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
        @Composable
        fun `MetaFeatures`(vararg args: Any?) = FYTxtConfig.observe { `MetaFeatures`.fmt(args) }

        /** Meta 扩展 */
        val `MetaFeaturesSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Meta Extensions"""
                        RootMLangTags.ZH -> """Meta 扩展"""
                        else -> null
                    }
                } ?: """Meta 扩展"""

        /** Meta 扩展 */
        @Composable
        fun `MetaFeaturesSummary`(vararg args: Any?) =
            FYTxtConfig.observe { `MetaFeaturesSummary`.fmt(args) }
    }

    object `More` {
        init {
            RootMLangGroups
        }

        /** 实验室 */
        val `Lab`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Lab"""
                        RootMLangTags.ZH -> """实验室"""
                        else -> null
                    }
                } ?: """实验室"""

        /** 实验室 */
        @Composable fun `Lab`(vararg args: Any?) = FYTxtConfig.observe { `Lab`.fmt(args) }

        /** SubStore · 实验功能 */
        val `LabSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """SubStore · Experiments"""
                        RootMLangTags.ZH -> """SubStore · 实验功能"""
                        else -> null
                    }
                } ?: """SubStore · 实验功能"""

        /** SubStore · 实验功能 */
        @Composable
        fun `LabSummary`(vararg args: Any?) = FYTxtConfig.observe { `LabSummary`.fmt(args) }

        /** 流量统计 */
        val `TrafficStatistics`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Traffic Statistics"""
                        RootMLangTags.ZH -> """流量统计"""
                        else -> null
                    }
                } ?: """流量统计"""

        /** 流量统计 */
        @Composable
        fun `TrafficStatistics`(vararg args: Any?) =
            FYTxtConfig.observe { `TrafficStatistics`.fmt(args) }

        /** 流量概览与最近请求 */
        val `TrafficStatisticsSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Traffic overview and recent requests"""
                        RootMLangTags.ZH -> """流量概览与最近请求"""
                        else -> null
                    }
                } ?: """流量概览与最近请求"""

        /** 流量概览与最近请求 */
        @Composable
        fun `TrafficStatisticsSummary`(vararg args: Any?) =
            FYTxtConfig.observe { `TrafficStatisticsSummary`.fmt(args) }

        /** 日志与诊断 */
        val `Logs`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Logs & Diagnostics"""
                        RootMLangTags.ZH -> """日志与诊断"""
                        else -> null
                    }
                } ?: """日志与诊断"""

        /** 日志与诊断 */
        @Composable fun `Logs`(vararg args: Any?) = FYTxtConfig.observe { `Logs`.fmt(args) }

        /** 运行日志、启动诊断与故障排查 */
        val `LogsSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Runtime logs, startup diagnostics & troubleshooting"""
                        RootMLangTags.ZH -> """运行日志、启动诊断与故障排查"""
                        else -> null
                    }
                } ?: """运行日志、启动诊断与故障排查"""

        /** 运行日志、启动诊断与故障排查 */
        @Composable
        fun `LogsSummary`(vararg args: Any?) = FYTxtConfig.observe { `LogsSummary`.fmt(args) }

        /** 关于 */
        val `About`
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
        @Composable fun `About`(vararg args: Any?) = FYTxtConfig.observe { `About`.fmt(args) }

        /** 版本与许可 */
        val `AboutSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Version & License"""
                        RootMLangTags.ZH -> """版本与许可"""
                        else -> null
                    }
                } ?: """版本与许可"""

        /** 版本与许可 */
        @Composable
        fun `AboutSummary`(vararg args: Any?) = FYTxtConfig.observe { `AboutSummary`.fmt(args) }
    }

    object `Error` {
        init {
            RootMLangGroups
        }

        /** 无法打开 WebView：%s */
        val `WebviewFailed`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Unable to open WebView: %s"""
                        RootMLangTags.ZH -> """无法打开 WebView：%s"""
                        else -> null
                    }
                } ?: """无法打开 WebView：%s"""

        /** 无法打开 WebView：%s */
        @Composable
        fun `WebviewFailed`(vararg args: Any?) = FYTxtConfig.observe { `WebviewFailed`.fmt(args) }
    }
}
