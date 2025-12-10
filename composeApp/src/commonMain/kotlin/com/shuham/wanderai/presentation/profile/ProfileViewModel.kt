package com.shuham.wanderai.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuham.wanderai.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        val user = authRepository.getCurrentUser()
        _state.update {
            it.copy(
                email = user?.email,
                displayName = user?.displayName
            )
        }
    }

    fun onAction(action: ProfileAction) {
        when (action) {
            ProfileAction.OnLogoutClicked -> {
                viewModelScope.launch {
                    authRepository.logout()
                    _state.update { it.copy(isLoggedOut = true) }
                }
            }
        }
    }
}
