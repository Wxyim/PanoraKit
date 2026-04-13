@file:Suppress("PackageDirectoryMismatch")

package dev.oom_wg.purejoy.mlang

import dev.oom_wg.purejoy.fyl.fytxt.FYTxtConfig

private typealias DiagnosticMLangTags = MLang.`MLangTags`

object DiagnosticLang {
    init {
        MLang.`MLangGroups`
    }

    val NoActiveIssues: String
        get() = pick(en = "No active issues", zh = "暂无活动问题")

    val AttentionItems: String
        get() = pick(en = "%d items need attention", zh = "%d 项需要关注")

    val RecentFailureItems: String
        get() = pick(en = "%d recent failures", zh = "最近 %d 条失败")

    val SourceReadyItems: String
        get() = pick(en = "%d sources ready", zh = "%d 个资源可用")

    val SourceStaleItems: String
        get() = pick(en = "%d sources need refresh", zh = "%d 个资源需要刷新")

    val SourcePendingItems: String
        get() = pick(en = "%d sources waiting for first sync", zh = "%d 个资源等待首次同步")

    val TraceTitle: String
        get() = pick(en = "Decision Trace", zh = "决策链路")

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

    object DetailPages {
        val Title: String
            get() = pick(en = "Diagnostic details", zh = "诊断详情")

        val AdvancedTitle: String
            get() = pick(en = "Advanced diagnostics", zh = "高级诊断")

        object Common {
            val NotAvailable: String
                get() = pick(en = "Not available", zh = "暂无")

            val NoActiveProfile: String
                get() = pick(en = "No active profile", zh = "当前没有活动配置")

            val UnnamedProfile: String
                get() = pick(en = "Unnamed profile", zh = "未命名配置")

            val Ready: String
                get() = pick(en = "Ready", zh = "就绪")

            val Waiting: String
                get() = pick(en = "Waiting", zh = "等待中")

            val Partial: String
                get() = pick(en = "Partial", zh = "部分完成")

            val Attention: String
                get() = pick(en = "Attention", zh = "需关注")

            val Current: String
                get() = pick(en = "Current", zh = "当前")

            val Empty: String
                get() = pick(en = "Empty", zh = "为空")

            val SourcePath: String
                get() = pick(en = "Source path", zh = "来源路径")

            val ConfigFingerprint: String
                get() = pick(en = "Config fingerprint", zh = "配置指纹")

            val LatestCapture: String
                get() = pick(en = "Latest capture", zh = "最近捕获")

            val TotalItems: String
                get() = pick(en = "%d items", zh = "%d 项")

            val StateSummary: String
                get() = pick(en = "State summary", zh = "状态摘要")

            val RepairLoop: String
                get() = pick(en = "Repair loop", zh = "修复闭环")

            val Signals: String
                get() = pick(en = "Signals", zh = "关键指标")

            val Identity: String
                get() = pick(en = "Object identity", zh = "对象身份")

            val Evidence: String
                get() = pick(en = "Evidence", zh = "证据")

            val CurrentView: String
                get() = pick(en = "Current view", zh = "当前视图")
        }

        object Console {
            val Headline: String
                get() = pick(en = "Diagnostic cockpit", zh = "诊断控制台")

            val RuntimeStable: String
                get() = pick(en = "Runtime path is stable", zh = "运行链路稳定")

            val RuntimeAttention: String
                get() = pick(en = "Runtime path needs attention", zh = "运行链路需要关注")

            val RuntimeIdle: String
                get() = pick(en = "Runtime is not active", zh = "运行时未启动")

            val RepairAndTriage: String
                get() = pick(en = "Repair and triage", zh = "修复与定位")

            val ExplainAndValidate: String
                get() = pick(en = "Explain and validate", zh = "解释与校验")

            val AdvancedAndUtilities: String
                get() = pick(en = "Advanced evidence and utilities", zh = "高级证据与工具")

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

            val BeginnerTier: String
                get() = pick(en = "L1 repair", zh = "L1 修复")

            val IntermediateTier: String
                get() = pick(en = "L2 explain", zh = "L2 解释")

            val AdvancedTier: String
                get() = pick(en = "L3 raw", zh = "L3 原始")
        }

