package com.example.skillflow.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.skillflow.domain.repository.SkillRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class DataSeedWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: SkillRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Timber.d("Starting DataSeedWorker...")
        return try {
            val success = repository.seedDatabase()
            if (success) Result.success() else Result.retry()
        } catch (e: Exception) {
            Timber.e(e, "DataSeedWorker failed")
            Result.failure()
        }
    }
}
