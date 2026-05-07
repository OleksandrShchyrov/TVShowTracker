package com.oshchyrov.tvshowtracker.data.mapper

import com.oshchyrov.tvshowtracker.data.remote.dto.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MappersTest {

    @Test
    fun showDtoToDomainMapsAllFields() {
        val dto = ShowDto(
            id = 1,
            name = "Breaking Bad",
            summary = "<p>A chemistry teacher turns drug lord.</p>",
            image = ImageDto(medium = "http://medium.jpg", original = "http://original.jpg"),
            genres = listOf("Drama", "Crime"),
            rating = RatingDto(average = 9.5),
            runtime = 60,
            language = "English",
            status = "Ended",
            premiered = "2008-01-20",
            network = NetworkDto(name = "AMC", country = CountryDto("United States")),
            webChannel = null,
            officialSite = "http://www.amctv.com",
        )

        val show = dto.toDomain()
        assertEquals(1, show.id)
        assertEquals("Breaking Bad", show.name)
        assertEquals("A chemistry teacher turns drug lord.", show.summary)
        assertEquals("http://original.jpg", show.imageUrl)
        assertEquals("http://medium.jpg", show.imageMediumUrl)
        assertEquals(listOf("Drama", "Crime"), show.genres)
        assertEquals(9.5, show.rating)
        assertEquals(60, show.runtime)
        assertEquals("English", show.language)
        assertEquals("Ended", show.status)
        assertEquals("2008-01-20", show.premiered)
        assertEquals("AMC", show.network)
    }

    @Test
    fun showDtoWithNullsHandledGracefully() {
        val dto = ShowDto(id = 2)
        val show = dto.toDomain()
        assertEquals(2, show.id)
        assertEquals("Unknown", show.name)
        assertNull(show.summary)
        assertNull(show.imageUrl)
        assertEquals(emptyList(), show.genres)
    }

    @Test
    fun episodeDtoToDomainMapsCorrectly() {
        val dto = EpisodeDto(
            id = 100,
            name = "Pilot",
            season = 1,
            number = 1,
            airdate = "2008-01-20",
            runtime = 58,
            image = ImageDto(medium = "http://ep.jpg", original = null),
            summary = "<p>Walt begins.</p>",
        )

        val episode = dto.toDomain(showId = 1)
        assertEquals(100, episode.id)
        assertEquals(1, episode.showId)
        assertEquals("Pilot", episode.name)
        assertEquals(1, episode.season)
        assertEquals(1, episode.number)
        assertEquals("2008-01-20", episode.airdate)
        assertEquals(58, episode.runtime)
        assertEquals("http://ep.jpg", episode.imageUrl)
        assertEquals("Walt begins.", episode.summary)
    }

    @Test
    fun htmlStrippedFromSummary() {
        val dto = ShowDto(id = 3, summary = "<p>Hello <b>World</b></p>")
        val show = dto.toDomain()
        assertEquals("Hello World", show.summary)
    }
}

