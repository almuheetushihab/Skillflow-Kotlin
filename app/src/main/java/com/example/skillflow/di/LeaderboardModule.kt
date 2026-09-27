package com.example.skillflow.di

import com.example.skillflow.data.repository.FakeLeaderboardRepositoryImpl
import com.example.skillflow.domain.repository.LeaderboardRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing [LeaderboardRepository] bindings.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class LeaderboardModule {

    @Binds
    @Singleton
    abstract fun bindLeaderboardRepository(
        impl: FakeLeaderboardRepositoryImpl
    ): LeaderboardRepository
}
