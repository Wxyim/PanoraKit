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

object MLangNetworkSettings {
    val `Title`: String
        get() = LocaleBootstrap.getString(R.string.network_settings_title)

    @Composable
    fun `Title`(vararg args: Any): String = stringResource(R.string.network_settings_title, *args)

    object `Section` {
        val `VpnService`: String
            get() = LocaleBootstrap.getString(R.string.network_settings_section_vpn_service)

        @Composable
        fun `VpnService`(vararg args: Any): String =
            stringResource(R.string.network_settings_section_vpn_service, *args)

        val `VpnOptions`: String
            get() = LocaleBootstrap.getString(R.string.network_settings_section_vpn_options)

        @Composable
        fun `VpnOptions`(vararg args: Any): String =
            stringResource(R.string.network_settings_section_vpn_options, *args)

        val `ProxyOptions`: String
            get() = LocaleBootstrap.getString(R.string.network_settings_section_proxy_options)

        @Composable
        fun `ProxyOptions`(vararg args: Any): String =
            stringResource(R.string.network_settings_section_proxy_options, *args)

        val `RootTunAdvanced`: String
            get() = LocaleBootstrap.getString(R.string.network_settings_section_root_tun_advanced)

        @Composable
        fun `RootTunAdvanced`(vararg args: Any): String =
            stringResource(R.string.network_settings_section_root_tun_advanced, *args)
    }

    object `VpnService` {
        val `RouteTrafficTitle`: String
            get() =
                LocaleBootstrap.getString(R.string.network_settings_vpn_service_route_traffic_title)

        @Composable
        fun `RouteTrafficTitle`(vararg args: Any): String =
            stringResource(R.string.network_settings_vpn_service_route_traffic_title, *args)

        val `RouteTrafficSummary`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.network_settings_vpn_service_route_traffic_summary
                )

        @Composable
        fun `RouteTrafficSummary`(vararg args: Any): String =
            stringResource(R.string.network_settings_vpn_service_route_traffic_summary, *args)

