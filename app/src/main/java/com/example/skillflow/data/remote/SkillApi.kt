package com.example.skillflow.data.remote

import com.example.skillflow.domain.model.CareerPath
import com.example.skillflow.domain.model.KnowledgeNugget
import retrofit2.http.GET
import retrofit2.http.Query

interface SkillApi {
    @GET("nuggets/daily")
    suspend fun getDailyNuggets(
        @Query("careerPathId") careerPathId: String,
        @Query("date") date: String
    ): List<KnowledgeNugget>

    @GET("career-paths")
    suspend fun getCareerPaths(): List<CareerPath>
}
