package com.example.skillflow.di

import com.example.skillflow.data.repository.SkillRepositoryImpl
import com.example.skillflow.domain.repository.SkillRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSkillRepository(
        skillRepositoryImpl: SkillRepositoryImpl
    ): SkillRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: com.example.skillflow.data.repository.SettingsRepositoryImpl
    ): com.example.skillflow.domain.repository.SettingsRepository
}
