package com.oshchyrov.tvshowtracker.presentation.details

import com.oshchyrov.tvshowtracker.domain.model.Show

data class DetailsState(
    val show: Show? = null,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null,
    val watchedCount: Int = 0,
    val totalEpisodes: Int = 0,
) {
    val progressPercentage: Float
        get() = if (totalEpisodes > 0) watchedCount.toFloat() / totalEpisodes else 0f
}

sealed interface DetailsIntent {
    data class LoadShow(val showId: Int) : DetailsIntent
    data object ToggleFavorite : DetailsIntent
}

