package dev.mcatta.polpetta

import dev.mcatta.polpetta.core.Reducer
import dev.mcatta.polpetta.core.ReducerFactoryBuilder
import dev.mcatta.polpetta.core.reducer
import dev.mcatta.polpetta.operators.Action
import dev.mcatta.polpetta.operators.SideEffect
import dev.mcatta.polpetta.operators.State
import dev.mcatta.polpetta.operators.StateModifier
import kotlin.reflect.KClass

/**
 * Intent factory used to create an [Reducer] starting from an action [A]
 */
public abstract class ReducerFactory<A : Action, S : State, E : SideEffect>(
    sideEffectFactory: SideEffectFactory<E>
) {

    private val _reducerDefinition: MutableMap<KClass<out State>, MutableList<ReducerFactoryBuilder<A, S>>> =
        mutableMapOf()
    private val _sideEffectFactory = sideEffectFactory

    /**
     * Return the [Reducer] bond to the action [action] and [FromState]
     *
     * @param action
     * @param fromState
     * @return Reducer if matches the pair
     */
    internal fun <FromState : S> getReducer(
        action: A,
        fromState: FromState
    ): Reducer<S>? =
        _reducerDefinition[fromState::class]
            ?.firstOrNull { item -> item.kClassAction == action::class }
            ?.build(action)

    /**
     * Define a [Reducer]'s body for the defined action [A]
     *
     * @param block
     */
    public inline fun <reified RA : A, reified FromState : S> on(
        noinline block: suspend SideEffectFactory<E>.(RA, StateModifier<FromState>) -> S
    ) {
        on(RA::class, FromState::class, block)
    }

    /**
     * Define a [Reducer]'s body for the defined action with class [KClass]
     *
     * @param kClassAction
     * @param kClassFromState
     * @param block
     */
    public fun <RA : A, FromState : S> on(
        kClassAction: KClass<RA>,
        kClassFromState: KClass<FromState>,
        block: suspend SideEffectFactory<E>.(RA, StateModifier<FromState>) -> S
    ) {
        _reducerDefinition.getOrPut(kClassFromState) { mutableListOf() }.add(
            ReducerFactoryBuilder(
                kClassAction = kClassAction,
                handler = { action ->
                    @Suppress("UNCHECKED_CAST")
                    (reducer { state -> block(_sideEffectFactory, action as RA, state as StateModifier<FromState>) })
                }
            )
        )
    }

}