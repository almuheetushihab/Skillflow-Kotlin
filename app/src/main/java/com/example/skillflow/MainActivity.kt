package com.example.skillflow

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.os.LocaleListCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.skillflow.domain.analytics.AnalyticsHelper
import com.example.skillflow.domain.manager.PlayStoreManager
import com.example.skillflow.domain.repository.AuthRepository
import com.example.skillflow.domain.repository.SettingsRepository
import com.example.skillflow.ui.navigation.Screen
import com.example.skillflow.ui.navigation.SkillFlowNavHost
import com.example.skillflow.ui.navigation.components.SkillFlowBottomBar
import com.example.skillflow.ui.theme.SkillflowTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * Root Activity of the SkillFlow application.
 */
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
                SkillFlowApp(
                    startDestination = startDestination,
                    playStoreManager = playStoreManager,
                    analyticsHelper = analyticsHelper
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        playStoreManager.resumeUpdate(this)
    }
}

/**
 * Main Composable that sets up the UI structure and navigation.
 */
@Composable
fun SkillFlowApp(
    startDestination: Any,
    playStoreManager: PlayStoreManager,
    analyticsHelper: AnalyticsHelper
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Track screen views for analytics
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { entry ->
            val routeName = entry.destination.route?.substringAfterLast('.') ?: "Unknown"
            analyticsHelper.logScreenView(routeName, routeName)
        }
    }

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
                SkillFlowBottomBar(
                    navController = navController,
                    currentDestination = currentDestination
                )
            }
        }
    ) { innerPadding ->
        SkillFlowNavHost(
            navController = navController,
            startDestination = startDestination,
            innerPadding = innerPadding,
            playStoreManager = playStoreManager
        )
    }
}
