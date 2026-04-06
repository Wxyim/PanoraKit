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

package com.github.yumelira.yumebox.data.util

import com.github.yumelira.yumebox.core.model.ConfigurationOverride

internal object ConfigurationOverrideMerger {

    fun merge(base: ConfigurationOverride, incoming: ConfigurationOverride): ConfigurationOverride {
        return base.copy(
            httpPort = incoming.httpPort ?: base.httpPort,
            socksPort = incoming.socksPort ?: base.socksPort,
            redirectPort = incoming.redirectPort ?: base.redirectPort,
            tproxyPort = incoming.tproxyPort ?: base.tproxyPort,
            mixedPort = incoming.mixedPort ?: base.mixedPort,
            authentication =
                MergeHelper.mergeListSimple(base.authentication, incoming.authentication),
            authenticationStart =
                MergeHelper.mergeListSimple(base.authenticationStart, incoming.authenticationStart),
            authenticationEnd =
                MergeHelper.mergeListSimple(base.authenticationEnd, incoming.authenticationEnd),
            skipAuthPrefixes =
                MergeHelper.mergeListSimple(base.skipAuthPrefixes, incoming.skipAuthPrefixes),
            skipAuthPrefixesStart =
                MergeHelper.mergeListSimple(
                    base.skipAuthPrefixesStart,
                    incoming.skipAuthPrefixesStart,
                ),
            skipAuthPrefixesEnd =
                MergeHelper.mergeListSimple(base.skipAuthPrefixesEnd, incoming.skipAuthPrefixesEnd),
            lanAllowedIps = MergeHelper.mergeListSimple(base.lanAllowedIps, incoming.lanAllowedIps),
            lanAllowedIpsStart =
                MergeHelper.mergeListSimple(base.lanAllowedIpsStart, incoming.lanAllowedIpsStart),
            lanAllowedIpsEnd =
                MergeHelper.mergeListSimple(base.lanAllowedIpsEnd, incoming.lanAllowedIpsEnd),
            lanDisallowedIps =
                MergeHelper.mergeListSimple(base.lanDisallowedIps, incoming.lanDisallowedIps),
            lanDisallowedIpsStart =
                MergeHelper.mergeListSimple(
                    base.lanDisallowedIpsStart,
                    incoming.lanDisallowedIpsStart,
                ),
            lanDisallowedIpsEnd =
                MergeHelper.mergeListSimple(base.lanDisallowedIpsEnd, incoming.lanDisallowedIpsEnd),
            allowLan = incoming.allowLan ?: base.allowLan,
            bindAddress = incoming.bindAddress ?: base.bindAddress,
            mode = incoming.mode ?: base.mode,
            logLevel = incoming.logLevel ?: base.logLevel,
            ipv6 = incoming.ipv6 ?: base.ipv6,
            externalController = incoming.externalController ?: base.externalController,
            externalControllerTLS = incoming.externalControllerTLS ?: base.externalControllerTLS,
            externalDohServer = incoming.externalDohServer ?: base.externalDohServer,
            externalControllerCors = mergeCors(base.externalControllerCors, incoming),
            externalControllerCorsForce =
                incoming.externalControllerCorsForce ?: base.externalControllerCorsForce,
            secret = incoming.secret ?: base.secret,
            hosts =
                MergeHelper.mergeMap(
                    base = base.hosts,
                    replace = incoming.hosts,
                    merge = incoming.hostsMerge,
                ),
            hostsMerge =
                MergeHelper.mergeMap(
                    base = base.hostsMerge,
                    replace = incoming.hostsMerge,
                    merge = null,
                ),
            unifiedDelay = incoming.unifiedDelay ?: base.unifiedDelay,
            geodataMode = incoming.geodataMode ?: base.geodataMode,
            tcpConcurrent = incoming.tcpConcurrent ?: base.tcpConcurrent,
            findProcessMode = incoming.findProcessMode ?: base.findProcessMode,
            keepAliveInterval = incoming.keepAliveInterval ?: base.keepAliveInterval,
            keepAliveIdle = incoming.keepAliveIdle ?: base.keepAliveIdle,
            interfaceName = incoming.interfaceName ?: base.interfaceName,
            routingMark = incoming.routingMark ?: base.routingMark,
            geositeMatcher = incoming.geositeMatcher ?: base.geositeMatcher,
            globalClientFingerprint =
                incoming.globalClientFingerprint ?: base.globalClientFingerprint,
            geoAutoUpdate = incoming.geoAutoUpdate ?: base.geoAutoUpdate,
            geoUpdateInterval = incoming.geoUpdateInterval ?: base.geoUpdateInterval,
            ruleProviders =
                MergeHelper.mergeProviderMap(
                    base = base.ruleProviders,
                    replace = incoming.ruleProviders,
                    merge = incoming.ruleProvidersMerge,
                ),
            ruleProvidersMerge =
                MergeHelper.mergeProviderMap(
                    base = base.ruleProvidersMerge,
                    replace = incoming.ruleProvidersMerge,
                    merge = null,
                ),
            proxyGroups =
                MergeHelper.mergeProxyGroupListSimple(base.proxyGroups, incoming.proxyGroups),
            proxyGroupsStart =
                MergeHelper.mergeProxyGroupListSimple(
                    base.proxyGroupsStart,
                    incoming.proxyGroupsStart,
                ),
            proxyGroupsEnd =
                MergeHelper.mergeProxyGroupListSimple(base.proxyGroupsEnd, incoming.proxyGroupsEnd),
            rules = MergeHelper.mergeListSimple(base.rules, incoming.rules),
            rulesStart = MergeHelper.mergeListSimple(base.rulesStart, incoming.rulesStart),
            rulesEnd = MergeHelper.mergeListSimple(base.rulesEnd, incoming.rulesEnd),
            subRules =
                MergeHelper.mergeMap(
                    base = base.subRules,
                    replace = incoming.subRules,
                    merge = incoming.subRulesMerge,
                ),
            subRulesMerge =
                MergeHelper.mergeMap(
                    base = base.subRulesMerge,
                    replace = incoming.subRulesMerge,
                    merge = null,
                ),
            proxies = MergeHelper.mergeProxyListSimple(base.proxies, incoming.proxies),
            proxiesStart =
                MergeHelper.mergeProxyListSimple(base.proxiesStart, incoming.proxiesStart),
            proxiesEnd = MergeHelper.mergeProxyListSimple(base.proxiesEnd, incoming.proxiesEnd),
            proxyProviders =
                MergeHelper.mergeProviderMap(
                    base = base.proxyProviders,
                    replace = incoming.proxyProviders,
                    merge = incoming.proxyProvidersMerge,
                ),
            proxyProvidersMerge =
                MergeHelper.mergeProviderMap(
                    base = base.proxyProvidersMerge,
                    replace = incoming.proxyProvidersMerge,
                    merge = null,
                ),
            dns = mergeDns(base.dns, incoming),
            dnsForce = incoming.dnsForce ?: base.dnsForce,
            app = mergeApp(base.app, incoming.app),
            profile = mergeProfile(base.profile, incoming.profile),
            tun = mergeTun(base.tun, incoming.tun),
            sniffer = mergeSniffer(base.sniffer, incoming),
            snifferForce = incoming.snifferForce ?: base.snifferForce,
            geoxurl = mergeGeoX(base.geoxurl, incoming),
            geoxurlForce = incoming.geoxurlForce ?: base.geoxurlForce,
        )
    }

