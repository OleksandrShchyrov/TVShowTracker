package com.oshchyrov.tvshowtracker.presentation.episodes

import com.oshchyrov.tvshowtracker.domain.model.AppError
import com.oshchyrov.tvshowtracker.test.FakeFavoriteRepository
import com.oshchyrov.tvshowtracker.test.FakeShowRepository
import com.oshchyrov.tvshowtracker.test.testEpisode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class EpisodesViewModelTest {

    private lateinit var showRepository: FakeShowRepository
    private lateinit var favoriteRepository: FakeFavoriteRepository
    private lateinit var viewModel: EpisodesViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        showRepository = FakeShowRepository()
        favoriteRepository = FakeFavoriteRepository()
        viewModel = EpisodesViewModel(showRepository, favoriteRepository)
    }

    @AfterTest
    fun tearDown() {
        viewModel.clear()
        Dispatchers.resetMain()
    }

    @Test
    fun loadGroupsEpisodesBySeason() = runTest {
        showRepository.episodesToReturn = listOf(
            testEpisode(id = 1, season = 1, number = 1),
            testEpisode(id = 2, season = 1, number = 2),
            testEpisode(id = 3, season = 2, number = 1),
        )

        viewModel.load(1)
        advanceUntilIdle()

        val seasons = viewModel.state.value.seasons
        assertEquals(2, seasons.size)
        assertEquals(1, seasons[0].number)
        assertEquals(2, seasons[0].episodes.size)
        assertEquals(2, seasons[1].number)
    }

    @Test
    fun loadSetsErrorOnFailure() = runTest {
        showRepository.shouldFail = true
        showRepository.error = AppError.Network

        viewModel.load(1)
        advanceUntilIdle()

        assertNotNull(viewModel.state.value.error)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun toggleEpisodeWatchedMarksEpisode() = runTest {
        showRepository.episodesToReturn = listOf(testEpisode(id = 10, season = 1, number = 1))

        viewModel.load(1)
        advanceUntilIdle()
        viewModel.toggleEpisodeWatched(episodeId = 10, season = 1, number = 1, isWatched = false)
        advanceUntilIdle()

        assertEquals(setOf(10), favoriteRepository.getWatchedEpisodeIds(1).first())
    }

    @Test
    fun toggleSeasonWatchedMarksAllSeasonEpisodes() = runTest {
        showRepository.episodesToReturn = listOf(
            testEpisode(id = 1, season = 1, number = 1),
            testEpisode(id = 2, season = 1, number = 2),
            testEpisode(id = 3, season = 2, number = 1),
        )

        viewModel.load(1)
        advanceUntilIdle()
        viewModel.toggleSeasonWatched(seasonNumber = 1, markWatched = true)
        advanceUntilIdle()

        val season1Episodes = viewModel.state.value.seasons.first { it.number == 1 }.episodes
        assertTrue(season1Episodes.all { it.isWatched })
    }
}
