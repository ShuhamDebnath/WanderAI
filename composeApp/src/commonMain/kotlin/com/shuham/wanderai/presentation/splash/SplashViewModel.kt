package com.shuham.wanderai.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuham.wanderai.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SplashViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SplashState())
    val state: StateFlow<SplashState> = _state.asStateFlow()

    init {
        // Check auth status as soon as the ViewModel is created
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            // Simulate a minimum splash screen time
            delay(1500) 
            
            val user = authRepository.getCurrentUser()
            if (user != null) {
                _state.update { it.copy(authStatus = AuthStatus.LOGGED_IN) }
            } else {
                _state.update { it.copy(authStatus = AuthStatus.LOGGED_OUT) }
            }
        }
    }
}
