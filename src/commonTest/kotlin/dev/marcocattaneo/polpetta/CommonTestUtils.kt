package dev.marcocattaneo.polpetta

import dev.marcocattaneo.polpetta.operators.Action
import dev.marcocattaneo.polpetta.operators.State

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