    private fun mergeDns(
        base: ConfigurationOverride.Dns,
        incoming: ConfigurationOverride,
    ): ConfigurationOverride.Dns {
        incoming.dnsForce?.let {
            return it
        }

        val incomingDns = incoming.dns
        return base.copy(
            enable = incomingDns.enable ?: base.enable,
            cacheAlgorithm = incomingDns.cacheAlgorithm ?: base.cacheAlgorithm,
            preferH3 = incomingDns.preferH3 ?: base.preferH3,
            listen = incomingDns.listen ?: base.listen,
            ipv6 = incomingDns.ipv6 ?: base.ipv6,
            ipv6Timeout = incomingDns.ipv6Timeout ?: base.ipv6Timeout,
            useHosts = incomingDns.useHosts ?: base.useHosts,
            useSystemHosts = incomingDns.useSystemHosts ?: base.useSystemHosts,
            respectRules = incomingDns.respectRules ?: base.respectRules,
            enhancedMode = incomingDns.enhancedMode ?: base.enhancedMode,
            fakeIpRange = incomingDns.fakeIpRange ?: base.fakeIpRange,
            fakeIpRange6 = incomingDns.fakeIpRange6 ?: base.fakeIpRange6,
            fakeIPFilterMode = incomingDns.fakeIPFilterMode ?: base.fakeIPFilterMode,
            fakeIpTtl = incomingDns.fakeIpTtl ?: base.fakeIpTtl,
            cacheMaxSize = incomingDns.cacheMaxSize ?: base.cacheMaxSize,
            directFollowPolicy = incomingDns.directFollowPolicy ?: base.directFollowPolicy,
            nameServer = MergeHelper.mergeListSimple(base.nameServer, incomingDns.nameServer),
            nameServerStart =
                MergeHelper.mergeListSimple(base.nameServerStart, incomingDns.nameServerStart),
            nameServerEnd =
                MergeHelper.mergeListSimple(base.nameServerEnd, incomingDns.nameServerEnd),
            fallback = MergeHelper.mergeListSimple(base.fallback, incomingDns.fallback),
            fallbackStart =
                MergeHelper.mergeListSimple(base.fallbackStart, incomingDns.fallbackStart),
            fallbackEnd = MergeHelper.mergeListSimple(base.fallbackEnd, incomingDns.fallbackEnd),
            defaultServer =
                MergeHelper.mergeListSimple(base.defaultServer, incomingDns.defaultServer),
            defaultServerStart =
                MergeHelper.mergeListSimple(
                    base.defaultServerStart,
                    incomingDns.defaultServerStart,
                ),
            defaultServerEnd =
                MergeHelper.mergeListSimple(base.defaultServerEnd, incomingDns.defaultServerEnd),
            fakeIpFilter = MergeHelper.mergeListSimple(base.fakeIpFilter, incomingDns.fakeIpFilter),
            fakeIpFilterStart =
                MergeHelper.mergeListSimple(base.fakeIpFilterStart, incomingDns.fakeIpFilterStart),
            fakeIpFilterEnd =
                MergeHelper.mergeListSimple(base.fakeIpFilterEnd, incomingDns.fakeIpFilterEnd),
            proxyServerNameserver =
                MergeHelper.mergeListSimple(
                    base.proxyServerNameserver,
                    incomingDns.proxyServerNameserver,
                ),
            proxyServerNameserverStart =
                MergeHelper.mergeListSimple(
                    base.proxyServerNameserverStart,
                    incomingDns.proxyServerNameserverStart,
                ),
            proxyServerNameserverEnd =
                MergeHelper.mergeListSimple(
                    base.proxyServerNameserverEnd,
                    incomingDns.proxyServerNameserverEnd,
                ),
            directNameserver =
                MergeHelper.mergeListSimple(base.directNameserver, incomingDns.directNameserver),
            directNameserverStart =
                MergeHelper.mergeListSimple(
                    base.directNameserverStart,
                    incomingDns.directNameserverStart,
                ),
            directNameserverEnd =
                MergeHelper.mergeListSimple(
                    base.directNameserverEnd,
                    incomingDns.directNameserverEnd,
                ),
            nameserverPolicy =
                MergeHelper.mergeMap(
                    base = base.nameserverPolicy,
                    replace = incomingDns.nameserverPolicy,
                    merge = incomingDns.nameserverPolicyMerge,
                ),
            nameserverPolicyMerge =
                MergeHelper.mergeMap(
                    base = base.nameserverPolicyMerge,
                    replace = incomingDns.nameserverPolicyMerge,
                    merge = null,
                ),
            proxyServerNameserverPolicy =
                MergeHelper.mergeMap(
                    base = base.proxyServerNameserverPolicy,
                    replace = incomingDns.proxyServerNameserverPolicy,
                    merge = incomingDns.proxyServerNameserverPolicyMerge,
                ),
            proxyServerNameserverPolicyMerge =
                MergeHelper.mergeMap(
                    base = base.proxyServerNameserverPolicyMerge,
                    replace = incomingDns.proxyServerNameserverPolicyMerge,
                    merge = null,
                ),
            fallbackFilter = mergeFallbackFilter(base.fallbackFilter, incomingDns),
            fallbackFilterForce = incomingDns.fallbackFilterForce ?: base.fallbackFilterForce,
        )
    }

