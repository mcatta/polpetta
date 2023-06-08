package dev.marcocattaneo.polpetta.core

import dev.marcocattaneo.polpetta.operators.Action
import dev.marcocattaneo.polpetta.operators.State
import kotlin.reflect.KClass

/**
 * Reducer Factory Builder used to wrap the Reduce function based on an action
 * @param kClassAction action type
 * @param handler reducer
 */
internal class ReducerFactoryBuilder<A : Action, S : State>(
    val kClassAction: KClass<out A>,
    val handler: (A) -> Reducer<S>
) {
    fun build(action: A): Reducer<S> = handler.invoke(action)
}