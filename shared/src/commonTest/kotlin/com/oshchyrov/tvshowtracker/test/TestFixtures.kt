package com.oshchyrov.tvshowtracker.test

import com.oshchyrov.tvshowtracker.domain.model.Episode
import com.oshchyrov.tvshowtracker.domain.model.Show

fun testShow(
    id: Int = 1,
    name: String = "Test Show",
) = Show(
    id = id,
    name = name,
    summary = "Summary",
    imageUrl = "http://original.jpg",
    imageMediumUrl = "http://medium.jpg",
    genres = listOf("Drama"),
    rating = 8.5,
    runtime = 45,
    language = "English",
    status = "Running",
    premiered = "2020-01-01",
    network = "HBO",
    webChannel = null,
    officialSite = "https://example.com",
)

fun testEpisode(
    id: Int = 100,
    showId: Int = 1,
    season: Int = 1,
    number: Int = 1,
    isWatched: Boolean = false,
) = Episode(
    id = id,
    showId = showId,
    name = "Episode $number",
    season = season,
    number = number,
    airdate = "2020-01-01",
    runtime = 45,
    imageUrl = null,
    summary = "Episode summary",
    isWatched = isWatched,
)
