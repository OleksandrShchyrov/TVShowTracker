package com.oshchyrov.tvshowtracker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.oshchyrov.tvshowtracker.presentation.details.DetailsIntent
import com.oshchyrov.tvshowtracker.presentation.details.DetailsViewModel
import com.oshchyrov.tvshowtracker.ui.localization.LocalAppLanguage
import com.oshchyrov.tvshowtracker.ui.localization.localizedString
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    showId: Int,
    onEpisodesClick: () -> Unit,
    onBackClick: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: DetailsViewModel = koinInject(),
) {
    val lang = LocalAppLanguage.current

    LaunchedEffect(showId) {
        viewModel.handleIntent(DetailsIntent.LoadShow(showId))
    }

    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.show?.name ?: localizedString("show_details")) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = localizedString("back")
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.handleIntent(DetailsIntent.ToggleFavorite) }) {
                        Icon(
                            imageVector = if (state.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = localizedString("favorite"),
                            tint = if (state.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> {
                Box(Modifier
                    .fillMaxSize()
                    .padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            state.error != null -> {
                Box(Modifier
                    .fillMaxSize()
                    .padding(padding), contentAlignment = Alignment.Center) {
                    Text("${localizedString("error")}: ${state.error}")
                }
            }

            state.show != null -> {
                val show = state.show!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(
                            start = 16.dp,
                            top = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp + contentPadding.calculateBottomPadding(),
                        ),
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

                    Text(
                        show.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

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
                        Text(
                            "${localizedString("status")}: $it",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    show.premiered?.let {
                        Text(
                            "${localizedString("premiered")}: $it",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    show.network?.let {
                        Text(
                            "${localizedString("network")}: $it",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    show.webChannel?.let {
                        Text(
                            "${localizedString("streaming")}: $it",
                            style = MaterialTheme.typography.bodyMedium
                        )
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
                                        localizedString("watch_progress"),
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
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp),
                                    strokeCap = StrokeCap.Round,
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "${state.watchedCount}/${state.totalEpisodes} ${
                                        localizedString(
                                            "episodes_format"
                                        )
                                    }",
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
                        Text(localizedString("view_episodes"))
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
