package com.oshchyrov.tvshowtracker.data.local.dao

import androidx.room.*
import com.oshchyrov.tvshowtracker.data.local.entity.FavoriteShowEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteShowDao {
    @Query("SELECT * FROM favorite_shows")
    fun getAll(): Flow<List<FavoriteShowEntity>>

    @Query("SELECT * FROM favorite_shows WHERE id = :showId")
    suspend fun getById(showId: Int): FavoriteShowEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_shows WHERE id = :showId)")
    fun isFavorite(showId: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(show: FavoriteShowEntity)

    @Query("DELETE FROM favorite_shows WHERE id = :showId")
    suspend fun delete(showId: Int)

    @Query("SELECT totalEpisodes FROM favorite_shows WHERE id = :showId")
    suspend fun getTotalEpisodes(showId: Int): Int?
}

