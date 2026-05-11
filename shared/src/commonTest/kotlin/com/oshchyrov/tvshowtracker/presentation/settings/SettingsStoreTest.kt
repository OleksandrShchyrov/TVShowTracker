package com.oshchyrov.tvshowtracker.presentation.settings

import com.oshchyrov.tvshowtracker.data.local.settings.SettingsStorage
import com.oshchyrov.tvshowtracker.domain.model.AppLanguage
import com.oshchyrov.tvshowtracker.domain.model.ThemeMode
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsStoreTest {

    @Test
    fun `loads saved settings from storage`() {
        val storage = FakeSettingsStorage(
            mutableMapOf(
                THEME_MODE_KEY to ThemeMode.DARK.name,
                LANGUAGE_KEY to AppLanguage.UKRAINIAN.code,
            )
        )

        val store = SettingsStore(storage)

        assertEquals(ThemeMode.DARK, store.settings.value.themeMode)
        assertEquals(AppLanguage.UKRAINIAN, store.settings.value.language)
    }

    @Test
    fun `updates theme and persists it`() {
        val storage = FakeSettingsStorage()
        val store = SettingsStore(storage)

        store.setTheme(ThemeMode.LIGHT)

        assertEquals(ThemeMode.LIGHT, store.settings.value.themeMode)
        assertEquals(ThemeMode.LIGHT.name, storage.values[THEME_MODE_KEY])
    }

    @Test
    fun `updates language and persists it`() {
        val storage = FakeSettingsStorage()
        val store = SettingsStore(storage)

        store.setLanguage(AppLanguage.UKRAINIAN)

        assertEquals(AppLanguage.UKRAINIAN, store.settings.value.language)
        assertEquals(AppLanguage.UKRAINIAN.code, storage.values[LANGUAGE_KEY])
    }

    private class FakeSettingsStorage(
        val values: MutableMap<String, String> = mutableMapOf(),
    ) : SettingsStorage {
        override fun getString(key: String): String? = values[key]

        override fun putString(key: String, value: String) {
            values[key] = value
        }
    }

    private companion object {
        const val THEME_MODE_KEY = "theme_mode"
        const val LANGUAGE_KEY = "language"
    }
}
