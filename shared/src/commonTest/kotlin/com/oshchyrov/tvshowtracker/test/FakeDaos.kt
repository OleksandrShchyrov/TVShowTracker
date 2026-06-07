package com.oshchyrov.tvshowtracker.test

import com.oshchyrov.tvshowtracker.data.local.dao.FavoriteShowDao
import com.oshchyrov.tvshowtracker.data.local.dao.WatchedEpisodeDao
import com.oshchyrov.tvshowtracker.data.local.entity.FavoriteShowEntity
import com.oshchyrov.tvshowtracker.data.local.entity.WatchedEpisodeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeFavoriteShowDao : FavoriteShowDao {
    private val shows = MutableStateFlow<List<FavoriteShowEntity>>(emptyList())

    override fun getAll(): Flow<List<FavoriteShowEntity>> = shows

    override suspend fun getById(showId: Int): FavoriteShowEntity? =
        shows.value.find { it.id == showId }

    override fun isFavorite(showId: Int): Flow<Boolean> =
        shows.map { list -> list.any { it.id == showId } }

    override suspend fun insert(show: FavoriteShowEntity) {
        shows.value = shows.value.filterNot { it.id == show.id } + show
    }

    override suspend fun delete(showId: Int) {
        shows.value = shows.value.filterNot { it.id == showId }
    }

    override suspend fun getTotalEpisodes(showId: Int): Int? =
        shows.value.find { it.id == showId }?.totalEpisodes
}

class FakeWatchedEpisodeDao : WatchedEpisodeDao {
    private val episodes = MutableStateFlow<List<WatchedEpisodeEntity>>(emptyList())

    override fun getWatchedEpisodeIds(showId: Int): Flow<List<Int>> =
        episodes.map { list -> list.filter { it.showId == showId }.map { it.episodeId } }

    override suspend fun getWatchedCount(showId: Int): Int =
        episodes.value.count { it.showId == showId }

    override suspend fun insert(entity: WatchedEpisodeEntity) {
        episodes.value = episodes.value.filterNot {
            it.showId == entity.showId && it.episodeId == entity.episodeId
        } + entity
    }

    override suspend fun insertAll(entities: List<WatchedEpisodeEntity>) {
        entities.forEach { insert(it) }
    }

    override suspend fun delete(showId: Int, episodeId: Int) {
        episodes.value = episodes.value.filterNot {
            it.showId == showId && it.episodeId == episodeId
        }
    }

    override suspend fun deleteAllInSeason(showId: Int, season: Int) {
        episodes.value = episodes.value.filterNot {
            it.showId == showId && it.season == season
        }
    }

    override suspend fun deleteAllForShow(showId: Int) {
        episodes.value = episodes.value.filterNot { it.showId == showId }
    }
}
