package com.example.skillflow.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CareerPath(
    val id: String,
    val name: String,
    val description: String,
    val iconUrl: String
)
