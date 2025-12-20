package com.shuham.wanderai.presentation.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuham.wanderai.data.model.TripResponse
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
            
            // Fetch from repository
            val tripsFromRepo = repository.getAllTrips()
            
            // Apply sorting logic
            val sortedTrips = when (_state.value.sortBy) {
                SortBy.NEWEST_FIRST -> tripsFromRepo.sortedByDescending { it.second.id } // Assuming ID contains timestamp
                SortBy.OLDEST_FIRST -> tripsFromRepo.sortedBy { it.second.id }
                SortBy.NAME_A_Z -> tripsFromRepo.sortedBy { it.second.tripName }
            }

            // Map to UI model
            val tripItems = sortedTrips.map { (id, response) ->
                val firstCity = response.destinations.firstOrNull() ?: "travel"
                TripItem(
                    id = id,
                    tripData = response,
                    imageUrl = getPlaceholderImageFor(firstCity)
                )
            }
            
            _state.update { it.copy(trips = tripItems, isLoading = false) }
        }
    }

    fun onAction(action: TripsAction) {
        when (action) {
            is TripsAction.OnSearchQueryChanged -> {
                _state.update { it.copy(searchQuery = action.query) }
            }
            is TripsAction.OnSortClicked -> {
                _state.update { it.copy(showSortMenu = true) }
            }
            is TripsAction.OnDismissSortMenu -> {
                _state.update { it.copy(showSortMenu = false) }
            }
            is TripsAction.OnSortSelected -> {
                _state.update { it.copy(sortBy = action.sortBy, showSortMenu = false) }
                loadTrips() // Re-load and sort the trips
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
                        loadTrips() // Refresh list after deletion
                    }
                }
            }
            TripsAction.OnDismissDeleteDialog -> {
                _state.update { it.copy(tripToDelete = null, showDeleteConfirmation = false) }
            }
            is TripsAction.OnTripClicked -> { /* Navigation handled by UI */ }
        }
    }
    
    private fun getPlaceholderImageFor(destination: String): String {
        val sanitizedDestination = destination.replace(" ", ",")
        return "https://loremflickr.com/800/600/$sanitizedDestination,travel,landmark"
    }
}
