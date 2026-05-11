import Foundation
import Shared

class SearchViewModelWrapper: ObservableObject {
    private let viewModel: SearchViewModel
    private var cancellable: Cancellable?

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
        cancellable = FlowWrapperKt.wrap(viewModel.state).collect(
            onEach: { [weak self] state in
                guard let self = self, let state = state as? SearchState else { return }
                DispatchQueue.main.async {
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
    }

    func onQueryChanged(_ query: String) {
        viewModel.updateQuery(query: query)
    }

    func retry() {
        viewModel.retrySearch()
    }

    deinit {
        cancellable?.cancel()
        viewModel.onCleared()
    }
}

class DetailsViewModelWrapper: ObservableObject {
    private let viewModel: DetailsViewModel
    private var cancellable: Cancellable?

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
        cancellable = FlowWrapperKt.wrap(viewModel.state).collect(
            onEach: { [weak self] state in
                guard let self = self, let state = state as? DetailsState else { return }
                DispatchQueue.main.async {
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
    }

    func toggleFavorite() {
        viewModel.toggleFavoriteSelection()
    }

    deinit {
        cancellable?.cancel()
        viewModel.onCleared()
    }
}

class EpisodesViewModelWrapper: ObservableObject {
    private let viewModel: EpisodesViewModel
    private var cancellable: Cancellable?

    @Published var seasons: [Season] = []
    @Published var isLoading: Bool = true

    init(showId: Int32) {
        viewModel = KoinHelpersKt.getEpisodesViewModel()
        observe()
        viewModel.load(showId: showId)
    }

    private func observe() {
        cancellable = FlowWrapperKt.wrap(viewModel.state).collect(
            onEach: { [weak self] state in
                guard let self = self, let state = state as? EpisodesState else { return }
                DispatchQueue.main.async {
                    self.seasons = state.seasons
                    self.isLoading = state.isLoading
                }
            },
            onComplete: {},
            onError: { _ in }
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
        cancellable?.cancel()
        viewModel.onCleared()
    }
}

class FavoritesViewModelWrapper: ObservableObject {
    private let viewModel: FavoritesViewModel
    private var cancellable: Cancellable?

    @Published var favorites: [FavoriteShow] = []
    @Published var isLoading: Bool = true
    @Published var isEmpty: Bool = false

    init() {
        viewModel = KoinHelpersKt.getFavoritesViewModel()
        observe()
        viewModel.loadFavorites()
    }

    private func observe() {
        cancellable = FlowWrapperKt.wrap(viewModel.state).collect(
            onEach: { [weak self] state in
                guard let self = self, let state = state as? FavoritesState else { return }
                DispatchQueue.main.async {
                    self.favorites = state.favorites
                    self.isLoading = state.isLoading
                    self.isEmpty = state.isEmpty
                }
            },
            onComplete: {},
            onError: { _ in }
        )
    }

    func removeFavorite(showId: Int32) {
        viewModel.removeFavoriteShow(showId: showId)
    }

    func refreshData() {
        viewModel.refreshData()
    }

    deinit {
        cancellable?.cancel()
        viewModel.onCleared()
    }
}

class SettingsViewModelWrapper: ObservableObject {
    private let settingsStore: SettingsStore
    private var cancellable: Cancellable?

    @Published var themeMode: ThemeMode = .system
    @Published var language: AppLanguage = .english

    init() {
        settingsStore = KoinHelpersKt.getSettingsStore()
        observe()
    }

    private func observe() {
        cancellable = FlowWrapperKt.wrap(settingsStore.settings).collect(
            onEach: { [weak self] state in
                guard let self = self, let state = state as? AppSettings else { return }
                DispatchQueue.main.async {
                    self.themeMode = state.themeMode
                    self.language = state.language
                }
            },
            onComplete: {},
            onError: { _ in }
        )
    }

    func setTheme(_ mode: ThemeMode) {
        settingsStore.setTheme(themeMode: mode)
    }

    func setLanguage(_ lang: AppLanguage) {
        settingsStore.setLanguage(language: lang)
    }

    deinit {
        cancellable?.cancel()
    }
}
