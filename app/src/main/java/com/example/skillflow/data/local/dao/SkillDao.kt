package com.example.skillflow.data.local.dao

import androidx.room.*
import com.example.skillflow.data.local.entity.CareerPathEntity
import com.example.skillflow.data.local.entity.NuggetEntity
import com.example.skillflow.data.local.entity.UserNoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SkillDao {
    @Query("SELECT * FROM nuggets WHERE careerPathId = :careerPathId ORDER BY priority ASC")
    fun getAllNuggetsByPath(careerPathId: String): Flow<List<NuggetEntity>>

    @Query("SELECT * FROM nuggets WHERE careerPathId = :careerPathId AND date = :date")
    fun getNuggetsByDate(careerPathId: String, date: String): Flow<List<NuggetEntity>>

    @Query("SELECT COUNT(*) FROM nuggets WHERE careerPathId = :careerPathId AND isDone = 1")
    fun getCompletedNuggetsCount(careerPathId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM nuggets WHERE careerPathId = :careerPathId")
    fun getTotalNuggetsCount(careerPathId: String): Flow<Int>

    @Query("SELECT * FROM nuggets WHERE careerPathId = :careerPathId AND isDone = 1 ORDER BY date DESC LIMIT 10")
    fun getRecentlyCompletedNuggets(careerPathId: String): Flow<List<NuggetEntity>>

    @Query("SELECT * FROM nuggets WHERE id = :id")
    fun getNuggetById(id: String): Flow<NuggetEntity?>

    @Query("SELECT * FROM nuggets WHERE id = :id")
    suspend fun getNuggetByIdSync(id: String): NuggetEntity?

    @Query("SELECT * FROM nuggets WHERE isSaved = 1")
    fun getSavedNuggets(): Flow<List<NuggetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNuggets(nuggets: List<NuggetEntity>)

    @Query("UPDATE nuggets SET isSaved = NOT isSaved WHERE id = :nuggetId")
    suspend fun toggleSaveNugget(nuggetId: String)

    @Query("UPDATE nuggets SET isDone = 1 WHERE id = :nuggetId")
    suspend fun markNuggetAsDone(nuggetId: String)

    @Query("UPDATE nuggets SET isMastered = :isMastered, completionDate = :completionDate WHERE id = :nuggetId")
    suspend fun updateMasteryStatus(nuggetId: String, isMastered: Boolean, completionDate: Long?)

    @Query("SELECT * FROM career_paths ORDER BY isUnlocked DESC, name ASC")
    fun getCareerPaths(): Flow<List<CareerPathEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCareerPaths(paths: List<CareerPathEntity>)

    // User Notes
    @Query("SELECT * FROM user_notes WHERE nuggetId = :nuggetId ORDER BY timestamp DESC")
    fun getNotesForNugget(nuggetId: String): Flow<List<UserNoteEntity>>

    @Query("SELECT * FROM nuggets WHERE title LIKE '%' || :query || '%' OR shortDescription LIKE '%' || :query || '%'")
    fun searchNuggets(query: String): Flow<List<NuggetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: UserNoteEntity)

    @Delete
    suspend fun deleteNote(note: UserNoteEntity)
}
