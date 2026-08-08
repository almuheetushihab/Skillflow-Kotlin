package com.example.skillflow.ui.navigation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.skillflow.R
import com.example.skillflow.ui.navigation.Screen

/**
 * Custom Bottom Navigation Bar for the SkillFlow app.
 * 
 * @param navController The navigation controller.
 * @param currentDestination The current navigation destination.
 */
@Composable
fun SkillFlowBottomBar(
    navController: NavHostController,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        Triple(Screen.Home, R.string.nav_home, Icons.Default.Home),
        Triple(Screen.Roadmap, R.string.nav_roadmap, Icons.Default.Route),
        Triple(Screen.Bookmarks, R.string.nav_saved, Icons.Default.Bookmark),
        Triple(Screen.Profile, R.string.nav_profile, Icons.Default.Person)
    )

    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        items.forEach { (screen, labelRes, icon) ->
            val label = stringResource(labelRes)
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, style = MaterialTheme.typography.labelMedium) },
                selected = currentDestination?.hierarchy?.any { it.hasRoute(screen::class) } == true,
                onClick = {
                    navController.navigate(screen) {
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
