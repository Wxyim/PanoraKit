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

@file:Suppress("PackageDirectoryMismatch")

package dev.oom_wg.purejoy.mlang

import dev.oom_wg.purejoy.fyl.fytxt.FYTxtConfig

private typealias StatusMLangTags = MLang.`MLangTags`

object MLangStatus {
    init {
        MLang.`MLangGroups`
    }

    val NoActiveIssues: String
        get() = pick(en = "No active issues", zh = "暂无活动问题")

    val AttentionItems: String
        get() = pick(en = "%d items need attention", zh = "%d 项需要关注")

    val SourceReadyItems: String
        get() = pick(en = "%d sources ready", zh = "%d 个资源可用")

    val SourceStaleItems: String
        get() = pick(en = "%d sources need refresh", zh = "%d 个资源需要刷新")

    val SourcePendingItems: String
        get() = pick(en = "%d sources waiting for first sync", zh = "%d 个资源等待首次同步")

    object Common {
        val NotAvailable: String
            get() = pick(en = "Not available", zh = "暂无")

        val Ready: String
            get() = pick(en = "Ready", zh = "就绪")

        val Waiting: String
            get() = pick(en = "Waiting", zh = "等待中")

        val Attention: String
            get() = pick(en = "Attention", zh = "需关注")

        val Failed: String
            get() = pick(en = "Failed", zh = "失败")

        val Applied: String
            get() = pick(en = "Applied", zh = "已应用")
    }

    object Retryability {
        val Retryable: String
            get() = pick(en = "Retryable", zh = "可重试")

        val RetryableAfterAction: String
            get() = pick(en = "Retryable after action", zh = "处理后可重试")

        val NonRetryable: String
            get() = pick(en = "Non-retryable", zh = "不可重试")
    }

    object Phase {
        val Init: String
            get() = pick(en = "Init", zh = "初始化")

        val Preparing: String
            get() = pick(en = "Preparing", zh = "准备")

        val Connecting: String
            get() = pick(en = "Connecting", zh = "连接")

        val Running: String
            get() = pick(en = "Running", zh = "运行")

        val Reloading: String
            get() = pick(en = "Reloading", zh = "重载")

        val Stopping: String
            get() = pick(en = "Stopping", zh = "停止")

        val Saving: String
            get() = pick(en = "Saving", zh = "保存")

        val Importing: String
            get() = pick(en = "Importing", zh = "导入")

        val Exporting: String
            get() = pick(en = "Exporting", zh = "导出")

        val Compiling: String
            get() = pick(en = "Compiling", zh = "编译")

        val Validating: String
            get() = pick(en = "Validating", zh = "校验")
    }

    object Impact {
        val None: String
            get() = pick(en = "No impact", zh = "无影响")

        val Degraded: String
            get() = pick(en = "Degraded", zh = "能力降级")

        val FeatureUnavailable: String
            get() = pick(en = "Feature unavailable", zh = "功能不可用")

        val ServiceDown: String
            get() = pick(en = "Service down", zh = "服务不可用")

        val DataLoss: String
            get() = pick(en = "Data loss risk", zh = "存在数据丢失风险")
    }

    object Log {
        val LiveLogs: String
            get() = pick(en = "Live logs", zh = "实时日志")

        val Archives: String
            get() = pick(en = "Archives", zh = "归档")

        val StartupArchives: String
            get() = pick(en = "Startup archives", zh = "启动归档")

        val Recording: String
            get() = pick(en = "Recording", zh = "记录中")

        val NotRecording: String
            get() = pick(en = "Not recording", zh = "未记录")
    }

    object Meta {
        val SectionTitle: String
            get() = pick(en = "Tools and entry points", zh = "工具与入口")

        val StateSummaryTitle: String
            get() = pick(en = "Current state", zh = "当前状态")

        val RuntimeTitle: String
            get() = pick(en = "Runtime", zh = "运行状态")

        val EffectiveRules: String
            get() = pick(en = "Effective rules", zh = "生效规则")

        val Sources: String
            get() = pick(en = "Remote resources", zh = "远程资源")

        val RuntimeIdle: String
            get() = pick(en = "Runtime is not active", zh = "运行时未启动")

        val RuntimeStable: String
            get() = pick(en = "Runtime is ready", zh = "运行时已就绪")

        val RuntimeAttention: String
            get() = pick(en = "Runtime needs attention", zh = "运行时需要关注")

        val RuntimeStarting: String
            get() = pick(en = "Runtime is preparing controller and payload", zh = "运行时正在准备控制器与载荷")

        val RuntimeRunningDegraded: String
            get() =
                pick(
                    en = "Runtime is running but some payload channels are still missing",
                    zh = "运行时已启动，但部分载荷通道仍未完成",
                )

        val RuntimeStopping: String
            get() = pick(en = "Runtime is shutting down", zh = "运行时正在停止")

        val IdleShort: String
            get() = pick(en = "Idle", zh = "空闲")

        val StartingShort: String
            get() = pick(en = "Starting", zh = "启动中")

        val StoppingShort: String
            get() = pick(en = "Stopping", zh = "停止中")

        val FailedShort: String
            get() = pick(en = "Failed", zh = "失败")

        val EffectiveRulesRuntimeSource: String
            get() = pick(en = "Runtime compiled config", zh = "运行时编译配置")

        val EffectiveRulesProfileSource: String
            get() = pick(en = "Active profile config", zh = "活动配置文件")
    }

    private fun pick(en: String, zh: String): String {
        return FYTxtConfig.activeTags.value.firstNotNullOfOrNull { activeTag ->
            when (activeTag as StatusMLangTags) {
                StatusMLangTags.EN -> en
                StatusMLangTags.ZH -> zh
            }
        } ?: zh
    }
}
