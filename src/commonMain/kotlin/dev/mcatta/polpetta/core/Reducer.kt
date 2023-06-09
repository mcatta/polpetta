package dev.mcatta.polpetta.core

import dev.mcatta.polpetta.operators.State
import dev.mcatta.polpetta.operators.StateModifier

/**
 * The reducer is a pure function that return a new state [S] starting from a [StateModifier]
 */
internal fun interface Reducer<S : State> {
    suspend fun reduce(currentState: StateModifier<S>): S
}

/**
 * This method allows to create an [Reducer]
 *
 * @param block function's body
 * @return Reducer function
 */
internal fun <S : State> reducer(
    block: suspend (StateModifier<S>) -> S
) = Reducer(block)