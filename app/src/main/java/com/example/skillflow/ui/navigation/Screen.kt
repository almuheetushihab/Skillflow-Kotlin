package com.example.skillflow.ui.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation destinations using Kotlin Serialization.
 */
sealed interface Screen {

    @Serializable
    data object Onboarding : Screen

    @Serializable
    data object Login : Screen

    @Serializable
    data object SignUp : Screen

    @Serializable
    data object ForgotPassword : Screen

    @Serializable
    data object Home : Screen

    @Serializable
    data class Detail(val nuggetId: String) : Screen

    @Serializable
    data object Roadmap : Screen

    @Serializable
    data object Quiz : Screen

    @Serializable
    data object Bookmarks : Screen

    @Serializable
    data object Profile : Screen

    @Serializable
    data object Settings : Screen

    @Serializable
    data object PrivacyPolicy : Screen
}
