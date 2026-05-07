package com.oshchyrov.tvshowtracker.presentation.favorites

import com.oshchyrov.tvshowtracker.domain.model.Episode
import com.oshchyrov.tvshowtracker.domain.model.FavoriteShow
import com.oshchyrov.tvshowtracker.domain.model.Show

data class FavoritesState(
    val favorites: List<FavoriteShow> = emptyList(),
    val isLoading: Boolean = true,
    val isEmpty: Boolean = false,
)

sealed interface FavoritesIntent {
    data object Load : FavoritesIntent
    data class RemoveFavorite(val showId: Int) : FavoritesIntent
}

