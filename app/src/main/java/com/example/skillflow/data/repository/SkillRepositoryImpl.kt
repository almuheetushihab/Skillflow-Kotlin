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
        CareerPath("android", "Android Developer", "Master modern mobile app development", ""),
        CareerPath("backend", "Backend Engineer", "Build scalable server-side systems", ""),
        CareerPath("uiux", "UI/UX Designer", "Craft beautiful and usable interfaces", ""),
        CareerPath("data", "Data Scientist", "Extract actionable insights from data", "")
    )

    private val mockNuggets = mapOf(
        "android" to listOf(
            KnowledgeNugget("a1", "Clean Architecture", "Separating concerns into layers (Data, Domain, Presentation) for testability. Data layer handles networking and DB, Domain holds business logic (UseCases), and Presentation manages UI state.", null, "android", false, false, ""),
            KnowledgeNugget("a2", "Hilt Dependency Injection", "Dagger-based dependency injection for Android that reduces boilerplate. It provides a standard way to use DI in your application by providing containers for every Android class.", null, "android", false, false, ""),
            KnowledgeNugget("a3", "Jetpack Compose", "Modern toolkit for building native UI with a declarative approach. It simplifies and accelerates UI development on Android with less code and powerful tools.", null, "android", false, false, ""),
            KnowledgeNugget("a4", "Coroutines & Flow", "Managing asynchronous tasks and reactive data streams effectively. Coroutines are light-weight threads, and Flow is a stream of data that can be computed asynchronously.", null, "android", false, false, ""),
            KnowledgeNugget("a5", "Material 3", "Latest evolution of Material Design for adaptive and beautiful UIs. It includes updated components, typography, and color systems for a more expressive look.", null, "android", false, false, ""),
            KnowledgeNugget("a6", "Retrofit Networking", "A type-safe HTTP client for Android and Java. It turns your HTTP API into a Java interface using annotations to describe the requests.", null, "android", false, false, ""),
            KnowledgeNugget("a7", "Room Database", "The Room persistence library provides an abstraction layer over SQLite to allow fluent database access while leveraging the full power of SQLite.", null, "android", false, false, ""),
            KnowledgeNugget("a8", "ViewModel & State", "The ViewModel class is designed to store and manage UI-related data in a lifecycle-conscious way. It allows data to survive configuration changes such as screen rotations.", null, "android", false, false, ""),
            KnowledgeNugget("a9", "Navigation Component", "Navigation refers to the interactions that allow users to navigate across, into, and back out from the different pieces of content within your app.", null, "android", false, false, ""),
            KnowledgeNugget("a10", "WorkManager", "WorkManager is the recommended solution for persistent work. Persistent work is scheduled even if the app restarts or the device reboots.", null, "android", false, false, ""),
            KnowledgeNugget("a11", "Unit Testing", "The goal of unit testing is to isolate each part of the program and show that the individual parts are correct. Use JUnit and Mockito for Android.", null, "android", false, false, ""),
            KnowledgeNugget("a12", "Performance Profiling", "Use Android Studio Profiler to inspect how your app uses CPU, memory, network, and battery resources in real-time.", null, "android", false, false, "")
        ),
        "uiux" to listOf(
            KnowledgeNugget("u1", "Typography Hierarchy", "Hierarchy, contrast, and spacing are key to readable interfaces. Use different font sizes and weights to guide the user\'s eye to the most important info.", null, "uiux", false, false, ""),
            KnowledgeNugget("u2", "Color Psychology", "Understanding how colors interact and influence user emotions. Blue conveys trust, red creates urgency, and green represents growth and success.", null, "uiux", false, false, ""),
            KnowledgeNugget("u3", "Grid Systems", "Using layout grids to maintain consistency and alignment. Grids help in creating a structured layout that works across different screen sizes.", null, "uiux", false, false, ""),
            KnowledgeNugget("u4", "Accessibility (a11y)", "Designing for everyone, including those with visual impairments. Use high contrast colors and provide alternative text for images.", null, "uiux", false, false, ""),
            KnowledgeNugget("u5", "Micro-interactions", "Subtle animations that provide feedback and delight users. They make the UI feel alive and responsive to user actions.", null, "uiux", false, false, ""),
            KnowledgeNugget("u6", "User Research", "The process of understanding user needs, behaviors, and motivations through various qualitative and quantitative methods.", null, "uiux", false, false, ""),
            KnowledgeNugget("u7", "Wireframing", "A low-fidelity way to show the structure of a page or app. It focuses on the layout of content rather than the visual design.", null, "uiux", false, false, ""),
            KnowledgeNugget("u8", "Prototyping", "Creating an interactive model of the final product to test and validate design ideas before full-scale development.", null, "uiux", false, false, ""),
            KnowledgeNugget("u9", "Visual Hierarchy", "Arranging elements to imply importance. Use size, color, and whitespace to create a clear path for the user to follow.", null, "uiux", false, false, ""),
            KnowledgeNugget("u10", "Usability Testing", "Testing the product with real users to identify any friction points or areas for improvement in the user experience.", null, "uiux", false, false, "")
        ),
        "backend" to listOf(
            KnowledgeNugget("b1", "RESTful APIs", "A standardized way to build web services that allow different systems to communicate over HTTP using methods like GET, POST, PUT, DELETE.", null, "backend", false, false, ""),
            KnowledgeNugget("b2", "SQL vs NoSQL", "Relational databases use SQL and schemas (PostgreSQL, MySQL), while NoSQL databases are schema-less and flexible (MongoDB, Redis).", null, "backend", false, false, ""),
            KnowledgeNugget("b3", "Authentication (JWT)", "JSON Web Tokens are an open standard for securely transmitting information between parties as a JSON object. Used for stateless auth.", null, "backend", false, false, ""),
            KnowledgeNugget("b4", "Microservices", "An architectural style that structures an application as a collection of small autonomous services modeled around a business domain.", null, "backend", false, false, ""),
            KnowledgeNugget("b5", "Docker & Containers", "Packaging applications and their dependencies into containers to ensure they run consistently across different environments.", null, "backend", false, false, ""),
            KnowledgeNugget("b6", "Message Queues (Kafka)", "Using systems like Kafka or RabbitMQ to enable asynchronous communication between different parts of a system.", null, "backend", false, false, ""),
            KnowledgeNugget("b7", "Caching (Redis)", "Storing frequently accessed data in memory to reduce latency and load on the primary database.", null, "backend", false, false, "")
        ),
        "data" to listOf(
            KnowledgeNugget("d1", "Python for Data", "Python is the most popular language for data science due to libraries like Pandas, NumPy, and Scikit-Learn.", null, "data", false, false, ""),
            KnowledgeNugget("d2", "Machine Learning Basics", "Teaching computers to learn from data without being explicitly programmed. Includes Supervised and Unsupervised learning.", null, "data", false, false, ""),
            KnowledgeNugget("d3", "Data Visualization", "Representing data graphically using charts, plots, and maps to identify patterns and trends (Matplotlib, Seaborn).", null, "data", false, false, ""),
            KnowledgeNugget("d4", "Feature Engineering", "The process of selecting, manipulating, and transforming raw data into features that can be used in supervised learning.", null, "data", false, false, ""),
            KnowledgeNugget("d5", "Natural Language Processing", "A branch of AI that gives computers the ability to understand, interpret, and generate human language.", null, "data", false, false, "")
        )
    )

    override fun getDailyNuggets(careerPathId: String): Flow<List<KnowledgeNugget>> = flow {
        val today = dateFormatter.format(Date())
        val nuggets = (mockNuggets[careerPathId] ?: mockNuggets["android"]!!)
            .take(3) // Only 3 per day as per SRS
            .map { it.copy(date = today) }
        emit(nuggets)
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