        object RuntimeHealth {
            val Title: String
                get() = pick(en = "Runtime Health", zh = "运行时健康详情")

            val Summary: String
                get() =
                    pick(
                        en = "Inspect lifecycle, readiness, failures, and source freshness",
                        zh = "查看运行时生命周期、就绪状态、失败链路与资源新鲜度",
                    )

            val Headline: String
                get() = pick(en = "Runtime health", zh = "运行时健康")

            val IdleShort: String
                get() = pick(en = "Idle", zh = "空闲")

            val StartingShort: String
                get() = pick(en = "Starting", zh = "启动中")

            val StoppingShort: String
                get() = pick(en = "Stopping", zh = "停止中")

            val FailedShort: String
                get() = pick(en = "Failed", zh = "失败")

            val ActiveProfile: String
                get() = pick(en = "Active profile", zh = "活动配置")

            val Lifecycle: String
                get() = pick(en = "Lifecycle", zh = "生命周期")

            val Payload: String
                get() = pick(en = "Payload readiness", zh = "载荷就绪")

            val Transport: String
                get() = pick(en = "Transport path", zh = "传输通路")

            val Config: String
                get() = pick(en = "Config pipeline", zh = "配置管线")

            val Logs: String
                get() = pick(en = "Log pipeline", zh = "日志通路")

            val Sources: String
                get() = pick(en = "Remote resources", zh = "远程资源")

            val Failures: String
                get() = pick(en = "Recent failures", zh = "最近失败")

            val Owner: String
                get() = pick(en = "Runtime owner", zh = "运行时宿主")

            val Mode: String
                get() = pick(en = "Target mode", zh = "目标模式")

            val Generation: String
                get() = pick(en = "Generation", zh = "代次")

            val PayloadReadyFormat: String
                get() = pick(en = "%d/%d channels ready", zh = "%d/%d 个通道已就绪")

            val RunningHealthy: String
                get() =
                    pick(
                        en = "Runtime accepted traffic and payload is complete",
                        zh = "运行时已承载流量且载荷完整",
                    )

            val RunningDegraded: String
                get() =
                    pick(
                        en = "Runtime is running but some payload channels are still missing",
                        zh = "运行时已启动，但部分载荷通道仍未完成",
                    )

            val Starting: String
                get() =
                    pick(en = "Runtime is preparing controller and payload", zh = "运行时正在准备控制器与载荷")

            val Stopping: String
                get() = pick(en = "Runtime is shutting down", zh = "运行时正在停止")

            val Idle: String
                get() = pick(en = "Runtime is idle", zh = "运行时空闲")

            val Failed: String
                get() = pick(en = "Runtime failed to reach a healthy state", zh = "运行时未能进入健康状态")

            val ConfigReady: String
                get() = pick(en = "Effective config is available for inspection", zh = "可检查当前生效配置")

            val ConfigMissing: String
                get() = pick(en = "Effective config is not available yet", zh = "当前还拿不到生效配置")

            val LogReady: String
                get() = pick(en = "Runtime log channel is ready", zh = "运行时日志通路已就绪")

            val LogWaiting: String
                get() = pick(en = "Runtime log channel is still warming up", zh = "运行时日志通路仍在预热")

            val SourcesHealthy: String
                get() = pick(en = "All %d remote resources are ready", zh = "全部 %d 个远程资源已就绪")

            val SourcesAttention: String
                get() = pick(en = "%d stale, %d pending", zh = "%d 个过期，%d 个待同步")

            val SourcesEmpty: String
                get() = pick(en = "No remote resources configured", zh = "当前没有远程资源")

            val FailuresClear: String
                get() = pick(en = "No recent structured failures", zh = "最近没有结构化失败")

            val FailuresAttention: String
                get() = pick(en = "%d recent failures recorded", zh = "记录到最近 %d 条失败")

            val OwnerNone: String
                get() = pick(en = "None", zh = "无")

            val OwnerLocalTun: String
                get() = pick(en = "Local TUN", zh = "本地 TUN")

            val OwnerLocalHttp: String
                get() = pick(en = "Local HTTP", zh = "本地 HTTP")

