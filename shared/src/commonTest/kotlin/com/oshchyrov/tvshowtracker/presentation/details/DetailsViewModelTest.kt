package com.oshchyrov.tvshowtracker.presentation.details

import com.oshchyrov.tvshowtracker.domain.model.AppError
import com.oshchyrov.tvshowtracker.domain.model.Outcome
import com.oshchyrov.tvshowtracker.test.FakeFavoriteRepository
import com.oshchyrov.tvshowtracker.test.FakeShowRepository
import com.oshchyrov.tvshowtracker.test.testEpisode
import com.oshchyrov.tvshowtracker.test.testShow
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
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModelTest {

    private lateinit var showRepository: FakeShowRepository
    private lateinit var favoriteRepository: FakeFavoriteRepository
    private lateinit var viewModel: DetailsViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        showRepository = FakeShowRepository()
        favoriteRepository = FakeFavoriteRepository()
        viewModel = DetailsViewModel(showRepository, favoriteRepository)
    }

    @AfterTest
    fun tearDown() {
        viewModel.clear()
        Dispatchers.resetMain()
    }

    @Test
    fun loadShowPopulatesState() = runTest {
        val show = testShow(id = 1, name = "Breaking Bad")
        showRepository.showsToReturn = listOf(show)
        showRepository.episodesToReturn = listOf(
            testEpisode(id = 10, showId = 1),
            testEpisode(id = 11, showId = 1),
        )

        viewModel.load(1)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertNotNull(state.show)
        assertEquals("Breaking Bad", state.show?.name)
        assertFalse(state.isLoading)
        assertEquals(2, state.totalEpisodes)
    }

    @Test
    fun loadShowSetsErrorOnFailure() = runTest {
        showRepository.shouldFail = true
        showRepository.error = AppError.NotFound

        viewModel.load(999)
        advanceUntilIdle()

        assertNotNull(viewModel.state.value.error)
        assertFalse(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.show)
    }

    @Test
    fun toggleFavoriteAddsShow() = runTest {
        val show = testShow(id = 1)
        showRepository.showsToReturn = listOf(show)
        showRepository.episodesToReturn = emptyList()

        viewModel.load(1)
        advanceUntilIdle()
        viewModel.toggleFavoriteSelection()
        advanceUntilIdle()

        assertTrue(viewModel.state.value.isFavorite)
    }

    @Test
    fun toggleFavoriteRemovesShow() = runTest {
        val show = testShow(id = 1)
        showRepository.showsToReturn = listOf(show)
        favoriteRepository.addFavorite(show, totalEpisodes = 10)

        viewModel.load(1)
        advanceUntilIdle()
        viewModel.toggleFavoriteSelection()
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isFavorite)
    }

    @Test
    fun watchedCountUpdatesFromRepository() = runTest {
        val show = testShow(id = 1)
        showRepository.showsToReturn = listOf(show)
        favoriteRepository.markEpisodeWatched(1, episodeId = 10, season = 1, number = 1)
        favoriteRepository.markEpisodeWatched(1, episodeId = 11, season = 1, number = 2)

        viewModel.load(1)
        advanceUntilIdle()

        assertEquals(2, viewModel.state.value.watchedCount)
    }
}
