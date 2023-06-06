package dev.marcocattaneo.polpetta

import dev.marcocattaneo.polpetta.operators.Action
import dev.marcocattaneo.polpetta.operators.State
import dev.marcocattaneo.polpetta.operators.StateModifier
import dev.marcocattaneo.polpetta.core.Reducer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

/**
 * Store wrapper, his ownership is to persist and mutate the state starting from an action [A]
 *
 * @param reducerFactory functional interface which creates Intent starting from an action
 * @param initialState initial state [S] defined on creation
 * @param coroutineScope coroutine scope
 */
abstract class StateStore<A : Action, S : State>(
    coroutineScope: CoroutineScope,
    initialState: S,
    reducerFactory: ReducerFactory<A, S>.() -> Unit
) {

    private val _reducerFactory: ReducerFactory<A, S> = object: ReducerFactory<A, S>() {}.apply(reducerFactory)
    private val _reducerQueue = Channel<Reducer<S>>()
    private val _stateFlow = MutableStateFlow(initialState)

    val stateFlow: StateFlow<S> = _stateFlow.asStateFlow()

    init {
        _reducerQueue
            .consumeAsFlow()
            .map { reducer -> reducer.reduce(currentState = StateModifier.of(_stateFlow.value)) }
            .onEach { newState -> _stateFlow.value = newState }
            .launchIn(coroutineScope)
    }

    /**
     * Dispatch an action that trigger a Reducer
     * @return true if the Action si defined
     */
    suspend fun dispatchAction(action: A): Boolean = _reducerFactory.getReducer(action)?.let { reducer ->
        _reducerQueue.send(reducer)
    } != null

}