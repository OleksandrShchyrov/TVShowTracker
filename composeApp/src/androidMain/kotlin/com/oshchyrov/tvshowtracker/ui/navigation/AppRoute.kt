package com.oshchyrov.tvshowtracker.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data object SearchRoute

@Serializable
data object FavoritesRoute

@Serializable
data object SettingsRoute

@Serializable
data class DetailsRoute(val showId: Int)

@Serializable
data class EpisodesRoute(val showId: Int)
