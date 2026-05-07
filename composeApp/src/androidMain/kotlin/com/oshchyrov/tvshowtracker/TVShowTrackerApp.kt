package com.oshchyrov.tvshowtracker

import android.app.Application
import com.oshchyrov.tvshowtracker.di.commonModule
import com.oshchyrov.tvshowtracker.di.platformModule
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TVShowTrackerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Napier.base(DebugAntilog())
        startKoin {
            androidContext(this@TVShowTrackerApp)
            modules(commonModule, platformModule)
        }
    }
}
