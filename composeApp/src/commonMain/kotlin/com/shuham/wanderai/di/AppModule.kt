package com.shuham.wanderai.di

import com.shuham.wanderai.data.OpenRouterService
import com.shuham.wanderai.data.repository.AuthRepositoryImpl
import com.shuham.wanderai.data.repository.TripRepositoryImpl
import com.shuham.wanderai.domain.repository.AuthRepository
import com.shuham.wanderai.domain.repository.TripRepository
import com.shuham.wanderai.presentation.auth.login.LoginViewModel
import com.shuham.wanderai.presentation.auth.signup.SignUpViewModel
import com.shuham.wanderai.presentation.home.HomeViewModel
import com.shuham.wanderai.presentation.splash.SplashViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    // Network Client
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            // --- ADD THIS TIMEOUT BLOCK ---
            install(HttpTimeout) {
                requestTimeoutMillis = 120_000 // 2 Minutes (AI needs time!)
                connectTimeoutMillis = 60_000
                socketTimeoutMillis = 120_000
            }
            // -----------------------------
        }
    }

    // Services
    singleOf(::OpenRouterService)

    // Repositories
    singleOf(::TripRepositoryImpl) bind TripRepository::class
    singleOf(::AuthRepositoryImpl) bind AuthRepository::class

    // ViewModels
    viewModelOf(::HomeViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::SignUpViewModel)
    viewModelOf(::SplashViewModel)
}
