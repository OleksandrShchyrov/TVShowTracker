package com.oshchyrov.tvshowtracker.data.remote.api

import com.oshchyrov.tvshowtracker.data.remote.dto.EpisodeDto
import com.oshchyrov.tvshowtracker.data.remote.dto.SearchResultDto
import com.oshchyrov.tvshowtracker.data.remote.dto.ShowDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class TvMazeApi(private val client: HttpClient) {

    companion object {
        private const val BASE_URL = "https://api.tvmaze.com"
    }

    suspend fun getShowsPage(page: Int): List<ShowDto> {
        return client.get("$BASE_URL/shows") {
            parameter("page", page)
        }.body()
    }

    suspend fun searchShows(query: String): List<SearchResultDto> {
        return client.get("$BASE_URL/search/shows") {
            parameter("q", query)
        }.body()
    }

    suspend fun getShow(id: Int): ShowDto {
        return client.get("$BASE_URL/shows/$id").body()
    }

    suspend fun getEpisodes(showId: Int): List<EpisodeDto> {
        return client.get("$BASE_URL/shows/$showId/episodes").body()
    }
}
