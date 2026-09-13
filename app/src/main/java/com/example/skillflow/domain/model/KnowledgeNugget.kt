package com.example.skillflow.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class KnowledgeNugget(
    val id: String,
    val title: String,
    val shortDescription: String,
    val content: String,
    val complexity: String, // Beginner, Intermediate, Advanced
    val imageUrl: String? = null,
    val videoUrl: String? = null,
    val careerPathId: String,
    val isDone: Boolean = false,
    val isSaved: Boolean = false,
    val isMastered: Boolean = false,
    val completionDate: Long? = null,
    val priority: Int = 0,
    val date: String,
    val quizzes: List<QuizQuestion> = emptyList()
)
