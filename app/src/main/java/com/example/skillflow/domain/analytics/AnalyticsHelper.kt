package com.example.skillflow.domain.analytics

/**
 * Interface for tracking user events and screen views across the app.
 */
interface AnalyticsHelper {
    /**
     * Tracks a screen view.
     * @param screenName The name of the screen.
     * @param screenClass The class of the screen.
     */
    fun logScreenView(screenName: String, screenClass: String)

    /**
     * Tracks when a user completes a Knowledge Nugget.
     * @param nuggetId The unique ID of the nugget.
     * @param title The title of the nugget.
     */
    fun logNuggetCompleted(nuggetId: String, title: String)

    /**
     * Tracks when a user finishes a quiz.
     * @param score The score achieved.
     * @param totalQuestions Total number of questions in the quiz.
     */
    fun logQuizFinished(score: Int, totalQuestions: Int)

    /**
     * Tracks a generic event.
     * @param eventName Name of the event.
     * @param params Key-value pairs of parameters.
     */
    fun logEvent(eventName: String, params: Map<String, Any> = emptyMap())
}
