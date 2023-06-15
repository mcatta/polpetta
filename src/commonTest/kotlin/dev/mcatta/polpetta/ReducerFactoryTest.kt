package dev.mcatta.polpetta

import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

internal class ReducerFactoryTest {

    private lateinit var reducerFactory: ReducerFactory<TestAction, TestState>

    @BeforeTest
    fun setup() {
        reducerFactory = object : ReducerFactory<TestAction, TestState>() {}
    }

    @Test
    fun `Test getting a the Reducer for a defined Action`() = runTest {
        // When
        reducerFactory.on<TestAction.Decrease> { _, stateModifier ->
            stateModifier.mutate<TestState.Count> { copy(counter = 42) }
        }

        // Then
        assertNotNull(reducerFactory.getReducer(TestAction.Decrease))
    }

    @Test
    fun `Test getting a the Reducer for a undefined Action`() = runTest {
        // When
        reducerFactory.on<TestAction.Decrease> { _, stateModifier ->
            stateModifier.mutate<TestState.Count> { copy(counter = 42) }
        }

        // Then
        assertFailsWith<IllegalStateException> { reducerFactory.getReducer(TestAction.Increase) }
    }

}