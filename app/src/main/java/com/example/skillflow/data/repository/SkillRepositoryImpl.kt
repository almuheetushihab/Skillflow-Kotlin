package com.example.skillflow.data.repository

import com.example.skillflow.data.local.dao.SkillDao
import com.example.skillflow.data.local.entity.toDomain
import com.example.skillflow.data.local.entity.toEntity
import com.example.skillflow.data.remote.SkillApi
import com.example.skillflow.domain.model.CareerPath
import com.example.skillflow.domain.model.KnowledgeNugget
import com.example.skillflow.domain.repository.SkillRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class SkillRepositoryImpl @Inject constructor(
    private val api: SkillApi,
    private val dao: SkillDao
) : SkillRepository {

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private val mockCareerPaths = listOf(
        CareerPath("android", "Android Developer", "Build beautiful mobile apps", ""),
        CareerPath("backend", "Backend Engineer", "Design scalable server systems", ""),
        CareerPath("uiux", "UI/UX Designer", "Create user-centric interfaces", ""),
        CareerPath("data", "Data Scientist", "Unlock insights from data", "")
    )

    private val mockNuggets = mapOf(
        "android" to listOf(
            KnowledgeNugget("a1", "Clean Architecture", "Separating concerns into layers (Data, Domain, Presentation) for testability.", null, "android", false, false, ""),
            KnowledgeNugget("a2", "Hilt DI", "Dagger-based dependency injection for Android that reduces boilerplate.", null, "android", false, false, ""),
            KnowledgeNugget("a3", "Jetpack Compose", "Modern toolkit for building native UI with a declarative approach.", null, "android", false, false, ""),
            KnowledgeNugget("a4", "Coroutines & Flow", "Managing asynchronous tasks and reactive data streams effectively.", null, "android", false, false, ""),
            KnowledgeNugget("a5", "Material 3", "Latest evolution of Material Design for adaptive and beautiful UIs.", null, "android", false, false, "")
        ),
        "uiux" to listOf(
            KnowledgeNugget("u1", "Typography Basics", "Hierarchy, contrast, and spacing are key to readable interfaces.", null, "uiux", false, false, ""),
            KnowledgeNugget("u2", "Color Theory", "Understanding how colors interact and influence user emotions.", null, "uiux", false, false, ""),
            KnowledgeNugget("u3", "Grid Systems", "Using layout grids to maintain consistency and alignment.", null, "uiux", false, false, ""),
            KnowledgeNugget("u4", "Accessibility (a11y)", "Designing for everyone, including those with visual impairments.", null, "uiux", false, false, ""),
            KnowledgeNugget("u5", "Micro-interactions", "Subtle animations that provide feedback and delight users.", null, "uiux", false, false, "")
        )
    )

    override fun getDailyNuggets(careerPathId: String): Flow<List<KnowledgeNugget>> = flow {
        val today = dateFormatter.format(Date())
        
        // Use mock data if local/remote fails or for demo
        val nuggets = (mockNuggets[careerPathId] ?: mockNuggets["android"]!!).map { it.copy(date = today) }
        emit(nuggets)

        /* // Commented out for now to ensure mock data is visible
        val localNuggets = dao.getDailyNuggets(careerPathId, today).first()
        if (localNuggets.isNotEmpty()) {
            emit(localNuggets.map { it.toDomain() })
        }

        try {
            val remoteNuggets = api.getDailyNuggets(careerPathId, today)
            dao.insertNuggets(remoteNuggets.map { it.toEntity() })
            val updatedLocal = dao.getDailyNuggets(careerPathId, today).first()
            emit(updatedLocal.map { it.toDomain() })
        } catch (e: Exception) {}
        */
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

    override fun getCareerPaths(): Flow<List<CareerPath>> = flow {
        emit(mockCareerPaths)
        /*
        val localPaths = dao.getCareerPaths().first()
        if (localPaths.isNotEmpty()) emit(localPaths.map { it.toDomain() })

        try {
            val remotePaths = api.getCareerPaths()
            dao.insertCareerPaths(remotePaths.map { it.toEntity() })
            val updatedLocal = dao.getCareerPaths().first()
            emit(updatedLocal.map { it.toDomain() })
        } catch (e: Exception) {}
        */
    }
}
