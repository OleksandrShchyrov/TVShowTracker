import Foundation
import Shared

/// Holds a Kotlin `Cancellable` so `@MainActor` wrappers can stop flow collection from
/// nonisolated `deinit` under Swift 6 strict concurrency.
final class FlowSubscription: @unchecked Sendable {
    nonisolated(unsafe) private var cancellable: (any Cancellable)?

    func set(_ cancellable: any Cancellable) {
        self.cancellable = cancellable
    }

    nonisolated func cancel() {
        cancellable?.cancel()
        cancellable = nil
    }
}

@MainActor
final class SearchViewModelWrapper: ObservableObject {
    nonisolated(unsafe) private let viewModel: SearchViewModel
    private let subscription = FlowSubscription()

    @Published var query: String = ""
    @Published var results: [Show] = []
    @Published var isLoading: Bool = false
    @Published var error: String? = nil
    @Published var isEmpty: Bool = false
    @Published var isInitialContent: Bool = false

    init() {
        viewModel = KoinHelpersKt.getSearchViewModel()
        observe()
        viewModel.loadInitialShows()
    }

    private func observe() {
        subscription.set(
            FlowWrapperKt.wrap(viewModel.state).collect(
                onEach: { [weak self] state in
                    guard let self, let state = state as? SearchState else { return }
                    Task { @MainActor in
                        self.results = state.results
                        self.isLoading = state.isLoading
                        self.error = state.error
                        self.isEmpty = state.isEmpty
                        self.isInitialContent = state.isInitialContent
                    }
                },
                onComplete: {},
                onError: { _ in }
            )
        )
    }

    func onQueryChanged(_ query: String) {
        viewModel.updateQuery(query: query)
    }

    func retry() {
        viewModel.retrySearch()
    }

    deinit {
        subscription.cancel()
        viewModel.clear()
    }
}

@MainActor
final class DetailsViewModelWrapper: ObservableObject {
    nonisolated(unsafe) private let viewModel: DetailsViewModel
    private let subscription = FlowSubscription()

    @Published var show: Show? = nil
    @Published var isFavorite: Bool = false
    @Published var isLoading: Bool = true
    @Published var watchedCount: Int = 0
    @Published var totalEpisodes: Int = 0

    init(showId: Int32) {
        viewModel = KoinHelpersKt.getDetailsViewModel()
        observe()
        viewModel.load(showId: showId)
    }

    private func observe() {
        subscription.set(
            FlowWrapperKt.wrap(viewModel.state).collect(
                onEach: { [weak self] state in
                    guard let self, let state = state as? DetailsState else { return }
                    Task { @MainActor in
                        self.show = state.show
                        self.isFavorite = state.isFavorite
                        self.isLoading = state.isLoading
                        self.watchedCount = Int(state.watchedCount)
                        self.totalEpisodes = Int(state.totalEpisodes)
                    }
                },
                onComplete: {},
                onError: { _ in }
            )
        )
    }

    func toggleFavorite() {
        viewModel.toggleFavoriteSelection()
    }

    deinit {
        subscription.cancel()
        viewModel.clear()
    }
}

@MainActor
final class EpisodesViewModelWrapper: ObservableObject {
    nonisolated(unsafe) private let viewModel: EpisodesViewModel
    private let subscription = FlowSubscription()

    @Published var seasons: [Season] = []
    @Published var isLoading: Bool = true

    init(showId: Int32) {
        viewModel = KoinHelpersKt.getEpisodesViewModel()
        observe()
        viewModel.load(showId: showId)
    }

    private func observe() {
        subscription.set(
            FlowWrapperKt.wrap(viewModel.state).collect(
                onEach: { [weak self] state in
                    guard let self, let state = state as? EpisodesState else { return }
                    Task { @MainActor in
                        self.seasons = state.seasons
                        self.isLoading = state.isLoading
                    }
                },
                onComplete: {},
                onError: { _ in }
            )
        )
    }

    func toggleEpisode(episode: Episode) {
        viewModel.toggleEpisodeWatched(
            episodeId: episode.id,
            season: episode.season,
            number: episode.number,
            isWatched: episode.isWatched
        )
    }

    func toggleSeason(seasonNumber: Int32, markWatched: Bool) {
        viewModel.toggleSeasonWatched(
            seasonNumber: seasonNumber,
            markWatched: markWatched
        )
    }

    deinit {
        subscription.cancel()
        viewModel.clear()
    }
}

@MainActor
final class FavoritesViewModelWrapper: ObservableObject {
    nonisolated(unsafe) private let viewModel: FavoritesViewModel
    private let subscription = FlowSubscription()

    @Published var favorites: [FavoriteShow] = []
    @Published var isLoading: Bool = true
    @Published var isEmpty: Bool = false

    init() {
        viewModel = KoinHelpersKt.getFavoritesViewModel()
        observe()
        viewModel.loadFavorites()
    }

    private func observe() {
        subscription.set(
            FlowWrapperKt.wrap(viewModel.state).collect(
                onEach: { [weak self] state in
                    guard let self, let state = state as? FavoritesState else { return }
                    Task { @MainActor in
                        self.favorites = state.favorites
                        self.isLoading = state.isLoading
                        self.isEmpty = state.isEmpty
                    }
                },
                onComplete: {},
                onError: { _ in }
            )
        )
    }

    func removeFavorite(showId: Int32) {
        viewModel.removeFavoriteShow(showId: showId)
    }

    func refreshData() {
        viewModel.refreshData()
    }

    deinit {
        subscription.cancel()
        viewModel.clear()
    }
}

@MainActor
final class SettingsViewModelWrapper: ObservableObject {
    nonisolated(unsafe) private let settingsStore: SettingsStore
    private let subscription = FlowSubscription()

    @Published var themeMode: ThemeMode = .system
    @Published var language: AppLanguage = .english

    init() {
        settingsStore = KoinHelpersKt.getSettingsStore()
        observe()
    }

    private func observe() {
        subscription.set(
            FlowWrapperKt.wrap(settingsStore.settings).collect(
                onEach: { [weak self] state in
                    guard let self, let state = state as? AppSettings else { return }
                    Task { @MainActor in
                        self.themeMode = state.themeMode
                        self.language = state.language
                    }
                },
                onComplete: {},
                onError: { _ in }
            )
        )
    }

    func setTheme(_ mode: ThemeMode) {
        settingsStore.setTheme(themeMode: mode)
    }

    func setLanguage(_ lang: AppLanguage) {
        settingsStore.setLanguage(language: lang)
    }

    deinit {
        subscription.cancel()
    }
}
