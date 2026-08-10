package com.example.skillflow.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class QuizQuestion(
    val id: String,
    val nuggetId: String, // Linked to a specific nugget
    val text: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String
)
