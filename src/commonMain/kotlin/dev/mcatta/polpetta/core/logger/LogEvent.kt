package dev.mcatta.polpetta.core.logger

/**
 * Log event enum
 *
 * @param key printed value
 */
internal enum class LogEvent(private val key: String) {
    DEBUG_EV_DISPATCH("ActionDispatched"),
    DEBUG_EV_PROCESSED("ActionProcessed "),
    DEBUG_EV_IGNORED("ActionIgnored   ");

    override fun toString(): String = key
}