package com.oshchyrov.tvshowtracker.presentation.settings

import com.oshchyrov.tvshowtracker.data.local.settings.SettingsStorage
import com.oshchyrov.tvshowtracker.domain.model.AppLanguage
import com.oshchyrov.tvshowtracker.domain.model.ThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private lateinit var storage: FakeSettingsStorage
    private lateinit var settingsStore: SettingsStore
    private lateinit var viewModel: SettingsViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        storage = FakeSettingsStorage()
        settingsStore = SettingsStore(storage)
        viewModel = SettingsViewModel(settingsStore)
    }

    @AfterTest
    fun tearDown() {
        viewModel.clear()
        Dispatchers.resetMain()
    }

    @Test
    fun initialStateReflectsStore() = runTest {
        advanceUntilIdle()
        assertEquals(ThemeMode.SYSTEM, viewModel.state.value.settings.themeMode)
        assertEquals(AppLanguage.ENGLISH, viewModel.state.value.settings.language)
    }

    @Test
    fun setThemeUpdatesState() = runTest {
        viewModel.setTheme(ThemeMode.DARK)
        advanceUntilIdle()

        assertEquals(ThemeMode.DARK, viewModel.state.value.settings.themeMode)
    }

    @Test
    fun setLanguageUpdatesState() = runTest {
        viewModel.setLanguage(AppLanguage.UKRAINIAN)
        advanceUntilIdle()

        assertEquals(AppLanguage.UKRAINIAN, viewModel.state.value.settings.language)
    }

    private class FakeSettingsStorage(
        private val values: MutableMap<String, String> = mutableMapOf(),
    ) : SettingsStorage {
        override fun getString(key: String): String? = values[key]
        override fun putString(key: String, value: String) {
            values[key] = value
        }
    }
}
