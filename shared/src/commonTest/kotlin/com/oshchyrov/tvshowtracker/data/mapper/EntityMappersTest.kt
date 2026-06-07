package com.oshchyrov.tvshowtracker.data.mapper

import com.oshchyrov.tvshowtracker.test.testShow
import kotlin.test.Test
import kotlin.test.assertEquals

class EntityMappersTest {

    @Test
    fun showToEntityAndBackPreservesFields() {
        val show = testShow(id = 42, name = "Succession")
        val entity = show.toEntity(totalEpisodes = 39)
        val restored = entity.toDomain()

        assertEquals(show.id, restored.id)
        assertEquals(show.name, restored.name)
        assertEquals(show.summary, restored.summary)
        assertEquals(show.imageUrl, restored.imageUrl)
        assertEquals(show.genres, restored.genres)
        assertEquals(show.rating, restored.rating)
        assertEquals(show.network, restored.network)
    }

    @Test
    fun entityToDomainSplitsGenres() {
        val show = testShow()
        val entity = show.toEntity(totalEpisodes = 10).copy(genres = "Drama,Crime,Thriller")
        val domain = entity.toDomain()

        assertEquals(listOf("Drama", "Crime", "Thriller"), domain.genres)
    }

    @Test
    fun entityToDomainHandlesEmptyGenres() {
        val show = testShow()
        val entity = show.toEntity(totalEpisodes = 5).copy(genres = "")
        val domain = entity.toDomain()

        assertEquals(emptyList(), domain.genres)
    }
}
