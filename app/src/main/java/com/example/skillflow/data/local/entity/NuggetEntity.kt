package com.example.skillflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.skillflow.domain.model.KnowledgeNugget
import com.example.skillflow.domain.model.QuizQuestion
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Entity(tableName = "nuggets")
data class NuggetEntity(
    @PrimaryKey val id: String,
    val title: String,
    val shortDescription: String,
    val content: String,
    val complexity: String,
    val imageUrl: String?,
    val videoUrl: String? = null,
    val careerPathId: String,
    val isDone: Boolean,
    val isSaved: Boolean,
    val isMastered: Boolean,
    val completionDate: Long?,
    val priority: Int,
    val date: String,
    val quizzesJson: String
)

fun NuggetEntity.toDomain(): KnowledgeNugget {
    val quizzes = try {
        Json.decodeFromString<List<QuizQuestion>>(quizzesJson)
    } catch (e: Exception) {
        emptyList()
    }
    return KnowledgeNugget(
        id = id,
        title = title,
        shortDescription = shortDescription,
        content = content,
        complexity = complexity,
        imageUrl = imageUrl,
        videoUrl = videoUrl,
        careerPathId = careerPathId,
        isDone = isDone,
        isSaved = isSaved,
        isMastered = isMastered,
        completionDate = completionDate,
        priority = priority,
        date = date,
        quizzes = quizzes
    )
}

fun KnowledgeNugget.toEntity(): NuggetEntity {
    return NuggetEntity(
        id = id,
        title = title,
        shortDescription = shortDescription,
        content = content,
        complexity = complexity,
        imageUrl = imageUrl,
        videoUrl = videoUrl,
        careerPathId = careerPathId,
        isDone = isDone,
        isSaved = isSaved,
        isMastered = isMastered,
        completionDate = completionDate,
        priority = priority,
        date = date,
        quizzesJson = Json.encodeToString(quizzes)
    )
}
