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

package com.github.nomadboxlab.monadbox.service.runtime.session

import com.github.nomadboxlab.monadbox.remote.RuntimeGatewayErrorCode
import com.github.nomadboxlab.monadbox.remote.RuntimeGatewayException
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
