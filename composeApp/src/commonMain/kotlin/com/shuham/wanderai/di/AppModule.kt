package com.shuham.wanderai.di

import com.shuham.wanderai.data.OpenRouterService
import com.shuham.wanderai.data.PlacesService
import com.shuham.wanderai.data.local.AppDatabase
import com.shuham.wanderai.data.repository.AuthRepositoryImpl
import com.shuham.wanderai.data.repository.TripRepositoryImpl
import com.shuham.wanderai.domain.repository.AuthRepository
import com.shuham.wanderai.domain.repository.TripRepository
import com.shuham.wanderai.presentation.auth.login.LoginViewModel
import com.shuham.wanderai.presentation.auth.signup.SignUpViewModel
import com.shuham.wanderai.presentation.home.HomeViewModel
import com.shuham.wanderai.presentation.map.MapViewModel
import com.shuham.wanderai.presentation.profile.ProfileViewModel
import com.shuham.wanderai.presentation.splash.SplashViewModel
import com.shuham.wanderai.presentation.trip_details.TripDetailsViewModel
import com.shuham.wanderai.presentation.trips.TripsViewModel
import com.shuham.wanderai.util.LocationService
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.scope.Scope
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    // Network
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 120_000
                connectTimeoutMillis = 60_000
                socketTimeoutMillis = 120_000
            }
        }
    }
    singleOf(::OpenRouterService)
    singleOf(::PlacesService)

    // Database
    single { getDatabase(this) } 
    single { get<AppDatabase>().tripDao() }

    // Platform Services
    single { getLocationService(this) } bind LocationService::class

    // Repositories
    singleOf(::TripRepositoryImpl) bind TripRepository::class
    singleOf(::AuthRepositoryImpl) bind AuthRepository::class

    // ViewModels
    viewModelOf(::HomeViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::SignUpViewModel)
    viewModelOf(::SplashViewModel)
    viewModelOf(::TripDetailsViewModel)
    viewModelOf(::TripsViewModel)
    viewModelOf(::MapViewModel)
    viewModelOf(::ProfileViewModel)
}

expect fun getDatabase(scope: Scope): AppDatabase
expect fun getLocationService(scope: Scope): LocationService
