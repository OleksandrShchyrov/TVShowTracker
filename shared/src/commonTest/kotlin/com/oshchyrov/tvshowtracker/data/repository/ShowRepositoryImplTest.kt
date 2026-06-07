package com.oshchyrov.tvshowtracker.data.repository

import com.oshchyrov.tvshowtracker.data.remote.api.TvMazeApi
import com.oshchyrov.tvshowtracker.domain.logging.Logger
import com.oshchyrov.tvshowtracker.domain.model.AppError
import com.oshchyrov.tvshowtracker.domain.model.Show
import com.oshchyrov.tvshowtracker.domain.model.Episode
import com.oshchyrov.tvshowtracker.domain.model.Outcome
import com.oshchyrov.tvshowtracker.test.SAMPLE_EPISODES_JSON
import com.oshchyrov.tvshowtracker.test.SAMPLE_SEARCH_JSON
import com.oshchyrov.tvshowtracker.test.SAMPLE_SHOW_DETAIL_JSON
import com.oshchyrov.tvshowtracker.test.SAMPLE_SHOW_JSON
import com.oshchyrov.tvshowtracker.test.createMockHttpClient
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ShowRepositoryImplTest {

    private val silentLogger = object : Logger {
        override fun error(message: String, throwable: Throwable?) = Unit
    }

    @Test
    fun getInitialShowsReturnsMappedShows() = runTest {
        val repo = createRepository { path ->
            when {
                path == "/shows" -> 200 to SAMPLE_SHOW_JSON
                else -> 404 to "{}"
            }
        }

        val result = repo.getInitialShows()

        assertIs<Outcome.Success<List<Show>>>(result)
        assertEquals(1, result.value.size)
        assertEquals("Under the Dome", result.value[0].name)
    }

    @Test
    fun getInitialShowsCachesResults() = runTest {
        var callCount = 0
        val repo = createRepository { path ->
            callCount++
            when {
                path == "/shows" -> 200 to SAMPLE_SHOW_JSON
                else -> 404 to "{}"
            }
        }

        repo.getInitialShows()
        repo.getInitialShows()

        assertEquals(1, callCount)
    }

    @Test
    fun searchShowsReturnsResults() = runTest {
        val repo = createRepository { path ->
            when {
                path == "/search/shows" -> 200 to SAMPLE_SEARCH_JSON
                else -> 404 to "{}"
            }
        }

        val result = repo.searchShows("breaking")

        assertIs<Outcome.Success<List<Show>>>(result)
        assertEquals("Breaking Bad", result.value[0].name)
    }

    @Test
    fun getShowDetailsReturnsShow() = runTest {
        val repo = createRepository { path ->
            when {
                path == "/shows/169" -> 200 to SAMPLE_SHOW_DETAIL_JSON
                else -> 404 to "{}"
            }
        }

        val result = repo.getShowDetails(169)

        assertIs<Outcome.Success<Show>>(result)
        assertEquals("Breaking Bad", result.value.name)
    }

    @Test
    fun getShowDetailsUsesCache() = runTest {
        var callCount = 0
        val repo = createRepository { path ->
            callCount++
            when {
                path == "/shows/169" -> 200 to SAMPLE_SHOW_DETAIL_JSON
                else -> 404 to "{}"
            }
        }

        repo.getShowDetails(169)
        repo.getShowDetails(169)

        assertEquals(1, callCount)
    }

    @Test
    fun getEpisodesFiltersSpecials() = runTest {
        val repo = createRepository { path ->
            when {
                path == "/shows/169/episodes" -> 200 to SAMPLE_EPISODES_JSON
                else -> 404 to "{}"
            }
        }

        val result = repo.getEpisodes(169)

        assertIs<Outcome.Success<List<Episode>>>(result)
        assertEquals(2, result.value.size)
        assertTrue(result.value.none { it.season == 0 })
    }

    @Test
    fun networkErrorReturnsFailure() = runTest {
        val repo = createRepository { _ -> 500 to "Internal Server Error" }

        val result = repo.getInitialShows()

        assertIs<Outcome.Failure>(result)
        assertEquals(AppError.Network, result.error)
    }

    @Test
    fun notFoundReturnsNotFoundError() = runTest {
        val repo = createRepository { _ -> 404 to "Not Found" }

        val result = repo.getShowDetails(999)

        assertIs<Outcome.Failure>(result)
        assertEquals(AppError.NotFound, result.error)
    }

    private fun createRepository(
        handler: suspend (String) -> Pair<Int, String>,
    ): ShowRepositoryImpl {
        val client = createMockHttpClient(handler)
        return ShowRepositoryImpl(TvMazeApi(client), silentLogger)
    }
}
