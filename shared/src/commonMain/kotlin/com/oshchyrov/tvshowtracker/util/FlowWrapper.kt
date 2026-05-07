package com.oshchyrov.tvshowtracker.util

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class FlowWrapper<T>(private val flow: Flow<T>) {
    fun collect(onEach: (T) -> Unit, onComplete: () -> Unit, onError: (Throwable) -> Unit): Cancellable {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
        scope.launch {
            try {
                flow.collect { onEach(it) }
                onComplete()
            } catch (e: CancellationException) {
                // ignore
            } catch (e: Throwable) {
                onError(e)
            }
        }
        return object : Cancellable {
            override fun cancel() { scope.cancel() }
        }
    }
}

interface Cancellable {
    fun cancel()
}

fun <T> StateFlow<T>.wrap(): FlowWrapper<T> = FlowWrapper(this)

