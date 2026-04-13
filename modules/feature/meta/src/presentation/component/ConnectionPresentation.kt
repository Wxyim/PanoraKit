package com.github.yumelira.yumebox.feature.meta.presentation.component

import com.github.yumelira.yumebox.core.model.ConnectionInfo
import com.github.yumelira.yumebox.domain.model.ExplanationChain
import com.github.yumelira.yumebox.domain.model.ExplanationStep
import com.github.yumelira.yumebox.domain.model.HealthCheckItem
import com.github.yumelira.yumebox.domain.model.HealthCheckSeverity
import com.github.yumelira.yumebox.domain.model.HealthReport
import dev.oom_wg.purejoy.mlang.MLang
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

data class ConnectionDisplayAddress(
    val title: String,
    val sourceAddress: String,
    val destinationAddress: String,
)

data class ConnectionDiagnostics(
    val healthReport: HealthReport,
    val explanationChain: ExplanationChain,
)

fun ConnectionInfo.toDisplayAddress(): ConnectionDisplayAddress {
    val host = metadata["host"]?.jsonPrimitive?.contentOrNull?.trim().orEmpty()
    val sourceIp = metadata["sourceIP"]?.jsonPrimitive?.contentOrNull?.trim().orEmpty()
    val sourcePort = metadata["sourcePort"]?.jsonPrimitive?.contentOrNull?.trim().orEmpty()
    val destinationIp = metadata["destinationIP"]?.jsonPrimitive?.contentOrNull?.trim().orEmpty()
    val destinationPort =
        metadata["destinationPort"]?.jsonPrimitive?.contentOrNull?.trim().orEmpty()

    val sourceAddress = joinHostPort(sourceIp, sourcePort)
    val destinationAddress =
        if (host.isNotBlank()) {
            joinHostPort(host, destinationPort)
        } else {
            joinHostPort(destinationIp, destinationPort)
        }

    return ConnectionDisplayAddress(
        title = destinationAddress.ifBlank { sourceAddress },
        sourceAddress = sourceAddress,
        destinationAddress = destinationAddress,
    )
}

fun ConnectionInfo.toDiagnostics(
    network: String,
    sourceAppName: String,
    sourcePackageName: String?,
    processName: String,
    displayAddress: ConnectionDisplayAddress,
): ConnectionDiagnostics {
    val healthItems =
        buildList {
            add(
                HealthCheckItem(
                    checkId = "$id-protocol",
                    label = network.uppercase(),
                    severity = HealthCheckSeverity.Ok,
                    detail = displayAddress.destinationAddress.ifBlank { displayAddress.title },
                    category = "connection",
                )
            )

            if (sourceAppName.isNotBlank()) {
                add(
                    HealthCheckItem(
                        checkId = "$id-source-app",
                        label = sourceAppName,
                        severity = HealthCheckSeverity.Info,
                        detail = sourcePackageName?.takeIf { it.isNotBlank() } ?: processName,
                        category = "identity",
                    )
                )
            }

            add(
                if (rule.isNotBlank()) {
                    HealthCheckItem(
                        checkId = "$id-rule",
                        label = rule,
                        severity = HealthCheckSeverity.Ok,
                        detail = rulePayload.takeIf { it.isNotBlank() },
                        category = "routing",
                    )
                } else {
                    HealthCheckItem(
                        checkId = "$id-rule-missing",
                        label = MLang.Connection.Detail.Rule,
                        severity = HealthCheckSeverity.Warning,
                        detail = displayAddress.title.ifBlank { displayAddress.destinationAddress },
                        category = "routing",
                    )
                }
            )

            add(
                if (chains.isNotEmpty()) {
                    HealthCheckItem(
                        checkId = "$id-chain",
                        label = chains.last(),
                        severity = HealthCheckSeverity.Ok,
                        detail = chains.dropLast(1).joinToString(" → ").takeIf { it.isNotBlank() },
                        category = "outbound",
                    )
                } else {
                    HealthCheckItem(
                        checkId = "$id-chain-missing",
                        label = displayAddress.destinationAddress.ifBlank { displayAddress.title },
                        severity = HealthCheckSeverity.Warning,
                        detail = network.uppercase(),
                        category = "outbound",
                    )
                }
            )
        }

    val explanationSteps =
        buildList {
            if (sourceAppName.isNotBlank()) {
                add(
                    ExplanationStep(
                        stage = MLang.Connection.Detail.SourceApp,
                        label = sourceAppName,
                        detail = sourcePackageName?.takeIf { it.isNotBlank() } ?: processName,
                    )
                )
            }
            add(
                ExplanationStep(
                    stage = MLang.Connection.Detail.Protocol,
                    label = network.uppercase(),
                )
            )
            if (displayAddress.sourceAddress.isNotBlank()) {
                add(
                    ExplanationStep(
                        stage = MLang.Connection.Detail.SourceAddress,
                        label = displayAddress.sourceAddress,
                    )
                )
            }
            if (rule.isNotBlank()) {
                add(
                    ExplanationStep(
                        stage = MLang.Connection.Detail.Rule,
                        label = rule,
                        detail = rulePayload.takeIf { it.isNotBlank() },
                    )
                )
            }
            add(
                ExplanationStep(
                    stage = MLang.Connection.Detail.DestinationAddress,
                    label = displayAddress.destinationAddress.ifBlank { displayAddress.title },
                    detail = chains.joinToString(" → ").takeIf { it.isNotBlank() },
                    matched = chains.isNotEmpty() || rule.isNotBlank(),
                )
            )
        }

    return ConnectionDiagnostics(
        healthReport =
            HealthReport(
                reportId = "connection:$id",
                generatedAtMillis = System.currentTimeMillis(),
                overallSeverity = healthItems.toOverallSeverity(),
                items = healthItems,
                runtimePhase = network.uppercase(),
            ),
        explanationChain =
            ExplanationChain(
                chainId = "connection:$id",
                steps = explanationSteps,
                conclusion = displayAddress.title.ifBlank { displayAddress.destinationAddress },
                isSuccess = healthItems.none { it.severity == HealthCheckSeverity.Warning },
            ),
    )
}

private fun joinHostPort(host: String, port: String): String {
    if (host.isBlank()) return ""
    return if (port.isBlank()) host else "$host:$port"
}

private fun List<HealthCheckItem>.toOverallSeverity(): HealthCheckSeverity {
    return when {
        any { it.severity == HealthCheckSeverity.Critical } -> HealthCheckSeverity.Critical
        any { it.severity == HealthCheckSeverity.Error } -> HealthCheckSeverity.Error
        any { it.severity == HealthCheckSeverity.Warning } -> HealthCheckSeverity.Warning
        any { it.severity == HealthCheckSeverity.Info } -> HealthCheckSeverity.Info
        else -> HealthCheckSeverity.Ok
    }
}
