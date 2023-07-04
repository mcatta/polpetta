package dev.mcatta.polpetta.operators

import dev.mcatta.polpetta.TestState
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals


internal class StateModifierTest {

    @Test
    fun `Test nothing operator`() {
        // Given
        val state = TestState.Count(42)

        // When
        val newState = StateModifier.of(state).nothing()

        // Then
        assertEquals(state, newState)
    }

    @Test
    fun `Test mutate operator`() = runTest {
        // Given
        val state = TestState.Count(42)

        // When
        val newState = StateModifier.of(state).mutate { copy(counter = 24) }

        // Then
        assertNotEquals(state, newState)
        assertEquals(24, newState.counter)
    }

    @Test
    fun `Test transform operator`() = runTest {
        // Given
        val state = TestState.Count(42)

        // When
        val newState = StateModifier.of(state).transform {
            TestState.Result(message = counter.toString())
        }

        // Then
        assertEquals("42", newState.message)
    }

}