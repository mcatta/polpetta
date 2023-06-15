package dev.mcatta.polpetta

import dev.mcatta.polpetta.operators.Action
import dev.mcatta.polpetta.operators.State

internal sealed interface TestState : State {
    data class Count(val counter: Int) : TestState
    data class Result(val message: String) : TestState
}

internal sealed interface TestAction : Action {
    object Increase : TestAction
    object Decrease : TestAction
    data class Set(val n: Int) : TestAction
    object DoNothing : TestAction
    object ToString : TestAction
}