package com.oshchyrov.tvshowtracker.data.remote.api

import com.oshchyrov.tvshowtracker.test.SAMPLE_EPISODES_JSON
import com.oshchyrov.tvshowtracker.test.SAMPLE_SEARCH_JSON
import com.oshchyrov.tvshowtracker.test.SAMPLE_SHOW_DETAIL_JSON
import com.oshchyrov.tvshowtracker.test.SAMPLE_SHOW_JSON
import com.oshchyrov.tvshowtracker.test.createMockHttpClient
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TvMazeApiTest {

    @Test
    fun getShowsPageReturnsShows() = runTest {
        val client = createMockHttpClient { path ->
            when {
                path == "/shows" -> 200 to SAMPLE_SHOW_JSON
                else -> 404 to "{}"
            }
        }
        val api = TvMazeApi(client)

        val shows = api.getShowsPage(0)

        assertEquals(1, shows.size)
        assertEquals("Under the Dome", shows[0].name)
    }

    @Test
    fun searchShowsReturnsResults() = runTest {
        val client = createMockHttpClient { path ->
            when {
                path == "/search/shows" -> 200 to SAMPLE_SEARCH_JSON
                else -> 404 to "{}"
            }
        }
        val api = TvMazeApi(client)

        val results = api.searchShows("breaking")

        assertEquals(1, results.size)
        assertEquals("Breaking Bad", results[0].show.name)
    }

    @Test
    fun getShowReturnsDetail() = runTest {
        val client = createMockHttpClient { path ->
            when {
                path == "/shows/169" -> 200 to SAMPLE_SHOW_DETAIL_JSON
                else -> 404 to "{}"
            }
        }
        val api = TvMazeApi(client)

        val show = api.getShow(169)

        assertEquals(169, show.id)
        assertEquals("Breaking Bad", show.name)
        assertEquals("AMC", show.network?.name)
    }

    @Test
    fun getEpisodesReturnsList() = runTest {
        val client = createMockHttpClient { path ->
            when {
                path == "/shows/169/episodes" -> 200 to SAMPLE_EPISODES_JSON
                else -> 404 to "{}"
            }
        }
        val api = TvMazeApi(client)

        val episodes = api.getEpisodes(169)

        assertEquals(3, episodes.size)
        assertEquals("Pilot", episodes[0].name)
    }
}
