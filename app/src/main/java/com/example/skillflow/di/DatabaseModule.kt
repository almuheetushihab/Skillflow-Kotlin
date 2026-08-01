package com.example.skillflow.di

import android.content.Context
import androidx.room.Room
import com.example.skillflow.data.local.SkillDatabase
import com.example.skillflow.data.local.dao.SkillDao
import com.example.skillflow.util.Constants.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideSkillDatabase(@ApplicationContext context: Context): SkillDatabase {
        return Room.databaseBuilder(
            context,
            SkillDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideSkillDao(database: SkillDatabase): SkillDao {
        return database.dao
    }
}
