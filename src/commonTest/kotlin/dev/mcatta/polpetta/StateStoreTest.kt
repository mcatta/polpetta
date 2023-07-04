package dev.mcatta.polpetta

import app.cash.turbine.test
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
internal class StateStoreTest {

    private lateinit var testStore: StateStore<TestAction, TestState, Nothing>
    private val testScope = CoroutineScope(UnconfinedTestDispatcher())

    private class TestStore(scope: CoroutineScope) : StateStore<TestAction, TestState, Nothing>(
        coroutineScope = scope,
        initialState = TestState.Count(0),
        reducerFactory = {
            on<TestAction.Decrease, TestState.Count> { _, stateModifier ->
                stateModifier.mutate { copy(counter = counter - 1) }
            }
            on<TestAction.Increase, TestState.Count> { _, stateModifier ->
                stateModifier.mutate { copy(counter = counter.delayedIncrease()) }
            }
            on<TestAction.Set, TestState.Count> { action, stateModifier ->
                stateModifier.mutate { copy(counter = action.n) }
            }
            on<TestAction.DoNothing, TestState.Count> { _, stateModifier ->
                stateModifier.nothing()
            }
            on<TestAction.ToString, TestState.Count> { _, stateModifier ->
                stateModifier.transform { TestState.Result(counter.toString()) }
            }
            on<TestAction.Increase, TestState.Result> { _, stateModifier ->
                stateModifier.transform {
                    TestState.Count(message.toInt() + 1)
                }
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
            assertEquals(0, (awaitItem() as TestState.Count).counter)
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
            testStore.dispatchAction(TestAction.ToString)
            testStore.dispatchAction(TestAction.Increase)

            // Then
            assertEquals(0, (awaitItem() as TestState.Count).counter)
            assertEquals(1, (awaitItem() as TestState.Count).counter)
            assertEquals(2, (awaitItem() as TestState.Count).counter)
            assertEquals(1, (awaitItem() as TestState.Count).counter)
            assertEquals(42, (awaitItem() as TestState.Count).counter)
            assertEquals("42", (awaitItem() as TestState.Result).message)
            assertEquals(43, (awaitItem() as TestState.Count).counter)
        }
    }

    @Test
    fun `Test unsupported action`() = runTest(context = testScope.coroutineContext) {
        // Given
        val testStore = object : StateStore<TestAction, TestState, TestSideEffect>(
            coroutineScope = testScope,
            initialState = TestState.Count(0),
            reducerFactory = {
                on<TestAction.DoNothing, TestState.Count> { _, stateModifier ->
                    stateModifier.nothing()
                }
            }
        ) {}

        testStore.stateFlow.test {
            // When
            testStore.dispatchAction(TestAction.Increase)

            // Then
            assertEquals(0, (awaitItem() as TestState.Count).counter)
        }
    }

    @Test
    fun `Test Intents execution and side effect`() = runTest(context = testScope.coroutineContext) {
        // Given
        val testStore = object : StateStore<TestAction, TestState, TestSideEffect>(
            coroutineScope = testScope,
            initialState = TestState.Count(0),
            reducerFactory = {
                on<TestAction.Increase, TestState.Count> { _, stateModifier ->
                    sideEffect(TestSideEffect.Toast)
                    stateModifier.mutate { copy(counter = counter + 1) }
                }
            }
        ) {}

        testStore.stateFlow.test {
            // When
            assertNull(testStore.sideEffectFlow.value)
            testStore.dispatchAction(TestAction.Increase)

            // Then
            assertEquals(0, (awaitItem() as TestState.Count).counter)
            assertEquals(1, (awaitItem() as TestState.Count).counter)
            assertNotNull(testStore.sideEffectFlow.value)
        }
    }

}

private suspend fun Int.delayedIncrease(): Int {
    delay(1_000)
    return this + 1
}