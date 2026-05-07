import SwiftUI
import Shared

struct MainTabView: View {
    @StateObject private var settingsWrapper = SettingsViewModelWrapper()
    
    var body: some View {
        TabView {
            SearchView()
                .tabItem {
                    Label(L10n.t("search", settingsWrapper.language), systemImage: "magnifyingglass")
                }
            FavoritesView()
                .tabItem {
                    Label(L10n.t("my_shows", settingsWrapper.language), systemImage: "heart.fill")
                }
            SettingsView(settingsWrapper: settingsWrapper)
                .tabItem {
                    Label(L10n.t("settings", settingsWrapper.language), systemImage: "gear")
                }
        }
        .environmentObject(settingsWrapper)
        .preferredColorScheme(
            settingsWrapper.themeMode == .dark ? .dark :
            settingsWrapper.themeMode == .light ? .light : nil
        )
    }
}

// MARK: - Search View

struct SearchView: View {
    @StateObject private var viewModelWrapper = SearchViewModelWrapper()
    @EnvironmentObject private var settingsWrapper: SettingsViewModelWrapper
    
    var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                searchContent
            }
            .navigationTitle(viewModelWrapper.isInitialContent ? L10n.t("browse_shows", settingsWrapper.language) : L10n.t("search_shows", settingsWrapper.language))
            .searchable(text: $viewModelWrapper.query, prompt: L10n.t("search_placeholder", settingsWrapper.language))
            .onChange(of: viewModelWrapper.query) { _, newValue in
                viewModelWrapper.onQueryChanged(newValue)
            }
        }
    }
    
    @ViewBuilder
    private var searchContent: some View {
        if viewModelWrapper.isLoading {
            Spacer()
            ProgressView()
            Spacer()
        } else if let error = viewModelWrapper.error {
            Spacer()
            VStack(spacing: 16) {
                Text("\(L10n.t("error", settingsWrapper.language)): \(error)")
                Button(L10n.t("retry", settingsWrapper.language)) { viewModelWrapper.retry() }
                    .buttonStyle(.borderedProminent)
            }
            Spacer()
        } else if viewModelWrapper.isEmpty {
            Spacer()
            Text(L10n.t("no_shows_found", settingsWrapper.language))
                .foregroundStyle(.secondary)
            Spacer()
        } else if viewModelWrapper.results.isEmpty {
            Spacer()
            Text(L10n.t("loading_shows", settingsWrapper.language))
                .foregroundStyle(.secondary)
            Spacer()
        } else {
            List {
                if viewModelWrapper.isInitialContent {
                    Text(L10n.t("popular_picks", settingsWrapper.language))
                        .font(.subheadline)
                        .foregroundStyle(.secondary)
                }
                ForEach(viewModelWrapper.results, id: \.id) { show in
                    NavigationLink(value: show.id) {
                        ShowRowView(show: show)
                    }
                }
            }
            .listStyle(.plain)
            .navigationDestination(for: Int32.self) { showId in
                DetailsView(showId: showId)
            }
        }
    }
}

// MARK: - Show Row

struct ShowRowView: View {
    let show: Show
    
    var body: some View {
        HStack(spacing: 12) {
            AsyncImage(url: URL(string: show.imageMediumUrl ?? "")) { phase in
                switch phase {
                case .success(let image):
                    image.resizable().aspectRatio(contentMode: .fill)
                case .failure:
                    Image(systemName: "tv").font(.title2).foregroundStyle(.secondary)
                default:
                    ProgressView()
                }
            }
            .frame(width: 60, height: 84)
            .clipShape(RoundedRectangle(cornerRadius: 8))
            
            VStack(alignment: .leading, spacing: 4) {
                Text(show.name)
                    .font(.headline)
                    .lineLimit(2)
                if !show.genres.isEmpty {
                    Text(show.genres.joined(separator: ", "))
                        .font(.caption)
                        .foregroundStyle(.secondary)
                }
                if let rating = show.rating {
                    Text("★ \(String(format: "%.1f", rating.doubleValue))")
                        .font(.caption)
                        .foregroundStyle(.orange)
                }
            }
        }
        .padding(.vertical, 4)
    }
}

