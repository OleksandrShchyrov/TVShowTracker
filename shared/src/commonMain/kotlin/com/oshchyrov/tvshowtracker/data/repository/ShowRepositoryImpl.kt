package com.oshchyrov.tvshowtracker.data.repository

import com.oshchyrov.tvshowtracker.data.mapper.toDomain
import com.oshchyrov.tvshowtracker.data.remote.api.TvMazeApi
import com.oshchyrov.tvshowtracker.domain.model.AppError
import com.oshchyrov.tvshowtracker.domain.model.Episode
import com.oshchyrov.tvshowtracker.domain.model.Outcome
import com.oshchyrov.tvshowtracker.domain.model.Show
import com.oshchyrov.tvshowtracker.domain.logging.Logger
import com.oshchyrov.tvshowtracker.domain.repository.ShowRepository
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ShowRepositoryImpl(
    private val api: TvMazeApi,
    private val logger: Logger,
) : ShowRepository {

    private val cacheMutex = Mutex()
    private val showCache = mutableMapOf<Int, Show>()
    private val episodesCache = mutableMapOf<Int, List<Episode>>()
    private val showsPageCache = mutableMapOf<Int, List<Show>>()

    override suspend fun getInitialShows(page: Int): Outcome<List<Show>> = execute("Load initial shows failed") {
        cacheMutex.withLock { showsPageCache[page] } ?: run {
            val shows = api.getShowsPage(page).map { it.toDomain() }
            cacheMutex.withLock {
                showsPageCache[page] = shows
                shows.forEach { showCache[it.id] = it }
            }
            shows
        }
    }

    override suspend fun searchShows(query: String): Outcome<List<Show>> = execute("Search failed") {
        val results = api.searchShows(query)
        val shows = results.map { it.show.toDomain() }
        cacheMutex.withLock {
            shows.forEach { showCache[it.id] = it }
        }
        shows
    }

    override suspend fun getShowDetails(showId: Int): Outcome<Show> = execute("Get show details failed") {
        cacheMutex.withLock { showCache[showId] } ?: run {
            val show = api.getShow(showId).toDomain()
            cacheMutex.withLock { showCache[show.id] = show }
            show
        }
    }

    override suspend fun getEpisodes(showId: Int): Outcome<List<Episode>> = execute("Get episodes failed") {
        cacheMutex.withLock { episodesCache[showId] } ?: run {
            val episodes = api.getEpisodes(showId).mapNotNull { dto ->
                // Filter out specials (season 0) or episodes without number
                if (dto.season == null || dto.season == 0 || dto.number == null || dto.number == 0) null
                else dto.toDomain(showId)
            }
            cacheMutex.withLock { episodesCache[showId] = episodes }
            episodes
        }
    }

    private suspend fun <T> execute(message: String, block: suspend () -> T): Outcome<T> {
        return try {
            Outcome.Success(block())
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (throwable: Throwable) {
            logger.error(message, throwable)
            Outcome.Failure(throwable.toAppError())
        }
    }

    private fun Throwable.toAppError(): AppError = when (this) {
        is HttpRequestTimeoutException -> AppError.Timeout
        is ResponseException -> {
            if (response.status.value == 404) AppError.NotFound else AppError.Network
        }
        else -> AppError.Unknown
    }
}

