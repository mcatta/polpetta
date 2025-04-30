package dev.mcatta.polpetta.core

import dev.mcatta.polpetta.TestAction
import dev.mcatta.polpetta.TestState
import dev.mcatta.polpetta.operators.StateModifier
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class ReducerFactoryBuilderTest {

    @Test
    fun `Test build returns reducer from handler`() = runTest {
        // Given
        val action = TestAction.Increase
        val reducer = Reducer<TestState.Count> { stateModifier ->
            stateModifier.mutate { copy(counter = counter + 1) }
        }
        val builder = ReducerFactoryBuilder(
            kClassAction = TestAction.Increase::class,
            kClassFromState = TestState.Count::class
        ) { reducer }

        // When
        val result = builder.build(action)

        // Then
        assertNotNull(result)
        assertEquals(reducer, result)
    }

    @Test
    fun `Test build returns reducer that correctly transforms state`() = runTest {
        // Given
        val initialState = TestState.Count(0)
        val action = TestAction.Increase
        val builder = ReducerFactoryBuilder(
            kClassAction = TestAction.Increase::class,
            kClassFromState = TestState.Count::class
        ) {
            Reducer { stateModifier ->
                stateModifier.mutate { copy(counter = counter + 1) }
            }
        }

        // When
        val reducer = builder.build(action)
        val stateModifier = StateModifier.of(initialState)
        val newState = reducer.reduce(stateModifier)

        // Then
        assertNotNull(newState)
        assertEquals(TestState.Count(1), newState)
    }

    @Test
    fun `Test build with parameterized action`() = runTest {
        // Given
        val value = 42
        val action = TestAction.Set(value)
        val builder = ReducerFactoryBuilder(
            kClassAction = TestAction.Set::class,
            kClassFromState = TestState.Count::class
        ) { setAction ->
            Reducer { stateModifier ->
                stateModifier.mutate { copy(counter = setAction.n) }
            }
        }

        // When
        val reducer = builder.build(action)
        val stateModifier = StateModifier.of(TestState.Count(0))
        val newState = reducer.reduce(stateModifier)

        // Then
        assertNotNull(newState)
        assertEquals(TestState.Count(value), newState)
    }
}