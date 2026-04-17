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

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.github.nomadboxlab.monadbox.core.locale.LocaleBootstrap
import com.github.nomadboxlab.monadbox.core.locale.R

object MLangProxy {
    val `Title`: String
        get() = LocaleBootstrap.getString(R.string.proxy_title)

    @Composable fun `Title`(vararg args: Any): String = stringResource(R.string.proxy_title, *args)

    object `Mode` {
        val `Direct`: String
            get() = LocaleBootstrap.getString(R.string.proxy_mode_direct)

        @Composable
        fun `Direct`(vararg args: Any): String = stringResource(R.string.proxy_mode_direct, *args)

        val `Global`: String
            get() = LocaleBootstrap.getString(R.string.proxy_mode_global)

        @Composable
        fun `Global`(vararg args: Any): String = stringResource(R.string.proxy_mode_global, *args)

        val `Rule`: String
            get() = LocaleBootstrap.getString(R.string.proxy_mode_rule)

        @Composable
        fun `Rule`(vararg args: Any): String = stringResource(R.string.proxy_mode_rule, *args)

        val `Unknown`: String
            get() = LocaleBootstrap.getString(R.string.proxy_mode_unknown)

        @Composable
        fun `Unknown`(vararg args: Any): String = stringResource(R.string.proxy_mode_unknown, *args)

        val `Switched`: String
            get() = LocaleBootstrap.getString(R.string.proxy_mode_switched)

        @Composable
        fun `Switched`(vararg args: Any): String =
            stringResource(R.string.proxy_mode_switched, *args)

        val `SwitchFailed`: String
            get() = LocaleBootstrap.getString(R.string.proxy_mode_switch_failed)

        @Composable
        fun `SwitchFailed`(vararg args: Any): String =
            stringResource(R.string.proxy_mode_switch_failed, *args)
    }

    object `Action` {
        val `Panel`: String
            get() = LocaleBootstrap.getString(R.string.proxy_action_panel)

        @Composable
        fun `Panel`(vararg args: Any): String = stringResource(R.string.proxy_action_panel, *args)

        val `Test`: String
            get() = LocaleBootstrap.getString(R.string.proxy_action_test)

        @Composable
        fun `Test`(vararg args: Any): String = stringResource(R.string.proxy_action_test, *args)

        val `Resources`: String
            get() = LocaleBootstrap.getString(R.string.proxy_action_resources)

        @Composable
        fun `Resources`(vararg args: Any): String =
            stringResource(R.string.proxy_action_resources, *args)

        val `ControlPanel`: String
            get() = LocaleBootstrap.getString(R.string.proxy_action_control_panel)

        @Composable
        fun `ControlPanel`(vararg args: Any): String =
            stringResource(R.string.proxy_action_control_panel, *args)

        val `More`: String
            get() = LocaleBootstrap.getString(R.string.proxy_action_more)

        @Composable
        fun `More`(vararg args: Any): String = stringResource(R.string.proxy_action_more, *args)

        val `GroupStyle`: String
            get() = LocaleBootstrap.getString(R.string.proxy_action_group_style)

        @Composable
        fun `GroupStyle`(vararg args: Any): String =
            stringResource(R.string.proxy_action_group_style, *args)

        val `ShowHiddenGroups`: String
            get() = LocaleBootstrap.getString(R.string.proxy_action_show_hidden_groups)

        @Composable
        fun `ShowHiddenGroups`(vararg args: Any): String =
            stringResource(R.string.proxy_action_show_hidden_groups, *args)

        val `Back`: String
            get() = LocaleBootstrap.getString(R.string.proxy_action_back)

        @Composable
        fun `Back`(vararg args: Any): String = stringResource(R.string.proxy_action_back, *args)

        val `Close`: String
            get() = LocaleBootstrap.getString(R.string.proxy_action_close)

        @Composable
        fun `Close`(vararg args: Any): String = stringResource(R.string.proxy_action_close, *args)

        val `TestDelay`: String
            get() = LocaleBootstrap.getString(R.string.proxy_action_test_delay)

