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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.oshchyrov.tvshowtracker.presentation.settings.SettingsStore
import com.oshchyrov.tvshowtracker.ui.localization.LocalAppLanguage
import com.oshchyrov.tvshowtracker.ui.localization.localizedString
import com.oshchyrov.tvshowtracker.ui.navigation.DetailsRoute
import com.oshchyrov.tvshowtracker.ui.navigation.EpisodesRoute
import com.oshchyrov.tvshowtracker.ui.navigation.FavoritesRoute
import com.oshchyrov.tvshowtracker.ui.navigation.SearchRoute
import com.oshchyrov.tvshowtracker.ui.navigation.SettingsRoute
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
    val settings by settingsStore.settings.collectAsStateWithLifecycle()

    TVShowTrackerTheme(themeMode = settings.themeMode) {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val topLevelRoutes = remember {
            listOf(TopLevelRoute.Search, TopLevelRoute.Favorites, TopLevelRoute.Settings)
        }

        val showBottomBar = topLevelRoutes.any { it.matches(currentDestination) }

        CompositionLocalProvider(LocalAppLanguage provides settings.language) {
            Scaffold(
                bottomBar = {
                    if (showBottomBar) {
                        NavigationBar {
                            topLevelRoutes.forEach { destination ->
                                NavigationBarItem(
                                    icon = {
                                        when (destination) {
                                            TopLevelRoute.Search -> Icon(
                                                Icons.Default.Search,
                                                contentDescription = null
                                            )

                                            TopLevelRoute.Favorites -> Icon(
                                                Icons.Default.Favorite,
                                                contentDescription = null
                                            )

                                            TopLevelRoute.Settings -> Icon(
                                                Icons.Default.Settings,
                                                contentDescription = null
                                            )
                                        }
                                    },
                                    label = {
                                        Text(
                                            localizedString(destination.labelKey)
                                        )
                                    },
                                    selected = destination.matches(currentDestination),
                                    onClick = {
                                        navController.navigate(destination.route) {
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
                    startDestination = SearchRoute,
                    modifier = Modifier,
                ) {
                    composable<SearchRoute> {
                        SearchScreen(
                            onShowClick = { showId -> navController.navigate(DetailsRoute(showId)) },
                            contentPadding = innerPadding,
                        )
                    }
                    composable<FavoritesRoute> {
                        FavoritesScreen(
                            onShowClick = { showId -> navController.navigate(DetailsRoute(showId)) },
                            contentPadding = innerPadding,
                        )
                    }
                    composable<SettingsRoute> {
                        SettingsScreen(contentPadding = innerPadding)
                    }
                    composable<DetailsRoute> { backStackEntry ->
                        val route = backStackEntry.toRoute<DetailsRoute>()
                        DetailsScreen(
                            showId = route.showId,
                            onEpisodesClick = { navController.navigate(EpisodesRoute(route.showId)) },
                            onBackClick = { navController.popBackStack() },
                            contentPadding = innerPadding,
                        )
                    }
                    composable<EpisodesRoute> { backStackEntry ->
                        val route = backStackEntry.toRoute<EpisodesRoute>()
                        EpisodesScreen(
                            showId = route.showId,
                            onBackClick = { navController.popBackStack() },
                            contentPadding = innerPadding,
                        )
                    }
                }
            }
        }
    }
}

private sealed class TopLevelRoute(
    val route: Any,
    private val routeName: String,
    val labelKey: String,
) {
    data object Search : TopLevelRoute(
        route = SearchRoute,
        routeName = "com.oshchyrov.tvshowtracker.ui.navigation.SearchRoute",
        labelKey = "search_shows",
    )
    data object Favorites : TopLevelRoute(
        route = FavoritesRoute,
        routeName = "com.oshchyrov.tvshowtracker.ui.navigation.FavoritesRoute",
        labelKey = "my_shows",
    )
    data object Settings : TopLevelRoute(
        route = SettingsRoute,
        routeName = "com.oshchyrov.tvshowtracker.ui.navigation.SettingsRoute",
        labelKey = "settings",
    )

    fun matches(destination: NavDestination?): Boolean = destination?.route == routeName
}
