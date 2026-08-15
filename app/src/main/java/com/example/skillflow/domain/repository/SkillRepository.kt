package com.example.skillflow.domain.repository

import com.example.skillflow.domain.model.CareerPath
import com.example.skillflow.domain.model.KnowledgeNugget
import com.example.skillflow.domain.model.QuizQuestion
import com.example.skillflow.domain.model.UserNote
import kotlinx.coroutines.flow.Flow

interface SkillRepository {
    fun getDailyNuggets(careerPathId: String): Flow<List<KnowledgeNugget>>
    fun getNuggetsByDate(careerPathId: String, date: String): Flow<List<KnowledgeNugget>>
    fun getSavedNuggets(): Flow<List<KnowledgeNugget>>
    suspend fun toggleSaveNugget(nuggetId: String)
    suspend fun markNuggetAsDone(nuggetId: String)
    suspend fun updateMasteryStatus(nuggetId: String, isMastered: Boolean)
    fun getCareerPaths(): Flow<List<CareerPath>>
    fun searchNuggets(query: String): Flow<List<KnowledgeNugget>>
    fun getNuggetById(id: String): Flow<KnowledgeNugget?>
    fun getDailyProgress(careerPathId: String, date: String): Flow<Pair<Int, Int>>
    fun getRecentlyLearnedTopics(careerPathId: String): Flow<List<String>>
    fun getQuizQuestions(careerPathId: String): Flow<List<QuizQuestion>>
    
    // Notes
    fun getNotesForNugget(nuggetId: String): Flow<List<UserNote>>
    suspend fun saveNote(note: UserNote)
    suspend fun deleteNote(note: UserNote)

    // Seeding
    suspend fun seedDatabase(): Boolean
}
