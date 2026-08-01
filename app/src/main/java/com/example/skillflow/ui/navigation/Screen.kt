package com.example.skillflow.ui.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
    object Detail : Screen("detail/{nuggetId}") {
        fun createRoute(nuggetId: String) = "detail/$nuggetId"
    }
    object Roadmap : Screen("roadmap")
    object Quiz : Screen("quiz")
    object Bookmarks : Screen("bookmarks")
    object Profile : Screen("profile")
}
