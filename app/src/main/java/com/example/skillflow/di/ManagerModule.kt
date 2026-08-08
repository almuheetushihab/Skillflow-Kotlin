package com.example.skillflow.di

import com.example.skillflow.data.manager.PlayStoreManagerImpl
import com.example.skillflow.domain.manager.PlayStoreManager
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
}
