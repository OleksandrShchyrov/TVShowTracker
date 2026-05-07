package com.oshchyrov.tvshowtracker.data.repository

import com.oshchyrov.tvshowtracker.data.mapper.toDomain
import com.oshchyrov.tvshowtracker.data.remote.api.TvMazeApi
import com.oshchyrov.tvshowtracker.domain.model.Episode
import com.oshchyrov.tvshowtracker.domain.model.Show
import com.oshchyrov.tvshowtracker.domain.repository.ShowRepository
import io.github.aakira.napier.Napier

class ShowRepositoryImpl(
    private val api: TvMazeApi,
) : ShowRepository {

    // Simple in-memory cache to avoid repeated API calls
    private val showCache = mutableMapOf<Int, Show>()
    private val episodesCache = mutableMapOf<Int, List<Episode>>()
    private val showsPageCache = mutableMapOf<Int, List<Show>>()

    override suspend fun getInitialShows(page: Int): Result<List<Show>> = runCatching {
        showsPageCache[page] ?: api.getShowsPage(page).map { it.toDomain() }.also { shows ->
            showsPageCache[page] = shows
            shows.forEach { showCache[it.id] = it }
        }
    }.onFailure { Napier.e("Load initial shows failed", it) }

    override suspend fun searchShows(query: String): Result<List<Show>> = runCatching {
        val results = api.searchShows(query)
        results.map { it.show.toDomain() }.also { shows ->
            shows.forEach { showCache[it.id] = it }
        }
    }.onFailure { Napier.e("Search failed", it) }

    override suspend fun getShowDetails(showId: Int): Result<Show> = runCatching {
        showCache[showId] ?: api.getShow(showId).toDomain().also {
            showCache[it.id] = it
        }
    }.onFailure { Napier.e("Get show details failed", it) }

    override suspend fun getEpisodes(showId: Int): Result<List<Episode>> = runCatching {
        episodesCache[showId] ?: api.getEpisodes(showId).mapNotNull { dto ->
            // Filter out specials (season 0) or episodes without number
            if (dto.season == null || dto.season == 0 || dto.number == null || dto.number == 0) null
            else dto.toDomain(showId)
        }.also { episodesCache[showId] = it }
    }.onFailure { Napier.e("Get episodes failed", it) }
}

