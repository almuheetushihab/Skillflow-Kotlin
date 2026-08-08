package com.example.skillflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.skillflow.domain.manager.PlayStoreManager

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var playStoreManager: PlayStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        playStoreManager.checkForUpdates(this)

        val startDestination = runBlocking {
            val isFirebaseUserLoggedIn = authRepository.getCurrentUserEmail() != null
            val isSessionActive = settingsRepository.isLoggedIn().first()
            val isOnboardingCompleted = settingsRepository.isOnboardingCompleted().first()
            
            when {
                !isFirebaseUserLoggedIn || !isSessionActive -> Screen.Login.route
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
                SkillFlowAppContent(startDestination, playStoreManager)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        playStoreManager.resumeUpdate(this)
    }
}

private fun navigateToBottomDestination(navController: NavHostController, screen: Screen) {
    navController.navigate(screen.route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }
}

@Composable
fun SkillFlowAppContent(
    startDestination: String,
    playStoreManager: PlayStoreManager
) {
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
            modifier = Modifier.fillMaxSize(), // Removed padding from here
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
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            composable(Screen.SignUp.route) {
                SignUpScreen(
                    onNavigateToLogin = { navController.popBackStack() },
                    onSignUpSuccess = {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            composable(Screen.ForgotPassword.route) {
                ForgotPasswordScreen(
                    onNavigateBack = { navController.popBackStack() },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToDetail = { id ->
                        navController.navigate(Screen.Detail.createRoute(id))
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            composable(
                route = Screen.Detail.route,
                arguments = listOf(navArgument("nuggetId") { type = NavType.StringType })
            ) {
                DetailScreen(
                    onNavigateBack = { navController.popBackStack() },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            composable(Screen.Roadmap.route) {
                RoadmapScreen(modifier = Modifier.padding(innerPadding))
            }
            composable(Screen.Bookmarks.route) {
                BookmarksScreen(
                    onNavigateToDetail = { id ->
                        navController.navigate(Screen.Detail.createRoute(id))
                    },
                    modifier = Modifier.padding(innerPadding)
                )
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
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToPrivacy = {
                        navController.navigate(Screen.PrivacyPolicy.route)
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            composable(Screen.PrivacyPolicy.route) {
                PrivacyPolicyScreen(
                    onNavigateBack = { navController.popBackStack() },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            composable(Screen.Quiz.route) {
                QuizScreen(
                    onFinish = { navController.popBackStack() },
                    playStoreManager = playStoreManager,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
