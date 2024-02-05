package dev.mcatta.polpetta

import dev.mcatta.polpetta.core.logger.LogEvent
import dev.mcatta.polpetta.core.logger.StoreLogger
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
    debugMode: Boolean = false,
    reducerFactory: ReducerFactory<A, S, E>.() -> Unit
) {

    private val _logger: StoreLogger<A, S>? = if (debugMode) StoreLogger(this::class.simpleName) else null

    private val _sideEffectFactory = SideEffectFactory<E>()
    private val _reducerFactory: ReducerFactory<A, S, E> = object : ReducerFactory<A, S, E>(
        _sideEffectFactory
    ) {}.apply(reducerFactory)
    private val _reducerQueue = Channel<A>(capacity = Channel.BUFFERED)
    private val _stateFlow = MutableStateFlow(initialState)

    public val stateFlow: StateFlow<S> = _stateFlow.asStateFlow()
    public val sideEffectFlow: StateFlow<E?> = _sideEffectFactory.sideEffectFlow

    init {
        _reducerQueue
            .consumeAsFlow()
            .map { action ->
                val currentState = _stateFlow.value

                reduceState(action, currentState).also { newState ->
                    if (newState != null) {
                        _logger?.log(LogEvent.DEBUG_EV_PROCESSED, action, currentState::class, newState::class)
                    } else {
                        _logger?.log(LogEvent.DEBUG_EV_IGNORED, action, currentState::class)
                    }
                }
            }
            .filterNotNull()
            .onEach { newState -> _stateFlow.value = newState }
            .launchIn(coroutineScope)
    }

    /**
     * Dispatch an action that trigger a Reducer
     *
     * @param action
     */
    public suspend fun dispatchAction(action: A): Unit = action
        .also { _logger?.log(LogEvent.DEBUG_EV_DISPATCH, action) }
        .let { _reducerQueue.send(it) }

    /**
     * Reduce the [currentState] into a new one based on the [action] and the [currentState] itself.
     * In case this pair is not defined into the [ReducerFactory] class this will return null
     *
     * @param action
     * @param currentState
     * @return new state or null
     */
    private suspend fun reduceState(
        action: A,
        currentState: S
    ): S? = _reducerFactory.getReducer(
        action = action,
        fromState = currentState
    )?.reduce(currentState = StateModifier.of(currentState))

}