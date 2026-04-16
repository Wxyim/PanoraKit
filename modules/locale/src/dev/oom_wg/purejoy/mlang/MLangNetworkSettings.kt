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

object MLangNetworkSettings {
    init {
        RootMLangGroups
    }

    /** 网络设置 */
    val `Title`
        get() =
            FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                it as RootMLangTags
                when (it) {
                    RootMLangTags.EN -> """Network Settings"""
                    RootMLangTags.ZH -> """网络设置"""
                    else -> null
                }
            } ?: """网络设置"""

    /** 网络设置 */
    @Composable fun `Title`(vararg args: Any?) = FYTxtConfig.observe { `Title`.fmt(args) }

    object `Section` {
        init {
            RootMLangGroups
        }

        /** 代理模式 */
        val `VpnService`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Proxy Mode"""
                        RootMLangTags.ZH -> """代理模式"""
                        else -> null
                    }
                } ?: """代理模式"""

        /** 代理模式 */
        @Composable
        fun `VpnService`(vararg args: Any?) = FYTxtConfig.observe { `VpnService`.fmt(args) }

        /** 服务配置 */
        val `VpnOptions`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Service Options"""
                        RootMLangTags.ZH -> """服务配置"""
                        else -> null
                    }
                } ?: """服务配置"""

        /** 服务配置 */
        @Composable
        fun `VpnOptions`(vararg args: Any?) = FYTxtConfig.observe { `VpnOptions`.fmt(args) }

        /** 访问控制 */
        val `ProxyOptions`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Access Control"""
                        RootMLangTags.ZH -> """访问控制"""
                        else -> null
                    }
                } ?: """访问控制"""

        /** 访问控制 */
        @Composable
        fun `ProxyOptions`(vararg args: Any?) = FYTxtConfig.observe { `ProxyOptions`.fmt(args) }

        /** 高级参数 */
        val `RootTunAdvanced`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Advanced Parameters"""
                        RootMLangTags.ZH -> """高级参数"""
                        else -> null
                    }
                } ?: """高级参数"""

        /** 高级参数 */
        @Composable
        fun `RootTunAdvanced`(vararg args: Any?) =
            FYTxtConfig.observe { `RootTunAdvanced`.fmt(args) }
    }

    object `VpnService` {
        init {
            RootMLangGroups
        }

        /** 路由系统流量 */
        val `RouteTrafficTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Route System Traffic"""
                        RootMLangTags.ZH -> """路由系统流量"""
                        else -> null
                    }
                } ?: """路由系统流量"""

        /** 路由系统流量 */
        @Composable
        fun `RouteTrafficTitle`(vararg args: Any?) =
            FYTxtConfig.observe { `RouteTrafficTitle`.fmt(args) }

        /** 选择当前用于接管系统流量的代理模式 */
        val `RouteTrafficSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Select which proxy mode should take over system traffic"""
                        RootMLangTags.ZH -> """选择当前用于接管系统流量的代理模式"""
                        else -> null
                    }
                } ?: """选择当前用于接管系统流量的代理模式"""

        /** 选择当前用于接管系统流量的代理模式 */
        @Composable
        fun `RouteTrafficSummary`(vararg args: Any?) =
            FYTxtConfig.observe { `RouteTrafficSummary`.fmt(args) }

        /** 当前生效：%s；目标配置：%s */
        val `RouteTrafficEffective`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Effective now: %s; configured target: %s"""
                        RootMLangTags.ZH -> """当前生效：%s；目标配置：%s"""
                        else -> null
                    }
                } ?: """当前生效：%s；目标配置：%s"""

        /** 当前生效：%s；目标配置：%s */
        @Composable
        fun `RouteTrafficEffective`(vararg args: Any?) =
            FYTxtConfig.observe { `RouteTrafficEffective`.fmt(args) }

        /** 正在串行切换：%s → %s */
        val `RouteTrafficApplying`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Applying serial switch: %s -> %s"""
                        RootMLangTags.ZH -> """正在串行切换：%s → %s"""
                        else -> null
                    }
                } ?: """正在串行切换：%s → %s"""

        /** 正在串行切换：%s → %s */
        @Composable
        fun `RouteTrafficApplying`(vararg args: Any?) =
            FYTxtConfig.observe { `RouteTrafficApplying`.fmt(args) }

        /** VPN 模式 */
        val `VpnMode`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """VPN Mode"""
                        RootMLangTags.ZH -> """VPN 模式"""
                        else -> null
                    }
                } ?: """VPN 模式"""

        /** VPN 模式 */
        @Composable fun `VpnMode`(vararg args: Any?) = FYTxtConfig.observe { `VpnMode`.fmt(args) }

        /** Root TUN */
        val `RootTunMode`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Root TUN"""
                        RootMLangTags.ZH -> """Root TUN"""
                        else -> null
                    }
                } ?: """Root TUN"""

        /** Root TUN */
        @Composable
        fun `RootTunMode`(vararg args: Any?) = FYTxtConfig.observe { `RootTunMode`.fmt(args) }

        /** HTTP 系统代理 */
        val `SystemProxy`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """HTTP System Proxy"""
                        RootMLangTags.ZH -> """HTTP 系统代理"""
                        else -> null
                    }
                } ?: """HTTP 系统代理"""

        /** HTTP 系统代理 */
        @Composable
        fun `SystemProxy`(vararg args: Any?) = FYTxtConfig.observe { `SystemProxy`.fmt(args) }
    }

    object `HttpMode` {
        init {
            RootMLangGroups
        }

        /** 本地 HTTP 代理 */
        val `InfoTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Local HTTP Proxy"""
                        RootMLangTags.ZH -> """本地 HTTP 代理"""
                        else -> null
                    }
                } ?: """本地 HTTP 代理"""

        /** 本地 HTTP 代理 */
        @Composable
        fun `InfoTitle`(vararg args: Any?) = FYTxtConfig.observe { `InfoTitle`.fmt(args) }

        /** 启动后在本机随机端口监听 HTTP 代理请求，需手动配置系统或应用代理地址。不创建 VPN 隧道，不拦截全局流量，仅为主动指向该端口的连接提供代理。 */
        val `InfoSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Listens on a random local port for HTTP proxy requests after startup. Requires manual proxy configuration in system or app settings. Does not create a VPN tunnel or intercept global traffic — only proxies connections explicitly directed to the listening port."""
                        RootMLangTags.ZH ->
                            """启动后在本机随机端口监听 HTTP 代理请求，需手动配置系统或应用代理地址。不创建 VPN 隧道，不拦截全局流量，仅为主动指向该端口的连接提供代理。"""
                        else -> null
                    }
                }
                    ?: """启动后在本机随机端口监听 HTTP 代理请求，需手动配置系统或应用代理地址。不创建 VPN 隧道，不拦截全局流量，仅为主动指向该端口的连接提供代理。"""

        /** 启动后在本机随机端口监听 HTTP 代理请求，需手动配置系统或应用代理地址。不创建 VPN 隧道，不拦截全局流量，仅为主动指向该端口的连接提供代理。 */
        @Composable
        fun `InfoSummary`(vararg args: Any?) = FYTxtConfig.observe { `InfoSummary`.fmt(args) }
    }

    object `VpnOptions` {
        init {
            RootMLangGroups
        }

        /** 绕过私有网络 */
        val `BypassPrivateTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Bypass Private Networks"""
                        RootMLangTags.ZH -> """绕过私有网络"""
                        else -> null
                    }
                } ?: """绕过私有网络"""

        /** 绕过私有网络 */
        @Composable
        fun `BypassPrivateTitle`(vararg args: Any?) =
            FYTxtConfig.observe { `BypassPrivateTitle`.fmt(args) }

        /** 绕过私有网络和本地地址 */
        val `BypassPrivateSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Bypass private networks and local addresses"""
                        RootMLangTags.ZH -> """绕过私有网络和本地地址"""
                        else -> null
                    }
                } ?: """绕过私有网络和本地地址"""

        /** 绕过私有网络和本地地址 */
        @Composable
        fun `BypassPrivateSummary`(vararg args: Any?) =
            FYTxtConfig.observe { `BypassPrivateSummary`.fmt(args) }

        /** DNS 劫持 */
        val `DnsHijackTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """DNS Hijack"""
                        RootMLangTags.ZH -> """DNS 劫持"""
                        else -> null
                    }
                } ?: """DNS 劫持"""

        /** DNS 劫持 */
        @Composable
        fun `DnsHijackTitle`(vararg args: Any?) = FYTxtConfig.observe { `DnsHijackTitle`.fmt(args) }

        /** 将所有 DNS 请求重定向到 MonadBox */
        val `DnsHijackSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Redirect all DNS requests to MonadBox"""
                        RootMLangTags.ZH -> """将所有 DNS 请求重定向到 MonadBox"""
                        else -> null
                    }
                } ?: """将所有 DNS 请求重定向到 MonadBox"""

        /** 将所有 DNS 请求重定向到 MonadBox */
        @Composable
        fun `DnsHijackSummary`(vararg args: Any?) =
            FYTxtConfig.observe { `DnsHijackSummary`.fmt(args) }

        /** 允许应用绕过 */
        val `AllowBypassTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Allow App Bypass"""
                        RootMLangTags.ZH -> """允许应用绕过"""
                        else -> null
                    }
                } ?: """允许应用绕过"""

        /** 允许应用绕过 */
        @Composable
        fun `AllowBypassTitle`(vararg args: Any?) =
            FYTxtConfig.observe { `AllowBypassTitle`.fmt(args) }

        /** 允许应用绕过 VPN */
        val `AllowBypassSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Allow apps to bypass VPN"""
                        RootMLangTags.ZH -> """允许应用绕过 VPN"""
                        else -> null
                    }
                } ?: """允许应用绕过 VPN"""

        /** 允许应用绕过 VPN */
        @Composable
        fun `AllowBypassSummary`(vararg args: Any?) =
            FYTxtConfig.observe { `AllowBypassSummary`.fmt(args) }

        /** 运行 IPv6 */
        val `EnableIpv6Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Enable IPv6"""
                        RootMLangTags.ZH -> """运行 IPv6"""
                        else -> null
                    }
                } ?: """运行 IPv6"""

        /** 运行 IPv6 */
        @Composable
        fun `EnableIpv6Title`(vararg args: Any?) =
            FYTxtConfig.observe { `EnableIpv6Title`.fmt(args) }

        /** 允许通过 VPN 路由 IPv6 流量 */
        val `EnableIpv6Summary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Allow IPv6 traffic through VPN"""
                        RootMLangTags.ZH -> """允许通过 VPN 路由 IPv6 流量"""
                        else -> null
                    }
                } ?: """允许通过 VPN 路由 IPv6 流量"""

        /** 允许通过 VPN 路由 IPv6 流量 */
        @Composable
        fun `EnableIpv6Summary`(vararg args: Any?) =
            FYTxtConfig.observe { `EnableIpv6Summary`.fmt(args) }

        /** VPN 系统代理 */
        val `SystemProxyTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """VPN Built-in System Proxy"""
                        RootMLangTags.ZH -> """VPN 系统代理"""
                        else -> null
                    }
                } ?: """VPN 系统代理"""

        /** VPN 系统代理 */
        @Composable
        fun `SystemProxyTitle`(vararg args: Any?) =
            FYTxtConfig.observe { `SystemProxyTitle`.fmt(args) }

        /** 仅在 VPN 模式下，为未走 TUN 的应用设置 HTTP 代理 */
        val `SystemProxySummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Only in VPN mode, set an HTTP proxy for apps outside the TUN path"""
                        RootMLangTags.ZH -> """仅在 VPN 模式下，为未走 TUN 的应用设置 HTTP 代理"""
                        else -> null
                    }
                } ?: """仅在 VPN 模式下，为未走 TUN 的应用设置 HTTP 代理"""

        /** 仅在 VPN 模式下，为未走 TUN 的应用设置 HTTP 代理 */
        @Composable
        fun `SystemProxySummary`(vararg args: Any?) =
            FYTxtConfig.observe { `SystemProxySummary`.fmt(args) }
    }

    object `ProxyOptions` {
        init {
            RootMLangGroups
        }

        /** TUN 协议栈 */
        val `TunStackTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """TUN Stack"""
                        RootMLangTags.ZH -> """TUN 协议栈"""
                        else -> null
                    }
                } ?: """TUN 协议栈"""

        /** TUN 协议栈 */
        @Composable
        fun `TunStackTitle`(vararg args: Any?) = FYTxtConfig.observe { `TunStackTitle`.fmt(args) }

        /** 系统 */
        val `TunStackSystem`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """System"""
                        RootMLangTags.ZH -> """系统"""
                        else -> null
                    }
                } ?: """系统"""

        /** 系统 */
        @Composable
        fun `TunStackSystem`(vararg args: Any?) = FYTxtConfig.observe { `TunStackSystem`.fmt(args) }

        /** GVisor */
        val `TunStackGvisor`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """GVisor"""
                        RootMLangTags.ZH -> """GVisor"""
                        else -> null
                    }
                } ?: """GVisor"""

        /** GVisor */
        @Composable
        fun `TunStackGvisor`(vararg args: Any?) = FYTxtConfig.observe { `TunStackGvisor`.fmt(args) }

        /** 混合 */
        val `TunStackMixed`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Mixed"""
                        RootMLangTags.ZH -> """混合"""
                        else -> null
                    }
                } ?: """混合"""

        /** 混合 */
        @Composable
        fun `TunStackMixed`(vararg args: Any?) = FYTxtConfig.observe { `TunStackMixed`.fmt(args) }

        /** 访问控制模式 */
        val `AccessControlModeTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Access Control Mode"""
                        RootMLangTags.ZH -> """访问控制模式"""
                        else -> null
                    }
                } ?: """访问控制模式"""

        /** 访问控制模式 */
        @Composable
        fun `AccessControlModeTitle`(vararg args: Any?) =
            FYTxtConfig.observe { `AccessControlModeTitle`.fmt(args) }

        /** 允许远程资源使用 HTTP */
        val `AllowNonLocalhostHttpRemoteTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Allow HTTP for Remote Resources"""
                        RootMLangTags.ZH -> """允许远程资源使用 HTTP"""
                        else -> null
                    }
                } ?: """允许远程资源使用 HTTP"""

        /** 允许远程资源使用 HTTP */
        @Composable
        fun `AllowNonLocalhostHttpRemoteTitle`(vararg args: Any?) =
            FYTxtConfig.observe { `AllowNonLocalhostHttpRemoteTitle`.fmt(args) }

        /** 默认仅允许 localhost 使用 HTTP；开启后可允许非 localhost 的 HTTP 远程链接（存在中间人风险） */
        val `AllowNonLocalhostHttpRemoteSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """By default, HTTP is only allowed for localhost; enabling this allows non-localhost HTTP remote URLs (MITM risk)."""
                        RootMLangTags.ZH ->
                            """默认仅允许 localhost 使用 HTTP；开启后可允许非 localhost 的 HTTP 远程链接（存在中间人风险）"""
                        else -> null
                    }
                } ?: """默认仅允许 localhost 使用 HTTP；开启后可允许非 localhost 的 HTTP 远程链接（存在中间人风险）"""

        /** 默认仅允许 localhost 使用 HTTP；开启后可允许非 localhost 的 HTTP 远程链接（存在中间人风险） */
        @Composable
        fun `AllowNonLocalhostHttpRemoteSummary`(vararg args: Any?) =
            FYTxtConfig.observe { `AllowNonLocalhostHttpRemoteSummary`.fmt(args) }

        /** 允许所有 */
        val `AllowAll`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Allow All"""
                        RootMLangTags.ZH -> """允许所有"""
                        else -> null
                    }
                } ?: """允许所有"""

        /** 允许所有 */
        @Composable fun `AllowAll`(vararg args: Any?) = FYTxtConfig.observe { `AllowAll`.fmt(args) }

        /** 允许选择 */
        val `AllowSelected`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Allow Selected"""
                        RootMLangTags.ZH -> """允许选择"""
                        else -> null
                    }
                } ?: """允许选择"""

        /** 允许选择 */
        @Composable
        fun `AllowSelected`(vararg args: Any?) = FYTxtConfig.observe { `AllowSelected`.fmt(args) }

        /** 拒绝选择 */
        val `RejectSelected`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Reject Selected"""
                        RootMLangTags.ZH -> """拒绝选择"""
                        else -> null
                    }
                } ?: """拒绝选择"""

        /** 拒绝选择 */
        @Composable
        fun `RejectSelected`(vararg args: Any?) = FYTxtConfig.observe { `RejectSelected`.fmt(args) }

        /** 管理访问控制列表 */
        val `ManageAccessControlTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Manage Access Control"""
                        RootMLangTags.ZH -> """管理访问控制列表"""
                        else -> null
                    }
                } ?: """管理访问控制列表"""

        /** 管理访问控制列表 */
        @Composable
        fun `ManageAccessControlTitle`(vararg args: Any?) =
            FYTxtConfig.observe { `ManageAccessControlTitle`.fmt(args) }

        /** 为应用和域名配置访问控制规则 */
        val `ManageAccessControlSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN ->
                            """Configure access control rules for apps and domains"""
                        RootMLangTags.ZH -> """为应用和域名配置访问控制规则"""
                        else -> null
                    }
                } ?: """为应用和域名配置访问控制规则"""

        /** 为应用和域名配置访问控制规则 */
        @Composable
        fun `ManageAccessControlSummary`(vararg args: Any?) =
            FYTxtConfig.observe { `ManageAccessControlSummary`.fmt(args) }
    }

    object `RootTun` {
        init {
            RootMLangGroups
        }

        /** 接口名称 */
        val `IfNameTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Interface Name"""
                        RootMLangTags.ZH -> """接口名称"""
                        else -> null
                    }
                } ?: """接口名称"""

        /** 接口名称 */
        @Composable
        fun `IfNameTitle`(vararg args: Any?) = FYTxtConfig.observe { `IfNameTitle`.fmt(args) }

        /** RootTun 创建的虚拟网卡名 */
        val `IfNameSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Virtual interface name created by RootTun"""
                        RootMLangTags.ZH -> """RootTun 创建的虚拟网卡名"""
                        else -> null
                    }
                } ?: """RootTun 创建的虚拟网卡名"""

        /** RootTun 创建的虚拟网卡名 */
        @Composable
        fun `IfNameSummary`(vararg args: Any?) = FYTxtConfig.observe { `IfNameSummary`.fmt(args) }

        /** MTU */
        val `MtuTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """MTU"""
                        RootMLangTags.ZH -> """MTU"""
                        else -> null
                    }
                } ?: """MTU"""

        /** MTU */
        @Composable fun `MtuTitle`(vararg args: Any?) = FYTxtConfig.observe { `MtuTitle`.fmt(args) }

        /** RootTun 链路的最大传输单元 */
        val `MtuSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Maximum transmission unit used by RootTun"""
                        RootMLangTags.ZH -> """RootTun 链路的最大传输单元"""
                        else -> null
                    }
                } ?: """RootTun 链路的最大传输单元"""

        /** RootTun 链路的最大传输单元 */
        @Composable
        fun `MtuSummary`(vararg args: Any?) = FYTxtConfig.observe { `MtuSummary`.fmt(args) }

        /** Android 用户 */
        val `AndroidUsersTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Android Users"""
                        RootMLangTags.ZH -> """Android 用户"""
                        else -> null
                    }
                } ?: """Android 用户"""

        /** Android 用户 */
        @Composable
        fun `AndroidUsersTitle`(vararg args: Any?) =
            FYTxtConfig.observe { `AndroidUsersTitle`.fmt(args) }

        /** 允许使用 RootTun 的 Android 用户 ID */
        val `AndroidUsersSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Android user IDs allowed to use RootTun"""
                        RootMLangTags.ZH -> """允许使用 RootTun 的 Android 用户 ID"""
                        else -> null
                    }
                } ?: """允许使用 RootTun 的 Android 用户 ID"""

        /** 允许使用 RootTun 的 Android 用户 ID */
        @Composable
        fun `AndroidUsersSummary`(vararg args: Any?) =
            FYTxtConfig.observe { `AndroidUsersSummary`.fmt(args) }

        /** 0, 10 */
        val `AndroidUsersPlaceholder`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """0, 10"""
                        RootMLangTags.ZH -> """0, 10"""
                        else -> null
                    }
                } ?: """0, 10"""

        /** 0, 10 */
        @Composable
        fun `AndroidUsersPlaceholder`(vararg args: Any?) =
            FYTxtConfig.observe { `AndroidUsersPlaceholder`.fmt(args) }

        /** 排除路由 */
        val `RouteExcludesTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Route Excludes"""
                        RootMLangTags.ZH -> """排除路由"""
                        else -> null
                    }
                } ?: """排除路由"""

        /** 排除路由 */
        @Composable
        fun `RouteExcludesTitle`(vararg args: Any?) =
            FYTxtConfig.observe { `RouteExcludesTitle`.fmt(args) }

        /** 不纳入 RootTun 路由的地址 */
        val `RouteExcludesSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Addresses excluded from RootTun routes"""
                        RootMLangTags.ZH -> """不纳入 RootTun 路由的地址"""
                        else -> null
                    }
                } ?: """不纳入 RootTun 路由的地址"""

        /** 不纳入 RootTun 路由的地址 */
        @Composable
        fun `RouteExcludesSummary`(vararg args: Any?) =
            FYTxtConfig.observe { `RouteExcludesSummary`.fmt(args) }

        /** 未设置排除地址 */
        val `RouteExcludesPlaceholder`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """No excluded addresses"""
                        RootMLangTags.ZH -> """未设置排除地址"""
                        else -> null
                    }
                } ?: """未设置排除地址"""

        /** 未设置排除地址 */
        @Composable
        fun `RouteExcludesPlaceholder`(vararg args: Any?) =
            FYTxtConfig.observe { `RouteExcludesPlaceholder`.fmt(args) }

        /** 自动路由 */
        val `AutoRouteTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Auto Route"""
                        RootMLangTags.ZH -> """自动路由"""
                        else -> null
                    }
                } ?: """自动路由"""

        /** 自动路由 */
        @Composable
        fun `AutoRouteTitle`(vararg args: Any?) = FYTxtConfig.observe { `AutoRouteTitle`.fmt(args) }

        /** 自动添加转发所需路由 */
        val `AutoRouteSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Automatically add required routes"""
                        RootMLangTags.ZH -> """自动添加转发所需路由"""
                        else -> null
                    }
                } ?: """自动添加转发所需路由"""

        /** 自动添加转发所需路由 */
        @Composable
        fun `AutoRouteSummary`(vararg args: Any?) =
            FYTxtConfig.observe { `AutoRouteSummary`.fmt(args) }

        /** 严格路由 */
        val `StrictRouteTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Strict Route"""
                        RootMLangTags.ZH -> """严格路由"""
                        else -> null
                    }
                } ?: """严格路由"""

        /** 严格路由 */
        @Composable
        fun `StrictRouteTitle`(vararg args: Any?) =
            FYTxtConfig.observe { `StrictRouteTitle`.fmt(args) }

        /** 仅允许命中的流量走 RootTun */
        val `StrictRouteSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Only matched traffic is routed into RootTun"""
                        RootMLangTags.ZH -> """仅允许命中的流量走 RootTun"""
                        else -> null
                    }
                } ?: """仅允许命中的流量走 RootTun"""

        /** 仅允许命中的流量走 RootTun */
        @Composable
        fun `StrictRouteSummary`(vararg args: Any?) =
            FYTxtConfig.observe { `StrictRouteSummary`.fmt(args) }

        /** 自动重定向 */
        val `AutoRedirectTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Auto Redirect"""
                        RootMLangTags.ZH -> """自动重定向"""
                        else -> null
                    }
                } ?: """自动重定向"""

        /** 自动重定向 */
        @Composable
        fun `AutoRedirectTitle`(vararg args: Any?) =
            FYTxtConfig.observe { `AutoRedirectTitle`.fmt(args) }

        /** 自动启用 RootTun 所需的重定向规则 */
        val `AutoRedirectSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Automatically enable required redirect rules"""
                        RootMLangTags.ZH -> """自动启用 RootTun 所需的重定向规则"""
                        else -> null
                    }
                } ?: """自动启用 RootTun 所需的重定向规则"""

        /** 自动启用 RootTun 所需的重定向规则 */
        @Composable
        fun `AutoRedirectSummary`(vararg args: Any?) =
            FYTxtConfig.observe { `AutoRedirectSummary`.fmt(args) }

        /** DNS 模式 */
        val `DnsModeTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """DNS Mode"""
                        RootMLangTags.ZH -> """DNS 模式"""
                        else -> null
                    }
                } ?: """DNS 模式"""

        /** DNS 模式 */
        @Composable
        fun `DnsModeTitle`(vararg args: Any?) = FYTxtConfig.observe { `DnsModeTitle`.fmt(args) }

        /** 选择 RedirHost 或 FakeIP */
        val `DnsModeSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Choose between RedirHost and FakeIP"""
                        RootMLangTags.ZH -> """选择 RedirHost 或 FakeIP"""
                        else -> null
                    }
                } ?: """选择 RedirHost 或 FakeIP"""

        /** 选择 RedirHost 或 FakeIP */
        @Composable
        fun `DnsModeSummary`(vararg args: Any?) = FYTxtConfig.observe { `DnsModeSummary`.fmt(args) }

        /** RedirHost */
        val `DnsModeRedirHost`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """RedirHost"""
                        RootMLangTags.ZH -> """RedirHost"""
                        else -> null
                    }
                } ?: """RedirHost"""

        /** RedirHost */
        @Composable
        fun `DnsModeRedirHost`(vararg args: Any?) =
            FYTxtConfig.observe { `DnsModeRedirHost`.fmt(args) }

        /** FakeIP */
        val `DnsModeFakeIp`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """FakeIP"""
                        RootMLangTags.ZH -> """FakeIP"""
                        else -> null
                    }
                } ?: """FakeIP"""

        /** FakeIP */
        @Composable
        fun `DnsModeFakeIp`(vararg args: Any?) = FYTxtConfig.observe { `DnsModeFakeIp`.fmt(args) }

        /** FakeIP IPv4 地址段 */
        val `FakeIpRangeTitle`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """FakeIP IPv4 Range"""
                        RootMLangTags.ZH -> """FakeIP IPv4 地址段"""
                        else -> null
                    }
                } ?: """FakeIP IPv4 地址段"""

        /** FakeIP IPv4 地址段 */
        @Composable
        fun `FakeIpRangeTitle`(vararg args: Any?) =
            FYTxtConfig.observe { `FakeIpRangeTitle`.fmt(args) }

        /** 仅在 FakeIP 模式下生效 */
        val `FakeIpRangeSummary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Only effective when DNS mode is FakeIP"""
                        RootMLangTags.ZH -> """仅在 FakeIP 模式下生效"""
                        else -> null
                    }
                } ?: """仅在 FakeIP 模式下生效"""

        /** 仅在 FakeIP 模式下生效 */
        @Composable
        fun `FakeIpRangeSummary`(vararg args: Any?) =
            FYTxtConfig.observe { `FakeIpRangeSummary`.fmt(args) }

        /** FakeIP IPv6 地址段 */
        val `FakeIpRange6Title`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """FakeIP IPv6 Range"""
                        RootMLangTags.ZH -> """FakeIP IPv6 地址段"""
                        else -> null
                    }
                } ?: """FakeIP IPv6 地址段"""

        /** FakeIP IPv6 地址段 */
        @Composable
        fun `FakeIpRange6Title`(vararg args: Any?) =
            FYTxtConfig.observe { `FakeIpRange6Title`.fmt(args) }

        /** 仅在 FakeIP 模式下生效 */
        val `FakeIpRange6Summary`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """Only effective when DNS mode is FakeIP"""
                        RootMLangTags.ZH -> """仅在 FakeIP 模式下生效"""
                        else -> null
                    }
                } ?: """仅在 FakeIP 模式下生效"""

        /** 仅在 FakeIP 模式下生效 */
        @Composable
        fun `FakeIpRange6Summary`(vararg args: Any?) =
            FYTxtConfig.observe { `FakeIpRange6Summary`.fmt(args) }
    }

    object `Error` {
        init {
            RootMLangGroups
        }

        /** VPN 权限被拒绝 */
        val `VpnDenied`
            get() =
                FYTxtConfig.activeTags.value.firstNotNullOfOrNull {
                    it as RootMLangTags
                    when (it) {
                        RootMLangTags.EN -> """VPN permission denied"""
                        RootMLangTags.ZH -> """VPN 权限被拒绝"""
                        else -> null
                    }
                } ?: """VPN 权限被拒绝"""

        /** VPN 权限被拒绝 */
        @Composable
        fun `VpnDenied`(vararg args: Any?) = FYTxtConfig.observe { `VpnDenied`.fmt(args) }
    }
}
