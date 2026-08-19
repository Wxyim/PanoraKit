package com.github.nomadboxlab.monadbox.service.runtime.session

import com.github.nomadboxlab.monadbox.core.model.Proxy
import com.github.nomadboxlab.monadbox.core.model.ProxyGroup
import com.github.nomadboxlab.monadbox.service.runtime.entity.Selection
import com.github.nomadboxlab.monadbox.service.runtime.records.SelectionPresentation
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Test

class SelectionPresentationTest {
    private val profileUuid = UUID.randomUUID()

    @Test
    fun persistedNonFirstNodeWinsOverRuntimeDefault() {
        val group = group(now = "node-a")

        val result =
            SelectionPresentation.apply(
                groups = listOf(group),
                selections = listOf(Selection(profileUuid, "AUTO", "node-b")),
            )

        assertEquals("node-b", result.single().now)
    }

    @Test
    fun unresolvedProviderKeepsPersistedNodeVisible() {
        val group = group(now = "node-a", proxies = emptyList())

        val result =
            SelectionPresentation.apply(
                groups = listOf(group),
                selections = listOf(Selection(profileUuid, "AUTO", "node-b")),
            )

        assertEquals("node-b", result.single().now)
    }

    @Test
    fun invalidPersistedNodeDoesNotHideRuntimeState() {
        val group = group(now = "node-a")

        val result =
            SelectionPresentation.apply(
                groups = listOf(group),
                selections = listOf(Selection(profileUuid, "AUTO", "missing")),
            )

        assertEquals("node-a", result.single().now)
    }

    private fun group(
        now: String,
        proxies: List<Proxy> = listOf(proxy("node-a"), proxy("node-b")),
    ): ProxyGroup {
        return ProxyGroup(
            name = "AUTO",
            type = Proxy.Type.Selector,
            proxies = proxies,
            now = now,
        )
    }

    private fun proxy(name: String): Proxy {
        return Proxy(
            name = name,
            title = name,
            subtitle = "",
            type = Proxy.Type.Socks5,
            delay = 0,
        )
    }
}