    private fun mergeFallbackFilter(
        base: ConfigurationOverride.DnsFallbackFilter,
        incoming: ConfigurationOverride.Dns,
    ): ConfigurationOverride.DnsFallbackFilter {
        incoming.fallbackFilterForce?.let {
            return it
        }

        val incomingFilter = incoming.fallbackFilter
        return base.copy(
            geoIp = incomingFilter.geoIp ?: base.geoIp,
            geoIpCode = incomingFilter.geoIpCode ?: base.geoIpCode,
            ipcidr = MergeHelper.mergeListSimple(base.ipcidr, incomingFilter.ipcidr),
            ipcidrStart = MergeHelper.mergeListSimple(base.ipcidrStart, incomingFilter.ipcidrStart),
            ipcidrEnd = MergeHelper.mergeListSimple(base.ipcidrEnd, incomingFilter.ipcidrEnd),
            geosite = MergeHelper.mergeListSimple(base.geosite, incomingFilter.geosite),
            geositeStart =
                MergeHelper.mergeListSimple(base.geositeStart, incomingFilter.geositeStart),
            geositeEnd = MergeHelper.mergeListSimple(base.geositeEnd, incomingFilter.geositeEnd),
            domain = MergeHelper.mergeListSimple(base.domain, incomingFilter.domain),
            domainStart = MergeHelper.mergeListSimple(base.domainStart, incomingFilter.domainStart),
            domainEnd = MergeHelper.mergeListSimple(base.domainEnd, incomingFilter.domainEnd),
        )
    }

