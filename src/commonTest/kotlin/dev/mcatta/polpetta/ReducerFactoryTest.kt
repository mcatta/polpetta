package dev.mcatta.polpetta

import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class ReducerFactoryTest {

    private lateinit var reducerFactory: ReducerFactory<TestAction, TestState, TestSideEffect>

    private val sideEffectFactory: SideEffectFactory<TestSideEffect> = SideEffectFactory()

    @BeforeTest
    fun setup() {
        reducerFactory = object : ReducerFactory<TestAction, TestState, TestSideEffect>(sideEffectFactory) {}
    }

    @Test
    fun `Test getting a the Reducer for a defined Action`() = runTest {
        // When
        reducerFactory.on<TestAction.Decrease, TestState.Count> { _, stateModifier ->
            stateModifier.mutate { copy(counter = 42) }
        }

        // Then
        assertNotNull(reducerFactory.getReducer(TestAction.Decrease, TestState.Count(0)))
    }

    @Test
    fun `Test getting a the Reducer for a undefined Action`() = runTest {
        // When
        reducerFactory.on<TestAction.Decrease, TestState.Count> { _, stateModifier ->
            stateModifier.mutate { copy(counter = 42) }
        }

        // Then
        assertNull(reducerFactory.getReducer(TestAction.Increase, TestState.Count(0)))
    }

}