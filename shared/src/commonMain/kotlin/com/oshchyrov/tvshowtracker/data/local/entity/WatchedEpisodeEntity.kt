package com.oshchyrov.tvshowtracker.data.local.entity

import androidx.room.Entity

@Entity(tableName = "watched_episodes", primaryKeys = ["showId", "episodeId"])
data class WatchedEpisodeEntity(
    val showId: Int,
    val episodeId: Int,
    val season: Int,
    val number: Int,
)

