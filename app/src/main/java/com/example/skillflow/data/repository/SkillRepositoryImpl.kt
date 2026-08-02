package com.example.skillflow.data.repository

import android.content.Context
import com.example.skillflow.data.local.dao.SkillDao
import com.example.skillflow.data.local.entity.toDomain
import com.example.skillflow.data.local.entity.toEntity
import com.example.skillflow.data.remote.SkillApi
import com.example.skillflow.domain.model.CareerPath
import com.example.skillflow.domain.model.KnowledgeNugget
import com.example.skillflow.domain.model.QuizQuestion
import com.example.skillflow.domain.repository.SkillRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class SkillRepositoryImpl @Inject constructor(
    private val api: SkillApi,
    private val dao: SkillDao,
    @ApplicationContext private val context: Context
) : SkillRepository {

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private val json = Json { 
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val allCareerPaths by lazy {
        try {
            val content = context.assets.open("career_paths.json").bufferedReader().use { it.readText() }
            val list = json.decodeFromString<List<CareerPath>>(content)
            if (list.isEmpty()) throw Exception("Empty list")
            list
        } catch (e: Exception) {
            listOf(
                CareerPath("android", "Android Developer", "Master modern mobile app development with Kotlin and Jetpack Compose.", ""),
                CareerPath("ios", "iOS Developer", "Build premium mobile experiences using Swift and SwiftUI.", ""),
                CareerPath("backend", "Backend Engineer", "Design and build scalable server-side systems and APIs.", ""),
                CareerPath("frontend", "Frontend Developer", "Create engaging web interfaces using React, Vue, or Angular.", ""),
                CareerPath("uiux", "UI/UX Designer", "Craft beautiful, intuitive, and accessible user experiences.", ""),
                CareerPath("data", "Data Scientist", "Extract actionable insights from complex data sets using AI and ML.", "")
            )
        }
    }

    private val allNuggets by lazy {
        try {
            val content = context.assets.open("nuggets.json").bufferedReader().use { it.readText() }
            val list = json.decodeFromString<List<KnowledgeNugget>>(content)
            if (list.isEmpty()) throw Exception("Empty list")
            list
        } catch (e: Exception) {
            listOf(
                KnowledgeNugget("a1", "Kotlin Fundamentals", "Kotlin is a modern, statically typed language. Key features include null safety, extension functions, and higher-order functions which make Android development more concise and robust.", null, "android", false, false, "2026-08-03"),
                KnowledgeNugget("a2", "Jetpack Compose Basics", "Compose is Android's modern toolkit for building native UI. It simplifies UI development with a declarative approach, allowing you to describe your UI and let Compose handle the rendering.", null, "android", false, false, "2026-08-03"),
                KnowledgeNugget("a3", "Clean Architecture", "Separating concerns into Data, Domain, and Presentation layers. This makes your code more testable, maintainable, and independent of external frameworks or databases.", null, "android", false, false, "2026-08-03"),
                KnowledgeNugget("a4", "Hilt Dependency Injection", "Hilt provides a standard way to use Dagger DI in your Android app. It simplifies the setup and manages the lifecycle of dependencies automatically.", null, "android", false, false, "2026-08-03"),
                KnowledgeNugget("a5", "Coroutines & Flow", "Managing background tasks efficiently without blocking the main thread. Flow provides a reactive stream of data that can be observed in the UI.", null, "android", false, false, "2026-08-03"),
                KnowledgeNugget("i1", "Swift Fundamentals", "Swift is a powerful and intuitive programming language for iOS, macOS, tvOS, and watchOS.", null, "ios", false, false, "2026-08-03"),
                KnowledgeNugget("b1", "RESTful API Design", "REST is an architectural style for providing standards between computer systems on the web.", null, "backend", false, false, "2026-08-03")
            )
        }
    }

    private val allQuizQuestions by lazy {
        try {
            val content = context.assets.open("quizzes.json").bufferedReader().use { it.readText() }
            val list = json.decodeFromString<List<QuizQuestion>>(content)
            if (list.isEmpty()) throw Exception("Empty quiz list")
            list
        } catch (e: Exception) {
            listOf(
                QuizQuestion("a1", "android", "What is the primary language for Android?", listOf("Java", "Kotlin"), 1, "Kotlin is preferred."),
                QuizQuestion("a2", "android", "What manages UI data?", listOf("Activity", "ViewModel"), 1, "ViewModel survives rotation.")
            )
        }
    }

    override fun getDailyNuggets(careerPathId: String): Flow<List<KnowledgeNugget>> = flow {
        val today = dateFormatter.format(Date())
        val path = if (careerPathId.isEmpty()) "android" else careerPathId
        
        dao.getDailyNuggets(path, today).collect { localNuggets ->
            if (localNuggets.isNotEmpty()) {
                emit(localNuggets.map { it.toDomain() })
            } else {
                val careerNuggets = allNuggets.filter { it.careerPathId == path }
                // Take 3 random or first 3 for simplicity
                val todayNuggets = careerNuggets.take(3).map { it.copy(date = today) }
                if (todayNuggets.isNotEmpty()) {
                    dao.insertNuggets(todayNuggets.map { it.toEntity() })
                } else {
                    emit(emptyList())
                }
            }
        }
    }

    override fun searchNuggets(query: String): Flow<List<KnowledgeNugget>> = flow {
        val filtered = allNuggets.filter { 
            it.title.contains(query, ignoreCase = true) || it.content.contains(query, ignoreCase = true)
        }
        emit(filtered)
    }

    override fun getNuggetById(id: String): Flow<KnowledgeNugget?> = flow {
        dao.getNuggetById(id).collect { entity ->
            if (entity != null) {
                emit(entity.toDomain())
            } else {
                emit(allNuggets.find { it.id == id })
            }
        }
    }

    override fun getSavedNuggets(): Flow<List<KnowledgeNugget>> {
        return dao.getSavedNuggets().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun toggleSaveNugget(nuggetId: String) {
        val existing = dao.getNuggetByIdSync(nuggetId)
        if (existing == null) {
            val assetNugget = allNuggets.find { it.id == nuggetId }
            assetNugget?.let {
                dao.insertNuggets(listOf(it.toEntity().copy(isSaved = true)))
            }
        } else {
            dao.toggleSaveNugget(nuggetId)
        }
    }

    override suspend fun markNuggetAsDone(nuggetId: String) {
        val existing = dao.getNuggetByIdSync(nuggetId)
        if (existing == null) {
            val assetNugget = allNuggets.find { it.id == nuggetId }
            assetNugget?.let {
                dao.insertNuggets(listOf(it.toEntity().copy(isDone = true)))
            }
        } else {
            dao.markNuggetAsDone(nuggetId)
        }
    }

    override fun getCareerPaths(): Flow<List<CareerPath>> = flow {
        emit(allCareerPaths)
    }

    override fun getDailyProgress(careerPathId: String, date: String): Flow<Pair<Int, Int>> {
        val path = if (careerPathId.isEmpty()) "android" else careerPathId
        return combine(
            dao.getCompletedNuggetsCount(path, date),
            dao.getTotalNuggetsCount(path, date)
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

    override fun getQuizQuestions(careerPathId: String): Flow<List<QuizQuestion>> = flow {
        val path = if (careerPathId.isEmpty()) "android" else careerPathId
        val pathQuestions = allQuizQuestions.filter { it.careerPathId == path }
        // Fallback to Android if path has no questions
        if (pathQuestions.isEmpty()) {
            emit(allQuizQuestions.filter { it.careerPathId == "android" })
        } else {
            emit(pathQuestions)
        }
    }
}
