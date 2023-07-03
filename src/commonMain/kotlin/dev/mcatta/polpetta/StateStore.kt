package dev.mcatta.polpetta

import dev.mcatta.polpetta.core.Reducer
import dev.mcatta.polpetta.operators.Action
import dev.mcatta.polpetta.operators.SideEffect
import dev.mcatta.polpetta.operators.State
import dev.mcatta.polpetta.operators.StateModifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

/**
 * Store wrapper, his ownership is to persist and mutate the state starting from an action [A]
 *
 * @param coroutineScope coroutine scope
 * @param initialState initial state [S] defined on creation
 * @param reducerFactory functional interface which creates Intent starting from an action
 */
public abstract class StateStore<A : Action, S : State, E : SideEffect>(
    coroutineScope: CoroutineScope,
    initialState: S,
    reducerFactory: ReducerFactory<A, S, E>.() -> Unit
) {

    private val _sideEffectFactory = SideEffectFactory<E>()
    private val _reducerFactory: ReducerFactory<A, S, E> = object : ReducerFactory<A, S, E>(
        _sideEffectFactory
    ) {}.apply(reducerFactory)
    private val _reducerQueue = Channel<Reducer<S>>()
    private val _stateFlow = MutableStateFlow(initialState)

    public val stateFlow: StateFlow<S> = _stateFlow.asStateFlow()
    public val sideEffectFlow: StateFlow<E?> = _sideEffectFactory.sideEffectFlow

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
    public suspend fun dispatchAction(action: A): Unit = _reducerFactory.getReducer(action).let { reducer ->
        _reducerQueue.send(reducer)
    }

}