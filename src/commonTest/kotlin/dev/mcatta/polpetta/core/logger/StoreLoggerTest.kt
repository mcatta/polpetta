package dev.mcatta.polpetta.core.logger

import dev.mcatta.polpetta.TestAction
import dev.mcatta.polpetta.TestState
import kotlin.test.Test
import kotlin.test.assertEquals

internal class StoreLoggerTest {

    private companion object {
        const val TEST_STORE_NAME = "TestStore"
    }

    private val storeLogger = StoreLogger<TestAction, TestState>(TEST_STORE_NAME)

    @Test
    fun `Test log DEBUG_EV_DISPATCH`() {
        // When
        val logMessage = storeLogger.log(
            logEvent = LogEvent.DEBUG_EV_DISPATCH,
            action = TestAction.Increase
        )

        // Then
        assertEquals("$TEST_STORE_NAME > ActionDispatched > action: Increase", logMessage)
    }

    @Test
    fun `Test log DEBUG_EV_PROCESSED`() {
        // When
        val logMessage = storeLogger.log(
            logEvent = LogEvent.DEBUG_EV_PROCESSED,
            action = TestAction.Increase,
            fromStateKlass = TestState.Count::class,
            toStateKlass = TestState.Result::class
        )

        // Then
        assertEquals("$TEST_STORE_NAME > ActionProcessed  > action: Increase > From Count to Result", logMessage)
    }

    @Test
    fun `Test log DEBUG_EV_IGNORED`() {
        // When
        val logMessage = storeLogger.log(
            logEvent = LogEvent.DEBUG_EV_IGNORED,
            action = TestAction.Increase,
            fromStateKlass = TestState.Count::class,
            toStateKlass = null
        )

        // Then
        assertEquals("$TEST_STORE_NAME > ActionIgnored    > action: Increase > From Count to null", logMessage)
    }

}