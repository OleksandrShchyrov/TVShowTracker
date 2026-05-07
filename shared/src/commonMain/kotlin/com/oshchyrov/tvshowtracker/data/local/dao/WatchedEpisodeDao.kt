package com.oshchyrov.tvshowtracker.data.local.dao

import androidx.room.*
import com.oshchyrov.tvshowtracker.data.local.entity.WatchedEpisodeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchedEpisodeDao {
    @Query("SELECT episodeId FROM watched_episodes WHERE showId = :showId")
    fun getWatchedEpisodeIds(showId: Int): Flow<List<Int>>

    @Query("SELECT COUNT(*) FROM watched_episodes WHERE showId = :showId")
    suspend fun getWatchedCount(showId: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: WatchedEpisodeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<WatchedEpisodeEntity>)

    @Query("DELETE FROM watched_episodes WHERE showId = :showId AND episodeId = :episodeId")
    suspend fun delete(showId: Int, episodeId: Int)

    @Query("DELETE FROM watched_episodes WHERE showId = :showId AND season = :season")
    suspend fun deleteAllInSeason(showId: Int, season: Int)

    @Query("DELETE FROM watched_episodes WHERE showId = :showId")
    suspend fun deleteAllForShow(showId: Int)
}

