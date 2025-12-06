package com.shuham.wanderai.presentation.splash

enum class AuthStatus {
    UNKNOWN, // Initial state
    LOGGED_IN,
    LOGGED_OUT
}

data class SplashState(
    val authStatus: AuthStatus = AuthStatus.UNKNOWN
)

sealed interface SplashAction {
    object CheckAuthStatus : SplashAction
}
