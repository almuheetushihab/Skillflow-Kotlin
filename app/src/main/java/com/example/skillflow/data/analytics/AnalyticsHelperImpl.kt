package com.example.skillflow.data.analytics

import android.os.Bundle
import com.example.skillflow.domain.analytics.AnalyticsHelper
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [AnalyticsHelper] using Firebase Analytics.
 */
@Singleton
class AnalyticsHelperImpl @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) : AnalyticsHelper {

    override fun logScreenView(screenName: String, screenClass: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    override fun logNuggetCompleted(nuggetId: String, title: String) {
        val bundle = Bundle().apply {
            putString("nugget_id", nuggetId)
            putString("nugget_title", title)
        }
        firebaseAnalytics.logEvent("nugget_completed", bundle)
    }

    override fun logQuizFinished(score: Int, totalQuestions: Int) {
        val bundle = Bundle().apply {
            putInt(FirebaseAnalytics.Param.SCORE, score)
            putInt("total_questions", totalQuestions)
            putDouble("percentage", (score.toFloat() / totalQuestions).toDouble())
        }
        firebaseAnalytics.logEvent("quiz_finished", bundle)
    }

    override fun logEvent(eventName: String, params: Map<String, Any>) {
        val bundle = Bundle().apply {
            params.forEach { (key, value) ->
                when (value) {
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Double -> putDouble(key, value)
                    is Boolean -> putBoolean(key, value)
                }
            }
        }
        firebaseAnalytics.logEvent(eventName, bundle)
    }
}
