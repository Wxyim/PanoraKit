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
import com.github.nomadboxlab.monadbox.core.locale.LocaleBootstrap
import com.github.nomadboxlab.monadbox.core.locale.R

object MLangOnboarding {
    object `Navigation` {
        val `Back`: String
            get() = LocaleBootstrap.getString(R.string.onboarding_navigation_back)

        @Composable
        fun `Back`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.onboarding_navigation_back, *args)

        val `Next`: String
            get() = LocaleBootstrap.getString(R.string.onboarding_navigation_next)

        @Composable
        fun `Next`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.onboarding_navigation_next, *args)

        val `Start`: String
            get() = LocaleBootstrap.getString(R.string.onboarding_navigation_start)

        @Composable
        fun `Start`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.onboarding_navigation_start, *args)

        val `Enter`: String
            get() = LocaleBootstrap.getString(R.string.onboarding_navigation_enter)

        @Composable
        fun `Enter`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.onboarding_navigation_enter, *args)
    }

    object `Permission` {
        val `Title`: String
            get() = LocaleBootstrap.getString(R.string.onboarding_permission_title)

        @Composable
        fun `Title`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.onboarding_permission_title, *args)

        val `Subtitle`: String
            get() = LocaleBootstrap.getString(R.string.onboarding_permission_subtitle)

        @Composable
        fun `Subtitle`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.onboarding_permission_subtitle, *args)

        object `Common` {
            val `Granted`: String
                get() = LocaleBootstrap.getString(R.string.onboarding_permission_common_granted)

            @Composable
            fun `Granted`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.onboarding_permission_common_granted, *args)
        }

        object `Notification` {
            val `Title`: String
                get() = LocaleBootstrap.getString(R.string.onboarding_permission_notification_title)

            @Composable
            fun `Title`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.onboarding_permission_notification_title, *args)

            val `SummaryNeed`: String
                get() =
                    LocaleBootstrap.getString(
                        R.string.onboarding_permission_notification_summary_need
                    )

            @Composable
            fun `SummaryNeed`(vararg args: Any): String =
                LocaleBootstrap.getString(
                    R.string.onboarding_permission_notification_summary_need,
                    *args,
                )

            val `SummaryNotRequired`: String
                get() =
                    LocaleBootstrap.getString(
                        R.string.onboarding_permission_notification_summary_not_required
                    )

            @Composable
            fun `SummaryNotRequired`(vararg args: Any): String =
                LocaleBootstrap.getString(
                    R.string.onboarding_permission_notification_summary_not_required,
                    *args,
                )
        }

        object `AppList` {
            val `Title`: String
                get() = LocaleBootstrap.getString(R.string.onboarding_permission_app_list_title)

            @Composable
            fun `Title`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.onboarding_permission_app_list_title, *args)

            val `SummaryNeed`: String
                get() =
                    LocaleBootstrap.getString(R.string.onboarding_permission_app_list_summary_need)

            @Composable
            fun `SummaryNeed`(vararg args: Any): String =
                LocaleBootstrap.getString(
                    R.string.onboarding_permission_app_list_summary_need,
                    *args,
                )

            val `SummaryNotRequired`: String
                get() =
                    LocaleBootstrap.getString(
                        R.string.onboarding_permission_app_list_summary_not_required
                    )

            @Composable
            fun `SummaryNotRequired`(vararg args: Any): String =
                LocaleBootstrap.getString(
                    R.string.onboarding_permission_app_list_summary_not_required,
                    *args,
                )
        }
    }

    object `Privacy` {
        val `Title`: String
            get() = LocaleBootstrap.getString(R.string.onboarding_privacy_title)

        @Composable
        fun `Title`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.onboarding_privacy_title, *args)

        val `Subtitle`: String
            get() = LocaleBootstrap.getString(R.string.onboarding_privacy_subtitle)

        @Composable
        fun `Subtitle`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.onboarding_privacy_subtitle, *args)

        val `RichTextLead`: String
            get() = LocaleBootstrap.getString(R.string.onboarding_privacy_rich_text_lead)

        @Composable
        fun `RichTextLead`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.onboarding_privacy_rich_text_lead, *args)

        val `RichTextPrefix`: String
            get() = LocaleBootstrap.getString(R.string.onboarding_privacy_rich_text_prefix)

        @Composable
        fun `RichTextPrefix`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.onboarding_privacy_rich_text_prefix, *args)

        val `RichTextSuffix`: String
            get() = LocaleBootstrap.getString(R.string.onboarding_privacy_rich_text_suffix)

        @Composable
        fun `RichTextSuffix`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.onboarding_privacy_rich_text_suffix, *args)

        val `PolicyLink`: String
            get() = LocaleBootstrap.getString(R.string.onboarding_privacy_policy_link)

        @Composable
        fun `PolicyLink`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.onboarding_privacy_policy_link, *args)

        object `Privacy` {
            val `Title`: String
                get() = LocaleBootstrap.getString(R.string.onboarding_privacy_privacy_title)

            @Composable
            fun `Title`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.onboarding_privacy_privacy_title, *args)
        }

        object `Accept` {
            val `Title`: String
                get() = LocaleBootstrap.getString(R.string.onboarding_privacy_accept_title)

            @Composable
            fun `Title`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.onboarding_privacy_accept_title, *args)
        }
    }

    object `Personalize` {
        val `Title`: String
            get() = LocaleBootstrap.getString(R.string.onboarding_personalize_title)

        @Composable
        fun `Title`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.onboarding_personalize_title, *args)

        val `Subtitle`: String
            get() = LocaleBootstrap.getString(R.string.onboarding_personalize_subtitle)

        @Composable
        fun `Subtitle`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.onboarding_personalize_subtitle, *args)
    }

    object `Finish` {
        val `Title`: String
            get() = LocaleBootstrap.getString(R.string.onboarding_finish_title)

        @Composable
        fun `Title`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.onboarding_finish_title, *args)

        val `Subtitle`: String
            get() = LocaleBootstrap.getString(R.string.onboarding_finish_subtitle)

        @Composable
        fun `Subtitle`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.onboarding_finish_subtitle, *args)
    }

    object `Project` {
        object `Github` {
            val `Title`: String
                get() = LocaleBootstrap.getString(R.string.onboarding_project_github_title)

            @Composable
            fun `Title`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.onboarding_project_github_title, *args)

            val `Summary`: String
                get() = LocaleBootstrap.getString(R.string.onboarding_project_github_summary)

            @Composable
            fun `Summary`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.onboarding_project_github_summary, *args)
        }
    }

    object `Sheet` {
        val `PrivacyPolicyTitle`: String
            get() = LocaleBootstrap.getString(R.string.onboarding_sheet_privacy_policy_title)

        @Composable
        fun `PrivacyPolicyTitle`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.onboarding_sheet_privacy_policy_title, *args)

        val `LoadFailed`: String
            get() = LocaleBootstrap.getString(R.string.onboarding_sheet_load_failed)

        @Composable
        fun `LoadFailed`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.onboarding_sheet_load_failed, *args)
    }
}
