package com.oshchyrov.tvshowtracker.di

import org.koin.core.Koin
import org.koin.core.context.startKoin

lateinit var koin: Koin
    private set

fun initKoin() {
    val app = startKoin {
        modules(commonModule, platformModule)
    }
    koin = app.koin
}
