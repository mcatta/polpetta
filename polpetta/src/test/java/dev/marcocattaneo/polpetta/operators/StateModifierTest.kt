package dev.marcocattaneo.polpetta.operators

import dev.marcocattaneo.polpetta.TestState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals


@OptIn(ExperimentalCoroutinesApi::class)
internal class StateModifierTest {

    @Test
    fun `Test nothing operator`() {
        // Given
        val state = TestState(42)

        // When
        val newState = StateModifier.of(state).nothing()

        // Then
        assertEquals(state, newState)
    }

    @Test
    fun `Test mutate operator`() = runTest {
        // Given
        val state = TestState(42)

        // When
        val newState = StateModifier.of(state).mutate { copy(counter = 24) }

        // Then
        assertNotEquals(state, newState)
        assertEquals(24, newState.counter)
    }

}