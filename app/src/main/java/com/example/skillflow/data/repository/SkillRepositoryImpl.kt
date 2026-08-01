package com.example.skillflow.data.repository

import com.example.skillflow.data.local.dao.SkillDao
import com.example.skillflow.data.local.entity.toDomain
import com.example.skillflow.data.local.entity.toEntity
import com.example.skillflow.data.remote.SkillApi
import com.example.skillflow.domain.model.CareerPath
import com.example.skillflow.domain.model.KnowledgeNugget
import com.example.skillflow.domain.repository.SkillRepository
import kotlinx.coroutines.flow.*
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
        CareerPath("android", "Android Developer", "Master modern mobile app development", ""),
        CareerPath("backend", "Backend Engineer", "Build scalable server-side systems", ""),
        CareerPath("uiux", "UI/UX Designer", "Craft beautiful and usable interfaces", ""),
        CareerPath("data", "Data Scientist", "Extract actionable insights from data", "")
    )

    private val mockNuggets = mapOf(
        "android" to listOf(
            KnowledgeNugget("a1", "Kotlin Fundamentals", "Kotlin is a modern, statically typed language. Key features include null safety, extension functions, and higher-order functions which make Android development more concise and robust.", null, "android", false, false, ""),
            KnowledgeNugget("a2", "Jetpack Compose Basics", "Compose is Android's modern toolkit for building native UI. It simplifies UI development with a declarative approach, allowing you to describe your UI and let Compose handle the rendering.", null, "android", false, false, ""),
            KnowledgeNugget("a3", "Clean Architecture", "Separating concerns into Data, Domain, and Presentation layers. This makes your code more testable, maintainable, and independent of external frameworks or databases.", null, "android", false, false, ""),
            KnowledgeNugget("a4", "Hilt Dependency Injection", "Hilt provides a standard way to use Dagger DI in your Android app. It simplifies the setup and manages the lifecycle of dependencies automatically.", null, "android", false, false, ""),
            KnowledgeNugget("a5", "Coroutines & Flow", "Managing background tasks efficiently without blocking the main thread. Flow provides a reactive stream of data that can be observed in the UI.", null, "android", false, false, ""),
            KnowledgeNugget("a6", "Retrofit Networking", "The most popular HTTP client for Android. It turns your API into a Java interface using annotations, making network calls type-safe and easy.", null, "android", false, false, ""),
            KnowledgeNugget("a7", "Room Database", "The recommended way to persist data locally. It provides an abstraction layer over SQLite and allows compile-time verification of queries.", null, "android", false, false, ""),
            KnowledgeNugget("a8", "ViewModel & State", "Storing and managing UI-related data in a lifecycle-conscious way. ViewModels survive configuration changes like screen rotations.", null, "android", false, false, ""),
            KnowledgeNugget("a9", "Navigation Component", "Simplifying navigation between screens. It handles the back stack and argument passing in a consistent way.", null, "android", false, false, ""),
            KnowledgeNugget("a10", "WorkManager", "For tasks that need to run even if the app exits or the device reboots. Perfect for syncing data or uploading logs.", null, "android", false, false, ""),
            KnowledgeNugget("a11", "Unit Testing with Mockito", "Ensuring your business logic works as expected. Mockito helps you mock dependencies and isolate the code under test.", null, "android", false, false, ""),
            KnowledgeNugget("a12", "Performance Profiling", "Using Android Studio Profiler to find memory leaks and CPU bottlenecks. Crucial for creating smooth, high-quality apps.", null, "android", false, false, ""),
            KnowledgeNugget("a13", "Material 3 Design", "Implementing the latest Material Design system. Use dynamic colors and updated components for a modern look.", null, "android", false, false, ""),
            KnowledgeNugget("a14", "Deep Linking", "Allowing users to navigate to specific screens in your app from external URLs or notifications.", null, "android", false, false, ""),
            KnowledgeNugget("a15", "App Modularization", "Breaking down a large app into smaller, independent modules. Improves build times and code organization.", null, "android", false, false, "")
        ),
        "uiux" to listOf(
            KnowledgeNugget("u1", "Typography Hierarchy", "Using different font sizes and weights to guide the user's attention. Good hierarchy makes content easy to scan.", null, "uiux", false, false, ""),
            KnowledgeNugget("u2", "Color Theory", "Understanding how colors interact and influence emotions. Blue builds trust, while red creates a sense of urgency.", null, "uiux", false, false, ""),
            KnowledgeNugget("u3", "Grid Systems", "Maintaining consistency and alignment using layout grids. Grids help in creating balanced and responsive designs.", null, "uiux", false, false, ""),
            KnowledgeNugget("u4", "Accessibility (a11y)", "Designing for everyone, including users with visual or motor impairments. High contrast and large tap targets are key.", null, "uiux", false, false, ""),
            KnowledgeNugget("u5", "Micro-interactions", "Subtle animations that provide feedback and make the app feel alive and responsive.", null, "uiux", false, false, ""),
            KnowledgeNugget("u6", "User Research", "The process of understanding user needs and behaviors through interviews, surveys, and usability testing.", null, "uiux", false, false, ""),
            KnowledgeNugget("u7", "Wireframing", "Creating low-fidelity blueprints of your UI. Focuses on structure and layout without worrying about visual details.", null, "uiux", false, false, ""),
            KnowledgeNugget("u8", "Design Systems", "A collection of reusable components and standards that guide design and development for consistency.", null, "uiux", false, false, ""),
            KnowledgeNugget("u9", "Heuristic Evaluation", "Reviewing a UI against established usability principles to find potential issues early in the design phase.", null, "uiux", false, false, ""),
            KnowledgeNugget("u10", "Figma Prototyping", "Connecting frames in Figma to create interactive models of your app. Essential for testing user flows.", null, "uiux", false, false, "")
        )
    )

    override fun getDailyNuggets(careerPathId: String): Flow<List<KnowledgeNugget>> = flow {
        val today = dateFormatter.format(Date())
        val path = if (careerPathId.isEmpty()) "android" else careerPathId
        
        // 1. Collect from DB first
        dao.getDailyNuggets(path, today).collect { localNuggets ->
            if (localNuggets.isNotEmpty()) {
                emit(localNuggets.map { it.toDomain() })
            } else {
                // 2. If empty, sync from mock and insert
                val mockList = mockNuggets[path] ?: mockNuggets["android"]!!
                val todayNuggets = mockList.take(3).map { it.copy(date = today) }
                dao.insertNuggets(todayNuggets.map { it.toEntity() })
                // The flow from DAO will automatically emit the new data
            }
        }
    }

    override fun searchNuggets(query: String): Flow<List<KnowledgeNugget>> = flow {
        val allNuggets = mockNuggets.values.flatten()
        val filtered = allNuggets.filter { 
            it.title.contains(query, ignoreCase = true) || it.content.contains(query, ignoreCase = true)
        }
        emit(filtered)
    }

    override fun getSavedNuggets(): Flow<List<KnowledgeNugget>> {
        return dao.getSavedNuggets().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun toggleSaveNugget(nuggetId: String) {
        // Ensure the nugget exists in DB before toggling (it might be from search or initial load)
        val allMock = mockNuggets.values.flatten()
        val nugget = allMock.find { it.id == nuggetId }
        if (nugget != null) {
            dao.insertNuggets(listOf(nugget.toEntity()))
        }
        dao.toggleSaveNugget(nuggetId)
    }

    override suspend fun markNuggetAsDone(nuggetId: String) {
        dao.markNuggetAsDone(nuggetId)
    }

    override fun getCareerPaths(): Flow<List<CareerPath>> = flow {
        emit(mockCareerPaths)
    }
}