// MARK: - Details View

struct DetailsView: View {
    let showId: Int32
    @StateObject private var viewModelWrapper: DetailsViewModelWrapper
    @EnvironmentObject private var settingsWrapper: SettingsViewModelWrapper
    
    init(showId: Int32) {
        self.showId = showId
        _viewModelWrapper = StateObject(wrappedValue: DetailsViewModelWrapper(showId: showId))
    }
    
    var body: some View {
        ScrollView {
            if viewModelWrapper.isLoading {
                ProgressView().padding(.top, 100)
            } else if let show = viewModelWrapper.show {
                VStack(alignment: .leading, spacing: 16) {
                    // Poster
                    if let imageUrl = show.imageUrl, let url = URL(string: imageUrl) {
                        AsyncImage(url: url) { phase in
                            switch phase {
                            case .success(let image):
                                image.resizable().aspectRatio(contentMode: .fit)
                            default:
                                Color.gray.opacity(0.2)
                            }
                        }
                        .frame(maxWidth: .infinity)
                        .frame(height: 300)
                        .clipShape(RoundedRectangle(cornerRadius: 16))
                    }
                    
                    Text(show.name).font(.title).bold()
                    
                    // Genres
                    if !show.genres.isEmpty {
                        ScrollView(.horizontal, showsIndicators: false) {
                            HStack {
                                ForEach(show.genres, id: \.self) { genre in
                                    Text(genre)
                                        .font(.caption)
                                        .padding(.horizontal, 10)
                                        .padding(.vertical, 5)
                                        .background(.secondary.opacity(0.2))
                                        .clipShape(Capsule())
                                }
                            }
                        }
                    }
                    
                    // Info
                    HStack(spacing: 16) {
                        if let rating = show.rating {
                            Label("★ \(String(format: "%.1f", rating.doubleValue))", systemImage: "star.fill")
                                .font(.caption)
                        }
                        if let lang = show.language {
                            Text(lang).font(.caption)
                        }
                        if let runtime = show.runtime {
                            Text("\(runtime)min").font(.caption)
                        }
                    }
                    
                    if let status = show.status {
                        Text("\(L10n.t("status", settingsWrapper.language)): \(status)").font(.subheadline)
                    }
                    if let premiered = show.premiered {
                        Text("\(L10n.t("premiered", settingsWrapper.language)): \(premiered)").font(.subheadline)
                    }
                    if let network = show.network {
                        Text("\(L10n.t("network", settingsWrapper.language)): \(network)").font(.subheadline)
                    }
                    
                    // Progress
                    if viewModelWrapper.isFavorite && viewModelWrapper.totalEpisodes > 0 {
                        NavigationLink {
                            EpisodesView(showId: showId)
                        } label: {
                            VStack(alignment: .leading, spacing: 8) {
                                HStack {
                                    Text(L10n.t("watch_progress", settingsWrapper.language)).font(.headline)
                                    Spacer()
                                    Image(systemName: "chevron.right")
                                        .font(.caption)
                                        .foregroundStyle(.secondary)
                                }
                                ProgressView(value: Double(viewModelWrapper.watchedCount), total: Double(viewModelWrapper.totalEpisodes))
                                Text("\(viewModelWrapper.watchedCount)/\(viewModelWrapper.totalEpisodes) \(L10n.t("episodes_count", settingsWrapper.language))")
                                    .font(.caption)
                                    .foregroundStyle(.secondary)
                            }
                            .foregroundStyle(.primary)
                        }
                        .padding()
                        .background(.secondary.opacity(0.1))
                        .clipShape(RoundedRectangle(cornerRadius: 12))
                    }
                    
                    if let summary = show.summary {
                        Text(summary).font(.body)
                    }
                    
                    NavigationLink(L10n.t("view_episodes", settingsWrapper.language)) {
                        EpisodesView(showId: showId)
                    }
                    .buttonStyle(.borderedProminent)
                    .frame(maxWidth: .infinity)
                }
                .padding()
            }
        }
        .navigationTitle(viewModelWrapper.show?.name ?? L10n.t("details", settingsWrapper.language))
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                Button(action: { viewModelWrapper.toggleFavorite() }) {
                    Image(systemName: viewModelWrapper.isFavorite ? "heart.fill" : "heart")
                        .foregroundStyle(viewModelWrapper.isFavorite ? .red : .primary)
                }
            }
        }
    }
}

