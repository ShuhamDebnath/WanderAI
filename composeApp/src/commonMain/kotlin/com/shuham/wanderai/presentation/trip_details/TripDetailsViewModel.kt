package com.shuham.wanderai.presentation.trip_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.shuham.wanderai.domain.repository.TripRepository
import com.shuham.wanderai.navigation.TripDetails
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TripDetailsViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val repository: TripRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TripDetailsState())
    val state: StateFlow<TripDetailsState> = _state.asStateFlow()

    private val _events = Channel<TripDetailsEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadTrip()
    }

    private fun loadTrip() {
        val tripDetailsArgs = savedStateHandle.toRoute<TripDetails>()
        val tripId = tripDetailsArgs.tripId

        println(" loadTrip() -> tripId $tripId")

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val trip = repository.getTrip(tripId)
            if (trip != null) {
                _state.update { it.copy(trip = trip, isLoading = false) }
            } else {
                _state.update { it.copy(isLoading = false, errorMessage = "Trip not found in local database.") }
            }
        }
    }

    fun onAction(action: TripDetailsAction) {
        when (action) {
            is TripDetailsAction.OnDaySelected -> {
                _state.update { it.copy(selectedDay = action.index) }
            }
            is TripDetailsAction.OnActivityClicked -> {
                _state.update { it.copy(selectedActivity = action.activity, selectedOption = null) }
            }
            is TripDetailsAction.OnOptionClicked -> {
                _state.update { it.copy(selectedOption = action.option, selectedActivity = null) }
            }
            TripDetailsAction.OnDismissBottomSheet -> {
                _state.update { it.copy(selectedActivity = null, selectedOption = null) }
            }
            TripDetailsAction.OnNavigateToMap -> {
                viewModelScope.launch {
                    _state.value.trip?.let {
                        println("TripDetailsAction.OnNavigateToMap -> tripId ${it.id}")
                        val dayNumber = it.days[_state.value.selectedDay].dayNumber
                        _events.send(TripDetailsEvent.NavigateToMap(it.id, dayNumber))
                    }
                }
            }
            TripDetailsAction.OnAddActivityClicked -> {
                // Placeholder
            }
        }
    }
}
