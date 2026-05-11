package com.oshchyrov.tvshowtracker.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.oshchyrov.tvshowtracker.presentation.settings.SettingsStore
import com.oshchyrov.tvshowtracker.ui.localization.LocalAppLanguage
import com.oshchyrov.tvshowtracker.ui.localization.localizedString
import com.oshchyrov.tvshowtracker.ui.screens.DetailsScreen
import com.oshchyrov.tvshowtracker.ui.screens.EpisodesScreen
import com.oshchyrov.tvshowtracker.ui.screens.FavoritesScreen
import com.oshchyrov.tvshowtracker.ui.screens.SearchScreen
import com.oshchyrov.tvshowtracker.ui.screens.SettingsScreen
import com.oshchyrov.tvshowtracker.ui.theme.TVShowTrackerTheme
import org.koin.compose.koinInject

@Composable
fun TVShowTrackerApp() {
    val settingsStore: SettingsStore = koinInject()
    val settings by settingsStore.settings.collectAsState()

    TVShowTrackerTheme(themeMode = settings.themeMode) {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val topLevelRoutes = remember { listOf("search", "favorites", "settings") }

        val showBottomBar = currentRoute in topLevelRoutes

        CompositionLocalProvider(LocalAppLanguage provides settings.language) {
            Scaffold(
                bottomBar = {
                    if (showBottomBar) {
                        NavigationBar {
                            topLevelRoutes.forEach { route ->
                                NavigationBarItem(
                                    icon = {
                                        when (route) {
                                            "search" -> Icon(
                                                Icons.Default.Search,
                                                contentDescription = null
                                            )

                                            "favorites" -> Icon(
                                                Icons.Default.Favorite,
                                                contentDescription = null
                                            )

                                            else -> Icon(
                                                Icons.Default.Settings,
                                                contentDescription = null
                                            )
                                        }
                                    },
                                    label = {
                                        Text(
                                            when (route) {
                                                "search" -> localizedString("search_shows")
                                                "favorites" -> localizedString("my_shows")
                                                else -> localizedString("settings")
                                            }
                                        )
                                    },
                                    selected = currentRoute == route,
                                    onClick = {
                                        navController.navigate(route) {
                                            launchSingleTop = true
                                            restoreState = true
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = "search",
                    modifier = Modifier,
                ) {
                    composable("search") {
                        SearchScreen(
                            onShowClick = { showId -> navController.navigate("details/$showId") },
                            contentPadding = innerPadding,
                        )
                    }
                    composable("favorites") {
                        FavoritesScreen(
                            onShowClick = { showId -> navController.navigate("details/$showId") },
                            contentPadding = innerPadding,
                        )
                    }
                    composable("settings") {
                        SettingsScreen(contentPadding = innerPadding)
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
                        )
                    }
                }
            }
        }
    }
}