            val OwnerRootTun: String
                get() = pick(en = "Root TUN", zh = "Root TUN")

            val ModeTun: String
                get() = pick(en = "TUN", zh = "TUN")

            val ModeRootTun: String
                get() = pick(en = "Root TUN", zh = "Root TUN")

            val ModeHttp: String
                get() = pick(en = "HTTP", zh = "HTTP")
        }

        object RuleSetInspector {
            val Title: String
                get() = pick(en = "Rule Set Inspector", zh = "规则集检查器")

            val Summary: String
                get() =
                    pick(
                        en = "Inspect effective rules, providers, and matcher targets",
                        zh = "检查当前生效规则、规则提供方与匹配目标分布",
                    )

            val Headline: String
                get() = pick(en = "Effective rule sets", zh = "生效规则集")

            val SourceRuntime: String
                get() = pick(en = "Runtime compiled config", zh = "运行时编译配置")

            val SourceProfile: String
                get() = pick(en = "Active profile config", zh = "活动配置文件")

            val SourceUnavailable: String
                get() = pick(en = "No effective config source", zh = "暂无可用的生效配置来源")

            val TotalRuleSets: String
                get() = pick(en = "Rule sets", zh = "规则集")

            val TotalMatchers: String
                get() = pick(en = "Matchers", zh = "匹配器")

            val OverlayRules: String
                get() = pick(en = "Overlay rules", zh = "叠加规则")

            val ProviderCount: String
                get() = pick(en = "Providers", zh = "提供方")

            val Matchers: String
                get() = pick(en = "Matchers", zh = "匹配器")

            val CompoundMatchers: String
                get() = pick(en = "Compound", zh = "复合规则")

            val Targets: String
                get() = pick(en = "Targets", zh = "目标分布")

            val ProviderUrl: String
                get() = pick(en = "Provider URL", zh = "提供方 URL")

            val ProviderBehavior: String
                get() = pick(en = "Provider behavior", zh = "提供方行为")

            val RemainingMatchers: String
                get() = pick(en = "+%d more matchers", zh = "+%d 条更多匹配器")

            val NoRuleSets: String
                get() = pick(en = "No rule sets available", zh = "当前没有可检查的规则集")

            val Inline: String
                get() = pick(en = "Inline rules", zh = "内联规则")

            val Override: String
                get() = pick(en = "Override rules", zh = "覆盖规则")

            val Provider: String
                get() = pick(en = "Provider-backed", zh = "提供方规则")

            val SubRule: String
                get() = pick(en = "Sub-rules", zh = "子规则")

            val Overview: String
                get() = pick(en = "%d sets · %d matchers", zh = "%d 组规则集 · %d 条匹配器")
        }

        object ExplanationChain {
            val Title: String
                get() = pick(en = "Explanation Chain", zh = "解释链详情")

            val Summary: String
                get() =
                    pick(
                        en =
                            "Inspect why the current profile, source, and runtime state led to the current outcome",
                        zh = "检查活动配置、来源与运行时状态为何导向当前结果",
                    )

            val Headline: String
                get() = pick(en = "Decision explanation", zh = "决策解释")

            val StageCount: String
                get() = pick(en = "Stages", zh = "阶段数")

            val BlockedStages: String
                get() = pick(en = "Blocked", zh = "阻塞阶段")

            val RecentEvents: String
                get() = pick(en = "Events", zh = "相关事件")

            val ChainId: String
                get() = pick(en = "Chain ID", zh = "链路 ID")

            val Conclusion: String
                get() = pick(en = "Conclusion", zh = "结论")

            val Profile: String
                get() = pick(en = "Profile", zh = "配置")

            val ConfigSource: String
                get() = pick(en = "Config source", zh = "配置来源")

            val Resources: String
                get() = pick(en = "Source registry", zh = "来源注册表")

            val RuntimeState: String
                get() = pick(en = "Runtime state", zh = "运行时状态")

            val RootCause: String
                get() = pick(en = "Root cause", zh = "根因")

            val Input: String
                get() = pick(en = "Input", zh = "输入")

            val Output: String
                get() = pick(en = "Output", zh = "输出")

            val Matched: String
                get() = pick(en = "Matched", zh = "已匹配")

