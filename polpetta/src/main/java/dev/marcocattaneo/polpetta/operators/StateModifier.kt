package dev.marcocattaneo.polpetta.operators

/**
 * State Modifier used to manipulate the current state inside an Intent
 */
class StateModifier<S : State> private constructor(
    private val state: S
) {

    companion object {
        fun <S : State> of(state: S) = StateModifier(state)
    }

    /**
     * Return the state without any changes
     */
    fun nothing(): S = state

    /**
     * Change the current state with new properties
     * @param mutator mutator callback
     */
    suspend fun mutate(mutator: suspend S.() -> S): S = mutator(state)

}