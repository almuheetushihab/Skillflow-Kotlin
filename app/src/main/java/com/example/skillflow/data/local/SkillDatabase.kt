package com.example.skillflow.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.skillflow.data.local.dao.SkillDao
import com.example.skillflow.data.local.entity.CareerPathEntity
import com.example.skillflow.data.local.entity.NuggetEntity

@Database(entities = [NuggetEntity::class, CareerPathEntity::class], version = 1, exportSchema = false)
abstract class SkillDatabase : RoomDatabase() {
    abstract val dao: SkillDao
}
