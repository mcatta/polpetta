package dev.mcatta.polpetta

import dev.mcatta.polpetta.core.ReducerFactoryBuilder
import dev.mcatta.polpetta.operators.Action
import dev.mcatta.polpetta.operators.State
import dev.mcatta.polpetta.operators.StateModifier
import dev.mcatta.polpetta.core.Reducer
import dev.mcatta.polpetta.core.reducer
import kotlin.reflect.KClass

/**
 * Intent factory used to create an [Reducer] starting from an action [A]
 */
public abstract class ReducerFactory<A : Action, S : State> {

    private val _reducerDefinition = mutableListOf<ReducerFactoryBuilder<A, S>>()

    /**
     * Return the [Reducer] bond to the action [action]
     * @param action
     * @throws IllegalStateException in case that action doesn't have any Reducers
     */
    internal fun getReducer(action: A): Reducer<S> = _reducerDefinition
        .firstOrNull { item -> item.kClassAction == action::class }
        ?.build(action) ?: throw IllegalStateException("There isn't any Reducer bond to this action $action")

    /**
     * Define a [Reducer]'s body for the defined action [A]
     * @param block
     */
    public inline fun <reified RA : A> on(noinline block: suspend (RA, StateModifier<S>) -> S) {
        on(RA::class, block)
    }

    /**
     * Define a [Reducer]'s body for the defined action with class [KClass]
     * @param kClass
     * @param block
     */
    public fun <RA : A> on(
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