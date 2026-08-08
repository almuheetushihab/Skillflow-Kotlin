
package com.example.skillflow.domain.manager

import android.app.Activity

/**
 * Interface for managing Google Play Store services like In-App Reviews and Updates.
 */
interface PlayStoreManager {
    /**
     * Checks for available app updates and triggers the update flow if necessary.
     * 
     * @param activity The activity context to launch the update flow.
     */
    fun checkForUpdates(activity: Activity)

    /**
     * Requests an in-app review from the user.
     * 
     * @param activity The activity context to launch the review flow.
     */
    fun requestReview(activity: Activity)

    /**
     * Should be called in onResume to handle pending updates.
     */
    fun resumeUpdate(activity: Activity)
}
