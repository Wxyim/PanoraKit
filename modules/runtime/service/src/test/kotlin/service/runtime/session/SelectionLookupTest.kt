package com.github.nomadboxlab.monadbox.service.runtime.session

import com.github.nomadboxlab.monadbox.service.runtime.entity.Selection
import com.github.nomadboxlab.monadbox.service.runtime.records.SelectionLookup
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SelectionLookupTest {
    private val profileUuid = UUID.randomUUID()

    @Test
    fun indexOfReturnsTrimmedSelectionPerGroup() {
        val index =
            SelectionLookup.indexOf(
                listOf(Selection(profileUuid, "AUTO", " node-b "), Selection(profileUuid, "G2", ""))
            )

        assertEquals("node-b", index["AUTO"])
        assertNull(index["G2"])
    }

    @Test
    fun indexOfMatchesPerGroupFirstOrNullLookup() {
        val selections =
            listOf(
                Selection(profileUuid, "AUTO", "node-a"),
                Selection(profileUuid, "G2", "node-c"),
                Selection(profileUuid, "AUTO", "node-z"),
            )

        val index = SelectionLookup.indexOf(selections)

        // Reference implementation: the lookup the runtime used before the index existed.
        selections
            .map { it.proxy }
            .distinct()
            .forEach { group ->
                val expected =
                    selections
                        .firstOrNull { it.proxy == group }
                        ?.selected
                        ?.trim()
                        ?.takeIf { it.isNotEmpty() }
                assertEquals(expected, index[group])
            }
        assertEquals("node-a", index["AUTO"])
        assertNull(index["missing"])
    }

    @Test
    fun blankFirstEntryIsNotReplacedByLaterDuplicate() {
        val index =
            SelectionLookup.indexOf(
                listOf(Selection(profileUuid, "AUTO", ""), Selection(profileUuid, "AUTO", "node-b"))
            )

        assertNull(index["AUTO"])
    }

    @Test
    fun mergeIntoReplacesExistingEntryInPlace() {
        val target =
            mutableListOf(
                Selection(profileUuid, "AUTO", "node-a"),
                Selection(profileUuid, "G2", "node-c"),
            )

        SelectionLookup.mergeInto(target, listOf(Selection(profileUuid, "AUTO", "node-b")))

        assertEquals(listOf("AUTO", "G2"), target.map { it.proxy })
        assertEquals(listOf("node-b", "node-c"), target.map { it.selected })
    }

    @Test
    fun mergeIntoAppendsOnlyUnknownKeys() {
        val target = mutableListOf(Selection(profileUuid, "AUTO", "node-a"))

        SelectionLookup.mergeInto(
            target,
            listOf(
                Selection(UUID.randomUUID(), "AUTO", "other-profile"),
                Selection(profileUuid, "G9", "node-z"),
            ),
        )

        assertEquals(listOf("AUTO", "AUTO", "G9"), target.map { it.proxy })
        assertEquals(listOf("node-a", "other-profile", "node-z"), target.map { it.selected })
    }

    @Test
    fun mergeIntoMatchesRepeatedSetSelected() {
        val additions =
            listOf(
                Selection(profileUuid, "AUTO", "node-a"),
                Selection(profileUuid, "G2", "node-c"),
                Selection(profileUuid, "AUTO", "node-b"),
                Selection(profileUuid, "G2", "node-c"),
            )

        val batched = mutableListOf(Selection(profileUuid, "AUTO", "stale"))
        SelectionLookup.mergeInto(batched, additions)

        val oneByOne = mutableListOf(Selection(profileUuid, "AUTO", "stale"))
        additions.forEach { SelectionLookup.mergeInto(oneByOne, listOf(it)) }

        assertEquals(oneByOne, batched)
    }
}
