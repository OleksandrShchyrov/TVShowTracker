package com.oshchyrov.tvshowtracker.data.local.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.oshchyrov.tvshowtracker.data.local.dao.FavoriteShowDao
import com.oshchyrov.tvshowtracker.data.local.dao.WatchedEpisodeDao
import com.oshchyrov.tvshowtracker.data.local.entity.FavoriteShowEntity
import com.oshchyrov.tvshowtracker.data.local.entity.WatchedEpisodeEntity

@Database(
    entities = [FavoriteShowEntity::class, WatchedEpisodeEntity::class],
    version = 1,
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteShowDao(): FavoriteShowDao
    abstract fun watchedEpisodeDao(): WatchedEpisodeDao

    companion object {
        const val DB_NAME = "tvshowtracker.db"
    }
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase>
