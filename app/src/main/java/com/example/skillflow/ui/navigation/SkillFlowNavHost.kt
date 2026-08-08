package com.example.skillflow.ui.navigation

import androidx.compose.animation.*
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.skillflow.domain.manager.PlayStoreManager
import com.example.skillflow.ui.auth.ForgotPasswordScreen
import com.example.skillflow.ui.auth.LoginScreen
import com.example.skillflow.ui.auth.SignUpScreen
import com.example.skillflow.ui.bookmarks.BookmarksScreen
import com.example.skillflow.ui.detail.DetailScreen
import com.example.skillflow.ui.home.HomeScreen
import com.example.skillflow.ui.onboarding.OnboardingScreen
import com.example.skillflow.ui.profile.PrivacyPolicyScreen
import com.example.skillflow.ui.profile.ProfileScreen
import com.example.skillflow.ui.profile.SettingsScreen
import com.example.skillflow.ui.quiz.QuizScreen
import com.example.skillflow.ui.roadmap.RoadmapScreen

/**
 * Centralized Navigation Host for the SkillFlow app.
 * 
 * @param navController The navigation controller.
 * @param startDestination The initial screen to display.
 * @param innerPadding Padding values from the root Scaffold.
 * @param playStoreManager Manager for Play Store integration.
 */
@Composable
fun SkillFlowNavHost(
    navController: NavHostController,
    startDestination: Any,
    innerPadding: PaddingValues,
    playStoreManager: PlayStoreManager
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
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
