package com.shuham.wanderai.presentation.loading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuham.wanderai.data.model.TripRequest
import com.shuham.wanderai.data.model.TripResponse
import com.shuham.wanderai.domain.repository.TripRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface LoadingUiState {
    object Idle : LoadingUiState
    object Loading : LoadingUiState
    data class Success(val response: TripResponse) : LoadingUiState
    data class Error(val message: String) : LoadingUiState
}

class LoadingViewModel(
    private val repository: TripRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoadingUiState>(LoadingUiState.Idle)
    val uiState: StateFlow<LoadingUiState> = _uiState.asStateFlow()

    fun generateTrip(request: TripRequest) {
        if (_uiState.value is LoadingUiState.Loading) return

        _uiState.update { LoadingUiState.Loading }

        viewModelScope.launch {
            val result = repository.generateTrip(request)
            
//            result.fold(
//                onSuccess = { response ->
//                    _uiState.update { LoadingUiState.Success(response) }
//                },
//                onFailure = { error ->
//                    _uiState.update { LoadingUiState.Error(error.message ?: "Unknown error occurred") }
//                }
//            )
        }
    }
}
