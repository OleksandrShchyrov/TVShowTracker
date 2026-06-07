package com.oshchyrov.tvshowtracker.data.remote.dto

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DtosSerializationTest {

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    @Test
    fun showDtoDeserializesFromJson() {
        val dto = json.decodeFromString<ShowDto>(
            """
            {
              "id": 1,
              "name": "Test Show",
              "genres": ["Drama"],
              "rating": { "average": 8.0 },
              "network": { "name": "HBO", "country": { "name": "US" } },
              "webChannel": { "name": "Netflix" }
            }
            """.trimIndent(),
        )

        assertEquals(1, dto.id)
        assertEquals("Test Show", dto.name)
        assertEquals("HBO", dto.network?.name)
        assertEquals("Netflix", dto.webChannel?.name)
    }

    @Test
    fun episodeDtoDeserializesFromJson() {
        val dto = json.decodeFromString<EpisodeDto>(
            """
            {
              "id": 10,
              "name": "Pilot",
              "season": 1,
              "number": 1,
              "image": { "medium": "http://img.jpg" }
            }
            """.trimIndent(),
        )

        assertEquals(10, dto.id)
        assertNotNull(dto.image)
        assertEquals("http://img.jpg", dto.image.medium)
    }

    @Test
    fun searchResultDtoDeserializesFromJson() {
        val dto = json.decodeFromString<SearchResultDto>(
            """
            {
              "score": 0.95,
              "show": { "id": 5, "name": "Found Show" }
            }
            """.trimIndent(),
        )

        assertEquals(0.95, dto.score)
        assertEquals("Found Show", dto.show.name)
    }
}
