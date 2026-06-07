package com.oshchyrov.tvshowtracker.test

import com.oshchyrov.tvshowtracker.domain.model.AppError
import com.oshchyrov.tvshowtracker.domain.model.Episode
import com.oshchyrov.tvshowtracker.domain.model.Outcome
import com.oshchyrov.tvshowtracker.domain.model.Show
import com.oshchyrov.tvshowtracker.domain.repository.FavoriteRepository
import com.oshchyrov.tvshowtracker.domain.repository.ShowRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeShowRepository : ShowRepository {
    var initialShowsToReturn: List<Show> = emptyList()
    var showsToReturn: List<Show> = emptyList()
    var episodesToReturn: List<Episode> = emptyList()
    var shouldFail: Boolean = false
    var error: AppError = AppError.Network

    override suspend fun getInitialShows(page: Int): Outcome<List<Show>> =
        if (shouldFail) Outcome.Failure(error) else Outcome.Success(initialShowsToReturn)

    override suspend fun searchShows(query: String): Outcome<List<Show>> =
        if (shouldFail) Outcome.Failure(error) else Outcome.Success(showsToReturn)

    override suspend fun getShowDetails(showId: Int): Outcome<Show> {
        if (shouldFail) return Outcome.Failure(error)
        return showsToReturn.find { it.id == showId }?.let { Outcome.Success(it) }
            ?: initialShowsToReturn.find { it.id == showId }?.let { Outcome.Success(it) }
            ?: Outcome.Failure(AppError.NotFound)
    }

    override suspend fun getEpisodes(showId: Int): Outcome<List<Episode>> =
        if (shouldFail) Outcome.Failure(error) else Outcome.Success(episodesToReturn)
}

class FakeFavoriteRepository : FavoriteRepository {
    private val favorites = MutableStateFlow<List<Show>>(emptyList())
    private val watchedByShow = MutableStateFlow<Map<Int, Set<Int>>>(emptyMap())
    private val totalEpisodesByShow = mutableMapOf<Int, Int>()

    override fun getFavoriteShows(): Flow<List<Show>> = favorites

    override fun isFavorite(showId: Int): Flow<Boolean> =
        favorites.map { shows -> shows.any { it.id == showId } }

    override suspend fun addFavorite(show: Show, totalEpisodes: Int) {
        totalEpisodesByShow[show.id] = totalEpisodes
        if (favorites.value.none { it.id == show.id }) {
            favorites.value = favorites.value + show
        }
    }

    override suspend fun removeFavorite(showId: Int) {
        favorites.value = favorites.value.filterNot { it.id == showId }
        watchedByShow.value = watchedByShow.value - showId
        totalEpisodesByShow.remove(showId)
    }

    override fun getWatchedEpisodeIds(showId: Int): Flow<Set<Int>> =
        watchedByShow.map { it[showId].orEmpty() }

    override suspend fun markEpisodeWatched(showId: Int, episodeId: Int, season: Int, number: Int) {
        val current = watchedByShow.value[showId].orEmpty()
        watchedByShow.value = watchedByShow.value + (showId to (current + episodeId))
    }

    override suspend fun markEpisodeUnwatched(showId: Int, episodeId: Int) {
        val current = watchedByShow.value[showId].orEmpty()
        watchedByShow.value = watchedByShow.value + (showId to (current - episodeId))
    }

    override suspend fun markSeasonWatched(showId: Int, episodes: List<Episode>) {
        val current = watchedByShow.value[showId].orEmpty()
        watchedByShow.value = watchedByShow.value + (showId to (current + episodes.map { it.id }.toSet()))
    }

    override suspend fun markSeasonUnwatched(showId: Int, season: Int) {
        // ViewModel tests delegate season unwatch to repository impl; fake tracks ids only.
        val current = watchedByShow.value[showId].orEmpty()
        watchedByShow.value = watchedByShow.value + (showId to current)
    }

    override suspend fun getWatchedCount(showId: Int): Int =
        watchedByShow.value[showId]?.size ?: 0

    override suspend fun getTotalEpisodes(showId: Int): Int =
        totalEpisodesByShow[showId] ?: 0
}
