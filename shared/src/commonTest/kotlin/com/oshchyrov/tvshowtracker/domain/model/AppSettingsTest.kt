package com.oshchyrov.tvshowtracker.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class AppSettingsTest {

    @Test
    fun themeModeFromStorageValueReturnsMatch() {
        assertEquals(ThemeMode.DARK, ThemeMode.fromStorageValue("DARK"))
    }

    @Test
    fun themeModeFromStorageValueDefaultsToSystem() {
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromStorageValue(null))
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromStorageValue("invalid"))
    }

    @Test
    fun appLanguageFromCodeReturnsMatch() {
        assertEquals(AppLanguage.UKRAINIAN, AppLanguage.fromCode("uk"))
    }

    @Test
    fun appLanguageFromCodeDefaultsToEnglish() {
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode(null))
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode("fr"))
    }

    @Test
    fun defaultAppSettings() {
        val settings = AppSettings()
        assertEquals(ThemeMode.SYSTEM, settings.themeMode)
        assertEquals(AppLanguage.ENGLISH, settings.language)
    }
}
