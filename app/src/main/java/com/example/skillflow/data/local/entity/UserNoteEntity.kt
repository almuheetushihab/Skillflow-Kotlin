package com.example.skillflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.skillflow.domain.model.UserNote

@Entity(tableName = "user_notes")
data class UserNoteEntity(
    @PrimaryKey val id: String,
    val nuggetId: String,
    val title: String,
    val noteContent: String,
    val timestamp: Long
)

fun UserNoteEntity.toDomain(): UserNote {
    return UserNote(
        id = id,
        nuggetId = nuggetId,
        title = title,
        noteContent = noteContent,
        timestamp = timestamp
    )
}

fun UserNote.toEntity(): UserNoteEntity {
    return UserNoteEntity(
        id = id,
        nuggetId = nuggetId,
        title = title,
        noteContent = noteContent,
        timestamp = timestamp
    )
}
