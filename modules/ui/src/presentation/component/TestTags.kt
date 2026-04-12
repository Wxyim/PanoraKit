/*
 * This file is part of YumeBox.
 *
 * YumeBox is free software: you can redistribute it and/or modify
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
 * Copyright (c)  YumeLira 2025 - Present
 *
 */

package com.github.yumelira.yumebox.presentation.component

/**
 * Stable semantic test tags for Compose UI testing. Custom components reference these tags via
 * [Modifier.testTag] to guarantee stable selection, assertion, and interaction in tests.
 */
object TestTags {

    object Home {
        const val StatusCapsule = "home_status_capsule"
        const val ProfileModeBadge = "home_profile_mode_badge"
        const val DownloadSpeed = "home_download_speed"
        const val UploadSpeed = "home_upload_speed"
    }

    object Profiles {
        const val ProfileList = "profiles_list"

        fun profileCard(id: String) = "profile_card_$id"
    }

    object Settings {
        const val SettingsList = "settings_list"
    }

    object AccessControl {
        const val AppList = "access_control_app_list"
        const val SearchField = "access_control_search"
    }

    object Proxy {
        const val NodeList = "proxy_node_list"

        fun nodeCard(name: String) = "proxy_node_$name"
    }

    object Editor {
        const val EditorField = "editor_field"
        const val SaveButton = "editor_save"
        const val CommandBar = "editor_command_bar"
    }

    object Dialog {
        const val ConfirmButton = "dialog_confirm"
        const val CancelButton = "dialog_cancel"
        const val Container = "dialog_container"
    }

    object Log {
        const val LogList = "log_list"
        const val ExportButton = "log_export"
    }

    object Navigation {
        const val BottomBar = "bottom_navigation_bar"

        fun tab(label: String) = "nav_tab_$label"
    }
}
