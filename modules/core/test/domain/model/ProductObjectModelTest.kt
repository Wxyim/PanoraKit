package com.github.yumelira.yumebox.domain.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductObjectModelTest {

    // ── Lifecycle: Normal Path ──

    @Test
    fun lifecycleTransitions_allowNormalRuntimePath() {
        assertTrue(
            ProductLifecycleTransitions.canTransition(
                ProductLifecycleState.Idle,
                ProductLifecycleState.Preparing,
            )
        )
        assertTrue(
            ProductLifecycleTransitions.canTransition(
                ProductLifecycleState.Preparing,
                ProductLifecycleState.Active,
            )
        )
        assertTrue(
            ProductLifecycleTransitions.canTransition(
                ProductLifecycleState.Active,
                ProductLifecycleState.Stopping,
            )
        )
        assertTrue(
            ProductLifecycleTransitions.canTransition(
                ProductLifecycleState.Stopping,
                ProductLifecycleState.Stopped,
            )
        )
    }

    // ── Lifecycle: Failure Path ──

    @Test
    fun lifecycleTransitions_allowFailureAndRecoveryPath() {
        assertTrue(
            ProductLifecycleTransitions.canTransition(
                ProductLifecycleState.Preparing,
                ProductLifecycleState.Failed,
            )
        )
        assertTrue(
            ProductLifecycleTransitions.canTransition(
                ProductLifecycleState.Failed,
                ProductLifecycleState.Preparing,
            )
        )
    }

    @Test
    fun lifecycleTransitions_allowFailureFromAnyActivePhase() {
        assertTrue(
            ProductLifecycleTransitions.canTransition(
                ProductLifecycleState.Active,
                ProductLifecycleState.Failed,
            )
        )
        assertTrue(
            ProductLifecycleTransitions.canTransition(
                ProductLifecycleState.Degraded,
                ProductLifecycleState.Failed,
            )
        )
        assertTrue(
            ProductLifecycleTransitions.canTransition(
                ProductLifecycleState.Stopping,
                ProductLifecycleState.Failed,
            )
        )
    }

    // ── Lifecycle: Interrupt Path ──

    @Test
    fun lifecycleTransitions_allowInterruptFromActiveToStopping() {
        assertTrue(
            ProductLifecycleTransitions.canTransition(
                ProductLifecycleState.Active,
                ProductLifecycleState.Stopping,
            )
        )
        assertTrue(
            ProductLifecycleTransitions.canTransition(
                ProductLifecycleState.Preparing,
                ProductLifecycleState.Stopping,
            )
        )
    }

    // ── Lifecycle: Retry Path ──

    @Test
    fun lifecycleTransitions_allowRetryFromFailedAndStopped() {
        assertTrue(
            ProductLifecycleTransitions.canTransition(
                ProductLifecycleState.Failed,
                ProductLifecycleState.Preparing,
            )
        )
        assertTrue(
            ProductLifecycleTransitions.canTransition(
                ProductLifecycleState.Stopped,
                ProductLifecycleState.Preparing,
            )
        )
        assertTrue(
            ProductLifecycleTransitions.canTransition(
                ProductLifecycleState.Stopped,
                ProductLifecycleState.Idle,
            )
        )
    }

    // ── Lifecycle: Recovery Path (Degraded → Active) ──

    @Test
    fun lifecycleTransitions_allowRecoveryFromDegraded() {
        assertTrue(
            ProductLifecycleTransitions.canTransition(
                ProductLifecycleState.Degraded,
                ProductLifecycleState.Active,
            )
        )
    }

    // ── Lifecycle: Conflict (Invalid Transitions) ──

    @Test
    fun lifecycleTransitions_rejectAbruptActiveToIdleTransition() {
        assertFalse(
            ProductLifecycleTransitions.canTransition(
                ProductLifecycleState.Active,
                ProductLifecycleState.Idle,
            )
        )
    }

    @Test
    fun lifecycleTransitions_rejectSkippingStoppingPhase() {
        assertFalse(
            ProductLifecycleTransitions.canTransition(
                ProductLifecycleState.Active,
                ProductLifecycleState.Stopped,
            )
        )
        assertFalse(
            ProductLifecycleTransitions.canTransition(
                ProductLifecycleState.Degraded,
                ProductLifecycleState.Stopped,
            )
        )
    }

    @Test
    fun lifecycleTransitions_selfTransitionsAlwaysAllowed() {
        ProductLifecycleState.entries.forEach { state ->
            assertTrue(
                "Self-transition $state → $state should be allowed",
                ProductLifecycleTransitions.canTransition(state, state),
            )
        }
    }
}
