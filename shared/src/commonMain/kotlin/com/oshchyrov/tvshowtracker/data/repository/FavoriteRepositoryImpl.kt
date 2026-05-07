package com.oshchyrov.tvshowtracker.data.repository

import com.oshchyrov.tvshowtracker.data.local.dao.FavoriteShowDao
import com.oshchyrov.tvshowtracker.data.local.dao.WatchedEpisodeDao
import com.oshchyrov.tvshowtracker.data.local.entity.WatchedEpisodeEntity
import com.oshchyrov.tvshowtracker.data.mapper.toDomain
import com.oshchyrov.tvshowtracker.data.mapper.toEntity
import com.oshchyrov.tvshowtracker.domain.model.Episode
import com.oshchyrov.tvshowtracker.domain.model.Show
import com.oshchyrov.tvshowtracker.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteRepositoryImpl(
    private val favoriteShowDao: FavoriteShowDao,
    private val watchedEpisodeDao: WatchedEpisodeDao,
) : FavoriteRepository {

    override fun getFavoriteShows(): Flow<List<Show>> =
        favoriteShowDao.getAll().map { entities -> entities.map { it.toDomain() } }

    override fun isFavorite(showId: Int): Flow<Boolean> =
        favoriteShowDao.isFavorite(showId)

    override suspend fun addFavorite(show: Show, totalEpisodes: Int) {
        favoriteShowDao.insert(show.toEntity(totalEpisodes))
    }

    override suspend fun removeFavorite(showId: Int) {
        favoriteShowDao.delete(showId)
        watchedEpisodeDao.deleteAllForShow(showId)
    }

    override fun getWatchedEpisodeIds(showId: Int): Flow<Set<Int>> =
        watchedEpisodeDao.getWatchedEpisodeIds(showId).map { it.toSet() }

    override suspend fun markEpisodeWatched(showId: Int, episodeId: Int, season: Int, number: Int) {
        watchedEpisodeDao.insert(WatchedEpisodeEntity(showId, episodeId, season, number))
    }

    override suspend fun markEpisodeUnwatched(showId: Int, episodeId: Int) {
        watchedEpisodeDao.delete(showId, episodeId)
    }

    override suspend fun markSeasonWatched(showId: Int, episodes: List<Episode>) {
        val entities = episodes.map { WatchedEpisodeEntity(showId, it.id, it.season, it.number) }
        watchedEpisodeDao.insertAll(entities)
    }

    override suspend fun markSeasonUnwatched(showId: Int, season: Int) {
        watchedEpisodeDao.deleteAllInSeason(showId, season)
    }

    override suspend fun getWatchedCount(showId: Int): Int =
        watchedEpisodeDao.getWatchedCount(showId)

    override suspend fun getTotalEpisodes(showId: Int): Int =
        favoriteShowDao.getTotalEpisodes(showId) ?: 0
}

