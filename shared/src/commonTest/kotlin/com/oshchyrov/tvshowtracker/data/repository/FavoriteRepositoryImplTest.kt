package com.oshchyrov.tvshowtracker.data.repository

import com.oshchyrov.tvshowtracker.test.FakeFavoriteShowDao
import com.oshchyrov.tvshowtracker.test.FakeWatchedEpisodeDao
import com.oshchyrov.tvshowtracker.test.testEpisode
import com.oshchyrov.tvshowtracker.test.testShow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FavoriteRepositoryImplTest {

    private val favoriteDao = FakeFavoriteShowDao()
    private val watchedDao = FakeWatchedEpisodeDao()
    private val repository = FavoriteRepositoryImpl(favoriteDao, watchedDao)

    @Test
    fun addFavoriteInsertsShow() = runTest {
        val show = testShow(id = 1, name = "Breaking Bad")

        repository.addFavorite(show, totalEpisodes = 62)

        val favorites = repository.getFavoriteShows().first()
        assertEquals(1, favorites.size)
        assertEquals("Breaking Bad", favorites[0].name)
    }

    @Test
    fun isFavoriteReturnsTrueAfterAdd() = runTest {
        val show = testShow(id = 2)

        repository.addFavorite(show, totalEpisodes = 10)

        assertTrue(repository.isFavorite(2).first())
    }

    @Test
    fun removeFavoriteDeletesShowAndWatchedEpisodes() = runTest {
        val show = testShow(id = 3)
        repository.addFavorite(show, totalEpisodes = 10)
        repository.markEpisodeWatched(3, episodeId = 100, season = 1, number = 1)

        repository.removeFavorite(3)

        assertFalse(repository.isFavorite(3).first())
        assertTrue(repository.getWatchedEpisodeIds(3).first().isEmpty())
    }

    @Test
    fun markEpisodeWatchedTracksEpisode() = runTest {
        repository.markEpisodeWatched(showId = 1, episodeId = 10, season = 1, number = 1)

        val watched = repository.getWatchedEpisodeIds(1).first()
        assertEquals(setOf(10), watched)
    }

    @Test
    fun markEpisodeUnwatchedRemovesEpisode() = runTest {
        repository.markEpisodeWatched(showId = 1, episodeId = 10, season = 1, number = 1)
        repository.markEpisodeUnwatched(showId = 1, episodeId = 10)

        assertTrue(repository.getWatchedEpisodeIds(1).first().isEmpty())
    }

    @Test
    fun markSeasonWatchedMarksAllEpisodes() = runTest {
        val episodes = listOf(
            testEpisode(id = 1, season = 1, number = 1),
            testEpisode(id = 2, season = 1, number = 2),
        )

        repository.markSeasonWatched(showId = 1, episodes = episodes)

        assertEquals(setOf(1, 2), repository.getWatchedEpisodeIds(1).first())
    }

    @Test
    fun markSeasonUnwatchedClearsSeason() = runTest {
        repository.markEpisodeWatched(showId = 1, episodeId = 1, season = 1, number = 1)
        repository.markEpisodeWatched(showId = 1, episodeId = 2, season = 2, number = 1)

        repository.markSeasonUnwatched(showId = 1, season = 1)

        assertEquals(setOf(2), repository.getWatchedEpisodeIds(1).first())
    }

    @Test
    fun getWatchedCountReturnsCorrectCount() = runTest {
        repository.markEpisodeWatched(showId = 1, episodeId = 1, season = 1, number = 1)
        repository.markEpisodeWatched(showId = 1, episodeId = 2, season = 1, number = 2)

        assertEquals(2, repository.getWatchedCount(1))
    }

    @Test
    fun getTotalEpisodesReturnsStoredValue() = runTest {
        repository.addFavorite(testShow(id = 5), totalEpisodes = 24)

        assertEquals(24, repository.getTotalEpisodes(5))
    }

    @Test
    fun entityRoundTripPreservesData() = runTest {
        val show = testShow(id = 7, name = "The Wire")
        repository.addFavorite(show, totalEpisodes = 60)

        val stored = repository.getFavoriteShows().first().first()
        assertEquals(show.id, stored.id)
        assertEquals(show.name, stored.name)
        assertEquals(show.genres, stored.genres)
    }
}
