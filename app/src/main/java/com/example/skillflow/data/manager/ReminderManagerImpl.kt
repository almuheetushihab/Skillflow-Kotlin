package com.example.skillflow.data.manager

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.skillflow.data.worker.DailyReminderWorker
import com.example.skillflow.domain.manager.ReminderManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ReminderManager {

    companion object {
        const val REMINDER_WORK_NAME = "DailyStreakReminderWork"
    }

    override fun scheduleReminder(reminderTimeMillis: Long) {
        val workManager = WorkManager.getInstance(context)

        val hours = (reminderTimeMillis / (1000 * 60 * 60)).toInt().coerceIn(0, 23)
        val minutes = ((reminderTimeMillis / (1000 * 60)) % 60).toInt().coerceIn(0, 59)

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hours)
            set(Calendar.MINUTE, minutes)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val initialDelay = calendar.timeInMillis - System.currentTimeMillis()

        val reminderWorkRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            REMINDER_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            reminderWorkRequest
        )
    }

    override fun cancelReminder() {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork(REMINDER_WORK_NAME)
    }
}
