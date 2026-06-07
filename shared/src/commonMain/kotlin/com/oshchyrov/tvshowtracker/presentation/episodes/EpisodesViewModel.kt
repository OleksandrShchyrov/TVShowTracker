package com.oshchyrov.tvshowtracker.presentation.episodes

import com.oshchyrov.tvshowtracker.domain.model.Episode
import com.oshchyrov.tvshowtracker.domain.model.Outcome
import com.oshchyrov.tvshowtracker.domain.model.Season
import com.oshchyrov.tvshowtracker.domain.model.userMessage
import com.oshchyrov.tvshowtracker.domain.repository.FavoriteRepository
import com.oshchyrov.tvshowtracker.domain.repository.ShowRepository
import com.oshchyrov.tvshowtracker.presentation.base.BaseViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EpisodesViewModel(
    private val showRepository: ShowRepository,
    private val favoriteRepository: FavoriteRepository,
) : BaseViewModel<EpisodesState, EpisodesIntent>(EpisodesState()) {

    private var showId: Int = -1
    private var allEpisodes: List<Episode> = emptyList()

    override fun handleIntent(intent: EpisodesIntent) {
        when (intent) {
            is EpisodesIntent.LoadEpisodes -> loadEpisodes(intent.showId)
            is EpisodesIntent.ToggleEpisodeWatched -> toggleEpisode(intent)
            is EpisodesIntent.ToggleSeasonWatched -> toggleSeason(intent)
        }
    }

    fun load(showId: Int) {
        handleIntent(EpisodesIntent.LoadEpisodes(showId))
    }

    fun toggleEpisodeWatched(
        episodeId: Int,
        season: Int,
        number: Int,
        isWatched: Boolean,
    ) {
        handleIntent(
            EpisodesIntent.ToggleEpisodeWatched(
                episodeId = episodeId,
                season = season,
                number = number,
                isWatched = isWatched,
            )
        )
    }

    fun toggleSeasonWatched(seasonNumber: Int, markWatched: Boolean) {
        handleIntent(EpisodesIntent.ToggleSeasonWatched(seasonNumber, markWatched))
    }

    private fun loadEpisodes(id: Int) {
        showId = id
        scope.launch {
            updateState { copy(isLoading = true, error = null) }
            when (val result = showRepository.getEpisodes(id)) {
                is Outcome.Success -> {
                    val episodes = result.value
                    allEpisodes = episodes
                    updateState { copy(isLoading = false) }
                }
                is Outcome.Failure -> {
                    updateState { copy(isLoading = false, error = result.error.userMessage()) }
                }
            }
        }
        scope.launch {
            favoriteRepository.getWatchedEpisodeIds(id).collectLatest { watchedIds ->
                val seasons = allEpisodes
                    .map { it.copy(isWatched = it.id in watchedIds) }
                    .groupBy { it.season }
                    .map { (num, eps) -> Season(num, eps.sortedBy { it.number }) }
                    .sortedBy { it.number }
                updateState { copy(seasons = seasons) }
            }
        }
    }

    private fun toggleEpisode(intent: EpisodesIntent.ToggleEpisodeWatched) {
        scope.launch {
            if (intent.isWatched) {
                favoriteRepository.markEpisodeUnwatched(showId, intent.episodeId)
            } else {
                favoriteRepository.markEpisodeWatched(showId, intent.episodeId, intent.season, intent.number)
            }
        }
    }

    private fun toggleSeason(intent: EpisodesIntent.ToggleSeasonWatched) {
        scope.launch {
            if (intent.markWatched) {
                val episodes = allEpisodes.filter { it.season == intent.seasonNumber }
                favoriteRepository.markSeasonWatched(showId, episodes)
            } else {
                favoriteRepository.markSeasonUnwatched(showId, intent.seasonNumber)
            }
        }
    }
}

