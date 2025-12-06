package com.shuham.wanderai.presentation.auth.login

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false, // New flag
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loginSuccess: Boolean = false,
    val showFeatureComingSoonDialog: Boolean = false
)

sealed interface LoginAction {
    data class OnEmailChanged(val email: String) : LoginAction
    data class OnPasswordChanged(val password: String) : LoginAction
    object OnTogglePasswordVisibility : LoginAction // New action
    object OnLoginClicked : LoginAction
    object OnGoogleSignInClicked : LoginAction
    object OnFacebookSignInClicked : LoginAction
    object OnAppleSignInClicked : LoginAction
    object OnErrorDismissed : LoginAction
    object OnFeatureComingSoonDismissed: LoginAction
}
