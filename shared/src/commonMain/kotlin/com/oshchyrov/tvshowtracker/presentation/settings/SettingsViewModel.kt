package com.oshchyrov.tvshowtracker.presentation.settings

import com.oshchyrov.tvshowtracker.domain.model.AppLanguage
import com.oshchyrov.tvshowtracker.domain.model.AppSettings
import com.oshchyrov.tvshowtracker.domain.model.ThemeMode
import com.oshchyrov.tvshowtracker.presentation.base.BaseViewModel
import kotlinx.coroutines.launch

data class SettingsState(
    val settings: AppSettings = AppSettings(),
)

sealed interface SettingsIntent {
    data class SetTheme(val themeMode: ThemeMode) : SettingsIntent
    data class SetLanguage(val language: AppLanguage) : SettingsIntent
}

class SettingsViewModel(
    private val settingsStore: SettingsStore,
) : BaseViewModel<SettingsState, SettingsIntent>(SettingsState(settingsStore.settings.value)) {

    init {
        scope.launch {
            settingsStore.settings.collect { settings ->
                updateState { copy(settings = settings) }
            }
        }
    }

    override fun handleIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.SetTheme -> settingsStore.setTheme(intent.themeMode)
            is SettingsIntent.SetLanguage -> settingsStore.setLanguage(intent.language)
        }
    }

    fun setTheme(themeMode: ThemeMode) {
        handleIntent(SettingsIntent.SetTheme(themeMode))
    }

    fun setLanguage(language: AppLanguage) {
        handleIntent(SettingsIntent.SetLanguage(language))
    }
}
