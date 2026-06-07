package com.oshchyrov.tvshowtracker.data.logging

import com.oshchyrov.tvshowtracker.domain.logging.Logger
import io.github.aakira.napier.Napier

class NapierLogger : Logger {
    override fun error(message: String, throwable: Throwable?) {
        if (throwable != null) {
            Napier.e(message, throwable)
        } else {
            Napier.e(message)
        }
    }
}

class NoOpLogger : Logger {
    override fun error(message: String, throwable: Throwable?) = Unit
}
