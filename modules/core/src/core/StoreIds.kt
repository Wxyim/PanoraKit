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



package com.github.yumelira.yumebox.core

/**
 * Single source of truth for all MMKV partition IDs used across
 * the app process (Koin-managed) and the service process (non-DI).
 */
object StoreIds {
    const val SETTINGS = "settings"
    const val NETWORK_SETTINGS = "network_settings"
    const val PROFILES = "profiles"
    const val SERVICE = "service"
    const val SERVICE_CACHE = "service_cache"
    const val RUNTIME_SNAPSHOT = "runtime_snapshot"
    const val ROOT_TUN_STATE = "root_tun_state"
    const val PROXY_DISPLAY = "proxy_display"
    const val TRAFFIC_STATISTICS = "traffic_statistics"
    const val PROFILE_LINKS = "profile_links"
    const val OVERRIDE_BINDINGS = "override_bindings"
}
