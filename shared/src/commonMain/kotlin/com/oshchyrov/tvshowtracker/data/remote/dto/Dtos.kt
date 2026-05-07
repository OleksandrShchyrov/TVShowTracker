package com.oshchyrov.tvshowtracker.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SearchResultDto(
    val score: Double? = null,
    val show: ShowDto,
)

@Serializable
data class ShowDto(
    val id: Int,
    val name: String? = null,
    val summary: String? = null,
    val image: ImageDto? = null,
    val genres: List<String>? = null,
    val rating: RatingDto? = null,
    val runtime: Int? = null,
    val language: String? = null,
    val status: String? = null,
    val premiered: String? = null,
    val network: NetworkDto? = null,
    val webChannel: WebChannelDto? = null,
    val officialSite: String? = null,
)

@Serializable
data class ImageDto(
    val medium: String? = null,
    val original: String? = null,
)

@Serializable
data class RatingDto(
    val average: Double? = null,
)

@Serializable
data class NetworkDto(
    val name: String? = null,
    val country: CountryDto? = null,
)

@Serializable
data class CountryDto(
    val name: String? = null,
)

@Serializable
data class WebChannelDto(
    val name: String? = null,
)

@Serializable
data class EpisodeDto(
    val id: Int,
    val name: String? = null,
    val season: Int? = null,
    val number: Int? = null,
    val airdate: String? = null,
    val runtime: Int? = null,
    val image: ImageDto? = null,
    val summary: String? = null,
)