        val `RouteTrafficEffective`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.network_settings_vpn_service_route_traffic_effective
                )

        @Composable
        fun `RouteTrafficEffective`(vararg args: Any): String =
            stringResource(R.string.network_settings_vpn_service_route_traffic_effective, *args)

        val `RouteTrafficApplying`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.network_settings_vpn_service_route_traffic_applying
                )

        @Composable
        fun `RouteTrafficApplying`(vararg args: Any): String =
            stringResource(R.string.network_settings_vpn_service_route_traffic_applying, *args)

        val `VpnMode`: String
            get() = LocaleBootstrap.getString(R.string.network_settings_vpn_service_vpn_mode)

        @Composable
        fun `VpnMode`(vararg args: Any): String =
            stringResource(R.string.network_settings_vpn_service_vpn_mode, *args)

        val `RootTunMode`: String
            get() = LocaleBootstrap.getString(R.string.network_settings_vpn_service_root_tun_mode)

        @Composable
        fun `RootTunMode`(vararg args: Any): String =
            stringResource(R.string.network_settings_vpn_service_root_tun_mode, *args)

        val `SystemProxy`: String
            get() = LocaleBootstrap.getString(R.string.network_settings_vpn_service_system_proxy)

        @Composable
        fun `SystemProxy`(vararg args: Any): String =
            stringResource(R.string.network_settings_vpn_service_system_proxy, *args)
    }

    object `HttpMode` {
        val `InfoTitle`: String
            get() = LocaleBootstrap.getString(R.string.network_settings_http_mode_info_title)

        @Composable
        fun `InfoTitle`(vararg args: Any): String =
            stringResource(R.string.network_settings_http_mode_info_title, *args)

        val `InfoSummary`: String
            get() = LocaleBootstrap.getString(R.string.network_settings_http_mode_info_summary)

        @Composable
        fun `InfoSummary`(vararg args: Any): String =
            stringResource(R.string.network_settings_http_mode_info_summary, *args)
    }

    object `VpnOptions` {
        val `BypassPrivateTitle`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.network_settings_vpn_options_bypass_private_title
                )

        @Composable
        fun `BypassPrivateTitle`(vararg args: Any): String =
            stringResource(R.string.network_settings_vpn_options_bypass_private_title, *args)

        val `BypassPrivateSummary`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.network_settings_vpn_options_bypass_private_summary
                )

        @Composable
        fun `BypassPrivateSummary`(vararg args: Any): String =
            stringResource(R.string.network_settings_vpn_options_bypass_private_summary, *args)

        val `DnsHijackTitle`: String
            get() =
                LocaleBootstrap.getString(R.string.network_settings_vpn_options_dns_hijack_title)

        @Composable
        fun `DnsHijackTitle`(vararg args: Any): String =
            stringResource(R.string.network_settings_vpn_options_dns_hijack_title, *args)

        val `DnsHijackSummary`: String
            get() =
                LocaleBootstrap.getString(R.string.network_settings_vpn_options_dns_hijack_summary)

        @Composable
        fun `DnsHijackSummary`(vararg args: Any): String =
            stringResource(R.string.network_settings_vpn_options_dns_hijack_summary, *args)

        val `AllowBypassTitle`: String
            get() =
                LocaleBootstrap.getString(R.string.network_settings_vpn_options_allow_bypass_title)

        @Composable
        fun `AllowBypassTitle`(vararg args: Any): String =
            stringResource(R.string.network_settings_vpn_options_allow_bypass_title, *args)

        val `AllowBypassSummary`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.network_settings_vpn_options_allow_bypass_summary
                )

        @Composable
        fun `AllowBypassSummary`(vararg args: Any): String =
            stringResource(R.string.network_settings_vpn_options_allow_bypass_summary, *args)

        val `EnableIpv6Title`: String
            get() =
                LocaleBootstrap.getString(R.string.network_settings_vpn_options_enable_ipv6_title)

        @Composable
        fun `EnableIpv6Title`(vararg args: Any): String =
            stringResource(R.string.network_settings_vpn_options_enable_ipv6_title, *args)

        val `EnableIpv6Summary`: String
            get() =
                LocaleBootstrap.getString(R.string.network_settings_vpn_options_enable_ipv6_summary)

        @Composable
        fun `EnableIpv6Summary`(vararg args: Any): String =
            stringResource(R.string.network_settings_vpn_options_enable_ipv6_summary, *args)

        val `SystemProxyTitle`: String
            get() =
                LocaleBootstrap.getString(R.string.network_settings_vpn_options_system_proxy_title)

        @Composable
        fun `SystemProxyTitle`(vararg args: Any): String =
            stringResource(R.string.network_settings_vpn_options_system_proxy_title, *args)

        val `SystemProxySummary`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.network_settings_vpn_options_system_proxy_summary
                )

        @Composable
        fun `SystemProxySummary`(vararg args: Any): String =
            stringResource(R.string.network_settings_vpn_options_system_proxy_summary, *args)
    }

    object `ProxyOptions` {
        val `TunStackTitle`: String
            get() =
                LocaleBootstrap.getString(R.string.network_settings_proxy_options_tun_stack_title)

        @Composable
        fun `TunStackTitle`(vararg args: Any): String =
            stringResource(R.string.network_settings_proxy_options_tun_stack_title, *args)

        val `TunStackSystem`: String
            get() =
                LocaleBootstrap.getString(R.string.network_settings_proxy_options_tun_stack_system)

        @Composable
        fun `TunStackSystem`(vararg args: Any): String =
            stringResource(R.string.network_settings_proxy_options_tun_stack_system, *args)

        val `TunStackGvisor`: String
            get() =
                LocaleBootstrap.getString(R.string.network_settings_proxy_options_tun_stack_gvisor)

        @Composable
        fun `TunStackGvisor`(vararg args: Any): String =
            stringResource(R.string.network_settings_proxy_options_tun_stack_gvisor, *args)

        val `TunStackMixed`: String
            get() =
                LocaleBootstrap.getString(R.string.network_settings_proxy_options_tun_stack_mixed)

        @Composable
        fun `TunStackMixed`(vararg args: Any): String =
            stringResource(R.string.network_settings_proxy_options_tun_stack_mixed, *args)

        val `AccessControlModeTitle`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.network_settings_proxy_options_access_control_mode_title
                )

        @Composable
        fun `AccessControlModeTitle`(vararg args: Any): String =
            stringResource(R.string.network_settings_proxy_options_access_control_mode_title, *args)

        val `AllowNonLocalhostHttpRemoteTitle`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.network_settings_proxy_options_allow_non_localhost_http_remote_title
                )

        @Composable
        fun `AllowNonLocalhostHttpRemoteTitle`(vararg args: Any): String =
            stringResource(
                R.string.network_settings_proxy_options_allow_non_localhost_http_remote_title,
                *args,
            )

        val `AllowNonLocalhostHttpRemoteSummary`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.network_settings_proxy_options_allow_non_localhost_http_remote_summary
                )

        @Composable
        fun `AllowNonLocalhostHttpRemoteSummary`(vararg args: Any): String =
            stringResource(
                R.string.network_settings_proxy_options_allow_non_localhost_http_remote_summary,
                *args,
            )

        val `AllowAll`: String
            get() = LocaleBootstrap.getString(R.string.network_settings_proxy_options_allow_all)

        @Composable
        fun `AllowAll`(vararg args: Any): String =
            stringResource(R.string.network_settings_proxy_options_allow_all, *args)

        val `AllowSelected`: String
            get() =
                LocaleBootstrap.getString(R.string.network_settings_proxy_options_allow_selected)

        @Composable
        fun `AllowSelected`(vararg args: Any): String =
            stringResource(R.string.network_settings_proxy_options_allow_selected, *args)

        val `RejectSelected`: String
            get() =
                LocaleBootstrap.getString(R.string.network_settings_proxy_options_reject_selected)

        @Composable
        fun `RejectSelected`(vararg args: Any): String =
            stringResource(R.string.network_settings_proxy_options_reject_selected, *args)

        val `ManageAccessControlTitle`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.network_settings_proxy_options_manage_access_control_title
                )

        @Composable
        fun `ManageAccessControlTitle`(vararg args: Any): String =
            stringResource(
                R.string.network_settings_proxy_options_manage_access_control_title,
                *args,
            )

        val `ManageAccessControlSummary`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.network_settings_proxy_options_manage_access_control_summary
                )

        @Composable
        fun `ManageAccessControlSummary`(vararg args: Any): String =
            stringResource(
                R.string.network_settings_proxy_options_manage_access_control_summary,
                *args,
            )
    }

    object `RootTun` {
        val `IfNameTitle`: String
            get() = LocaleBootstrap.getString(R.string.network_settings_root_tun_if_name_title)

        @Composable
        fun `IfNameTitle`(vararg args: Any): String =
            stringResource(R.string.network_settings_root_tun_if_name_title, *args)

        val `IfNameSummary`: String
            get() = LocaleBootstrap.getString(R.string.network_settings_root_tun_if_name_summary)

        @Composable
        fun `IfNameSummary`(vararg args: Any): String =
            stringResource(R.string.network_settings_root_tun_if_name_summary, *args)

        val `MtuTitle`: String
            get() = LocaleBootstrap.getString(R.string.network_settings_root_tun_mtu_title)

        @Composable
        fun `MtuTitle`(vararg args: Any): String =
            stringResource(R.string.network_settings_root_tun_mtu_title, *args)

        val `MtuSummary`: String
            get() = LocaleBootstrap.getString(R.string.network_settings_root_tun_mtu_summary)

        @Composable
        fun `MtuSummary`(vararg args: Any): String =
            stringResource(R.string.network_settings_root_tun_mtu_summary, *args)

        val `AndroidUsersTitle`: String
            get() =
                LocaleBootstrap.getString(R.string.network_settings_root_tun_android_users_title)

        @Composable
        fun `AndroidUsersTitle`(vararg args: Any): String =
            stringResource(R.string.network_settings_root_tun_android_users_title, *args)

        val `AndroidUsersSummary`: String
            get() =
                LocaleBootstrap.getString(R.string.network_settings_root_tun_android_users_summary)

        @Composable
        fun `AndroidUsersSummary`(vararg args: Any): String =
            stringResource(R.string.network_settings_root_tun_android_users_summary, *args)

        val `AndroidUsersPlaceholder`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.network_settings_root_tun_android_users_placeholder
                )

        @Composable
        fun `AndroidUsersPlaceholder`(vararg args: Any): String =
            stringResource(R.string.network_settings_root_tun_android_users_placeholder, *args)

        val `RouteExcludesTitle`: String
            get() =
                LocaleBootstrap.getString(R.string.network_settings_root_tun_route_excludes_title)

        @Composable
        fun `RouteExcludesTitle`(vararg args: Any): String =
            stringResource(R.string.network_settings_root_tun_route_excludes_title, *args)

        val `RouteExcludesSummary`: String
            get() =
                LocaleBootstrap.getString(R.string.network_settings_root_tun_route_excludes_summary)

        @Composable
        fun `RouteExcludesSummary`(vararg args: Any): String =
            stringResource(R.string.network_settings_root_tun_route_excludes_summary, *args)

        val `RouteExcludesPlaceholder`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.network_settings_root_tun_route_excludes_placeholder
                )

        @Composable
        fun `RouteExcludesPlaceholder`(vararg args: Any): String =
            stringResource(R.string.network_settings_root_tun_route_excludes_placeholder, *args)

        val `AutoRouteTitle`: String
            get() = LocaleBootstrap.getString(R.string.network_settings_root_tun_auto_route_title)

        @Composable
        fun `AutoRouteTitle`(vararg args: Any): String =
            stringResource(R.string.network_settings_root_tun_auto_route_title, *args)

        val `AutoRouteSummary`: String
            get() = LocaleBootstrap.getString(R.string.network_settings_root_tun_auto_route_summary)

        @Composable
        fun `AutoRouteSummary`(vararg args: Any): String =
            stringResource(R.string.network_settings_root_tun_auto_route_summary, *args)

        val `StrictRouteTitle`: String
            get() = LocaleBootstrap.getString(R.string.network_settings_root_tun_strict_route_title)

        @Composable
        fun `StrictRouteTitle`(vararg args: Any): String =
            stringResource(R.string.network_settings_root_tun_strict_route_title, *args)

        val `StrictRouteSummary`: String
            get() =
                LocaleBootstrap.getString(R.string.network_settings_root_tun_strict_route_summary)

        @Composable
        fun `StrictRouteSummary`(vararg args: Any): String =
            stringResource(R.string.network_settings_root_tun_strict_route_summary, *args)

        val `AutoRedirectTitle`: String
            get() =
                LocaleBootstrap.getString(R.string.network_settings_root_tun_auto_redirect_title)

        @Composable
        fun `AutoRedirectTitle`(vararg args: Any): String =
            stringResource(R.string.network_settings_root_tun_auto_redirect_title, *args)

        val `AutoRedirectSummary`: String
            get() =
                LocaleBootstrap.getString(R.string.network_settings_root_tun_auto_redirect_summary)

        @Composable
        fun `AutoRedirectSummary`(vararg args: Any): String =
            stringResource(R.string.network_settings_root_tun_auto_redirect_summary, *args)

        val `DnsModeTitle`: String
            get() = LocaleBootstrap.getString(R.string.network_settings_root_tun_dns_mode_title)

        @Composable
        fun `DnsModeTitle`(vararg args: Any): String =
            stringResource(R.string.network_settings_root_tun_dns_mode_title, *args)

        val `DnsModeSummary`: String
            get() = LocaleBootstrap.getString(R.string.network_settings_root_tun_dns_mode_summary)

        @Composable
        fun `DnsModeSummary`(vararg args: Any): String =
            stringResource(R.string.network_settings_root_tun_dns_mode_summary, *args)

        val `DnsModeRedirHost`: String
            get() =
                LocaleBootstrap.getString(R.string.network_settings_root_tun_dns_mode_redir_host)

        @Composable
        fun `DnsModeRedirHost`(vararg args: Any): String =
            stringResource(R.string.network_settings_root_tun_dns_mode_redir_host, *args)

        val `DnsModeFakeIp`: String
            get() = LocaleBootstrap.getString(R.string.network_settings_root_tun_dns_mode_fake_ip)

        @Composable
        fun `DnsModeFakeIp`(vararg args: Any): String =
            stringResource(R.string.network_settings_root_tun_dns_mode_fake_ip, *args)

        val `FakeIpRangeTitle`: String
            get() =
                LocaleBootstrap.getString(R.string.network_settings_root_tun_fake_ip_range_title)

        @Composable
        fun `FakeIpRangeTitle`(vararg args: Any): String =
            stringResource(R.string.network_settings_root_tun_fake_ip_range_title, *args)

        val `FakeIpRangeSummary`: String
            get() =
                LocaleBootstrap.getString(R.string.network_settings_root_tun_fake_ip_range_summary)

        @Composable
        fun `FakeIpRangeSummary`(vararg args: Any): String =
            stringResource(R.string.network_settings_root_tun_fake_ip_range_summary, *args)

        val `FakeIpRange6Title`: String
            get() =
                LocaleBootstrap.getString(R.string.network_settings_root_tun_fake_ip_range6_title)

        @Composable
        fun `FakeIpRange6Title`(vararg args: Any): String =
            stringResource(R.string.network_settings_root_tun_fake_ip_range6_title, *args)

        val `FakeIpRange6Summary`: String
            get() =
                LocaleBootstrap.getString(R.string.network_settings_root_tun_fake_ip_range6_summary)

        @Composable
        fun `FakeIpRange6Summary`(vararg args: Any): String =
            stringResource(R.string.network_settings_root_tun_fake_ip_range6_summary, *args)
    }

    object `Error` {
        val `VpnDenied`: String
            get() = LocaleBootstrap.getString(R.string.network_settings_error_vpn_denied)

        @Composable
        fun `VpnDenied`(vararg args: Any): String =
            stringResource(R.string.network_settings_error_vpn_denied, *args)
    }
}
