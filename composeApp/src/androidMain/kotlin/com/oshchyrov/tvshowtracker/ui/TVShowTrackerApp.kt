package com.oshchyrov.tvshowtracker.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.oshchyrov.tvshowtracker.presentation.settings.SettingsViewModel
import com.oshchyrov.tvshowtracker.ui.screens.DetailsScreen
import com.oshchyrov.tvshowtracker.ui.screens.EpisodesScreen
import com.oshchyrov.tvshowtracker.ui.screens.FavoritesScreen
import com.oshchyrov.tvshowtracker.ui.screens.SearchScreen
import com.oshchyrov.tvshowtracker.ui.screens.SettingsScreen
import com.oshchyrov.tvshowtracker.ui.theme.TVShowTrackerTheme
import com.oshchyrov.tvshowtracker.util.Strings
import org.koin.compose.koinInject

@Composable
fun TVShowTrackerApp() {
    val settingsViewModel: SettingsViewModel = koinInject()
    val settingsState by settingsViewModel.state.collectAsState()
    val lang = settingsState.settings.language

    TVShowTrackerTheme(themeMode = settingsState.settings.themeMode) {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val showBottomBar = currentRoute in listOf("search", "favorites")

        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar {
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Search, contentDescription = null) },
                            label = { Text(Strings.get("search_shows", lang)) },
                            selected = currentRoute == "search",
                            onClick = {
                                if (currentRoute != "search") {
                                    navController.navigate("search") {
                                        popUpTo("search") { inclusive = true }
                                    }
                                }
                            }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                            label = { Text(Strings.get("my_shows", lang)) },
                            selected = currentRoute == "favorites",
                            onClick = {
                                if (currentRoute != "favorites") {
                                    navController.navigate("favorites") {
                                        popUpTo("search")
                                    }
                                }
                            }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                            label = { Text(Strings.get("settings", lang)) },
                            selected = false,
                            onClick = { navController.navigate("settings") }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "search",
                modifier = Modifier.padding(innerPadding),
            ) {
                composable("search") {
                    SearchScreen(
                        onShowClick = { showId -> navController.navigate("details/$showId") },
                        settingsViewModel = settingsViewModel,
                    )
                }
                composable("favorites") {
                    FavoritesScreen(
                        onShowClick = { showId -> navController.navigate("details/$showId") },
                        settingsViewModel = settingsViewModel,
                    )
                }
                composable("settings") {
                    SettingsScreen(
                        onBackClick = { navController.popBackStack() },
                        settingsViewModel = settingsViewModel,
                    )
                }
                composable(
                    "details/{showId}",
                    arguments = listOf(navArgument("showId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val showId = backStackEntry.arguments?.getInt("showId") ?: return@composable
                    DetailsScreen(
                        showId = showId,
                        onEpisodesClick = { navController.navigate("episodes/$showId") },
                        onBackClick = { navController.popBackStack() },
                        settingsViewModel = settingsViewModel,
                    )
                }
                composable(
                    "episodes/{showId}",
                    arguments = listOf(navArgument("showId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val showId = backStackEntry.arguments?.getInt("showId") ?: return@composable
                    EpisodesScreen(
                        showId = showId,
                        onBackClick = { navController.popBackStack() },
                        settingsViewModel = settingsViewModel,
                    )
                }
            }
        }
    }
}
