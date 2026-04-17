/*
 * This file is part of MonadBox.
 *
 * MonadBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
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
    "unused",
)

package dev.oom_wg.purejoy.mlang

import com.github.nomadboxlab.monadbox.core.locale.LocaleBootstrap
import com.github.nomadboxlab.monadbox.core.locale.R

object MLangStatus {
    val `NoActiveIssues`: String
        get() = LocaleBootstrap.getString(R.string.status_no_active_issues)

    val `AttentionItems`: String
        get() = LocaleBootstrap.getString(R.string.status_attention_items)

    val `SourceReadyItems`: String
        get() = LocaleBootstrap.getString(R.string.status_source_ready_items)

    val `SourceStaleItems`: String
        get() = LocaleBootstrap.getString(R.string.status_source_stale_items)

    val `SourcePendingItems`: String
        get() = LocaleBootstrap.getString(R.string.status_source_pending_items)

    object `Common` {
        val `NotAvailable`: String
            get() = LocaleBootstrap.getString(R.string.status_common_not_available)

        val `Ready`: String
            get() = LocaleBootstrap.getString(R.string.status_common_ready)

        val `Waiting`: String
            get() = LocaleBootstrap.getString(R.string.status_common_waiting)

        val `Attention`: String
            get() = LocaleBootstrap.getString(R.string.status_common_attention)

        val `Failed`: String
            get() = LocaleBootstrap.getString(R.string.status_common_failed)

        val `Applied`: String
            get() = LocaleBootstrap.getString(R.string.status_common_applied)
    }

    object `Retryability` {
        val `Retryable`: String
            get() = LocaleBootstrap.getString(R.string.status_retryability_retryable)

        val `RetryableAfterAction`: String
            get() = LocaleBootstrap.getString(R.string.status_retryability_retryable_after_action)

        val `NonRetryable`: String
            get() = LocaleBootstrap.getString(R.string.status_retryability_non_retryable)
    }

    object `Phase` {
        val `Init`: String
            get() = LocaleBootstrap.getString(R.string.status_phase_init)

        val `Preparing`: String
            get() = LocaleBootstrap.getString(R.string.status_phase_preparing)

        val `Connecting`: String
            get() = LocaleBootstrap.getString(R.string.status_phase_connecting)

        val `Running`: String
            get() = LocaleBootstrap.getString(R.string.status_phase_running)

        val `Reloading`: String
            get() = LocaleBootstrap.getString(R.string.status_phase_reloading)

        val `Stopping`: String
            get() = LocaleBootstrap.getString(R.string.status_phase_stopping)

        val `Saving`: String
            get() = LocaleBootstrap.getString(R.string.status_phase_saving)

        val `Importing`: String
            get() = LocaleBootstrap.getString(R.string.status_phase_importing)

        val `Exporting`: String
            get() = LocaleBootstrap.getString(R.string.status_phase_exporting)

        val `Compiling`: String
            get() = LocaleBootstrap.getString(R.string.status_phase_compiling)

        val `Validating`: String
            get() = LocaleBootstrap.getString(R.string.status_phase_validating)
    }

    object `Impact` {
        val `None`: String
            get() = LocaleBootstrap.getString(R.string.status_impact_none)

        val `Degraded`: String
            get() = LocaleBootstrap.getString(R.string.status_impact_degraded)

        val `FeatureUnavailable`: String
            get() = LocaleBootstrap.getString(R.string.status_impact_feature_unavailable)

        val `ServiceDown`: String
            get() = LocaleBootstrap.getString(R.string.status_impact_service_down)

        val `DataLoss`: String
            get() = LocaleBootstrap.getString(R.string.status_impact_data_loss)
    }

    object `Log` {
        val `LiveLogs`: String
            get() = LocaleBootstrap.getString(R.string.status_log_live_logs)

        val `Archives`: String
            get() = LocaleBootstrap.getString(R.string.status_log_archives)

        val `StartupArchives`: String
            get() = LocaleBootstrap.getString(R.string.status_log_startup_archives)

        val `Recording`: String
            get() = LocaleBootstrap.getString(R.string.status_log_recording)

        val `NotRecording`: String
            get() = LocaleBootstrap.getString(R.string.status_log_not_recording)
    }

    object `Meta` {
        val `SectionTitle`: String
            get() = LocaleBootstrap.getString(R.string.status_meta_section_title)

        val `StateSummaryTitle`: String
            get() = LocaleBootstrap.getString(R.string.status_meta_state_summary_title)

        val `RuntimeTitle`: String
            get() = LocaleBootstrap.getString(R.string.status_meta_runtime_title)

        val `EffectiveRules`: String
            get() = LocaleBootstrap.getString(R.string.status_meta_effective_rules)

        val `Sources`: String
            get() = LocaleBootstrap.getString(R.string.status_meta_sources)

        val `RuntimeIdle`: String
            get() = LocaleBootstrap.getString(R.string.status_meta_runtime_idle)

        val `RuntimeStable`: String
            get() = LocaleBootstrap.getString(R.string.status_meta_runtime_stable)

        val `RuntimeAttention`: String
            get() = LocaleBootstrap.getString(R.string.status_meta_runtime_attention)

        val `RuntimeStarting`: String
            get() = LocaleBootstrap.getString(R.string.status_meta_runtime_starting)

        val `RuntimeRunningDegraded`: String
            get() = LocaleBootstrap.getString(R.string.status_meta_runtime_running_degraded)

        val `RuntimeStopping`: String
            get() = LocaleBootstrap.getString(R.string.status_meta_runtime_stopping)

        val `IdleShort`: String
            get() = LocaleBootstrap.getString(R.string.status_meta_idle_short)

        val `StartingShort`: String
            get() = LocaleBootstrap.getString(R.string.status_meta_starting_short)

        val `StoppingShort`: String
            get() = LocaleBootstrap.getString(R.string.status_meta_stopping_short)

        val `FailedShort`: String
            get() = LocaleBootstrap.getString(R.string.status_meta_failed_short)

        val `EffectiveRulesRuntimeSource`: String
            get() = LocaleBootstrap.getString(R.string.status_meta_effective_rules_runtime_source)

        val `EffectiveRulesProfileSource`: String
            get() = LocaleBootstrap.getString(R.string.status_meta_effective_rules_profile_source)
    }
}
