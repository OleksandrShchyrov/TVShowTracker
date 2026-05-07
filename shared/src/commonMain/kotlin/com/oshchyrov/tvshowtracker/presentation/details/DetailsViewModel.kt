package com.oshchyrov.tvshowtracker.presentation.details

import com.oshchyrov.tvshowtracker.domain.repository.FavoriteRepository
import com.oshchyrov.tvshowtracker.domain.repository.ShowRepository
import com.oshchyrov.tvshowtracker.presentation.base.BaseViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val showRepository: ShowRepository,
    private val favoriteRepository: FavoriteRepository,
) : BaseViewModel<DetailsState, DetailsIntent>(DetailsState()) {

    private var showId: Int = -1

    override fun handleIntent(intent: DetailsIntent) {
        when (intent) {
            is DetailsIntent.LoadShow -> loadShow(intent.showId)
            is DetailsIntent.ToggleFavorite -> toggleFavorite()
        }
    }

    fun load(showId: Int) {
        handleIntent(DetailsIntent.LoadShow(showId))
    }

    fun toggleFavoriteSelection() {
        handleIntent(DetailsIntent.ToggleFavorite)
    }

    private fun loadShow(id: Int) {
        showId = id
        scope.launch {
            updateState { copy(isLoading = true, error = null) }
            showRepository.getShowDetails(id)
                .onSuccess { show ->
                    updateState { copy(show = show, isLoading = false) }
                    loadEpisodeCount(id)
                }
                .onFailure { e ->
                    updateState { copy(isLoading = false, error = e.message) }
                }
        }
        scope.launch {
            favoriteRepository.isFavorite(id).collectLatest { isFav ->
                updateState { copy(isFavorite = isFav) }
            }
        }
        scope.launch {
            favoriteRepository.getWatchedEpisodeIds(id).collectLatest { ids ->
                updateState { copy(watchedCount = ids.size) }
            }
        }
    }

    private fun loadEpisodeCount(id: Int) {
        scope.launch {
            showRepository.getEpisodes(id).onSuccess { episodes ->
                updateState { copy(totalEpisodes = episodes.size) }
            }
        }
    }

    private fun toggleFavorite() {
        val show = state.value.show ?: return
        scope.launch {
            if (state.value.isFavorite) {
                favoriteRepository.removeFavorite(show.id)
            } else {
                favoriteRepository.addFavorite(show, state.value.totalEpisodes)
            }
        }
    }
}

