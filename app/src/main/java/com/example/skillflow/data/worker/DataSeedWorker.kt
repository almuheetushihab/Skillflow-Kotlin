package com.example.skillflow.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.skillflow.data.local.dao.SkillDao
import com.example.skillflow.data.local.entity.CareerPathEntity
import com.example.skillflow.data.local.entity.NuggetEntity
import com.example.skillflow.data.remote.dto.SeedDataDto
import com.example.skillflow.data.util.JsonAssetManager
import com.example.skillflow.domain.model.QuizQuestion
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Worker responsible for seeding the database with initial data from a JSON asset.
 */
@HiltWorker
class DataSeedWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val skillDao: SkillDao,
    private val jsonAssetManager: JsonAssetManager
) : CoroutineWorker(context, workerParams) {

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override suspend fun doWork(): Result {
        Timber.d("Starting data seeding...")
        val today = dateFormatter.format(Date())
        
        return try {
            val seedData: SeedDataDto? = jsonAssetManager.readAsset("seed_data.json")
            
            seedData?.careerPaths?.forEach { pathDto ->
                // Insert Career Path
                skillDao.insertCareerPaths(listOf(
                    CareerPathEntity(
                        id = pathDto.id,
                        name = pathDto.title,
                        description = pathDto.description,
                        iconUrl = pathDto.iconUrl,
                        isUnlocked = pathDto.id == "android" // Default unlock android
                    )
                ))

                // Insert Nuggets
                val nuggetEntities = pathDto.nuggets.mapIndexed { index, nuggetDto ->
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
                        quizzesJson = Json.encodeToString(domainQuizzes)
                    )
                }
                skillDao.insertNuggets(nuggetEntities)
                
                Timber.d("Seeded path: ${pathDto.title} with ${nuggetEntities.size} nuggets")
            }
            
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Error seeding data")
            Result.failure()
        }
    }
}
