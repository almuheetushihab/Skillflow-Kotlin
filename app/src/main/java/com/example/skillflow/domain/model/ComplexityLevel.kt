package com.example.skillflow.domain.model

/**
 * Enum representing the difficulty/complexity level of a Knowledge Nugget.
 */
enum class ComplexityLevel(val levelName: String) {
    BEGINNER("Beginner"),
    INTERMEDIATE("Intermediate"),
    ADVANCED("Advanced");

    companion object {
        fun fromString(value: String?): ComplexityLevel {
            return entries.firstOrNull { 
                it.levelName.equals(value, ignoreCase = true) || it.name.equals(value, ignoreCase = true)
            } ?: BEGINNER
        }
    }
}
