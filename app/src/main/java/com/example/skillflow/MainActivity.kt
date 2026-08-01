package com.example.skillflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.skillflow.ui.bookmarks.BookmarksScreen
import com.example.skillflow.ui.detail.DetailScreen
import com.example.skillflow.ui.home.HomeScreen
import com.example.skillflow.ui.navigation.Screen
import com.example.skillflow.ui.onboarding.OnboardingScreen
import com.example.skillflow.ui.profile.ProfileScreen
import com.example.skillflow.ui.roadmap.RoadmapScreen
import com.example.skillflow.ui.theme.SkillflowTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SkillflowTheme {
                SkillFlowAppContent()
            }
        }
    }
}

@Composable
fun SkillFlowAppContent() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.route in listOf(
        Screen.Home.route,
        Screen.Roadmap.route,
        Screen.Bookmarks.route,
        Screen.Profile.route
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    val items = listOf(
                        Triple(Screen.Home, "Home", Icons.Default.Home),
                        Triple(Screen.Roadmap, "Roadmap", Icons.Default.Route),
                        Triple(Screen.Bookmarks, "Saved", Icons.Default.Bookmark),
                        Triple(Screen.Profile, "Profile", Icons.Default.Person)
                    )
                    items.forEach { (screen, label, icon) ->
                        NavigationBarItem(
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label, style = MaterialTheme.typography.labelMedium) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
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
            startDestination = Screen.Onboarding.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { slideInHorizontally { it } + fadeIn() },
            exitTransition = { slideOutHorizontally { -it } + fadeOut() },
            popEnterTransition = { slideInHorizontally { -it } + fadeIn() },
            popExitTransition = { slideOutHorizontally { it } + fadeOut() }
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                })
            }
            composable(Screen.Home.route) {
                HomeScreen(onNavigateToDetail = { id ->
                    navController.navigate(Screen.Detail.createRoute(id))
                })
            }
            composable(
                route = Screen.Detail.route,
                arguments = listOf(navArgument("nuggetId") { type = NavType.StringType })
            ) {
                DetailScreen(onNavigateBack = { navController.popBackStack() })
            }
            composable(Screen.Roadmap.route) {
                RoadmapScreen()
            }
            composable(Screen.Bookmarks.route) {
                BookmarksScreen(onNavigateToDetail = { id ->
                    navController.navigate(Screen.Detail.createRoute(id))
                })
            }
            composable(Screen.Profile.route) {
                ProfileScreen(onResetOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(0) { inclusive = true }
                    }
                })
            }
        }
    }
}
