package com.github.yumelira.yumebox.domain.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductObjectModelTest {
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
    fun lifecycleTransitions_rejectAbruptActiveToIdleTransition() {
        assertFalse(
            ProductLifecycleTransitions.canTransition(
                ProductLifecycleState.Active,
                ProductLifecycleState.Idle,
            )
        )
    }
}