        @Composable
        fun `TestDelay`(vararg args: Any): String =
            stringResource(R.string.proxy_action_test_delay, *args)

        val `SortMode`: String
            get() = LocaleBootstrap.getString(R.string.proxy_action_sort_mode)

        @Composable
        fun `SortMode`(vararg args: Any): String =
            stringResource(R.string.proxy_action_sort_mode, *args)

        val `SortModeSummary`: String
            get() = LocaleBootstrap.getString(R.string.proxy_action_sort_mode_summary)

        @Composable
        fun `SortModeSummary`(vararg args: Any): String =
            stringResource(R.string.proxy_action_sort_mode_summary, *args)

        val `AddProfile`: String
            get() = LocaleBootstrap.getString(R.string.proxy_action_add_profile)

        @Composable
        fun `AddProfile`(vararg args: Any): String =
            stringResource(R.string.proxy_action_add_profile, *args)

        val `AddProvider`: String
            get() = LocaleBootstrap.getString(R.string.proxy_action_add_provider)

        @Composable
        fun `AddProvider`(vararg args: Any): String =
            stringResource(R.string.proxy_action_add_provider, *args)

        val `AddOverride`: String
            get() = LocaleBootstrap.getString(R.string.proxy_action_add_override)

        @Composable
        fun `AddOverride`(vararg args: Any): String =
            stringResource(R.string.proxy_action_add_override, *args)

        val `AddChain`: String
            get() = LocaleBootstrap.getString(R.string.proxy_action_add_chain)

        @Composable
        fun `AddChain`(vararg args: Any): String =
            stringResource(R.string.proxy_action_add_chain, *args)
    }

    object `Empty` {
        val `NoNodes`: String
            get() = LocaleBootstrap.getString(R.string.proxy_empty_no_nodes)

        @Composable
        fun `NoNodes`(vararg args: Any): String =
            stringResource(R.string.proxy_empty_no_nodes, *args)

        val `Hint`: String
            get() = LocaleBootstrap.getString(R.string.proxy_empty_hint)

        @Composable
        fun `Hint`(vararg args: Any): String = stringResource(R.string.proxy_empty_hint, *args)
    }

    object `Testing` {
        val `Group`: String
            get() = LocaleBootstrap.getString(R.string.proxy_testing_group)

        @Composable
        fun `Group`(vararg args: Any): String = stringResource(R.string.proxy_testing_group, *args)

        val `All`: String
            get() = LocaleBootstrap.getString(R.string.proxy_testing_all)

        @Composable
        fun `All`(vararg args: Any): String = stringResource(R.string.proxy_testing_all, *args)

        val `RequestSent`: String
            get() = LocaleBootstrap.getString(R.string.proxy_testing_request_sent)

        @Composable
        fun `RequestSent`(vararg args: Any): String =
            stringResource(R.string.proxy_testing_request_sent, *args)

        val `Failed`: String
            get() = LocaleBootstrap.getString(R.string.proxy_testing_failed)

        @Composable
        fun `Failed`(vararg args: Any): String =
            stringResource(R.string.proxy_testing_failed, *args)

        val `InProgress`: String
            get() = LocaleBootstrap.getString(R.string.proxy_testing_in_progress)

        @Composable
        fun `InProgress`(vararg args: Any): String =
            stringResource(R.string.proxy_testing_in_progress, *args)
    }

    object `Selection` {
        val `Switched`: String
            get() = LocaleBootstrap.getString(R.string.proxy_selection_switched)

        @Composable
        fun `Switched`(vararg args: Any): String =
            stringResource(R.string.proxy_selection_switched, *args)

        val `Failed`: String
            get() = LocaleBootstrap.getString(R.string.proxy_selection_failed)

        @Composable
        fun `Failed`(vararg args: Any): String =
            stringResource(R.string.proxy_selection_failed, *args)

        val `Error`: String
            get() = LocaleBootstrap.getString(R.string.proxy_selection_error)

