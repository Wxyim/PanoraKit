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

object MLangLog {
    init {
        RootMLangGroups
    }

    /** 日志 */
    val `Title`
        get() =
            FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                it as RootMLangTags
                when (it) {
                    RootMLangTags.EN -> """Logs"""
                    RootMLangTags.ZH -> """日志"""
                    else -> null
                }
            } ?: """日志"""

    /** 日志 */
    @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

    object `Action` {
        init {
            RootMLangGroups
        }

        /** 停止记录 */
        val `StopRecording`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Stop Recording"""
                        RootMLangTags.ZH -> """停止记录"""
                        else -> null
                    }
                } ?: """停止记录"""

        /** 停止记录 */
        @Composable
        fun `StopRecording`(vararg args: Any?) = FYTxtConfig.observe { `StopRecording`.fmt(args) }

        /** 开始记录 */
        val `StartRecording`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Start Recording"""
                        RootMLangTags.ZH -> """开始记录"""
                        else -> null
                    }
                } ?: """开始记录"""

        /** 开始记录 */
        @Composable
        fun `StartRecording`(vararg args: Any?) = FYTxtConfig.observe { `StartRecording`.fmt(args) }

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

        /** 清理日志 */
        val `Cleanup`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Cleanup Logs"""
                        RootMLangTags.ZH -> """清理日志"""
                        else -> null
                    }
                } ?: """清理日志"""

        /** 清理日志 */
        @Composable fun `Cleanup`(vararg args: Any?) = FYTxtConfig.observe { `Cleanup`.fmt(args) }

        /** 日志清理完成 */
        val `CleanupDone`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Log cleanup completed"""
                        RootMLangTags.ZH -> """日志清理完成"""
                        else -> null
                    }
                } ?: """日志清理完成"""

        /** 日志清理完成 */
        @Composable
        fun `CleanupDone`(vararg args: Any?) = FYTxtConfig.observe { `CleanupDone`.fmt(args) }

        /** 导出诊断包 */
        val `ExportDebugBundle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Export Debug Bundle"""
                        RootMLangTags.ZH -> """导出诊断包"""
                        else -> null
                    }
                } ?: """导出诊断包"""

        /** 导出诊断包 */
        @Composable
        fun `ExportDebugBundle`(vararg args: Any?) =
            FYTxtConfig.observe { `ExportDebugBundle`.fmt(args) }

        /** 包含运行状态与脱敏日志，可能仍含服务器地址等上下文；仅分享给可信接收方。 */
        val `ExportDebugBundleWarning`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Includes runtime state and sanitized logs. May still contain server/context details; share only with trusted recipients."""
                        RootMLangTags.ZH -> """包含运行状态与脱敏日志，可能仍含服务器地址等上下文；仅分享给可信接收方。"""
                        else -> null
                    }
                } ?: """包含运行状态与脱敏日志，可能仍含服务器地址等上下文；仅分享给可信接收方。"""

        /** 包含运行状态与脱敏日志，可能仍含服务器地址等上下文；仅分享给可信接收方。 */
        @Composable
        fun `ExportDebugBundleWarning`(vararg args: Any?) =
            FYTxtConfig.observe { `ExportDebugBundleWarning`.fmt(args) }

        /** 导出 */
        val `Export`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Export"""
                        RootMLangTags.ZH -> """导出"""
                        else -> null
                    }
                } ?: """导出"""

        /** 导出 */
        @Composable fun `Export`(vararg args: Any?) = FYTxtConfig.observe { `Export`.fmt(args) }

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

        /** 导出成功 */
        val `ExportDone`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Export completed"""
                        RootMLangTags.ZH -> """导出成功"""
                        else -> null
                    }
                } ?: """导出成功"""

        /** 导出成功 */
        @Composable
        fun `ExportDone`(vararg args: Any?) = FYTxtConfig.observe { `ExportDone`.fmt(args) }
    }

    object `Empty` {
        init {
            RootMLangGroups
        }

        /** 暂无日志记录 */
        val `NoLogs`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """No log records"""
                        RootMLangTags.ZH -> """暂无日志记录"""
                        else -> null
                    }
                } ?: """暂无日志记录"""

        /** 暂无日志记录 */
        @Composable fun `NoLogs`(vararg args: Any?) = FYTxtConfig.observe { `NoLogs`.fmt(args) }

        /** 点击右下角按钮开始记录日志 */
        val `StartRecordingHint`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Click the bottom-right button to start recording logs"""
                        RootMLangTags.ZH -> """点击右下角按钮开始记录日志"""
                        else -> null
                    }
                } ?: """点击右下角按钮开始记录日志"""

        /** 点击右下角按钮开始记录日志 */
        @Composable
        fun `StartRecordingHint`(vararg args: Any?) =
            FYTxtConfig.observe { `StartRecordingHint`.fmt(args) }

        /** 从主页启动内核后将自动开始记录 */
        val `AutoRecordHint`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Recording starts automatically when the kernel is started from Home"""
                        RootMLangTags.ZH -> """从主页启动内核后将自动开始记录"""
                        else -> null
                    }
                } ?: """从主页启动内核后将自动开始记录"""

        /** 从主页启动内核后将自动开始记录 */
        @Composable
        fun `AutoRecordHint`(vararg args: Any?) = FYTxtConfig.observe { `AutoRecordHint`.fmt(args) }
    }

    object `Detail` {
        init {
            RootMLangGroups
        }

        /** 等待日志... */
        val `WaitingLog`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Waiting for logs..."""
                        RootMLangTags.ZH -> """等待日志..."""
                        else -> null
                    }
                } ?: """等待日志..."""

        /** 等待日志... */
        @Composable
        fun `WaitingLog`(vararg args: Any?) = FYTxtConfig.observe { `WaitingLog`.fmt(args) }

        /** 日志将在产生时显示 */
        val `WillShowWhenGenerated`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Logs will appear when generated"""
                        RootMLangTags.ZH -> """日志将在产生时显示"""
                        else -> null
                    }
                } ?: """日志将在产生时显示"""

        /** 日志将在产生时显示 */
        @Composable
        fun `WillShowWhenGenerated`(vararg args: Any?) =
            FYTxtConfig.observe { `WillShowWhenGenerated`.fmt(args) }
    }

    object `History` {
        init {
            RootMLangGroups
        }

        /** 运行日志归档 */
        val `Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Runtime Log Archives"""
                        RootMLangTags.ZH -> """运行日志归档"""
                        else -> null
                    }
                } ?: """运行日志归档"""

        /** 运行日志归档 */
        @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

        /** 实时运行日志（当前会话） */
        val `LiveSection`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Live Runtime Logs (Current Session)"""
                        RootMLangTags.ZH -> """实时运行日志（当前会话）"""
                        else -> null
                    }
                } ?: """实时运行日志（当前会话）"""

        /** 实时运行日志（当前会话） */
        @Composable
        fun `LiveSection`(vararg args: Any?) = FYTxtConfig.observe { `LiveSection`.fmt(args) }

        /** 记录中 */
        val `Recording`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Recording"""
                        RootMLangTags.ZH -> """记录中"""
                        else -> null
                    }
                } ?: """记录中"""

        /** 记录中 */
        @Composable
        fun `Recording`(vararg args: Any?) = FYTxtConfig.observe { `Recording`.fmt(args) }

        /** %s · %s */
        val `ItemSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """%s · %s"""
                        RootMLangTags.ZH -> """%s · %s"""
                        else -> null
                    }
                } ?: """%s · %s"""

        /** %s · %s */
        @Composable
        fun `ItemSummary`(vararg args: Any?) = FYTxtConfig.observe { `ItemSummary`.fmt(args) }
    }

    object `Startup` {
        init {
            RootMLangGroups
        }

        /** 启动诊断日志 */
        val `Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Startup Diagnostics"""
                        RootMLangTags.ZH -> """启动诊断日志"""
                        else -> null
                    }
                } ?: """启动诊断日志"""

        /** 启动诊断日志 */
        @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

        /** 启动阶段诊断 */
        val `LiveSection`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Startup Phase Diagnostics"""
                        RootMLangTags.ZH -> """启动阶段诊断"""
                        else -> null
                    }
                } ?: """启动阶段诊断"""

        /** 启动阶段诊断 */
        @Composable
        fun `LiveSection`(vararg args: Any?) = FYTxtConfig.observe { `LiveSection`.fmt(args) }

        /** %s · %s */
        val `ItemSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """%s · %s"""
                        RootMLangTags.ZH -> """%s · %s"""
                        else -> null
                    }
                } ?: """%s · %s"""

        /** %s · %s */
        @Composable
        fun `ItemSummary`(vararg args: Any?) = FYTxtConfig.observe { `ItemSummary`.fmt(args) }
    }
}
