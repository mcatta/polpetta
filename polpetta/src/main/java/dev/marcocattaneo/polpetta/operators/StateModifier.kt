package dev.marcocattaneo.polpetta.operators

/**
 * State Modifier used to manipulate the current state inside an Intent
 */
class StateModifier<S : State> private constructor(
    private val state: S
) {

    companion object {
        /**
         * Smart constructor for the StateModifier creation
         * @param state
         */
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
    suspend fun <CurrentState : S> mutate(
        mutator: suspend CurrentState.() -> CurrentState
    ): CurrentState = mutator(getAndCheck())

    /**
     * Change the current state into a new one
     * @param transformer transformer callback
     */
    suspend fun <FromState : S, ToState : S> transform(
        transformer: suspend FromState.() -> ToState
    ): ToState = transformer(getAndCheck())

    /**
     * This function get the current state and validate is value
     */
    @Suppress("UNCHECKED_CAST")
    private fun <CS : S> getAndCheck(): CS {
        val currentState: CS? = state as? CS
        check(currentState != null) { "The current state is different by the specified" }
        return currentState
    }

}