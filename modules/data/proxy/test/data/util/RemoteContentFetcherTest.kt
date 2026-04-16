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

package com.github.nomadboxlab.monadbox.data.util

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
