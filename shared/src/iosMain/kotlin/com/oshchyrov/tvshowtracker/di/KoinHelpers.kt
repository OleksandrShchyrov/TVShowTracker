package com.oshchyrov.tvshowtracker.di

import com.oshchyrov.tvshowtracker.presentation.details.DetailsViewModel
import com.oshchyrov.tvshowtracker.presentation.episodes.EpisodesViewModel
import com.oshchyrov.tvshowtracker.presentation.favorites.FavoritesViewModel
import com.oshchyrov.tvshowtracker.presentation.search.SearchViewModel
import com.oshchyrov.tvshowtracker.presentation.settings.SettingsStore
import com.oshchyrov.tvshowtracker.presentation.settings.SettingsViewModel

fun getSearchViewModel(): SearchViewModel = koin.get()
fun getDetailsViewModel(): DetailsViewModel = koin.get()
fun getEpisodesViewModel(): EpisodesViewModel = koin.get()
fun getFavoritesViewModel(): FavoritesViewModel = koin.get()
fun getSettingsStore(): SettingsStore = koin.get()
fun getSettingsViewModel(): SettingsViewModel = koin.get()
