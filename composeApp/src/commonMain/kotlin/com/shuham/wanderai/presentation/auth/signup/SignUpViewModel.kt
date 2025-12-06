package com.shuham.wanderai.presentation.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuham.wanderai.domain.repository.AuthRepository
import com.shuham.wanderai.util.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SignUpState())
    val state: StateFlow<SignUpState> = _state.asStateFlow()

    fun onAction(action: SignUpAction) {
        when (action) {
            is SignUpAction.OnUsernameChanged -> _state.update { it.copy(username = action.username) }
            is SignUpAction.OnEmailChanged -> _state.update { it.copy(email = action.email) }
            is SignUpAction.OnPasswordChanged -> _state.update { it.copy(password = action.password) }
            is SignUpAction.OnConfirmPasswordChanged -> _state.update { it.copy(confirmPassword = action.confirmPassword) }
            SignUpAction.OnTogglePasswordVisibility -> _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            SignUpAction.OnToggleConfirmPasswordVisibility -> _state.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
            SignUpAction.OnSignUpClicked -> signUpUser()
            SignUpAction.OnErrorDismissed -> _state.update { it.copy(errorMessage = null) }
            SignUpAction.OnGoogleSignInClicked, 
            SignUpAction.OnFacebookSignInClicked, 
            SignUpAction.OnAppleSignInClicked -> {
                _state.update { it.copy(showFeatureComingSoonDialog = true) }
            }
            SignUpAction.OnFeatureComingSoonDismissed -> {
                 _state.update { it.copy(showFeatureComingSoonDialog = false) }
            }
        }
    }

    private fun signUpUser() {
        val currentState = _state.value
        if (currentState.password != currentState.confirmPassword) {
            _state.update { it.copy(errorMessage = "Passwords do not match.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val result = authRepository.signUp(currentState.email, currentState.password)

            when (result) {
                is NetworkResult.Success -> {
                    // Optionally, update the user's profile with the username here
                    // result.data?.updateProfile { displayName = currentState.username }
                    _state.update { it.copy(isLoading = false, signUpSuccess = true) }
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                is NetworkResult.Loading -> { /* Already handled */ }
            }
        }
    }
}
