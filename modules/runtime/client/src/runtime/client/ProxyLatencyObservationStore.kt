package com.github.yumelira.yumebox.runtime.client

import com.github.yumelira.yumebox.core.model.Proxy
import com.github.yumelira.yumebox.domain.model.ProxyGroupInfo
import com.github.yumelira.yumebox.domain.model.ProxyLatencyState
import com.github.yumelira.yumebox.domain.model.toObservedProxyLatencyState
import com.github.yumelira.yumebox.domain.model.toProxyLatencyState

internal class ProxyLatencyObservationStore {
    private var scopeKey: String? = null
    private val observedStates = LinkedHashMap<String, ProxyLatencyState>()

    fun bind(scopeKey: String?) {
        if (this.scopeKey == scopeKey) return
        this.scopeKey = scopeKey
        observedStates.clear()
    }

    fun clear() {
        scopeKey = null
        observedStates.clear()
    }

    fun record(proxyName: String, delay: Int) {
        recordObservedState(proxyName, delay.toObservedProxyLatencyState())
    }

    fun recordObservedProxies(proxies: List<Proxy>) {
        proxies.forEach { proxy -> record(proxy.name, proxy.delay) }
    }

    fun merge(groups: List<ProxyGroupInfo>): List<ProxyGroupInfo> {
        if (groups.isEmpty()) return groups

        var updatedGroups: MutableList<ProxyGroupInfo>? = null
        groups.forEachIndexed { index, group ->
            val mergedProxies = mergeProxies(group.proxies)
            val nextGroup =
                if (mergedProxies === group.proxies) group else group.copy(proxies = mergedProxies)

            if (updatedGroups == null && nextGroup !== group) {
                updatedGroups = ArrayList(groups.size)
                var head = 0
                while (head < index) {
                    updatedGroups?.add(groups[head])
                    head += 1
                }
            }
            updatedGroups?.add(nextGroup)
        }

        return updatedGroups ?: groups
    }

    private fun recordObservedState(proxyName: String, latencyState: ProxyLatencyState) {
        if (proxyName.isBlank()) return
        if (!latencyState.isObserved) return
        observedStates[proxyName] = latencyState
    }

    private fun mergeProxies(proxies: List<Proxy>): List<Proxy> {
        if (proxies.isEmpty()) return proxies

        var updatedProxies: MutableList<Proxy>? = null
        proxies.forEachIndexed { index, proxy ->
            val nextDelay = mergedDelay(proxy)
            val nextProxy = if (nextDelay == proxy.delay) proxy else proxy.copy(delay = nextDelay)

            if (updatedProxies == null && nextProxy !== proxy) {
                updatedProxies = ArrayList(proxies.size)
                var head = 0
                while (head < index) {
                    updatedProxies?.add(proxies[head])
                    head += 1
                }
            }
            updatedProxies?.add(nextProxy)
        }

        return updatedProxies ?: proxies
    }

    private fun mergedDelay(proxy: Proxy): Int {
        if (proxy.delay.toProxyLatencyState().isObserved) {
            observedStates[proxy.name] = proxy.delay.toProxyLatencyState()
            return proxy.delay
        }

        return when (val observed = observedStates[proxy.name]) {
            is ProxyLatencyState.Available -> observed.delayMs
            ProxyLatencyState.Timeout -> ProxyLatencyState.DISPLAY_TIMEOUT_DELAY_MS
            else -> proxy.delay
        }
    }
}
