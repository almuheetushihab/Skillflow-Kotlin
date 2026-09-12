package com.example.skillflow.domain.manager

/**
 * Interface for scheduling and canceling daily reminder notifications using WorkManager.
 */
interface ReminderManager {
    /**
     * Schedules or updates a periodic work request for the daily reminder at the given time.
     *
     * @param reminderTimeMillis Time in milliseconds from midnight (e.g. 21 * 3600 * 1000 for 9:00 PM).
     */
    fun scheduleReminder(reminderTimeMillis: Long)

    /**
     * Cancels any scheduled daily reminder work requests.
     */
    fun cancelReminder()
}
