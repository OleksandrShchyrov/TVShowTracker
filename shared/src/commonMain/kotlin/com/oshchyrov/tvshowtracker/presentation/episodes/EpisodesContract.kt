package com.oshchyrov.tvshowtracker.presentation.episodes

import com.oshchyrov.tvshowtracker.domain.model.Season

data class EpisodesState(
    val seasons: List<Season> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

sealed interface EpisodesIntent {
    data class LoadEpisodes(val showId: Int) : EpisodesIntent
    data class ToggleEpisodeWatched(val episodeId: Int, val season: Int, val number: Int, val isWatched: Boolean) : EpisodesIntent
    data class ToggleSeasonWatched(val seasonNumber: Int, val markWatched: Boolean) : EpisodesIntent
}

