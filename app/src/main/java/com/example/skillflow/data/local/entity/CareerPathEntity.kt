package com.example.skillflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.skillflow.domain.model.CareerPath

@Entity(tableName = "career_paths")
data class CareerPathEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val iconUrl: String
)

fun CareerPathEntity.toDomain(): CareerPath {
    return CareerPath(
        id = id,
        name = name,
        description = description,
        iconUrl = iconUrl
    )
}

fun CareerPath.toEntity(): CareerPathEntity {
    return CareerPathEntity(
        id = id,
        name = name,
        description = description,
        iconUrl = iconUrl
    )
}
