package com.shuham.wanderai.presentation.map

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.shuham.wanderai.domain.repository.TripRepository
import com.shuham.wanderai.navigation.Map
import com.shuham.wanderai.util.toMapMarkers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MapViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: TripRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MapState())
    val state: StateFlow<MapState> = _state.asStateFlow()

    init {
        // 1. Get arguments from the navigation route
        val mapArgs: Map = savedStateHandle.toRoute()
        println("mapArgs $mapArgs  id ${mapArgs.tripId} day ${mapArgs.dayNumber}")
        loadMarkers(mapArgs.tripId, mapArgs.dayNumber)
    }

    private fun loadMarkers(tripId: String, dayNumber: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            // 2. Fetch the full trip data from the repository
            val trip = repository.getTrip(tripId)
            
            // 3. Use the extension function to convert trip data into map markers
            val markers = trip?.toMapMarkers(selectedDay = dayNumber) ?: emptyList()
            println("markers $markers")
            
            _state.update { it.copy(markers = markers, isLoading = false) }
        }
    }
}
