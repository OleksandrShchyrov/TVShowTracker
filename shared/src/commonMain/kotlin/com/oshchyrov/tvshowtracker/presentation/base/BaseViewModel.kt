package com.oshchyrov.tvshowtracker.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<State, Intent>(initialState: State) : ViewModel() {
    protected val scope = viewModelScope
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    protected fun updateState(reducer: State.() -> State) {
        _state.value = _state.value.reducer()
    }

    abstract fun handleIntent(intent: Intent)

    fun clear() {
        scope.cancel()
    }
}

