package com.oshchyrov.tvshowtracker.domain.model

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

enum class AppLanguage(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    UKRAINIAN("uk", "Українська"),
}

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val language: AppLanguage = AppLanguage.ENGLISH,
)

