package com.oshchyrov.tvshowtracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.oshchyrov.tvshowtracker.domain.model.AppLanguage
import com.oshchyrov.tvshowtracker.domain.model.ThemeMode
import com.oshchyrov.tvshowtracker.presentation.settings.SettingsViewModel
import com.oshchyrov.tvshowtracker.util.Strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    settingsViewModel: SettingsViewModel,
) {
    val state by settingsViewModel.state.collectAsState()
    val lang = state.settings.language

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(Strings.get("settings", lang)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = Strings.get("back", lang))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            // Theme section
            Text(
                text = Strings.get("theme", lang),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ThemeMode.entries.forEach { mode ->
                    val label = when (mode) {
                        ThemeMode.LIGHT -> Strings.get("theme_light", lang)
                        ThemeMode.DARK -> Strings.get("theme_dark", lang)
                        ThemeMode.SYSTEM -> Strings.get("theme_system", lang)
                    }
                    FilterChip(
                        selected = state.settings.themeMode == mode,
                        onClick = { settingsViewModel.setTheme(mode) },
                        label = { Text(label) },
                        shape = RoundedCornerShape(12.dp),
                    )
                }
            }

            HorizontalDivider()

            // Language section
            Text(
                text = Strings.get("language", lang),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AppLanguage.entries.forEach { language ->
                    FilterChip(
                        selected = state.settings.language == language,
                        onClick = { settingsViewModel.setLanguage(language) },
                        label = { Text(language.displayName) },
                        shape = RoundedCornerShape(12.dp),
                    )
                }
            }
        }
    }
}

