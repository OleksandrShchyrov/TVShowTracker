package com.oshchyrov.tvshowtracker.domain.model

data class Episode(
    val id: Int,
    val showId: Int,
    val name: String,
    val season: Int,
    val number: Int,
    val airdate: String?,
    val runtime: Int?,
    val imageUrl: String?,
    val summary: String?,
    val isWatched: Boolean = false,
)

