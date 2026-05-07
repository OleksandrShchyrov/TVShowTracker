package com.oshchyrov.tvshowtracker.domain.model

data class Season(
    val number: Int,
    val episodes: List<Episode>,
)

