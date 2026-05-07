package com.oshchyrov.tvshowtracker.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class FavoriteShowTest {

    @Test
    fun progressPercentageCalculatesCorrectly() {
        val fav = FavoriteShow(
            show = testShow(),
            totalEpisodes = 10,
            watchedEpisodes = 5,
            nextUnwatchedEpisode = null,
        )
        assertEquals(0.5f, fav.progressPercentage)
    }

    @Test
    fun progressPercentageIsZeroWhenNoEpisodes() {
        val fav = FavoriteShow(
            show = testShow(),
            totalEpisodes = 0,
            watchedEpisodes = 0,
            nextUnwatchedEpisode = null,
        )
        assertEquals(0f, fav.progressPercentage)
    }

    @Test
    fun progressPercentageIsOneWhenAllWatched() {
        val fav = FavoriteShow(
            show = testShow(),
            totalEpisodes = 8,
            watchedEpisodes = 8,
            nextUnwatchedEpisode = null,
        )
        assertEquals(1f, fav.progressPercentage)
    }

    private fun testShow() = Show(
        id = 1, name = "Test", summary = null, imageUrl = null, imageMediumUrl = null,
        genres = emptyList(), rating = null, runtime = null, language = null,
        status = null, premiered = null, network = null, webChannel = null, officialSite = null,
    )
}

