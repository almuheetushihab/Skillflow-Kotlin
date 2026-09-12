package com.example.skillflow.di

import com.example.skillflow.data.manager.TtsManagerImpl
import com.example.skillflow.domain.manager.TtsManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TtsModule {

    @Binds
    @Singleton
    abstract fun bindTtsManager(
        ttsManagerImpl: TtsManagerImpl
    ): TtsManager
}
