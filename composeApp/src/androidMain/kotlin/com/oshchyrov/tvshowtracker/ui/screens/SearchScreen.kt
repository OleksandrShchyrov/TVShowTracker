package com.oshchyrov.tvshowtracker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.oshchyrov.tvshowtracker.domain.model.Show
import com.oshchyrov.tvshowtracker.presentation.search.SearchViewModel
import com.oshchyrov.tvshowtracker.presentation.settings.SettingsViewModel
import com.oshchyrov.tvshowtracker.util.Strings
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onShowClick: (Int) -> Unit,
    settingsViewModel: SettingsViewModel,
    viewModel: SearchViewModel = koinInject(),
) {
    val settingsState by settingsViewModel.state.collectAsState()
    val lang = settingsState.settings.language
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadInitialShows()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    if (state.isInitialContent) Strings.get("browse_shows", lang)
                    else Strings.get("search_shows", lang)
                )
            }
        )

        OutlinedTextField(
            value = state.query,
            onValueChange = { viewModel.updateQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text(Strings.get("search_placeholder", lang)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
        )

        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text("${Strings.get("error", lang)}: ${state.error}", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { viewModel.retrySearch() }) {
                        Text(Strings.get("retry", lang))
                    }
                }
            }
            state.isEmpty -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(Strings.get("no_shows_found", lang), style = MaterialTheme.typography.bodyLarge)
                }
            }
            state.query.isBlank() && state.results.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        Strings.get("loading_shows", lang),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (state.isInitialContent) {
                        item {
                            Text(
                                text = Strings.get("popular_picks", lang),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 4.dp),
                            )
                        }
                    }
                    items(state.results, key = { it.id }) { show ->
                        ShowListItem(show = show, onClick = { onShowClick(show.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun ShowListItem(show: Show, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = show.imageMediumUrl,
                contentDescription = show.name,
                modifier = Modifier
                    .size(80.dp, 112.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = show.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (show.genres.isNotEmpty()) {
                    Text(
                        text = show.genres.joinToString(", "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                show.rating?.let {
                    Text(
                        text = "★ $it",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                show.status?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}
