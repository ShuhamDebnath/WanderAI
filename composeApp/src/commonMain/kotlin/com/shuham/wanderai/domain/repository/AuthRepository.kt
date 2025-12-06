package com.shuham.wanderai.domain.repository

import com.shuham.wanderai.util.NetworkResult
import dev.gitlive.firebase.auth.FirebaseUser

interface AuthRepository {
    suspend fun login(email: String, password: String): NetworkResult<FirebaseUser>
    suspend fun signUp(email: String, password: String): NetworkResult<FirebaseUser>
    suspend fun logout()
    fun getCurrentUser(): FirebaseUser?
}
