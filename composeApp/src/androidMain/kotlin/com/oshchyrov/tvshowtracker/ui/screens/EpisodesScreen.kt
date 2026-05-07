package com.oshchyrov.tvshowtracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.oshchyrov.tvshowtracker.domain.model.Episode
import com.oshchyrov.tvshowtracker.domain.model.Season
import com.oshchyrov.tvshowtracker.presentation.episodes.EpisodesIntent
import com.oshchyrov.tvshowtracker.presentation.episodes.EpisodesViewModel
import com.oshchyrov.tvshowtracker.presentation.settings.SettingsViewModel
import com.oshchyrov.tvshowtracker.util.Strings
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpisodesScreen(
    showId: Int,
    onBackClick: () -> Unit,
    settingsViewModel: SettingsViewModel,
    viewModel: EpisodesViewModel = koinInject(),
) {
    val settingsState by settingsViewModel.state.collectAsState()
    val lang = settingsState.settings.language

    LaunchedEffect(showId) {
        viewModel.handleIntent(EpisodesIntent.LoadEpisodes(showId))
    }

    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(Strings.get("episodes", lang)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = Strings.get("back", lang))
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text("${Strings.get("error", lang)}: ${state.error}")
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    state.seasons.forEach { season ->
                        item(key = "season_header_${season.number}") {
                            SeasonHeader(
                                season = season,
                                lang = lang,
                                onToggle = { markWatched ->
                                    viewModel.handleIntent(
                                        EpisodesIntent.ToggleSeasonWatched(season.number, markWatched)
                                    )
                                }
                            )
                        }
                        items(season.episodes, key = { it.id }) { episode ->
                            EpisodeItem(
                                episode = episode,
                                onToggleWatched = {
                                    viewModel.handleIntent(
                                        EpisodesIntent.ToggleEpisodeWatched(
                                            episodeId = episode.id,
                                            season = episode.season,
                                            number = episode.number,
                                            isWatched = episode.isWatched,
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SeasonHeader(
    season: Season,
    lang: com.oshchyrov.tvshowtracker.domain.model.AppLanguage,
    onToggle: (Boolean) -> Unit,
) {
    val allWatched = season.episodes.all { it.isWatched }
    val watchedCount = season.episodes.count { it.isWatched }

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "${Strings.get("season", lang)} ${season.number}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "$watchedCount/${season.episodes.size}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.width(8.dp))
        TextButton(onClick = { onToggle(!allWatched) }) {
            Text(if (allWatched) Strings.get("unwatch_all", lang) else Strings.get("watch_all", lang))
        }
    }
}

@Composable
private fun EpisodeItem(episode: Episode, onToggleWatched: () -> Unit) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            episode.imageUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp, 48.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Crop,
                )
                Spacer(Modifier.width(12.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "S${episode.season}E${episode.number}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = episode.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                )
                episode.airdate?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Checkbox(
                checked = episode.isWatched,
                onCheckedChange = { onToggleWatched() },
            )
        }
    }
}
