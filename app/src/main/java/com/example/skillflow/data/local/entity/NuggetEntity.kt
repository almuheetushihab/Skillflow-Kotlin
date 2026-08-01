package com.example.skillflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.skillflow.domain.model.KnowledgeNugget

@Entity(tableName = "nuggets")
data class NuggetEntity(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val imageUrl: String?,
    val careerPathId: String,
    val isDone: Boolean,
    val isSaved: Boolean,
    val date: String
)

fun NuggetEntity.toDomain(): KnowledgeNugget {
    return KnowledgeNugget(
        id = id,
        title = title,
        content = content,
        imageUrl = imageUrl,
        careerPathId = careerPathId,
        isDone = isDone,
        isSaved = isSaved,
        date = date
    )
}

fun KnowledgeNugget.toEntity(): NuggetEntity {
    return NuggetEntity(
        id = id,
        title = title,
        content = content,
        imageUrl = imageUrl,
        careerPathId = careerPathId,
        isDone = isDone,
        isSaved = isSaved,
        date = date
    )
}
