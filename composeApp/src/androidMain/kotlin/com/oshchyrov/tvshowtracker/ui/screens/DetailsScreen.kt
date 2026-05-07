package com.oshchyrov.tvshowtracker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.oshchyrov.tvshowtracker.presentation.details.DetailsIntent
import com.oshchyrov.tvshowtracker.presentation.details.DetailsViewModel
import com.oshchyrov.tvshowtracker.presentation.settings.SettingsViewModel
import com.oshchyrov.tvshowtracker.util.Strings
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    showId: Int,
    onEpisodesClick: () -> Unit,
    onBackClick: () -> Unit,
    settingsViewModel: SettingsViewModel,
    viewModel: DetailsViewModel = koinInject(),
) {
    val settingsState by settingsViewModel.state.collectAsState()
    val lang = settingsState.settings.language

    LaunchedEffect(showId) {
        viewModel.handleIntent(DetailsIntent.LoadShow(showId))
    }

    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.show?.name ?: Strings.get("show_details", lang)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = Strings.get("back", lang))
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.handleIntent(DetailsIntent.ToggleFavorite) }) {
                        Icon(
                            imageVector = if (state.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = Strings.get("favorite", lang),
                            tint = if (state.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                        )
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
            state.show != null -> {
                val show = state.show!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    // Poster
                    show.imageUrl?.let { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = show.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop,
                        )
                    }

                    Text(show.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

                    if (show.genres.isNotEmpty()) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            show.genres.forEach { genre ->
                                SuggestionChip(onClick = {}, label = { Text(genre) })
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        show.rating?.let { InfoChip("★ $it") }
                        show.language?.let { InfoChip(it) }
                        show.runtime?.let { InfoChip("${it}min") }
                    }

                    show.status?.let {
                        Text("${Strings.get("status", lang)}: $it", style = MaterialTheme.typography.bodyMedium)
                    }
                    show.premiered?.let {
                        Text("${Strings.get("premiered", lang)}: $it", style = MaterialTheme.typography.bodyMedium)
                    }
                    show.network?.let {
                        Text("${Strings.get("network", lang)}: $it", style = MaterialTheme.typography.bodyMedium)
                    }
                    show.webChannel?.let {
                        Text("${Strings.get("streaming", lang)}: $it", style = MaterialTheme.typography.bodyMedium)
                    }

                    // Clickable Watch Progress → opens episodes
                    if (state.isFavorite && state.totalEpisodes > 0) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onEpisodesClick),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        Strings.get("watch_progress", lang),
                                        style = MaterialTheme.typography.titleSmall,
                                        modifier = Modifier.weight(1f),
                                    )
                                    Text(
                                        "›",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { state.progressPercentage },
                                    modifier = Modifier.fillMaxWidth().height(8.dp),
                                    strokeCap = StrokeCap.Round,
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "${state.watchedCount}/${state.totalEpisodes} ${Strings.get("episodes_format", lang)}",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                    }

                    show.summary?.let {
                        Text(it, style = MaterialTheme.typography.bodyMedium)
                    }

                    Button(
                        onClick = onEpisodesClick,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(Strings.get("view_episodes", lang))
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoChip(text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }
}
