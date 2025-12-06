package com.shuham.wanderai.data.repository

import com.shuham.wanderai.domain.repository.AuthRepository
import com.shuham.wanderai.util.NetworkResult
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth

class AuthRepositoryImpl : AuthRepository {

    private val auth: FirebaseAuth = Firebase.auth

    override suspend fun login(email: String, password: String): NetworkResult<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password)
            val user = result.user
            if (user != null) {
                NetworkResult.Success(user)
            } else {
                NetworkResult.Error("Authentication succeeded but user is null")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkResult.Error(e.message ?: "Login failed")
        }
    }

    override suspend fun signUp(email: String, password: String): NetworkResult<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password)
            val user = result.user
            if (user != null) {
                NetworkResult.Success(user)
            } else {
                NetworkResult.Error("Sign up succeeded but user is null")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkResult.Error(e.message ?: "Sign up failed")
        }
    }

    override suspend fun logout() {
        auth.signOut()
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}
