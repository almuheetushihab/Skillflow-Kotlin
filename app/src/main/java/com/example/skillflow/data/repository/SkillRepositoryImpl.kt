package com.example.skillflow.data.repository

import android.content.Context
import com.example.skillflow.data.local.dao.SkillDao
import com.example.skillflow.data.local.entity.CareerPathEntity
import com.example.skillflow.data.local.entity.NuggetEntity
import com.example.skillflow.data.local.entity.toDomain
import com.example.skillflow.data.local.entity.toEntity
import com.example.skillflow.data.remote.SkillApi
import com.example.skillflow.data.remote.dto.SeedDataDto
import com.example.skillflow.domain.model.CareerPath
import com.example.skillflow.domain.model.KnowledgeNugget
import com.example.skillflow.domain.model.QuizQuestion
import com.example.skillflow.domain.model.UserNote
import com.example.skillflow.domain.repository.SkillRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import timber.log.Timber

class SkillRepositoryImpl @Inject constructor(
    private val api: SkillApi,
    private val dao: SkillDao,
    @ApplicationContext private val context: Context
) : SkillRepository {

    private val json = Json { 
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val assetCareerPaths by lazy {
        try {
            val content = context.assets.open("career_paths.json").bufferedReader().use { it.readText() }
            json.decodeFromString<List<CareerPath>>(content)
        } catch (e: Exception) {
            listOf(
                CareerPath(id = "android", name = "Android Developer", description = "Master modern mobile app development.", iconUrl = "", isUnlocked = true)
            )
        }
    }

    override fun getDailyNuggets(careerPathId: String): Flow<List<KnowledgeNugget>> {
        val path = if (careerPathId.isEmpty()) "android" else careerPathId
        return dao.getAllNuggetsByPath(path).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getNuggetsByDate(careerPathId: String, date: String): Flow<List<KnowledgeNugget>> {
        val path = if (careerPathId.isEmpty()) "android" else careerPathId
        return dao.getAllNuggetsByPath(path).map { entities ->
            entities.map { it.toDomain() }.filter { nugget ->
                val masteredDate = nugget.completionDate?.let {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it))
                }
                masteredDate == date || nugget.date == date
            }
        }
    }

    override fun getNuggetById(id: String): Flow<KnowledgeNugget?> {
        return dao.getNuggetById(id).map { it?.toDomain() }
    }

    override fun getSavedNuggets(): Flow<List<KnowledgeNugget>> {
        return dao.getSavedNuggets().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun toggleSaveNugget(nuggetId: String) {
        dao.toggleSaveNugget(nuggetId)
    }

    override suspend fun markNuggetAsDone(nuggetId: String) {
        dao.markNuggetAsDone(nuggetId)
    }

    override suspend fun updateMasteryStatus(nuggetId: String, isMastered: Boolean) {
        val completionDate = if (isMastered) System.currentTimeMillis() else null
        dao.updateMasteryStatus(nuggetId, isMastered, completionDate)
        if (isMastered) dao.markNuggetAsDone(nuggetId)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getCareerPaths(): Flow<List<CareerPath>> {
        return dao.getCareerPaths().flatMapLatest { entities ->
            if (entities.isEmpty()) flowOf(assetCareerPaths) 
            else flowOf(entities.map { it.toDomain() })
        }
    }

    override fun searchNuggets(query: String): Flow<List<KnowledgeNugget>> {
        return dao.searchNuggets(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getDailyProgress(careerPathId: String, date: String): Flow<Pair<Int, Int>> {
        val path = if (careerPathId.isEmpty()) "android" else careerPathId
        return combine(
            dao.getCompletedNuggetsCount(path),
            dao.getTotalNuggetsCount(path)
        ) { completed, total ->
            completed to total
        }
    }

    override fun getRecentlyLearnedTopics(careerPathId: String): Flow<List<String>> {
        val path = if (careerPathId.isEmpty()) "android" else careerPathId
        return dao.getRecentlyCompletedNuggets(path).map { list ->
            list.map { it.title }
        }
    }

    override fun getQuizQuestions(careerPathId: String): Flow<List<QuizQuestion>> {
        val path = if (careerPathId.isEmpty()) "android" else careerPathId
        return dao.getAllNuggetsByPath(path).map { nuggets ->
            nuggets.flatMap { it.toDomain().quizzes }
        }
    }

    override fun getNotesForNugget(nuggetId: String): Flow<List<UserNote>> {
        return dao.getNotesForNugget(nuggetId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun saveNote(note: UserNote) {
        dao.insertNote(note.toEntity())
    }

    override suspend fun deleteNote(note: UserNote) {
        dao.deleteNote(note.toEntity())
    }

    override suspend fun seedDatabase(): Boolean {
        Timber.d("Manual seeding triggered...")
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return try {
            val content = context.assets.open("seed_data.json").bufferedReader().use { it.readText() }
            val seedData = json.decodeFromString<SeedDataDto>(content)
            
            seedData.careerPaths.forEach { pathDto ->
                dao.insertCareerPaths(listOf(
                    CareerPathEntity(
                        id = pathDto.id,
                        name = pathDto.title,
                        description = pathDto.description,
                        iconUrl = pathDto.iconUrl,
                        isUnlocked = pathDto.id == "android"
                    )
                ))

                val entities = pathDto.nuggets.mapIndexed { index, nuggetDto ->
                    val domainQuizzes = nuggetDto.quizzes.map { q ->
                        QuizQuestion(
                            id = q.id,
                            nuggetId = nuggetDto.id,
                            text = q.text,
                            options = q.options,
                            correctAnswerIndex = q.correctAnswerIndex,
                            explanation = q.explanation
                        )
                    }
                    NuggetEntity(
                        id = nuggetDto.id,
                        title = nuggetDto.title,
                        shortDescription = nuggetDto.shortDescription,
                        content = nuggetDto.content,
                        complexity = nuggetDto.complexity,
                        imageUrl = nuggetDto.imageUrl,
                        careerPathId = nuggetDto.categoryId,
                        isDone = false,
                        isSaved = false,
                        isMastered = false,
                        completionDate = null,
                        priority = index,
                        date = today,
                        quizzesJson = json.encodeToString(domainQuizzes)
                    )
                }
                dao.insertNuggets(entities)
            }
            Timber.d("Seeding completed successfully")
            true
        } catch (e: Exception) {
            Timber.e(e, "Seeding failed")
            false
        }
    }
}
