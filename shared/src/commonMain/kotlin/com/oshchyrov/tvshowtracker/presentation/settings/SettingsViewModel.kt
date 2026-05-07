package com.oshchyrov.tvshowtracker.presentation.settings

import com.oshchyrov.tvshowtracker.domain.model.AppLanguage
import com.oshchyrov.tvshowtracker.domain.model.AppSettings
import com.oshchyrov.tvshowtracker.domain.model.ThemeMode
import com.oshchyrov.tvshowtracker.presentation.base.BaseViewModel

data class SettingsState(
    val settings: AppSettings = AppSettings(),
)

sealed interface SettingsIntent {
    data class SetTheme(val themeMode: ThemeMode) : SettingsIntent
    data class SetLanguage(val language: AppLanguage) : SettingsIntent
}

class SettingsViewModel : BaseViewModel<SettingsState, SettingsIntent>(SettingsState()) {

    override fun handleIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.SetTheme -> updateState {
                copy(settings = settings.copy(themeMode = intent.themeMode))
            }
            is SettingsIntent.SetLanguage -> updateState {
                copy(settings = settings.copy(language = intent.language))
            }
        }
    }

    fun setTheme(themeMode: ThemeMode) {
        handleIntent(SettingsIntent.SetTheme(themeMode))
    }

    fun setLanguage(language: AppLanguage) {
        handleIntent(SettingsIntent.SetLanguage(language))
    }
}

