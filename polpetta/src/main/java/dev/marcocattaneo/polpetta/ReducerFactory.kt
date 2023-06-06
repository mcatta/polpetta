package dev.marcocattaneo.polpetta

import dev.marcocattaneo.polpetta.core.ReducerFactoryBuilder
import dev.marcocattaneo.polpetta.operators.Action
import dev.marcocattaneo.polpetta.operators.State
import dev.marcocattaneo.polpetta.operators.StateModifier
import dev.marcocattaneo.polpetta.core.Reducer
import dev.marcocattaneo.polpetta.core.reducer
import kotlin.reflect.KClass

/**
 * Intent factory used to create an [Reducer] starting from an action [A]
 */
abstract class ReducerFactory<A : Action, S : State> {

    private val _reducerDefinition = mutableListOf<ReducerFactoryBuilder<A, S>>()

    internal fun getReducer(action: A): Reducer<S>? = _reducerDefinition
        .firstOrNull { item -> item.kClassAction.java == action.javaClass }
        ?.build(action)

    /**
     * Define a [Reducer]'s body for the defined action [A]
     * @param block
     */
    inline fun <reified RA : A> on(noinline block: suspend (RA, StateModifier<S>) -> S) {
        on(RA::class, block)
    }

    /**
     * Define a [Reducer]'s body for the defined action with class [KClass]
     * @param kClass
     * @param block
     */
    fun <RA : A> on(
        kClass: KClass<RA>,
        block: suspend (RA, StateModifier<S>) -> S
    ) {
        _reducerDefinition.add(
            ReducerFactoryBuilder(
                kClassAction = kClass,
                handler = { action ->
                    @Suppress("UNCHECKED_CAST")
                    (reducer { state -> block(action as RA, state) })
                }
            )
        )
    }

}