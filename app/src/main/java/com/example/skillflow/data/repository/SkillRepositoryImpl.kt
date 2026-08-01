package com.example.skillflow.data.repository

import com.example.skillflow.data.local.dao.SkillDao
import com.example.skillflow.data.local.entity.toDomain
import com.example.skillflow.data.local.entity.toEntity
import com.example.skillflow.data.remote.SkillApi
import com.example.skillflow.domain.model.CareerPath
import com.example.skillflow.domain.model.KnowledgeNugget
import com.example.skillflow.domain.repository.SkillRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class SkillRepositoryImpl @Inject constructor(
    private val api: SkillApi,
    private val dao: SkillDao
) : SkillRepository {

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun getDailyNuggets(careerPathId: String): Flow<List<KnowledgeNugget>> = flow {
        val today = dateFormatter.format(Date())
        // First, emit local data
        val localNuggets = dao.getDailyNuggets(careerPathId, today).first()
        emit(localNuggets.map { it.toDomain() })

        // Then, try to fetch from remote
        try {
            val remoteNuggets = api.getDailyNuggets(careerPathId, today)
            dao.insertNuggets(remoteNuggets.map { it.toEntity() })
            // Re-emit from local after update
            val updatedLocal = dao.getDailyNuggets(careerPathId, today).first()
            emit(updatedLocal.map { it.toDomain() })
        } catch (e: Exception) {
            // Handle error (e.g., logging)
        }
    }

    override fun getSavedNuggets(): Flow<List<KnowledgeNugget>> {
        return dao.getSavedNuggets().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun toggleSaveNugget(nuggetId: String) {
        dao.toggleSaveNugget(nuggetId)
    }

    override suspend fun markNuggetAsDone(nuggetId: String) {
        dao.markNuggetAsDone(nuggetId)
    }

    override fun getCareerPaths(): Flow<List<CareerPath>> = flow {
        // Emit local
        val localPaths = dao.getCareerPaths().first()
        emit(localPaths.map { it.toDomain() })

        // Fetch remote
        try {
            val remotePaths = api.getCareerPaths()
            dao.insertCareerPaths(remotePaths.map { it.toEntity() })
            val updatedLocal = dao.getCareerPaths().first()
            emit(updatedLocal.map { it.toDomain() })
        } catch (e: Exception) {
            // Error
        }
    }
}