    private fun mergeApp(
        base: ConfigurationOverride.App,
        incoming: ConfigurationOverride.App,
    ): ConfigurationOverride.App {
        return base.copy(appendSystemDns = incoming.appendSystemDns ?: base.appendSystemDns)
    }

    private fun mergeProfile(
        base: ConfigurationOverride.Profile,
        incoming: ConfigurationOverride.Profile,
    ): ConfigurationOverride.Profile {
        return base.copy(
            storeSelected = incoming.storeSelected ?: base.storeSelected,
            storeFakeIp = incoming.storeFakeIp ?: base.storeFakeIp,
        )
    }

    private fun mergeCors(
        base: ConfigurationOverride.ExternalControllerCors,
        incoming: ConfigurationOverride,
    ): ConfigurationOverride.ExternalControllerCors {
        incoming.externalControllerCorsForce?.let {
            return it
        }

        val incomingCors = incoming.externalControllerCors
        return base.copy(
            allowOrigins =
                MergeHelper.mergeListSimple(base.allowOrigins, incomingCors.allowOrigins),
            allowOriginsStart =
                MergeHelper.mergeListSimple(base.allowOriginsStart, incomingCors.allowOriginsStart),
            allowOriginsEnd =
                MergeHelper.mergeListSimple(base.allowOriginsEnd, incomingCors.allowOriginsEnd),
            allowPrivateNetwork = incomingCors.allowPrivateNetwork ?: base.allowPrivateNetwork,
        )
    }

    private fun mergeGeoX(
        base: ConfigurationOverride.GeoXUrl,
        incoming: ConfigurationOverride,
    ): ConfigurationOverride.GeoXUrl {
        incoming.geoxurlForce?.let {
            return it
        }

        val incomingGeoX = incoming.geoxurl
        return base.copy(
            geoip = incomingGeoX.geoip ?: base.geoip,
            mmdb = incomingGeoX.mmdb ?: base.mmdb,
            geosite = incomingGeoX.geosite ?: base.geosite,
        )
    }

    private fun mergeTun(
        base: ConfigurationOverride.Tun,
        incoming: ConfigurationOverride.Tun,
    ): ConfigurationOverride.Tun {
        return base.copy(
            enable = incoming.enable ?: base.enable,
            stack = incoming.stack ?: base.stack,
            dnsHijack = MergeHelper.mergeListSimple(base.dnsHijack, incoming.dnsHijack),
            dnsHijackStart =
                MergeHelper.mergeListSimple(base.dnsHijackStart, incoming.dnsHijackStart),
            dnsHijackEnd = MergeHelper.mergeListSimple(base.dnsHijackEnd, incoming.dnsHijackEnd),
            autoRoute = incoming.autoRoute ?: base.autoRoute,
            autoDetectInterface = incoming.autoDetectInterface ?: base.autoDetectInterface,
            autoRedirect = incoming.autoRedirect ?: base.autoRedirect,
            mtu = incoming.mtu ?: base.mtu,
            gso = incoming.gso ?: base.gso,
            gsoMaxSize = incoming.gsoMaxSize ?: base.gsoMaxSize,
            strictRoute = incoming.strictRoute ?: base.strictRoute,
            disableIcmpForwarding = incoming.disableIcmpForwarding ?: base.disableIcmpForwarding,
            routeAddress = MergeHelper.mergeListSimple(base.routeAddress, incoming.routeAddress),
            routeAddressStart =
                MergeHelper.mergeListSimple(base.routeAddressStart, incoming.routeAddressStart),
            routeAddressEnd =
                MergeHelper.mergeListSimple(base.routeAddressEnd, incoming.routeAddressEnd),
            routeExcludeAddress =
                MergeHelper.mergeListSimple(base.routeExcludeAddress, incoming.routeExcludeAddress),
            routeExcludeAddressStart =
                MergeHelper.mergeListSimple(
                    base.routeExcludeAddressStart,
                    incoming.routeExcludeAddressStart,
                ),
            routeExcludeAddressEnd =
                MergeHelper.mergeListSimple(
                    base.routeExcludeAddressEnd,
                    incoming.routeExcludeAddressEnd,
                ),
            endpointIndependentNat = incoming.endpointIndependentNat ?: base.endpointIndependentNat,
            includePackage =
                MergeHelper.mergeListSimple(base.includePackage, incoming.includePackage),
            includePackageStart =
                MergeHelper.mergeListSimple(base.includePackageStart, incoming.includePackageStart),
            includePackageEnd =
                MergeHelper.mergeListSimple(base.includePackageEnd, incoming.includePackageEnd),
            excludePackage =
                MergeHelper.mergeListSimple(base.excludePackage, incoming.excludePackage),
            excludePackageStart =
                MergeHelper.mergeListSimple(base.excludePackageStart, incoming.excludePackageStart),
            excludePackageEnd =
                MergeHelper.mergeListSimple(base.excludePackageEnd, incoming.excludePackageEnd),
        )
    }

