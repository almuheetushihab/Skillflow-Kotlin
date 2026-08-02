package com.example.skillflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.*
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.skillflow.domain.repository.SettingsRepository
import com.example.skillflow.ui.auth.ForgotPasswordScreen
import com.example.skillflow.ui.auth.LoginScreen
import com.example.skillflow.ui.auth.SignUpScreen
import com.example.skillflow.ui.bookmarks.BookmarksScreen
import com.example.skillflow.ui.detail.DetailScreen
import com.example.skillflow.ui.home.HomeScreen
import com.example.skillflow.ui.navigation.Screen
import com.example.skillflow.ui.onboarding.OnboardingScreen
import com.example.skillflow.ui.profile.ProfileScreen
import com.example.skillflow.ui.profile.SettingsScreen
import com.example.skillflow.ui.quiz.QuizScreen
import com.example.skillflow.ui.roadmap.RoadmapScreen
import com.example.skillflow.ui.theme.SkillflowTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val startDestination = runBlocking {
            val isLoggedIn = settingsRepository.isLoggedIn().first()
            val isOnboardingCompleted = settingsRepository.isOnboardingCompleted().first()
            
            when {
                !isLoggedIn -> Screen.Login.route
                !isOnboardingCompleted -> Screen.Onboarding.route
                else -> Screen.Home.route
            }
        }

        setContent {
            val language by settingsRepository.getLanguage().collectAsState(
                initial = AppCompatDelegate.getApplicationLocales().toLanguageTags().ifEmpty { "en" }
            )
            
            LaunchedEffect(language) {
                val currentLocales = AppCompatDelegate.getApplicationLocales()
                if (currentLocales.toLanguageTags() != language) {
                    val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(language)
                    AppCompatDelegate.setApplicationLocales(appLocale)
                }
            }

            SkillflowTheme {
                SkillFlowAppContent(startDestination)
            }
        }
    }
}

@Composable
fun SkillFlowAppContent(startDestination: String) {
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
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    val items = listOf(
                        Triple(Screen.Home, R.string.nav_home, Icons.Default.Home),
                        Triple(Screen.Roadmap, R.string.nav_roadmap, Icons.Default.Route),
                        Triple(Screen.Bookmarks, R.string.nav_saved, Icons.Default.Bookmark),
                        Triple(Screen.Profile, R.string.nav_profile, Icons.Default.Person)
                    )
                    items.forEach { (screen, labelRes, icon) ->
                        val label = stringResource(labelRes)
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
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { slideInHorizontally { it } + fadeIn() },
            exitTransition = { slideOutHorizontally { -it } + fadeOut() },
            popEnterTransition = { slideInHorizontally { -it } + fadeIn() },
            popExitTransition = { slideOutHorizontally { it } + fadeOut() }
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) },
                    onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                    onLoginSuccess = { 
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.SignUp.route) {
                SignUpScreen(
                    onNavigateToLogin = { navController.popBackStack() },
                    onSignUpSuccess = {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.ForgotPassword.route) {
                ForgotPasswordScreen(onNavigateBack = { navController.popBackStack() })
            }
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
                ProfileScreen(
                    onResetOnboarding = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings.route)
                    },
                    onNavigateToQuiz = {
                        navController.navigate(Screen.Quiz.route)
                    }
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Quiz.route) {
                QuizScreen(onFinish = { navController.popBackStack() })
            }
        }
    }
}
