package com.oshchyrov.tvshowtracker.di

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.oshchyrov.tvshowtracker.data.local.db.AppDatabase
import com.oshchyrov.tvshowtracker.data.local.settings.SettingsStorage
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.dsl.module
import androidx.core.content.edit

actual val platformModule = module {
    single<HttpClientEngineFactory<*>> { OkHttp }
    single<SettingsStorage> { AndroidSettingsStorage(get()) }
    single<AppDatabase> {
        val context: Context = get()
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DB_NAME,
        ).setDriver(BundledSQLiteDriver()).build()
    }
}

private class AndroidSettingsStorage(
    context: Context,
) : SettingsStorage {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun getString(key: String): String? = preferences.getString(key, null)

    override fun putString(key: String, value: String) {
        preferences.edit { putString(key, value) }
    }

    private companion object {
        const val PREFERENCES_NAME = "tvshowtracker_settings"
    }
}
