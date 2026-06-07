package com.oshchyrov.tvshowtracker.di

import com.oshchyrov.tvshowtracker.data.logging.NapierLogger
import com.oshchyrov.tvshowtracker.data.remote.api.TvMazeApi
import com.oshchyrov.tvshowtracker.domain.logging.Logger
import com.oshchyrov.tvshowtracker.data.repository.FavoriteRepositoryImpl
import com.oshchyrov.tvshowtracker.data.repository.ShowRepositoryImpl
import com.oshchyrov.tvshowtracker.domain.repository.FavoriteRepository
import com.oshchyrov.tvshowtracker.domain.repository.ShowRepository
import com.oshchyrov.tvshowtracker.presentation.details.DetailsViewModel
import com.oshchyrov.tvshowtracker.presentation.episodes.EpisodesViewModel
import com.oshchyrov.tvshowtracker.presentation.favorites.FavoritesViewModel
import com.oshchyrov.tvshowtracker.presentation.search.SearchViewModel
import com.oshchyrov.tvshowtracker.presentation.settings.SettingsStore
import com.oshchyrov.tvshowtracker.presentation.settings.SettingsViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module

val commonModule = module {
    single {
        val engineFactory: HttpClientEngineFactory<*> = get()
        HttpClient(engineFactory) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            install(Logging) {
                level = LogLevel.NONE
            }
        }
    }

    single { TvMazeApi(get()) }

    single { get<com.oshchyrov.tvshowtracker.data.local.db.AppDatabase>().favoriteShowDao() }
    single { get<com.oshchyrov.tvshowtracker.data.local.db.AppDatabase>().watchedEpisodeDao() }

    single<Logger> { NapierLogger() }
    single<ShowRepository> { ShowRepositoryImpl(get(), get()) }
    single<FavoriteRepository> { FavoriteRepositoryImpl(get(), get()) }

    factory { SearchViewModel(get()) }
    factory { DetailsViewModel(get(), get()) }
    factory { EpisodesViewModel(get(), get()) }
    factory { FavoritesViewModel(get(), get()) }
    single { SettingsStore(get()) }
    factory { SettingsViewModel(get()) }
}

expect val platformModule: Module