// MARK: - Episodes View

struct EpisodesView: View {
    let showId: Int32
    @StateObject private var viewModelWrapper: EpisodesViewModelWrapper
    @EnvironmentObject private var settingsWrapper: SettingsViewModelWrapper
    
    init(showId: Int32) {
        self.showId = showId
        _viewModelWrapper = StateObject(wrappedValue: EpisodesViewModelWrapper(showId: showId))
    }
    
    var body: some View {
        Group {
            if viewModelWrapper.isLoading {
                ProgressView()
            } else {
                List {
                    ForEach(viewModelWrapper.seasons, id: \.number) { season in
                        Section {
                            ForEach(season.episodes, id: \.id) { episode in
                                EpisodeRowView(
                                    episode: episode,
                                    onToggle: { viewModelWrapper.toggleEpisode(episode: episode) }
                                )
                            }
                        } header: {
                            HStack {
                                Text("\(L10n.t("season", settingsWrapper.language)) \(season.number)")
                                Spacer()
                                let allWatched = season.episodes.allSatisfy { $0.isWatched }
                                Button(allWatched ? L10n.t("unwatch_all", settingsWrapper.language) : L10n.t("watch_all", settingsWrapper.language)) {
                                    viewModelWrapper.toggleSeason(seasonNumber: season.number, markWatched: !allWatched)
                                }
                                .font(.caption)
                            }
                        }
                    }
                }
                .listStyle(.insetGrouped)
            }
        }
        .navigationTitle(L10n.t("episodes", settingsWrapper.language))
    }
}

struct EpisodeRowView: View {
    let episode: Episode
    let onToggle: () -> Void
    
    var body: some View {
        HStack {
            if let imageUrl = episode.imageUrl, let url = URL(string: imageUrl) {
                AsyncImage(url: url) { phase in
                    switch phase {
                    case .success(let image):
                        image.resizable().aspectRatio(contentMode: .fill)
                    default:
                        Color.gray.opacity(0.3)
                    }
                }
                .frame(width: 56, height: 42)
                .clipShape(RoundedRectangle(cornerRadius: 4))
            }
            
            VStack(alignment: .leading, spacing: 2) {
                Text("S\(episode.season)E\(episode.number)")
                    .font(.caption2)
                    .foregroundStyle(.secondary)
                Text(episode.name)
                    .font(.subheadline)
                    .lineLimit(1)
                if let airdate = episode.airdate {
                    Text(airdate).font(.caption2).foregroundStyle(.secondary)
                }
            }
            
            Spacer()
            
            Button(action: onToggle) {
                Image(systemName: episode.isWatched ? "checkmark.circle.fill" : "circle")
                    .foregroundStyle(episode.isWatched ? .green : .secondary)
            }
        }
    }
}

// MARK: - Favorites View

struct FavoritesView: View {
    @StateObject private var viewModelWrapper = FavoritesViewModelWrapper()
    @EnvironmentObject private var settingsWrapper: SettingsViewModelWrapper
    
