package com.oshchyrov.tvshowtracker.presentation.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<State, Intent>(initialState: State) {
    protected val scope: CoroutineScope = MainScope()
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    protected fun updateState(reducer: State.() -> State) {
        _state.value = _state.value.reducer()
    }

    abstract fun handleIntent(intent: Intent)

    fun onCleared() {
        scope.cancel()
    }
}