            val Blocked: String
                get() = pick(en = "Blocked", zh = "已阻塞")

            val HealthyConclusion: String
                get() = pick(en = "Runtime path is internally consistent", zh = "运行路径内部一致")

            val AttentionConclusion: String
                get() = pick(en = "At least one stage needs attention", zh = "至少有一个阶段需要关注")

            val PreparingConclusion: String
                get() = pick(en = "The chain is still preparing inputs", zh = "链路仍在准备输入")

            val NoChain: String
                get() = pick(en = "No explanation steps available", zh = "当前没有可展示的解释步骤")

            val Overview: String
                get() = pick(en = "%d stages · %d blocked", zh = "%d 个阶段 · %d 个阻塞")
        }

        object RawTrace {
            val Title: String
                get() = pick(en = "Raw Trace", zh = "原始 trace 详情")

            val Summary: String
                get() =
                    pick(
                        en =
                            "Inspect structured events, runtime snapshot, and raw runtime log buffers",
                        zh = "检查结构化事件、运行时快照与底层运行时日志缓冲",
                    )

            val Headline: String
                get() = pick(en = "Raw trace detail", zh = "原始链路")

            val TraceId: String
                get() = pick(en = "Trace ID", zh = "trace ID")

            val EventCount: String
                get() = pick(en = "Events", zh = "事件数")

            val FailureCount: String
                get() = pick(en = "Failures", zh = "失败数")

            val RawSections: String
                get() = pick(en = "Sections", zh = "原始分段")

            val LatestEvent: String
                get() = pick(en = "Latest event", zh = "最近事件")

            val RuntimePhase: String
                get() = pick(en = "Runtime phase", zh = "运行阶段")

            val RuntimeSnapshot: String
                get() = pick(en = "Runtime snapshot", zh = "运行时快照")

            val StructuredEvents: String
                get() = pick(en = "Structured events", zh = "结构化事件")

            val RuntimeBuffer: String
                get() = pick(en = "Runtime log buffer", zh = "运行时日志缓冲")

            val RawPayload: String
                get() = pick(en = "Raw payload", zh = "原始载荷")

            val NoEvents: String
                get() = pick(en = "No trace events captured yet", zh = "当前还没有捕获到 trace 事件")

            val SectionOverview: String
                get() = pick(en = "%d sections · %d lines", zh = "%d 个分段 · %d 行")

            val DnsResolve: String
                get() = pick(en = "DNS", zh = "DNS")

            val RuleMatch: String
                get() = pick(en = "Rule", zh = "规则")

            val PolicySelect: String
                get() = pick(en = "Policy", zh = "策略")

            val OutboundConnect: String
                get() = pick(en = "Outbound", zh = "出站")

            val Transport: String
                get() = pick(en = "Transport", zh = "传输")

            val Complete: String
                get() = pick(en = "Complete", zh = "完成")

            val Failed: String
                get() = pick(en = "Failed", zh = "失败")
        }

        object SnapshotHistory {
            val Title: String
                get() = pick(en = "Snapshot History", zh = "快照历史页")

            val Summary: String
                get() =
                    pick(
                        en = "Track effective config fingerprints and recent runtime captures",
                        zh = "追踪生效配置指纹与最近运行时捕获记录",
                    )

            val Headline: String
                get() = pick(en = "Workspace snapshots", zh = "工作区快照")

            val TotalSnapshots: String
                get() = pick(en = "Snapshots", zh = "快照数")

            val ProfilesCovered: String
                get() = pick(en = "Profiles", zh = "涉及配置")

            val CurrentFingerprint: String
                get() = pick(en = "Current fingerprint", zh = "当前指纹")

            val NoSnapshots: String
                get() = pick(en = "No snapshots captured yet", zh = "当前还没有捕获到快照")

            val CaptureLabel: String
                get() = pick(en = "%s · %s", zh = "%s · %s")

            val SnapshotSummary: String
                get() = pick(en = "%s · %s · %s", zh = "%s · %s · %s")

            val AutoSave: String
                get() = pick(en = "Auto save", zh = "自动保存")

            val ManualSave: String
                get() = pick(en = "Manual save", zh = "手动保存")

