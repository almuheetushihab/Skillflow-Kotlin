package com.example.skillflow.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserNote(
    val id: String,
    val nuggetId: String,
    val noteContent: String,
    val timestamp: Long
)
