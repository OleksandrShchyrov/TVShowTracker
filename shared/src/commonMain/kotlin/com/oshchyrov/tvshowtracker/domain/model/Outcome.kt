package com.oshchyrov.tvshowtracker.domain.model

sealed interface Outcome<out T> {
    data class Success<T>(val value: T) : Outcome<T>
    data class Failure(val error: AppError) : Outcome<Nothing>
}

sealed interface AppError {
    data object Network : AppError
    data object Timeout : AppError
    data object NotFound : AppError
    data object Unknown : AppError
}

fun AppError.userMessage(): String = when (this) {
    AppError.Network -> "Network error"
    AppError.Timeout -> "Request timed out"
    AppError.NotFound -> "Not found"
    AppError.Unknown -> "Unknown error"
}
