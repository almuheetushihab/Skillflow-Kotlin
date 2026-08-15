package com.example.skillflow.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.skillflow.data.local.dao.SkillDao
import com.example.skillflow.data.local.entity.CareerPathEntity
import com.example.skillflow.data.local.entity.NuggetEntity
import com.example.skillflow.data.local.entity.UserNoteEntity

@Database(
    entities = [NuggetEntity::class, CareerPathEntity::class, UserNoteEntity::class],
    version = 3, // Bumped to 3 for UserNote title field addition
    exportSchema = false
)
abstract class SkillDatabase : RoomDatabase() {
    abstract val dao: SkillDao
}
