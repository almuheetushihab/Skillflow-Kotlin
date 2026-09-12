package com.example.skillflow.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.skillflow.data.notification.NotificationHelper
import com.example.skillflow.domain.repository.SettingsRepository
import com.example.skillflow.domain.repository.SkillRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HiltWorker
class DailyReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val skillRepository: SkillRepository,
    private val settingsRepository: SettingsRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Timber.d("Executing DailyReminderWorker...")
        return try {
            val isEnabled = settingsRepository.isNotificationEnabled().first()
            if (!isEnabled) {
                Timber.d("Daily notifications disabled in settings.")
                return Result.success()
            }

            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val careerPathId = settingsRepository.getSelectedCareerPath().first() ?: "android"
            val streakCount = settingsRepository.getStreakCount().first()

            val nuggets = skillRepository.getNuggetsByDate(careerPathId, today).first()
            val hasStudiedToday = nuggets.any { it.isMastered || it.isDone }

            val title = "SkillFlow Streak Protector"
            val message = if (hasStudiedToday) {
                "You're on fire! 🚀 Your $streakCount day streak is safe. See you tomorrow!"
            } else {
                "Don't lose your $streakCount day streak! 🔥 Spend 5 minutes to learn something new."
            }

            notificationHelper.showNotification(title, message)
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Error running DailyReminderWorker")
            Result.failure()
        }
    }
}
