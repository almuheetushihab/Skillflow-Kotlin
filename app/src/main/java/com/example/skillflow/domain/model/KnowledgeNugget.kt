package com.example.skillflow.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class KnowledgeNugget(
    val id: String,
    val title: String,
    val content: String,
    val imageUrl: String? = null,
    val careerPathId: String,
    val isDone: Boolean = false,
    val isSaved: Boolean = false,
    val date: String
)
