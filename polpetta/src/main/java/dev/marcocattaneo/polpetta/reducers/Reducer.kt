package dev.marcocattaneo.polpetta.reducers

import dev.marcocattaneo.polpetta.operators.State
import dev.marcocattaneo.polpetta.operators.StateModifier

/**
 * The reducer is a pure function that return a new state [S] starting from a [StateModifier]
 */
internal fun interface Reducer<S : State> {
    suspend fun reduce(currentState: StateModifier<S>): S
}

/**
 * This method allows to create an [Reducer]
 */
internal fun <S : State> reducer(block: suspend (StateModifier<S>) -> S) = Reducer(block)