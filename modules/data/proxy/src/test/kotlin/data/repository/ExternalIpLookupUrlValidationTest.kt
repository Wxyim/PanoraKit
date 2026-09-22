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
 *
 */

package com.github.nomadboxlab.monadbox.data.repository

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExternalIpLookupUrlValidationTest {
    @Test
    fun https_allowsAnyHost() {
        assertTrue(isAllowedExternalIpLookupUrl("https://example.com"))
        assertTrue(isAllowedExternalIpLookupUrl("https://example.com:8443/path"))
        assertTrue(isAllowedExternalIpLookupUrl("https://user:pass@example.com/ip"))
        assertTrue(isAllowedExternalIpLookupUrl("https://[::1]/ip"))
        assertTrue(isAllowedExternalIpLookupUrl("HTTPS://EXAMPLE.COM"))
    }

    @Test
    fun http_allowsOnlyLoopback() {
        assertTrue(isAllowedExternalIpLookupUrl("http://127.0.0.1"))
        assertTrue(isAllowedExternalIpLookupUrl("http://127.0.0.1:8080/ip"))
        assertTrue(isAllowedExternalIpLookupUrl("http://localhost/ip"))
        assertTrue(isAllowedExternalIpLookupUrl("http://[::1]:8080/ip"))
        assertFalse(isAllowedExternalIpLookupUrl("http://example.com/ip"))
        assertFalse(isAllowedExternalIpLookupUrl("http://192.168.1.10/ip"))
    }

    @Test
    fun userinfo_doesNotBreakHostParsing() {
        assertTrue(isAllowedExternalIpLookupUrl("https://user@example.com/ip"))
        assertFalse(isAllowedExternalIpLookupUrl("http://user@example.com/ip"))
        assertFalse(isAllowedExternalIpLookupUrl("http://user@192.168.1.10/ip"))
    }

    @Test
    fun malformedOrUnsupportedUrlsAreRejected() {
        assertFalse(isAllowedExternalIpLookupUrl(""))
        assertFalse(isAllowedExternalIpLookupUrl("example.com"))
        assertFalse(isAllowedExternalIpLookupUrl("ftp://example.com/ip"))
        assertFalse(isAllowedExternalIpLookupUrl("https://"))
        assertFalse(isAllowedExternalIpLookupUrl("https:///path"))
        assertFalse(isAllowedExternalIpLookupUrl("http://"))
    }
}
