package com.oshchyrov.tvshowtracker.di

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.oshchyrov.tvshowtracker.data.local.db.AppDatabase
import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*
import org.koin.dsl.module

actual val platformModule = module {
    single<HttpClientEngineFactory<*>> { OkHttp }
    single<AppDatabase> {
        val context: Context = get()
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DB_NAME,
        ).setDriver(BundledSQLiteDriver()).build()
    }
}
