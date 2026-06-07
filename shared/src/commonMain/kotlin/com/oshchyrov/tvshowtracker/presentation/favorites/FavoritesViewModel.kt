package com.oshchyrov.tvshowtracker.presentation.favorites

import com.oshchyrov.tvshowtracker.domain.model.Episode
import com.oshchyrov.tvshowtracker.domain.model.FavoriteShow
import com.oshchyrov.tvshowtracker.domain.model.Outcome
import com.oshchyrov.tvshowtracker.domain.model.Show
import com.oshchyrov.tvshowtracker.domain.repository.FavoriteRepository
import com.oshchyrov.tvshowtracker.domain.repository.ShowRepository
import com.oshchyrov.tvshowtracker.presentation.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoriteRepository: FavoriteRepository,
    private val showRepository: ShowRepository,
) : BaseViewModel<FavoritesState, FavoritesIntent>(FavoritesState()) {

    private var loadJob: Job? = null

    override fun handleIntent(intent: FavoritesIntent) {
        when (intent) {
            is FavoritesIntent.Load -> load()
            is FavoritesIntent.RemoveFavorite -> removeFavorite(intent.showId)
        }
    }

    fun loadFavorites() {
        handleIntent(FavoritesIntent.Load)
    }

    fun removeFavoriteShow(showId: Int) {
        handleIntent(FavoritesIntent.RemoveFavorite(showId))
    }

    private fun load() {
        loadJob?.cancel()
        loadJob = scope.launch {
            favoriteRepository.getFavoriteShows().collectLatest { shows ->
                if (shows.isEmpty()) {
                    updateState { copy(favorites = emptyList(), isLoading = false, isEmpty = true) }
                } else {
                    // Build initial list
                    refreshFavorites(shows)
                }
            }
        }
    }

    fun refreshData() {
        scope.launch {
            val shows = favoriteRepository.getFavoriteShows().first()
            if (shows.isNotEmpty()) {
                refreshFavorites(shows)
            }
        }
    }

    private suspend fun refreshFavorites(shows: List<Show>) {
        val favorites = shows.map { show -> buildFavoriteShow(show) }
        updateState { copy(favorites = favorites, isLoading = false, isEmpty = false) }
    }

    private suspend fun buildFavoriteShow(show: Show): FavoriteShow {
        val totalEpisodes = favoriteRepository.getTotalEpisodes(show.id)
        val watchedCount = favoriteRepository.getWatchedCount(show.id)
        val nextUnwatched = calculateNextUnwatched(show.id)
        return FavoriteShow(
            show = show,
            totalEpisodes = totalEpisodes,
            watchedEpisodes = watchedCount,
            nextUnwatchedEpisode = nextUnwatched,
        )
    }

    private suspend fun calculateNextUnwatched(showId: Int): Episode? {
        val episodes = when (val result = showRepository.getEpisodes(showId)) {
            is Outcome.Success -> result.value
            is Outcome.Failure -> return null
        }
        val watchedSet = favoriteRepository.getWatchedEpisodeIds(showId).first()
        return episodes
            .sortedWith(compareBy({ it.season }, { it.number }))
            .firstOrNull { it.id !in watchedSet }
    }

    private fun removeFavorite(showId: Int) {
        scope.launch {
            favoriteRepository.removeFavorite(showId)
        }
    }
}
