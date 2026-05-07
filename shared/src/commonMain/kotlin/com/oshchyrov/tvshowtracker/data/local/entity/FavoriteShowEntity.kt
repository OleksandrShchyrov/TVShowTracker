package com.oshchyrov.tvshowtracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_shows")
data class FavoriteShowEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val summary: String?,
    val imageUrl: String?,
    val imageMediumUrl: String?,
    val genres: String,
    val rating: Double?,
    val runtime: Int?,
    val language: String?,
    val status: String?,
    val premiered: String?,
    val network: String?,
    val webChannel: String?,
    val totalEpisodes: Int,
)