        @Composable
        fun `Error`(vararg args: Any): String =
            stringResource(R.string.proxy_selection_error, *args)

        val `Current`: String
            get() = LocaleBootstrap.getString(R.string.proxy_selection_current)

        @Composable
        fun `Current`(vararg args: Any): String =
            stringResource(R.string.proxy_selection_current, *args)

        val `CurrentNode`: String
            get() = LocaleBootstrap.getString(R.string.proxy_selection_current_node)

        @Composable
        fun `CurrentNode`(vararg args: Any): String =
            stringResource(R.string.proxy_selection_current_node, *args)

        val `NodeCount`: String
            get() = LocaleBootstrap.getString(R.string.proxy_selection_node_count)

        @Composable
        fun `NodeCount`(vararg args: Any): String =
            stringResource(R.string.proxy_selection_node_count, *args)

        val `Latency`: String
            get() = LocaleBootstrap.getString(R.string.proxy_selection_latency)

        @Composable
        fun `Latency`(vararg args: Any): String =
            stringResource(R.string.proxy_selection_latency, *args)

        val `Timeout`: String
            get() = LocaleBootstrap.getString(R.string.proxy_selection_timeout)

        @Composable
        fun `Timeout`(vararg args: Any): String =
            stringResource(R.string.proxy_selection_timeout, *args)

        val `UnknownLatency`: String
            get() = LocaleBootstrap.getString(R.string.proxy_selection_unknown_latency)

        @Composable
        fun `UnknownLatency`(vararg args: Any): String =
            stringResource(R.string.proxy_selection_unknown_latency, *args)
    }

    object `SortMode` {
        val `Default`: String
            get() = LocaleBootstrap.getString(R.string.proxy_sort_mode_default)

        @Composable
        fun `Default`(vararg args: Any): String =
            stringResource(R.string.proxy_sort_mode_default, *args)

        val `ByName`: String
            get() = LocaleBootstrap.getString(R.string.proxy_sort_mode_by_name)

        @Composable
        fun `ByName`(vararg args: Any): String =
            stringResource(R.string.proxy_sort_mode_by_name, *args)

        val `ByLatency`: String
            get() = LocaleBootstrap.getString(R.string.proxy_sort_mode_by_latency)

        @Composable
        fun `ByLatency`(vararg args: Any): String =
            stringResource(R.string.proxy_sort_mode_by_latency, *args)
    }

    object `DisplayMode` {
        val `SingleDetailed`: String
            get() = LocaleBootstrap.getString(R.string.proxy_display_mode_single_detailed)

        @Composable
        fun `SingleDetailed`(vararg args: Any): String =
            stringResource(R.string.proxy_display_mode_single_detailed, *args)

        val `SingleSimple`: String
            get() = LocaleBootstrap.getString(R.string.proxy_display_mode_single_simple)

        @Composable
        fun `SingleSimple`(vararg args: Any): String =
            stringResource(R.string.proxy_display_mode_single_simple, *args)

        val `DoubleDetailed`: String
            get() = LocaleBootstrap.getString(R.string.proxy_display_mode_double_detailed)

        @Composable
        fun `DoubleDetailed`(vararg args: Any): String =
            stringResource(R.string.proxy_display_mode_double_detailed, *args)

        val `DoubleSimple`: String
            get() = LocaleBootstrap.getString(R.string.proxy_display_mode_double_simple)

        @Composable
        fun `DoubleSimple`(vararg args: Any): String =
            stringResource(R.string.proxy_display_mode_double_simple, *args)
    }

    object `GroupStyle` {
        val `Inline`: String
            get() = LocaleBootstrap.getString(R.string.proxy_group_style_inline)

        @Composable
        fun `Inline`(vararg args: Any): String =
            stringResource(R.string.proxy_group_style_inline, *args)

        val `Floating`: String
            get() = LocaleBootstrap.getString(R.string.proxy_group_style_floating)

        @Composable
        fun `Floating`(vararg args: Any): String =
            stringResource(R.string.proxy_group_style_floating, *args)
    }
}
