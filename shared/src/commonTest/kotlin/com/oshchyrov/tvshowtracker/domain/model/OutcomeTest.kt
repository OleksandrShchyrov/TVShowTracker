package com.oshchyrov.tvshowtracker.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class OutcomeTest {

    @Test
    fun successCarriesValue() {
        val outcome = Outcome.Success("data")
        assertIs<Outcome.Success<String>>(outcome)
        assertEquals("data", outcome.value)
    }

    @Test
    fun failureCarriesError() {
        val outcome = Outcome.Failure(AppError.Network)
        assertIs<Outcome.Failure>(outcome)
        assertEquals(AppError.Network, outcome.error)
    }

    @Test
    fun userMessageForNetworkError() {
        assertEquals("Network error", AppError.Network.userMessage())
    }

    @Test
    fun userMessageForTimeoutError() {
        assertEquals("Request timed out", AppError.Timeout.userMessage())
    }

    @Test
    fun userMessageForNotFoundError() {
        assertEquals("Not found", AppError.NotFound.userMessage())
    }

    @Test
    fun userMessageForUnknownError() {
        assertEquals("Unknown error", AppError.Unknown.userMessage())
    }
}
