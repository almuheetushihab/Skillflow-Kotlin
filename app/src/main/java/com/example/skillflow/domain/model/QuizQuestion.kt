package com.example.skillflow.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class QuizQuestion(
    val id: String,
    val careerPathId: String,
    val text: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String
)
