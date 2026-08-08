package com.example.skillflow.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SeedDataDto(
    @SerialName("career_paths")
    val careerPaths: List<CareerPathSeedDto>
)

@Serializable
data class CareerPathSeedDto(
    val id: String,
    val title: String,
    val description: String,
    @SerialName("icon_url")
    val iconUrl: String,
    @SerialName("knowledge_nuggets")
    val nuggets: List<NuggetSeedDto>,
    @SerialName("quiz_questions")
    val quizzes: List<QuizSeedDto>
)

@Serializable
data class NuggetSeedDto(
    val id: String,
    val title: String,
    val content: String,
    @SerialName("category_id")
    val categoryId: String,
    @SerialName("image_url")
    val imageUrl: String? = null
)

@Serializable
data class QuizSeedDto(
    val id: String,
    @SerialName("category_id")
    val categoryId: String,
    val text: String,
    val options: List<String>,
    @SerialName("correct_answer_index")
    val correctAnswerIndex: Int,
    val explanation: String
)
