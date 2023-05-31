package dev.marcocattaneo.polpetta

import app.cash.turbine.test
import dev.marcocattaneo.polpetta.operators.Action
import dev.marcocattaneo.polpetta.operators.State
import dev.marcocattaneo.polpetta.reducers.reducer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
internal class StateStoreTest {

    private lateinit var testStore: StateStore<TestAction, TestState>
    private val testScope = CoroutineScope(UnconfinedTestDispatcher())

    private class TestStore(scope: CoroutineScope) : StateStore<TestAction, TestState>(
        coroutineScope = scope,
        initialState = TestState(0),
        reducerFactory =  {
            on<TestAction.Decrease> {
                reducer { state -> state.mutate { copy(counter = counter - 1) } }
            }
            on<TestAction.Increase> {
                reducer { state -> state.mutate { copy(counter = counter.delayedIncrease()) } }
            }
            on<TestAction.Set> { action ->
                reducer { state -> state.mutate { copy(counter = action.n) } }
            }
            on<TestAction.DoNothing> {
                reducer { state -> state.nothing() }
            }
        }
    )

    @BeforeTest
    fun setup() {
        testStore = TestStore(testScope)
    }

    @Test
    fun `Test initial state`() = runTest(context = testScope.coroutineContext) {
        testStore.stateFlow.test {
            // Then
            assertEquals(0, awaitItem().counter)
        }
    }

    @Test
    fun `Test Intents execution`() = runTest(context = testScope.coroutineContext) {
        testStore.stateFlow.test {
            // When
            testStore.dispatchAction(TestAction.Increase)
            testStore.dispatchAction(TestAction.Increase)
            testStore.dispatchAction(TestAction.DoNothing)
            testStore.dispatchAction(TestAction.Decrease)
            testStore.dispatchAction(TestAction.Set(42))

            // Then
            assertEquals(0, awaitItem().counter)
            assertEquals(1, awaitItem().counter)
            assertEquals(2, awaitItem().counter)
            assertEquals(1, awaitItem().counter)
            assertEquals(42, awaitItem().counter)
        }
    }

}

private suspend fun Int.delayedIncrease(): Int {
    delay(1_000)
    return this + 1
}