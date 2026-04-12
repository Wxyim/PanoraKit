package com.github.yumelira.yumebox.service.runtime.session

import com.github.yumelira.yumebox.remote.RuntimeGatewayErrorCode
import com.github.yumelira.yumebox.remote.RuntimeGatewayException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RuntimeSpecTest {
    @Test
    fun toDiagnosticMessage_prefersRootCauseMessageAndPrefixesType() {
        val root = IllegalStateException("native bridge init failed")
        val wrapper = RuntimeException("", root)

        val message = wrapper.toDiagnosticMessage("fallback")

        assertTrue(message.contains("java.lang.IllegalStateException"))
        assertTrue(message.contains("native bridge init failed"))
    }

    @Test
    fun toRuntimeFailure_preservesGatewayCodeFromRuntimeGatewayException() {
        val exception =
            RuntimeGatewayException(
                code = RuntimeGatewayErrorCode.RUNTIME_RESTART_FAILED,
                message = "restart failed",
            )

        val failure =
            exception.toRuntimeFailure(
                fallbackCode = RuntimeGatewayErrorCode.RUNTIME_START_FAILED,
                fallbackMessage = "fallback",
            )

        assertEquals(RuntimeGatewayErrorCode.RUNTIME_RESTART_FAILED, failure.code)
        assertEquals("restart failed", failure.message)
    }

    @Test
    fun toRuntimeFailure_addsStructuredExplanationFields() {
        val exception =
            RuntimeGatewayException(
                code = RuntimeGatewayErrorCode.RUNTIME_CONFIG_COMPILE_FAILED,
                message = "invalid rule",
            )

        val failure =
            exception.toRuntimeFailure(
                fallbackCode = RuntimeGatewayErrorCode.RUNTIME_START_FAILED,
                fallbackMessage = "fallback",
            )

        assertEquals(RuntimeFailureCategory.Configuration, failure.category)
        assertEquals(RuntimeFailurePhase.ConfigurationCompile, failure.phase)
        assertEquals(RuntimeFailureImpact.ConfigurationNotApplied, failure.impact)
        assertEquals(RuntimeFailureRetryability.UserActionRequired, failure.retryability)
        assertTrue(failure.suggestedAction.contains("profile"))
        assertEquals(
            "RUNTIME_CONFIG_COMPILE_FAILED: invalid rule",
            failure.runtimeGatewayMessage("fallback"),
        )
    }
}