            val PreChange: String
                get() = pick(en = "Pre-change", zh = "变更前")

            val PostImport: String
                get() = pick(en = "Post-import", zh = "导入后")

            val Rollback: String
                get() = pick(en = "Rollback", zh = "回滚")

            val Overview: String
                get() = pick(en = "%d snapshots stored", zh = "已存储 %d 个快照")
        }

        object SourceRegistry {
            val Title: String
                get() = pick(en = "Source Registry Overview", zh = "来源注册表总览")

            val Summary: String
                get() =
                    pick(
                        en =
                            "Inspect effective config files, providers, and remote override sources in one registry",
                        zh = "统一检查生效配置文件、提供方与远程覆盖来源",
                    )

            val Headline: String
                get() = pick(en = "Source registry", zh = "来源注册表")

            val TotalSources: String
                get() = pick(en = "Sources", zh = "来源数")

            val RemoteSources: String
                get() = pick(en = "Remote", zh = "远程")

            val LocalSources: String
                get() = pick(en = "Local", zh = "本地")

            val EffectiveSources: String
                get() = pick(en = "Effective", zh = "生效来源")

            val RegistryRole: String
                get() = pick(en = "Role", zh = "角色")

            val SourceType: String
                get() = pick(en = "Type", zh = "类型")

            val SyncState: String
                get() = pick(en = "Sync", zh = "同步")

            val Owner: String
                get() = pick(en = "Owner", zh = "归属")

            val Interval: String
                get() = pick(en = "Interval", zh = "间隔")

            val ItemCount: String
                get() = pick(en = "Items", zh = "条目数")

            val RuntimeConfig: String
                get() = pick(en = "Runtime config", zh = "运行时配置")

            val ProfileConfig: String
                get() = pick(en = "Profile config", zh = "配置文件")

            val RemoteOverride: String
                get() = pick(en = "Remote override", zh = "远程覆盖")

            val RuleProvider: String
                get() = pick(en = "Rule provider", zh = "规则提供方")

            val ProxyProvider: String
                get() = pick(en = "Proxy provider", zh = "代理提供方")

            val Effective: String
                get() = pick(en = "Effective", zh = "当前生效")

            val NoSources: String
                get() = pick(en = "No sources registered", zh = "当前没有注册来源")

            val RegistryOverview: String
                get() = pick(en = "%d total · %d remote", zh = "共 %d 个 · 远程 %d 个")

            val StateReady: String
                get() = pick(en = "Ready", zh = "已就绪")

            val StateWaiting: String
                get() = pick(en = "Waiting", zh = "等待中")

            val StateSyncing: String
                get() = pick(en = "Syncing", zh = "同步中")

            val StateFailed: String
                get() = pick(en = "Failed", zh = "失败")

            val StateStale: String
                get() = pick(en = "Stale", zh = "已过期")

            val TypeRemoteUrl: String
                get() = pick(en = "Remote URL", zh = "远程 URL")

            val TypeLocalFile: String
                get() = pick(en = "Local file", zh = "本地文件")

            val TypeInlineContent: String
                get() = pick(en = "Inline content", zh = "内联内容")

            val TypeRuleProvider: String
                get() = pick(en = "Rule provider", zh = "规则提供方")

            val TypeProxyProvider: String
                get() = pick(en = "Proxy provider", zh = "代理提供方")
        }

        object Remediation {
            val Title: String
                get() = pick(en = "Repair loop", zh = "修复闭环")

            val HealthySummary: String
                get() =
                    pick(
                        en =
                            "State is stable. Re-run diagnostics or inspect adjacent pages when needed.",
                        zh = "当前状态稳定；如需确认，可重新抓取诊断或查看相邻页面。",
                    )

            val AttentionSummary: String
                get() =
                    pick(
                        en =
                            "Resolve the highest-priority runtime or source issue first, then re-check the chain.",
                        zh = "优先处理最高优先级的运行时或来源问题，再回看链路是否收敛。",
                    )

            val Execute: String
                get() = pick(en = "Apply", zh = "执行")

            val Inspect: String
                get() = pick(en = "Inspect", zh = "检查")

