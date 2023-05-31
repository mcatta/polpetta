package dev.marcocattaneo.polpetta

import dev.marcocattaneo.polpetta.operators.Action
import dev.marcocattaneo.polpetta.operators.State

internal data class TestState(val counter: Int) : State

internal sealed interface TestAction : Action {
    object Increase : TestAction
    object Decrease : TestAction
    data class Set(val n: Int) : TestAction
    object DoNothing : TestAction
}