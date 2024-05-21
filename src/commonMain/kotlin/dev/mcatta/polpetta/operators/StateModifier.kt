package dev.mcatta.polpetta.operators

/**
 * State Modifier used to manipulate the current state inside an Intent
 */
public class StateModifier<FromState : State> private constructor(
    private val state: FromState
) {

    public companion object {
        /**
         * Smart constructor for the StateModifier creation
         *
         * @param state
         */
        public fun <FromState : State> of(
            state: FromState
        ): StateModifier<FromState> = StateModifier(state)
    }

    /**
     * Return the state without any changes
     */
    public fun nothing(): FromState = state

    /**
     * Change the current state with new properties
     *
     * @param mutator mutator callback
     * @return the mutated [FromState]
     */
    public suspend fun mutate(
        mutator: suspend FromState.() -> FromState
    ): FromState = mutator(getAndCheck())

    /**
     * Change the current state into a new one
     *
     * @param transformer transformer callback
     * @return a new state that inherited from [State]
     */
    public suspend fun <ToState : State> transform(
        transformer: suspend FromState.() -> ToState
    ): ToState = transformer(getAndCheck())

    /**
     * This function get the current state and validate is value
     *
     * @return the current state or throws an exception
     */
    @Suppress("UNCHECKED_CAST")
    private fun <CS : FromState> getAndCheck(): CS {
        val currentState: CS? = state as? CS
        checkNotNull(currentState) { "The current state is different by the specified" }
        return currentState
    }

}