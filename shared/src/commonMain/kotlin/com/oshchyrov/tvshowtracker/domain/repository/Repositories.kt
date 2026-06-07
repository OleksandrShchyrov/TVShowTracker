package com.oshchyrov.tvshowtracker.domain.repository

import com.oshchyrov.tvshowtracker.domain.model.Episode
import com.oshchyrov.tvshowtracker.domain.model.Outcome
import com.oshchyrov.tvshowtracker.domain.model.Show
import kotlinx.coroutines.flow.Flow

interface ShowRepository {
    suspend fun getInitialShows(page: Int = 0): Outcome<List<Show>>
    suspend fun searchShows(query: String): Outcome<List<Show>>
    suspend fun getShowDetails(showId: Int): Outcome<Show>
    suspend fun getEpisodes(showId: Int): Outcome<List<Episode>>
}

interface FavoriteRepository {
    fun getFavoriteShows(): Flow<List<Show>>
    fun isFavorite(showId: Int): Flow<Boolean>
    suspend fun addFavorite(show: Show, totalEpisodes: Int)
    suspend fun removeFavorite(showId: Int)
    fun getWatchedEpisodeIds(showId: Int): Flow<Set<Int>>
    suspend fun markEpisodeWatched(showId: Int, episodeId: Int, season: Int, number: Int)
    suspend fun markEpisodeUnwatched(showId: Int, episodeId: Int)
    suspend fun markSeasonWatched(showId: Int, episodes: List<Episode>)
    suspend fun markSeasonUnwatched(showId: Int, season: Int)
    suspend fun getWatchedCount(showId: Int): Int
    suspend fun getTotalEpisodes(showId: Int): Int
}