    private fun mergeSniffer(
        base: ConfigurationOverride.Sniffer,
        incoming: ConfigurationOverride,
    ): ConfigurationOverride.Sniffer {
        incoming.snifferForce?.let {
            return it
        }

        val incomingSniffer = incoming.sniffer
        return base.copy(
            enable = incomingSniffer.enable ?: base.enable,
            sniff = mergeSniff(base.sniff, incomingSniffer),
            forceDnsMapping = incomingSniffer.forceDnsMapping ?: base.forceDnsMapping,
            parsePureIp = incomingSniffer.parsePureIp ?: base.parsePureIp,
            overrideDestination = incomingSniffer.overrideDestination ?: base.overrideDestination,
            forceDomain =
                MergeHelper.mergeListSimple(base.forceDomain, incomingSniffer.forceDomain),
            forceDomainStart =
                MergeHelper.mergeListSimple(
                    base.forceDomainStart,
                    incomingSniffer.forceDomainStart,
                ),
            forceDomainEnd =
                MergeHelper.mergeListSimple(base.forceDomainEnd, incomingSniffer.forceDomainEnd),
            skipDomain = MergeHelper.mergeListSimple(base.skipDomain, incomingSniffer.skipDomain),
            skipDomainStart =
                MergeHelper.mergeListSimple(base.skipDomainStart, incomingSniffer.skipDomainStart),
            skipDomainEnd =
                MergeHelper.mergeListSimple(base.skipDomainEnd, incomingSniffer.skipDomainEnd),
            skipSrcAddress =
                MergeHelper.mergeListSimple(base.skipSrcAddress, incomingSniffer.skipSrcAddress),
            skipSrcAddressStart =
                MergeHelper.mergeListSimple(
                    base.skipSrcAddressStart,
                    incomingSniffer.skipSrcAddressStart,
                ),
            skipSrcAddressEnd =
                MergeHelper.mergeListSimple(
                    base.skipSrcAddressEnd,
                    incomingSniffer.skipSrcAddressEnd,
                ),
            skipDstAddress =
                MergeHelper.mergeListSimple(base.skipDstAddress, incomingSniffer.skipDstAddress),
            skipDstAddressStart =
                MergeHelper.mergeListSimple(
                    base.skipDstAddressStart,
                    incomingSniffer.skipDstAddressStart,
                ),
            skipDstAddressEnd =
                MergeHelper.mergeListSimple(
                    base.skipDstAddressEnd,
                    incomingSniffer.skipDstAddressEnd,
                ),
        )
    }

    private fun mergeSniff(
        base: ConfigurationOverride.Sniff,
        incoming: ConfigurationOverride.Sniffer,
    ): ConfigurationOverride.Sniff {
        incoming.sniffForce?.let {
            return it
        }

        val incomingSniff = incoming.sniff
        return base.copy(
            http = mergeProtocol(base.http, incomingSniff.http),
            tls = mergeProtocol(base.tls, incomingSniff.tls),
            quic = mergeProtocol(base.quic, incomingSniff.quic),
        )
    }

    private fun mergeProtocol(
        base: ConfigurationOverride.ProtocolConfig,
        incoming: ConfigurationOverride.ProtocolConfig,
    ): ConfigurationOverride.ProtocolConfig {
        return base.copy(
            ports = MergeHelper.mergeListSimple(base.ports, incoming.ports),
            portsStart = MergeHelper.mergeListSimple(base.portsStart, incoming.portsStart),
            portsEnd = MergeHelper.mergeListSimple(base.portsEnd, incoming.portsEnd),
            overrideDestination = incoming.overrideDestination ?: base.overrideDestination,
        )
    }
}
