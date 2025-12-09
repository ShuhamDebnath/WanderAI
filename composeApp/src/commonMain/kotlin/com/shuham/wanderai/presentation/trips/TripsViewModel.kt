package com.shuham.wanderai.presentation.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuham.wanderai.domain.repository.TripRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TripsViewModel(
    private val repository: TripRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TripsState())
    val state: StateFlow<TripsState> = _state.asStateFlow()

    fun loadTrips() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val trips = repository.getAllTrips().map { (id, response) ->
                val firstCity = response.destinations.firstOrNull() ?: "travel"
                TripItem(
                    id = id,
                    tripData = response,
                    imageUrl = getPlaceholderImageFor(firstCity)
                )
            }
            _state.update { it.copy(trips = trips, isLoading = false) }
        }
    }

    fun onAction(action: TripsAction) {
        when (action) {
            is TripsAction.OnSearchQueryChanged -> {
                _state.update { it.copy(searchQuery = action.query) }
            }
            is TripsAction.OnDeleteTripClicked -> {
                _state.update { it.copy(tripToDelete = action.trip, showDeleteConfirmation = true) }
            }
            TripsAction.OnConfirmDelete -> {
                viewModelScope.launch {
                    _state.value.tripToDelete?.let {
                        repository.deleteTrip(it.id)
                        _state.update { state ->
                            state.copy(
                                tripToDelete = null, 
                                showDeleteConfirmation = false
                            )
                        }
                        // Refresh the list after deletion
                        loadTrips()
                    }
                }
            }
            TripsAction.OnDismissDeleteDialog -> {
                _state.update { it.copy(tripToDelete = null, showDeleteConfirmation = false) }
            }
            is TripsAction.OnTripClicked -> { /* Navigation is handled by UI */ }
        }
    }
    
    private fun getPlaceholderImageFor(destination: String): String {
        val sanitizedDestination = destination.replace(" ", ",")
        return "https://loremflickr.com/800/600/$sanitizedDestination,travel,landmark"
    }
}
