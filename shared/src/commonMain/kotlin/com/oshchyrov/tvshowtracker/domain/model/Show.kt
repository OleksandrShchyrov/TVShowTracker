package com.oshchyrov.tvshowtracker.domain.model

data class Show(
    val id: Int,
    val name: String,
    val summary: String?,
    val imageUrl: String?,
    val imageMediumUrl: String?,
    val genres: List<String>,
    val rating: Double?,
    val runtime: Int?,
    val language: String?,
    val status: String?,
    val premiered: String?,
    val network: String?,
    val webChannel: String?,
    val officialSite: String?,
)

