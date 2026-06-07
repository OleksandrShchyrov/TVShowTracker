package com.oshchyrov.tvshowtracker.domain.logging

/**
 * Domain-facing logging contract. Data and presentation layers depend on this
 * abstraction instead of a concrete logging library.
 */
interface Logger {
    fun error(message: String, throwable: Throwable? = null)
}
