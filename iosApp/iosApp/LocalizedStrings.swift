import Foundation
import Shared

enum L10n {
    static func t(_ key: String, _ language: AppLanguage) -> String {
        switch language {
        case .ukrainian:
            return uk[key] ?? en[key] ?? key
        default:
            return en[key] ?? key
        }
    }

    private static let en: [String: String] = [
        "search": "Search",
        "search_shows": "Search Shows",
        "browse_shows": "Browse Shows",
        "my_shows": "My Shows",
        "settings": "Settings",
        "search_placeholder": "Search TV shows...",
        "popular_picks": "Popular catalog picks from TVmaze",
        "no_shows_found": "No shows found",
        "loading_shows": "Loading shows to browse...",
        "retry": "Retry",
        "error": "Error",
        "details": "Details",
        "episodes": "Episodes",
        "view_episodes": "View Episodes",
        "watch_progress": "Watch Progress",
        "status": "Status",
        "premiered": "Premiered",
        "network": "Network",
        "season": "Season",
        "watch_all": "Watch All",
        "unwatch_all": "Unwatch All",
        "no_saved_shows": "No saved shows yet",
        "add_shows_hint": "Search and add shows to your list",
        "next": "Next",
        "theme": "Theme",
        "theme_system": "System",
        "theme_light": "Light",
        "theme_dark": "Dark",
        "language": "Language",
        "english": "English",
        "ukrainian": "Українська",
        "episodes_count": "episodes"
    ]

    private static let uk: [String: String] = [
        "search": "Пошук",
        "search_shows": "Пошук серіалів",
        "browse_shows": "Каталог серіалів",
        "my_shows": "Мої серіали",
        "settings": "Налаштування",
        "search_placeholder": "Шукати серіали...",
        "popular_picks": "Популярні серіали з TVmaze",
        "no_shows_found": "Серіалів не знайдено",
        "loading_shows": "Завантаження серіалів...",
        "retry": "Повторити",
        "error": "Помилка",
        "details": "Деталі",
        "episodes": "Епізоди",
        "view_episodes": "Переглянути епізоди",
        "watch_progress": "Прогрес перегляду",
        "status": "Статус",
        "premiered": "Прем'єра",
        "network": "Мережа",
        "season": "Сезон",
        "watch_all": "Позначити все",
        "unwatch_all": "Скасувати все",
        "no_saved_shows": "Ще немає збережених серіалів",
        "add_shows_hint": "Шукайте та додавайте серіали до списку",
        "next": "Далі",
        "theme": "Тема",
        "theme_system": "Системна",
        "theme_light": "Світла",
        "theme_dark": "Темна",
        "language": "Мова",
        "english": "English",
        "ukrainian": "Українська",
        "episodes_count": "епізодів"
    ]
}

