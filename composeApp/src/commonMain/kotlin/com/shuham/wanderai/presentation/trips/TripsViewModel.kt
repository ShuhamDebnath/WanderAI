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

    init {
        loadTrips()
    }

    private fun loadTrips() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val trips = repository.getAllTrips().map { (id, response) ->
                // Extract the first city name for the image search
                val firstCity = response.destinations.firstOrNull() ?: "travel"
                
                TripItem(
                    id = id,
                    tripData = response,
                    // Dynamic image based on destination name
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
            is TripsAction.OnTripClicked -> {
                // Navigation is handled by the UI observing this or a one-time event if needed
                // But here the UI handles the click directly via callback
            }
        }
    }
    
    private fun getPlaceholderImageFor(destination: String): String {
        // Using LoremFlickr for dynamic images based on keywords
        // We append a timestamp to the URL to avoid caching the same image for different trips if the destination is the same
        // (Though for the same trip, we want it stable, so we could hash the ID if we had it here, but destination is fine)
        val sanitizedDestination = destination.replace(" ", ",")
        return "https://loremflickr.com/800/600/$sanitizedDestination,travel,landmark"
    }
}
