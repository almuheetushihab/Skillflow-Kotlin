package com.example.skillflow.data.local.dao

import androidx.room.*
import com.example.skillflow.data.local.entity.CareerPathEntity
import com.example.skillflow.data.local.entity.NuggetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SkillDao {
    @Query("SELECT * FROM nuggets WHERE careerPathId = :careerPathId AND date = :date")
    fun getDailyNuggets(careerPathId: String, date: String): Flow<List<NuggetEntity>>

    @Query("SELECT * FROM nuggets WHERE isSaved = 1")
    fun getSavedNuggets(): Flow<List<NuggetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNuggets(nuggets: List<NuggetEntity>)

    @Query("UPDATE nuggets SET isSaved = NOT isSaved WHERE id = :nuggetId")
    suspend fun toggleSaveNugget(nuggetId: String)

    @Query("UPDATE nuggets SET isDone = 1 WHERE id = :nuggetId")
    suspend fun markNuggetAsDone(nuggetId: String)

    @Query("SELECT * FROM career_paths")
    fun getCareerPaths(): Flow<List<CareerPathEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCareerPaths(paths: List<CareerPathEntity>)
}
