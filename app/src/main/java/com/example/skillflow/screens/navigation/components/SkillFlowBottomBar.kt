package com.example.skillflow.screens.navigation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.skillflow.R
import com.example.skillflow.screens.navigation.Screen
import com.example.skillflow.screens.theme.SkillflowTheme

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

@Preview(showBackground = true)
@Composable
fun SkillFlowBottomBarContentPreview() {
    SkillflowTheme {
        NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
            NavigationBarItem(
                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                label = { Text("Home") },
                selected = true,
                onClick = {}
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Route, contentDescription = "Roadmap") },
                label = { Text("Roadmap") },
                selected = false,
                onClick = {}
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Bookmark, contentDescription = "Saved") },
                label = { Text("Saved") },
                selected = false,
                onClick = {}
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                label = { Text("Profile") },
                selected = false,
                onClick = {}
            )
        }
    }
}
