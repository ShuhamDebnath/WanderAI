package com.shuham.wanderai.presentation.trip_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.shuham.wanderai.domain.repository.TripRepository
import com.shuham.wanderai.navigation.TripDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TripDetailsViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val repository: TripRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TripDetailsState())
    val state: StateFlow<TripDetailsState> = _state.asStateFlow()

    init {
        loadTrip()
    }

    private fun loadTrip() {
        // Retrieve tripId from navigation arguments using Type-Safe Navigation
        val tripDetailsArgs = savedStateHandle.toRoute<TripDetails>()
        val tripId = tripDetailsArgs.tripId

        viewModelScope.launch {
            val trip = repository.getTrip(tripId)
            if (trip != null) {
                _state.update { it.copy(trip = trip, isLoading = false) }
            } else {
                _state.update { it.copy(isLoading = false, errorMessage = "Trip not found") }
            }
        }
    }

    fun onAction(action: TripDetailsAction) {
        when (action) {
            is TripDetailsAction.OnDaySelected -> {
                _state.update { it.copy(selectedDay = action.day) }
            }
            TripDetailsAction.OnAddActivityClicked -> {
                // Handle add activity logic
            }

            else -> {}
        }
    }
}
