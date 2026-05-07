package com.oshchyrov.tvshowtracker.util

import com.oshchyrov.tvshowtracker.domain.model.AppLanguage

object Strings {
    fun get(key: String, language: AppLanguage): String {
        return when (language) {
            AppLanguage.UKRAINIAN -> ukrainianStrings[key] ?: englishStrings[key] ?: key
            AppLanguage.ENGLISH -> englishStrings[key] ?: key
        }
    }

    private val englishStrings = mapOf(
        "search_shows" to "Search Shows",
        "browse_shows" to "Browse Shows",
        "my_shows" to "My Shows",
        "settings" to "Settings",
        "search_placeholder" to "Search TV shows...",
        "popular_picks" to "Popular catalog picks from TVmaze",
        "no_shows_found" to "No shows found",
        "loading_shows" to "Loading shows to browse...",
        "retry" to "Retry",
        "error" to "Error",
        "show_details" to "Show Details",
        "episodes" to "Episodes",
        "view_episodes" to "View Episodes",
        "watch_progress" to "Watch Progress",
        "status" to "Status",
        "premiered" to "Premiered",
        "network" to "Network",
        "streaming" to "Streaming",
        "season" to "Season",
        "no_saved_shows" to "No saved shows yet",
        "add_shows_hint" to "Search and add shows to your list",
        "watch_all" to "Watch All",
        "unwatch_all" to "Unwatch All",
        "next" to "Next",
        "back" to "Back",
        "favorite" to "Favorite",
        "remove" to "Remove",
        "theme" to "Theme",
        "language" to "Language",
        "theme_light" to "Light",
        "theme_dark" to "Dark",
        "theme_system" to "System",
        "episodes_format" to "episodes",
        "swipe_to_remove" to "Swipe to remove",
    )

    private val ukrainianStrings = mapOf(
        "search_shows" to "Пошук серіалів",
        "browse_shows" to "Каталог серіалів",
        "my_shows" to "Мої серіали",
        "settings" to "Налаштування",
        "search_placeholder" to "Шукати серіали...",
        "popular_picks" to "Популярні серіали з TVmaze",
        "no_shows_found" to "Серіалів не знайдено",
        "loading_shows" to "Завантаження серіалів...",
        "retry" to "Повторити",
        "error" to "Помилка",
        "show_details" to "Деталі серіалу",
        "episodes" to "Епізоди",
        "view_episodes" to "Переглянути епізоди",
        "watch_progress" to "Прогрес перегляду",
        "status" to "Статус",
        "premiered" to "Прем'єра",
        "network" to "Мережа",
        "streaming" to "Стрімінг",
        "season" to "Сезон",
        "no_saved_shows" to "Ще немає збережених серіалів",
        "add_shows_hint" to "Шукайте та додавайте серіали до списку",
        "watch_all" to "Дивитись все",
        "unwatch_all" to "Скасувати все",
        "next" to "Далі",
        "back" to "Назад",
        "favorite" to "Обране",
        "remove" to "Видалити",
        "theme" to "Тема",
        "language" to "Мова",
        "theme_light" to "Світла",
        "theme_dark" to "Темна",
        "theme_system" to "Системна",
        "episodes_format" to "епізодів",
        "swipe_to_remove" to "Свайпніть щоб видалити",
    )
}

