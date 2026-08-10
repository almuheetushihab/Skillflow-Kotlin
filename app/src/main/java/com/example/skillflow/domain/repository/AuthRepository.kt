package com.example.skillflow.domain.repository

import com.example.skillflow.domain.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Authentication operations.
 */
interface AuthRepository {
    fun login(email: String, password: String): Flow<Resource<Unit>>
    fun signUp(name: String, email: String, phone: String, password: String): Flow<Resource<Unit>>
    fun logout(): Flow<Resource<Unit>>
    fun deleteAccount(): Flow<Resource<Unit>>
    fun getCurrentUserEmail(): String?
    fun getCurrentUserName(): String?
}
