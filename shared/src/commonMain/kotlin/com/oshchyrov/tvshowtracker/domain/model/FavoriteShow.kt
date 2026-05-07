package com.oshchyrov.tvshowtracker.domain.model

data class FavoriteShow(
    val show: Show,
    val totalEpisodes: Int,
    val watchedEpisodes: Int,
    val nextUnwatchedEpisode: Episode?,
) {
    val progressPercentage: Float
        get() = if (totalEpisodes > 0) watchedEpisodes.toFloat() / totalEpisodes else 0f
}

