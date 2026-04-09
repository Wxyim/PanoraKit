package com.github.yumelira.yumebox.data.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RemoteContentFetcherTest {
    @Test
    fun fetchText_rejectsInvalidSchemeWithoutNetwork() {
        val error =
            runCatching { RemoteContentFetcher.fetchText("ftp://example.com/file", 1024) }
                .exceptionOrNull() as? RemoteFetchException

        assertEquals(RemoteFetchFailureReason.InvalidScheme, error?.reason)
    }

    @Test
    fun fetchText_rejectsInsecureNonLocalhostHttpWithoutNetwork() {
        val error =
            runCatching { RemoteContentFetcher.fetchText("http://example.com/file", 1024) }
                .exceptionOrNull() as? RemoteFetchException

        assertEquals(RemoteFetchFailureReason.HttpsRequired, error?.reason)
    }

    @Test
    fun extractDecodedFileName_preservesLiteralPlus() {
        val fileName =
            RemoteContentFetcher.extractDecodedFileName(
                "https://example.com/rules%2Balpha+beta.json"
            )

        assertEquals("rules+alpha+beta.json", fileName)
    }

    @Test
    fun extractDecodedFileName_returnsNullForBlankSegment() {
        assertTrue(
            RemoteContentFetcher.extractDecodedFileName("https://example.com/").isNullOrEmpty()
        )
    }
}
