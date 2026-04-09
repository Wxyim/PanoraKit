package com.github.yumelira.yumebox.runtime.client

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ControllerSecurityPolicyTest {
    @Test
    fun acceptsLoopbackHosts() {
        assertTrue(isLoopbackControllerHost("localhost"))
        assertTrue(isLoopbackControllerHost("127.0.0.1"))
        assertTrue(isLoopbackControllerHost("[::1]"))
    }

    @Test
    fun rejectsNonLoopbackHosts() {
        assertFalse(isLoopbackControllerHost("192.168.1.1"))
        assertFalse(isLoopbackControllerHost("example.com"))
        assertFalse(isLoopbackControllerHost("::ffff:127.0.0.1"))
    }
}
