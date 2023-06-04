package dev.marcocattaneo.polpetta

import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

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
            stateModifier.mutate { copy(counter = 42) }
        }

        // Then
        assertNotNull(reducerFactory.getReducer(TestAction.Decrease))
    }

    @Test
    fun `Test getting a the Reducer for a undefined Action`() = runTest {
        // When
        reducerFactory.on<TestAction.Decrease> { _, stateModifier ->
            stateModifier.mutate { copy(counter = 42) }
        }

        // Then
        assertNull(reducerFactory.getReducer(TestAction.Increase))
    }

}