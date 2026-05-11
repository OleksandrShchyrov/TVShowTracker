package com.oshchyrov.tvshowtracker.presentation.settings

import com.oshchyrov.tvshowtracker.data.local.settings.SettingsStorage
import com.oshchyrov.tvshowtracker.domain.model.AppLanguage
import com.oshchyrov.tvshowtracker.domain.model.AppSettings
import com.oshchyrov.tvshowtracker.domain.model.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsStore(
    private val storage: SettingsStorage,
) {
    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    fun setTheme(themeMode: ThemeMode) {
        updateSettings { copy(themeMode = themeMode) }
    }

    fun setLanguage(language: AppLanguage) {
        updateSettings { copy(language = language) }
    }

    private fun updateSettings(transform: AppSettings.() -> AppSettings) {
        val updatedSettings = _settings.value.transform()
        if (updatedSettings == _settings.value) return

        persist(updatedSettings)
        _settings.value = updatedSettings
    }

    private fun loadSettings(): AppSettings {
        return AppSettings(
            themeMode = ThemeMode.fromStorageValue(storage.getString(THEME_MODE_KEY)),
            language = AppLanguage.fromCode(storage.getString(LANGUAGE_KEY)),
        )
    }

    private fun persist(settings: AppSettings) {
        storage.putString(THEME_MODE_KEY, settings.themeMode.name)
        storage.putString(LANGUAGE_KEY, settings.language.code)
    }

    private companion object {
        const val THEME_MODE_KEY = "theme_mode"
        const val LANGUAGE_KEY = "language"
    }
}