            val Export: String
                get() = pick(en = "Export", zh = "导出")

            val Success: String
                get() = pick(en = "Applied", zh = "已应用")

            val Info: String
                get() = pick(en = "Info", zh = "提示")

            val Warning: String
                get() = pick(en = "Attention", zh = "需关注")

            val Pending: String
                get() = pick(en = "Waiting", zh = "等待中")

            val Failed: String
                get() = pick(en = "Failed", zh = "失败")

            val InProgress: String
                get() = pick(en = "Applying...", zh = "处理中...")

            val RefreshPage: String
                get() = pick(en = "Refresh diagnostics", zh = "刷新诊断")

            val RefreshPageSummary: String
                get() =
                    pick(
                        en = "Capture a new diagnostic snapshot for this page.",
                        zh = "重新抓取当前页面依赖的诊断快照。",
                    )

            val StartRuntime: String
                get() = pick(en = "Start runtime", zh = "启动运行时")

            val StartRuntimeSummary: String
                get() =
                    pick(
                        en = "Start the active profile with the current runtime mode.",
                        zh = "按当前运行模式启动活动配置。",
                    )

            val ReloadRuntime: String
                get() = pick(en = "Reload current profile", zh = "重载当前配置")

            val ReloadRuntimeSummary: String
                get() =
                    pick(
                        en = "Keep the current mode and reload the active profile into runtime.",
                        zh = "保留当前模式，并将活动配置重新加载到运行时。",
                    )

            val RestartRuntime: String
                get() = pick(en = "Restart runtime", zh = "重启运行时")

            val RestartRuntimeSummary: String
                get() =
                    pick(
                        en = "Rebuild the runtime pipeline and re-apply the current config.",
                        zh = "完整重建运行时链路并重新应用当前配置。",
                    )

            val RestartRuntimeConfirm: String
                get() =
                    pick(
                        en =
                            "This will interrupt the current proxy session and rebuild the runtime immediately.",
                        zh = "这会中断当前代理会话，并立即重建运行时。",
                    )

            val RefreshSources: String
                get() = pick(en = "Refresh sources", zh = "刷新来源")

            val RefreshSourcesSummary: String
                get() =
                    pick(
                        en =
                            "Refresh remote providers and remote overrides, then re-apply active overrides when needed.",
                        zh = "刷新远程 provider 与远程覆盖来源，并在需要时回灌活动覆盖。",
                    )

            val OpenRuntimeHealth: String
                get() = pick(en = "Open runtime health", zh = "查看运行时健康")

            val OpenRuntimeHealthSummary: String
                get() =
                    pick(
                        en = "Return to lifecycle and readiness health checks.",
                        zh = "返回查看生命周期与就绪性健康项。",
                    )

            val OpenRuleSetInspector: String
                get() = pick(en = "Open rule set inspector", zh = "查看规则集检查器")

            val OpenRuleSetInspectorSummary: String
                get() =
                    pick(
                        en =
                            "Inspect effective rules and matcher distribution for the current config.",
                        zh = "查看当前配置的生效规则与匹配分布。",
                    )

            val OpenExplanationChain: String
                get() = pick(en = "Open explanation chain", zh = "查看解释链")

            val OpenExplanationChainSummary: String
                get() =
                    pick(
                        en = "Review which stage blocked or degraded the current path.",
                        zh = "回看是哪一个阶段阻塞或降级了当前链路。",
                    )

            val OpenRawTrace: String
                get() = pick(en = "Open raw trace", zh = "查看原始 trace")

            val OpenRawTraceSummary: String
                get() =
                    pick(
                        en = "Inspect structured events and runtime buffers for this path.",
                        zh = "检查这条链路对应的结构化事件与运行时缓冲。",
                    )

            val OpenSourceRegistry: String
                get() = pick(en = "Open source registry", zh = "查看来源注册表")

            val OpenSourceRegistrySummary: String
                get() =
                    pick(
                        en = "Inspect effective sources, freshness, and ownership in one registry.",
                        zh = "在一个注册表里查看生效来源、新鲜度和归属。",
                    )

            val OpenProviders: String
                get() = pick(en = "Open providers", zh = "查看 providers")

