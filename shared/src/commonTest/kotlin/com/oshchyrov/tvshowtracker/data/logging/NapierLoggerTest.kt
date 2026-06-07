package com.oshchyrov.tvshowtracker.data.logging

import com.oshchyrov.tvshowtracker.domain.logging.Logger
import kotlin.test.Test
import kotlin.test.assertTrue

class NapierLoggerTest {

    @Test
    fun napierLoggerImplementsLoggerInterface() {
        val logger: Logger = NapierLogger()
        logger.error("test message")
        logger.error("test with throwable", RuntimeException("boom"))
        assertTrue(true)
    }

    @Test
    fun noOpLoggerDoesNotThrow() {
        val logger: Logger = NoOpLogger()
        logger.error("silent")
        logger.error("silent with cause", IllegalStateException())
        assertTrue(true)
    }
}
