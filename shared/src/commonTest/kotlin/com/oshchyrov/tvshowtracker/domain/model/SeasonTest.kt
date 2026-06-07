package com.oshchyrov.tvshowtracker.domain.model

import com.oshchyrov.tvshowtracker.test.testEpisode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SeasonTest {

    @Test
    fun seasonHoldsEpisodes() {
        val episodes = listOf(
            testEpisode(id = 1, number = 1),
            testEpisode(id = 2, number = 2),
        )
        val season = Season(number = 1, episodes = episodes)
        assertEquals(1, season.number)
        assertEquals(2, season.episodes.size)
    }

    @Test
    fun seasonCanBeEmpty() {
        val season = Season(number = 2, episodes = emptyList())
        assertTrue(season.episodes.isEmpty())
    }
}
