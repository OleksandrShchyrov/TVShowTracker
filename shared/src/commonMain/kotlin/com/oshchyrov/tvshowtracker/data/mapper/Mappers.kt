package com.oshchyrov.tvshowtracker.data.mapper

import com.oshchyrov.tvshowtracker.data.local.entity.FavoriteShowEntity
import com.oshchyrov.tvshowtracker.data.remote.dto.EpisodeDto
import com.oshchyrov.tvshowtracker.data.remote.dto.ShowDto
import com.oshchyrov.tvshowtracker.domain.model.Episode
import com.oshchyrov.tvshowtracker.domain.model.Show

fun ShowDto.toDomain(): Show = Show(
    id = id,
    name = name ?: "Unknown",
    summary = summary?.stripHtml(),
    imageUrl = image?.original,
    imageMediumUrl = image?.medium,
    genres = genres ?: emptyList(),
    rating = rating?.average,
    runtime = runtime,
    language = language,
    status = status,
    premiered = premiered,
    network = network?.name,
    webChannel = webChannel?.name,
    officialSite = officialSite,
)

fun EpisodeDto.toDomain(showId: Int): Episode = Episode(
    id = id,
    showId = showId,
    name = name ?: "Episode ${number ?: "?"}",
    season = season ?: 0,
    number = number ?: 0,
    airdate = airdate,
    runtime = runtime,
    imageUrl = image?.medium,
    summary = summary?.stripHtml(),
)

fun FavoriteShowEntity.toDomain(): Show = Show(
    id = id,
    name = name,
    summary = summary,
    imageUrl = imageUrl,
    imageMediumUrl = imageMediumUrl,
    genres = genres.split(",").filter { it.isNotBlank() },
    rating = rating,
    runtime = runtime,
    language = language,
    status = status,
    premiered = premiered,
    network = network,
    webChannel = webChannel,
    officialSite = null,
)

fun Show.toEntity(totalEpisodes: Int): FavoriteShowEntity = FavoriteShowEntity(
    id = id,
    name = name,
    summary = summary,
    imageUrl = imageUrl,
    imageMediumUrl = imageMediumUrl,
    genres = genres.joinToString(","),
    rating = rating,
    runtime = runtime,
    language = language,
    status = status,
    premiered = premiered,
    network = network,
    webChannel = webChannel,
    totalEpisodes = totalEpisodes,
)

private fun String.stripHtml(): String {
    return this.replace(Regex("<[^>]*>"), "").trim()
}

