package com.example.skillflow

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.skillflow.domain.analytics.AnalyticsHelper
import com.example.skillflow.domain.manager.PlayStoreManager
import com.example.skillflow.domain.repository.AuthRepository
import com.example.skillflow.domain.repository.SettingsRepository
import com.example.skillflow.ui.auth.ForgotPasswordScreen
import com.example.skillflow.ui.auth.LoginScreen
import com.example.skillflow.ui.auth.SignUpScreen
import com.example.skillflow.ui.bookmarks.BookmarksScreen
import com.example.skillflow.ui.detail.DetailScreen
import com.example.skillflow.ui.home.HomeScreen
import com.example.skillflow.ui.navigation.Screen
import com.example.skillflow.ui.onboarding.OnboardingScreen
import com.example.skillflow.ui.profile.PrivacyPolicyScreen
import com.example.skillflow.ui.profile.ProfileScreen
import com.example.skillflow.ui.profile.SettingsScreen
import com.example.skillflow.ui.quiz.QuizScreen
import com.example.skillflow.ui.roadmap.RoadmapScreen
import com.example.skillflow.ui.theme.SkillflowTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var playStoreManager: PlayStoreManager

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        playStoreManager.checkForUpdates(this)

        val startDestination: Any = runBlocking {
            val isFirebaseUserLoggedIn = authRepository.getCurrentUserEmail() != null
            val isSessionActive = settingsRepository.isLoggedIn().first()
            val isOnboardingCompleted = settingsRepository.isOnboardingCompleted().first()
            
            when {
                !isFirebaseUserLoggedIn || !isSessionActive -> Screen.Login
                !isOnboardingCompleted -> Screen.Onboarding
                else -> Screen.Home
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
                val navController = rememberNavController()
                
                // Track screen views
                LaunchedEffect(navController) {
                    navController.currentBackStackEntryFlow.collect { entry ->
                        val routeName = entry.destination.route?.substringAfterLast('.') ?: "Unknown"
                        analyticsHelper.logScreenView(routeName, routeName)
                    }
                }

                SkillFlowAppContent(startDestination, playStoreManager, navController)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        playStoreManager.resumeUpdate(this)
    }
}

private fun navigateToBottomDestination(navController: NavHostController, screen: Screen) {
    navController.navigate(screen) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun SkillFlowAppContent(
    startDestination: Any,
    playStoreManager: PlayStoreManager,
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.let { dest ->
        dest.hasRoute<Screen.Home>() ||
        dest.hasRoute<Screen.Roadmap>() ||
        dest.hasRoute<Screen.Bookmarks>() ||
        dest.hasRoute<Screen.Profile>()
    } ?: false

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
                            selected = currentDestination?.hierarchy?.any { it.hasRoute(screen::class) } == true,
                            onClick = {
                                navigateToBottomDestination(navController, screen)
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
            modifier = Modifier.fillMaxSize(),
            enterTransition = { slideInHorizontally { it } + fadeIn() },
            exitTransition = { slideOutHorizontally { -it } + fadeOut() },
            popEnterTransition = { slideInHorizontally { -it } + fadeIn() },
            popExitTransition = { slideOutHorizontally { it } + fadeOut() }
        ) {
            composable<Screen.Login> {
                LoginScreen(
                    onNavigateToSignUp = { navController.navigate(Screen.SignUp) },
                    onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword) },
                    onLoginSuccess = { 
                        navController.navigate(Screen.Onboarding) {
                            popUpTo(Screen.Login) { inclusive = true }
                        }
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            composable<Screen.SignUp> {
                SignUpScreen(
                    onNavigateToLogin = { navController.popBackStack() },
                    onSignUpSuccess = {
                        navController.navigate(Screen.Onboarding) {
                            popUpTo(Screen.Login) { inclusive = true }
                        }
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            composable<Screen.ForgotPassword> {
                ForgotPasswordScreen(
                    onNavigateBack = { navController.popBackStack() },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            composable<Screen.Onboarding> {
                OnboardingScreen(
                    onNavigateToHome = {
                        navController.navigate(Screen.Home) {
                            popUpTo(Screen.Onboarding) { inclusive = true }
                        }
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            composable<Screen.Home> {
                HomeScreen(
                    onNavigateToDetail = { id ->
                        navController.navigate(Screen.Detail(nuggetId = id))
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            composable<Screen.Detail> { backStackEntry ->
                val detail: Screen.Detail = backStackEntry.toRoute()
                DetailScreen(
                    onNavigateBack = { navController.popBackStack() },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            composable<Screen.Roadmap> {
                RoadmapScreen(modifier = Modifier.padding(innerPadding))
            }
            composable<Screen.Bookmarks> {
                BookmarksScreen(
                    onNavigateToDetail = { id ->
                        navController.navigate(Screen.Detail(nuggetId = id))
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            composable<Screen.Profile> {
                ProfileScreen(
                    onResetOnboarding = {
                        navController.navigate(Screen.Login) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings)
                    },
                    onNavigateToQuiz = {
                        navController.navigate(Screen.Quiz)
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            composable<Screen.Settings> {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onLogout = {
                        navController.navigate(Screen.Login) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToPrivacy = {
                        navController.navigate(Screen.PrivacyPolicy)
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            composable<Screen.PrivacyPolicy> {
                PrivacyPolicyScreen(
                    onNavigateBack = { navController.popBackStack() },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            composable<Screen.Quiz> {
                QuizScreen(
                    onFinish = { navController.popBackStack() },
                    playStoreManager = playStoreManager,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