    var body: some View {
        NavigationStack {
            Group {
                if viewModelWrapper.isLoading {
                    ProgressView()
                } else if viewModelWrapper.isEmpty {
                    VStack(spacing: 8) {
                        Text(L10n.t("no_saved_shows", settingsWrapper.language))
                            .font(.title3)
                        Text(L10n.t("add_shows_hint", settingsWrapper.language))
                            .foregroundStyle(.secondary)
                    }
                } else {
                    List {
                        ForEach(viewModelWrapper.favorites, id: \.show.id) { favorite in
                            NavigationLink(value: favorite.show.id) {
                                FavoriteRowView(favorite: favorite)
                            }
                        }
                        .onDelete { indexSet in
                            for index in indexSet {
                                let fav = viewModelWrapper.favorites[index]
                                viewModelWrapper.removeFavorite(showId: fav.show.id)
                            }
                        }
                    }
                    .listStyle(.plain)
                    .navigationDestination(for: Int32.self) { showId in
                        DetailsView(showId: showId)
                    }
                }
            }
            .navigationTitle(L10n.t("my_shows", settingsWrapper.language))
            .onAppear {
                viewModelWrapper.refreshData()
            }
        }
    }
}

struct FavoriteRowView: View {
    let favorite: FavoriteShow
    @EnvironmentObject private var settingsWrapper: SettingsViewModelWrapper
    
    var body: some View {
        HStack(spacing: 12) {
            AsyncImage(url: URL(string: favorite.show.imageMediumUrl ?? "")) { phase in
                switch phase {
                case .success(let image):
                    image.resizable().aspectRatio(contentMode: .fill)
                default:
                    Image(systemName: "tv").foregroundStyle(.secondary)
                }
            }
            .frame(width: 50, height: 70)
            .clipShape(RoundedRectangle(cornerRadius: 6))
            
            VStack(alignment: .leading, spacing: 4) {
                Text(favorite.show.name)
                    .font(.headline)
                    .lineLimit(1)
                ProgressView(value: Double(favorite.progressPercentage))
                Text("\(favorite.watchedEpisodes)/\(favorite.totalEpisodes) \(L10n.t("episodes_count", settingsWrapper.language)) (\(Int(favorite.progressPercentage * 100))%)")
                    .font(.caption)
                    .foregroundStyle(.secondary)
                if let next = favorite.nextUnwatchedEpisode {
                    Text("\(L10n.t("next", settingsWrapper.language)): S\(next.season)E\(next.number) - \(next.name)")
                        .font(.caption)
                        .foregroundStyle(.tint)
                        .lineLimit(1)
                }
            }
        }
    }
}

// MARK: - Settings View

struct SettingsView: View {
    @ObservedObject var settingsWrapper: SettingsViewModelWrapper
    
    var body: some View {
        NavigationStack {
            Form {
                Section(L10n.t("theme", settingsWrapper.language)) {
                    Picker(L10n.t("theme", settingsWrapper.language), selection: Binding(
                        get: { settingsWrapper.themeMode },
                        set: { settingsWrapper.setTheme($0) }
                    )) {
                        Text(L10n.t("theme_system", settingsWrapper.language)).tag(ThemeMode.system)
                        Text(L10n.t("theme_light", settingsWrapper.language)).tag(ThemeMode.light)
                        Text(L10n.t("theme_dark", settingsWrapper.language)).tag(ThemeMode.dark)
                    }
                    .pickerStyle(.segmented)
                }
                
                Section(L10n.t("language", settingsWrapper.language)) {
                    Picker(L10n.t("language", settingsWrapper.language), selection: Binding(
                        get: { settingsWrapper.language },
                        set: { settingsWrapper.setLanguage($0) }
                    )) {
                        Text(L10n.t("english", settingsWrapper.language)).tag(AppLanguage.english)
                        Text(L10n.t("ukrainian", settingsWrapper.language)).tag(AppLanguage.ukrainian)
                    }
                    .pickerStyle(.segmented)
                }
            }
            .navigationTitle(L10n.t("settings", settingsWrapper.language))
        }
    }
}
