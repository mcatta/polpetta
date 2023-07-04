package dev.mcatta.polpetta

import dev.mcatta.polpetta.operators.SideEffect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

public class SideEffectFactory<E : SideEffect> {

    private val _sideEffectFlow: MutableStateFlow<E?> = MutableStateFlow(null)
    public val sideEffectFlow: StateFlow<E?> = _sideEffectFlow.asStateFlow()

    /**
     * Dispatch a [sideEffect]
     *
     * @param sideEffect
     */
    public suspend fun sideEffect(sideEffect: E) {
        _sideEffectFlow.emit(sideEffect)
    }
}