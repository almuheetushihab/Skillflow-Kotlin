package com.example.skillflow.di

import com.example.skillflow.data.analytics.AnalyticsHelperImpl
import com.example.skillflow.data.manager.PlayStoreManagerImpl
import com.example.skillflow.data.manager.ReminderManagerImpl
import com.example.skillflow.domain.analytics.AnalyticsHelper
import com.example.skillflow.domain.manager.PlayStoreManager
import com.example.skillflow.domain.manager.ReminderManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ManagerModule {

    @Binds
    @Singleton
    abstract fun bindPlayStoreManager(
        playStoreManagerImpl: PlayStoreManagerImpl
    ): PlayStoreManager

    @Binds
    @Singleton
    abstract fun bindAnalyticsHelper(
        analyticsHelperImpl: AnalyticsHelperImpl
    ): AnalyticsHelper

    @Binds
    @Singleton
    abstract fun bindReminderManager(
        reminderManagerImpl: ReminderManagerImpl
    ): ReminderManager
}
