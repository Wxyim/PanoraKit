package com.github.yumelira.yumebox.domain.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductChangeTransitionsTest {

    // ── Normal Path: Synced → Modified → Applying → Applied → Synced ──

    @Test
    fun changeTransitions_normalEditAndApplyPath() {
        assertTrue(
            ProductChangeTransitions.canTransition(
                ProductChangeState.Synced,
                ProductChangeState.Modified,
            )
        )
        assertTrue(
            ProductChangeTransitions.canTransition(
                ProductChangeState.Modified,
                ProductChangeState.Applying,
            )
        )
        assertTrue(
            ProductChangeTransitions.canTransition(
                ProductChangeState.Applying,
                ProductChangeState.Applied,
            )
        )
        assertTrue(
            ProductChangeTransitions.canTransition(
                ProductChangeState.Applied,
                ProductChangeState.Synced,
            )
        )
    }

    // ── Failure Path: Applying → Invalid ──

    @Test
    fun changeTransitions_applyingCanFailToInvalid() {
        assertTrue(
            ProductChangeTransitions.canTransition(
                ProductChangeState.Applying,
                ProductChangeState.Invalid,
            )
        )
    }

    @Test
    fun changeTransitions_invalidCanReturnToModified() {
        assertTrue(
            ProductChangeTransitions.canTransition(
                ProductChangeState.Invalid,
                ProductChangeState.Modified,
            )
        )
    }

    // ── Conflict Path: Modified → Conflicted, Applying → Conflicted ──

    @Test
    fun changeTransitions_conflictFromModifiedAndApplying() {
        assertTrue(
            ProductChangeTransitions.canTransition(
                ProductChangeState.Modified,
                ProductChangeState.Conflicted,
            )
        )
        assertTrue(
            ProductChangeTransitions.canTransition(
                ProductChangeState.Applying,
                ProductChangeState.Conflicted,
            )
        )
    }

    @Test
    fun changeTransitions_conflictCanBeResolvedByEditOrRevert() {
        assertTrue(
            ProductChangeTransitions.canTransition(
                ProductChangeState.Conflicted,
                ProductChangeState.Modified,
            )
        )
        assertTrue(
            ProductChangeTransitions.canTransition(
                ProductChangeState.Conflicted,
                ProductChangeState.Reverted,
            )
        )
    }

    // ── Revert Path: Modified → Reverted → Synced ──

    @Test
    fun changeTransitions_revertFromModifiedOrInvalid() {
        assertTrue(
            ProductChangeTransitions.canTransition(
                ProductChangeState.Modified,
                ProductChangeState.Reverted,
            )
        )
        assertTrue(
            ProductChangeTransitions.canTransition(
                ProductChangeState.Invalid,
                ProductChangeState.Reverted,
            )
        )
        assertTrue(
            ProductChangeTransitions.canTransition(
                ProductChangeState.Reverted,
                ProductChangeState.Synced,
            )
        )
    }

    // ── Recovery Path: Reverted → Modified (re-edit after revert) ──

    @Test
    fun changeTransitions_allowReEditAfterRevert() {
        assertTrue(
            ProductChangeTransitions.canTransition(
                ProductChangeState.Reverted,
                ProductChangeState.Modified,
            )
        )
    }

    // ── Interrupt Path: Applied → Modified (immediate re-edit) ──

    @Test
    fun changeTransitions_allowImmediateReEditAfterApply() {
        assertTrue(
            ProductChangeTransitions.canTransition(
                ProductChangeState.Applied,
                ProductChangeState.Modified,
            )
        )
    }

    // ── Invalid Transitions ──

    @Test
    fun changeTransitions_rejectDirectSyncedToApplying() {
        assertFalse(
            ProductChangeTransitions.canTransition(
                ProductChangeState.Synced,
                ProductChangeState.Applying,
            )
        )
    }

    @Test
    fun changeTransitions_rejectDirectSyncedToConflicted() {
        assertFalse(
            ProductChangeTransitions.canTransition(
                ProductChangeState.Synced,
                ProductChangeState.Conflicted,
            )
        )
    }

    @Test
    fun changeTransitions_selfTransitionsAlwaysAllowed() {
        ProductChangeState.entries.forEach { state ->
            assertTrue(
                "Self-transition $state → $state should be allowed",
                ProductChangeTransitions.canTransition(state, state),
            )
        }
    }
}
