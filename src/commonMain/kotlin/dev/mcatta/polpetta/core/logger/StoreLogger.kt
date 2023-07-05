package dev.mcatta.polpetta.core.logger

import dev.mcatta.polpetta.operators.Action
import dev.mcatta.polpetta.operators.State
import kotlin.reflect.KClass

/**
 * This is a Logger that we can use in order to log witch events are dispatched and executed or not
 *
 * @param stateStoreName StateStore's name
 */
internal class StoreLogger<A : Action, S : State> internal constructor(
    private val stateStoreName: String?
) {

    /**
     * Print log
     *
     * @param logEvent log event type
     * @param action action event
     * @param fromStateKlass current state
     * @param toStateKlass reduced state
     */
    internal fun log(
        logEvent: LogEvent,
        action: A,
        fromStateKlass: KClass<out S>? = null,
        toStateKlass: KClass<out S>? = null
    ): String = buildLogMessage(
        logEvent = logEvent,
        action = action,
        message = if (fromStateKlass != null)
            "From ${fromStateKlass.simpleName} to ${toStateKlass?.simpleName}"
        else null
    ).also(::println)

    /**
     * Build log message from arguments
     *
     * @param logEvent log event type
     * @param action action type
     * @param message extra
     */
    private fun buildLogMessage(logEvent: LogEvent, action: A, message: String?) = with(StringBuilder()) {
        append(stateStoreName)
        append(" > $logEvent")
        append(" > action: ${action::class.simpleName}")
        message?.let {
            append(" > $it")
        }
        toString()
    }

}