            val OpenProvidersSummary: String
                get() =
                    pick(
                        en =
                            "Inspect provider and remote override update status in the providers page.",
                        zh = "到 providers 页面检查 provider 与远程覆盖的更新状态。",
                    )

            val OpenLogs: String
                get() = pick(en = "Open logs", zh = "查看日志")

            val OpenLogsSummary: String
                get() =
                    pick(
                        en = "Inspect recent logs around this diagnostic event.",
                        zh = "查看围绕这次诊断事件的最近日志。",
                    )

            val CopyRawTrace: String
                get() = pick(en = "Copy raw trace", zh = "复制原始 trace")

            val CopyRawTraceSummary: String
                get() =
                    pick(
                        en = "Copy the current raw trace payload to the clipboard.",
                        zh = "把当前原始 trace 载荷复制到剪贴板。",
                    )

            val ResultStarted: String
                get() =
                    pick(
                        en = "Runtime started and the current config was applied.",
                        zh = "运行时已启动，并已应用当前配置。",
                    )

            val ResultReloaded: String
                get() =
                    pick(
                        en = "Active profile reloaded into the running runtime.",
                        zh = "活动配置已重新加载到运行中的运行时。",
                    )

            val ResultRestarted: String
                get() =
                    pick(
                        en = "Runtime restarted and the current config was rebuilt.",
                        zh = "运行时已重启，并已重建当前配置。",
                    )

            val ResultApplied: String
                get() = pick(en = "The requested action was applied.", zh = "请求的动作已应用。")

            val ResultDeferred: String
                get() =
                    pick(
                        en =
                            "Runtime is not in a state where this can apply immediately. The action is deferred.",
                        zh = "当前运行时无法立即应用该动作，已转为待生效状态。",
                    )

            val ResultPermissionPending: String
                get() =
                    pick(
                        en = "VPN permission is required before the action can continue.",
                        zh = "继续执行前需要先授予 VPN 权限。",
                    )

            val ResultActionFailed: String
                get() =
                    pick(
                        en =
                            "The action did not complete. Check the current diagnostics or global error feedback.",
                        zh = "动作未完成，请查看当前诊断或全局错误反馈。",
                    )

            val ResultActionUnsupported: String
                get() =
                    pick(
                        en = "This action is not available in the current context.",
                        zh = "当前上下文下该动作不可用。",
                    )

            val ResultSourcesRefreshed: String
                get() = pick(en = "%d sources refreshed.", zh = "已刷新 %d 个来源。")

            val ResultSourcesPartial: String
                get() =
                    pick(
                        en = "%d sources refreshed, %d still failed.",
                        zh = "已刷新 %d 个来源，仍有 %d 个失败。",
                    )

            val ResultSourcesDeferred: String
                get() =
                    pick(
                        en =
                            "%d remote sources refreshed. Runtime-bound providers will continue after runtime starts.",
                        zh = "已刷新 %d 个远程来源；依赖运行时的 provider 会在运行时启动后继续处理。",
                    )

            val ResultSourcesEmpty: String
                get() =
                    pick(
                        en = "There are no refreshable sources in the current context.",
                        zh = "当前上下文下没有可刷新的来源。",
                    )

            val ResultCopied: String
                get() = pick(en = "Copied the current raw trace payload.", zh = "已复制当前原始 trace 载荷。")

            val ResultRefreshed: String
                get() = pick(en = "Requested a fresh diagnostic capture.", zh = "已请求重新抓取诊断快照。")

            val StartRuntimeFailure: String
                get() = pick(en = "Failed to start runtime", zh = "启动运行时失败")

            val ReloadRuntimeFailure: String
                get() = pick(en = "Failed to reload current profile", zh = "重载当前配置失败")

            val RestartRuntimeFailure: String
                get() = pick(en = "Failed to restart runtime", zh = "重启运行时失败")
        }
    }

    private fun pick(en: String, zh: String): String {
        return FYTxtConfig.activeTags.value.firstNotNullOfOrNull { activeTag ->
            when (activeTag as DiagnosticMLangTags) {
                DiagnosticMLangTags.EN -> en
                DiagnosticMLangTags.ZH -> zh
            }
        } ?: zh
    }
}
