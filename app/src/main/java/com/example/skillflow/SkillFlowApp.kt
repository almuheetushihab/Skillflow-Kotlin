package com.example.skillflow

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.skillflow.data.worker.DataSeedWorker
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class SkillFlowApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        scheduleInitialSeeding()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    private fun scheduleInitialSeeding() {
        val workRequest = OneTimeWorkRequestBuilder<DataSeedWorker>().build()
        WorkManager.getInstance(this).enqueueUniqueWork(
            "DataSeedWork",
            ExistingWorkPolicy.REPLACE, // Force seeding on each app start for now to ensure data is there
            workRequest
        )
    }
}
