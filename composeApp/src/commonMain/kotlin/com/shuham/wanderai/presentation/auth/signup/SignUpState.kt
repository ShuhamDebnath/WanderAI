package com.shuham.wanderai.presentation.auth.signup

data class SignUpState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false, // New flag
    val isConfirmPasswordVisible: Boolean = false, // New flag
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val signUpSuccess: Boolean = false,
    val showFeatureComingSoonDialog: Boolean = false
)

sealed interface SignUpAction {
    data class OnUsernameChanged(val username: String) : SignUpAction
    data class OnEmailChanged(val email: String) : SignUpAction
    data class OnPasswordChanged(val password: String) : SignUpAction
    data class OnConfirmPasswordChanged(val confirmPassword: String) : SignUpAction
    object OnTogglePasswordVisibility : SignUpAction // New action
    object OnToggleConfirmPasswordVisibility : SignUpAction // New action
    object OnSignUpClicked : SignUpAction
    object OnErrorDismissed : SignUpAction
    object OnGoogleSignInClicked : SignUpAction 
    object OnFacebookSignInClicked : SignUpAction
    object OnAppleSignInClicked : SignUpAction
    object OnFeatureComingSoonDismissed: SignUpAction
}
