package com.oshchyrov.tvshowtracker.presentation.favorites

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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

    private lateinit var favoriteRepository: FakeFavoriteRepository
    private lateinit var showRepository: FakeShowRepository
    private lateinit var viewModel: FavoritesViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        favoriteRepository = FakeFavoriteRepository()
        showRepository = FakeShowRepository()
        viewModel = FavoritesViewModel(favoriteRepository, showRepository)
    }

    @AfterTest
    fun tearDown() {
        viewModel.clear()
        Dispatchers.resetMain()
    }

    @Test
    fun loadFavoritesShowsEmptyState() = runTest {
        viewModel.loadFavorites()
        advanceUntilIdle()

        assertTrue(viewModel.state.value.isEmpty)
        assertTrue(viewModel.state.value.favorites.isEmpty())
    }

    @Test
    fun loadFavoritesPopulatesList() = runTest {
        val show = testShow(id = 1, name = "Breaking Bad")
        favoriteRepository.addFavorite(show, totalEpisodes = 62)
        showRepository.episodesToReturn = listOf(
            testEpisode(id = 10, showId = 1, season = 1, number = 1),
        )

        viewModel.loadFavorites()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(1, state.favorites.size)
        assertEquals("Breaking Bad", state.favorites[0].show.name)
        assertEquals(62, state.favorites[0].totalEpisodes)
    }

    @Test
    fun removeFavoriteClearsFromList() = runTest {
        val show = testShow(id = 1)
        favoriteRepository.addFavorite(show, totalEpisodes = 10)
        showRepository.episodesToReturn = emptyList()

        viewModel.loadFavorites()
        advanceUntilIdle()
        viewModel.removeFavoriteShow(1)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.isEmpty)
    }

    @Test
    fun refreshDataUpdatesProgress() = runTest {
        val show = testShow(id = 1)
        favoriteRepository.addFavorite(show, totalEpisodes = 2)
        showRepository.episodesToReturn = listOf(
            testEpisode(id = 10, showId = 1, season = 1, number = 1),
            testEpisode(id = 11, showId = 1, season = 1, number = 2),
        )
        favoriteRepository.markEpisodeWatched(1, episodeId = 10, season = 1, number = 1)

        viewModel.loadFavorites()
        advanceUntilIdle()
        viewModel.refreshData()
        advanceUntilIdle()

        assertEquals(1, viewModel.state.value.favorites[0].watchedEpisodes)
    }
}
