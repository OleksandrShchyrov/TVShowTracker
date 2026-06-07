package com.oshchyrov.tvshowtracker.presentation.search

import com.oshchyrov.tvshowtracker.domain.model.Outcome
import com.oshchyrov.tvshowtracker.domain.model.userMessage
import com.oshchyrov.tvshowtracker.domain.repository.ShowRepository
import com.oshchyrov.tvshowtracker.presentation.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val showRepository: ShowRepository,
) : BaseViewModel<SearchState, SearchIntent>(SearchState()) {

    private var searchJob: Job? = null

    override fun handleIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.QueryChanged -> onQueryChanged(intent.query)
            is SearchIntent.Retry -> retry()
        }
    }

    fun loadInitialShows() {
        scope.launch {
            updateState { copy(isLoading = true, error = null, isEmpty = false) }
            when (val result = showRepository.getInitialShows()) {
                is Outcome.Success -> {
                    val shows = result.value
                    updateState {
                        copy(
                            results = shows,
                            isLoading = false,
                            isEmpty = shows.isEmpty(),
                            isInitialContent = true,
                        )
                    }
                }
                is Outcome.Failure -> {
                    updateState {
                        copy(
                            isLoading = false,
                            error = result.error.userMessage(),
                            isInitialContent = true,
                        )
                    }
                }
            }
        }
    }

    fun updateQuery(query: String) {
        handleIntent(SearchIntent.QueryChanged(query))
    }

    fun retrySearch() {
        handleIntent(SearchIntent.Retry)
    }

    private fun retry() {
        if (state.value.query.isBlank()) {
            loadInitialShows()
        } else {
            search(state.value.query)
        }
    }

    private fun onQueryChanged(query: String) {
        updateState { copy(query = query) }
        searchJob?.cancel()
        if (query.isBlank()) {
            loadInitialShows()
            return
        }
        searchJob = scope.launch {
            delay(400) // debounce
            search(query)
        }
    }

    private fun search(query: String) {
        if (query.isBlank()) return
        scope.launch {
            updateState { copy(isLoading = true, error = null, isInitialContent = false) }
            when (val result = showRepository.searchShows(query)) {
                is Outcome.Success -> {
                    val shows = result.value
                    updateState {
                        copy(
                            results = shows,
                            isLoading = false,
                            isEmpty = shows.isEmpty(),
                            isInitialContent = false,
                        )
                    }
                }
                is Outcome.Failure -> {
                    updateState { copy(isLoading = false, error = result.error.userMessage(), isInitialContent = false) }
                }
            }
        }
    }
}

