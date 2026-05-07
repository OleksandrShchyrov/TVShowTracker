package com.oshchyrov.tvshowtracker.presentation.search

import com.oshchyrov.tvshowtracker.domain.model.Show

data class SearchState(
    val query: String = "",
    val results: List<Show> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEmpty: Boolean = false,
    val isInitialContent: Boolean = false,
)

sealed interface SearchIntent {
    data class QueryChanged(val query: String) : SearchIntent
    data object Retry : SearchIntent
}

