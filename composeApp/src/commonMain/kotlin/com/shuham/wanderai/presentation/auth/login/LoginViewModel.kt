package com.shuham.wanderai.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuham.wanderai.domain.repository.AuthRepository
import com.shuham.wanderai.util.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.OnEmailChanged -> _state.update { it.copy(email = action.email) }
            is LoginAction.OnPasswordChanged -> _state.update { it.copy(password = action.password) }
            LoginAction.OnTogglePasswordVisibility -> _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            LoginAction.OnLoginClicked -> loginUser()
            LoginAction.OnErrorDismissed -> _state.update { it.copy(errorMessage = null) }
            LoginAction.OnGoogleSignInClicked, 
            LoginAction.OnFacebookSignInClicked, 
            LoginAction.OnAppleSignInClicked -> {
                _state.update { it.copy(showFeatureComingSoonDialog = true) }
            }
            LoginAction.OnFeatureComingSoonDismissed -> {
                 _state.update { it.copy(showFeatureComingSoonDialog = false) }
            }
        }
    }

    private fun loginUser() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            
            val result = authRepository.login(_state.value.email, _state.value.password)
            
            when (result) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(isLoading = false, loginSuccess = true) }
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                is NetworkResult.Loading -> {
                    // Already handled
                }
            }
        }
    }
}
