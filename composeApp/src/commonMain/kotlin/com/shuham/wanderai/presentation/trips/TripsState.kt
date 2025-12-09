package com.shuham.wanderai.presentation.trips

import com.shuham.wanderai.data.model.TripResponse

data class TripsState(
    val trips: List<TripItem> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val tripToDelete: TripItem? = null,
    val showDeleteConfirmation: Boolean = false
)

data class TripItem(
    val id: String,
    val tripData: TripResponse,
    val imageUrl: String = "https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?q=80&w=1000&auto=format&fit=crop", // Placeholder
    val status: String = "Completed", // Mock status
    val dateRange: String = "Jan 12 - Jan 15, 2024" // Mock date
)

sealed interface TripsAction {
    data class OnSearchQueryChanged(val query: String) : TripsAction
    data class OnTripClicked(val tripId: String) : TripsAction
    data class OnDeleteTripClicked(val trip: TripItem) : TripsAction
    object OnConfirmDelete : TripsAction
    object OnDismissDeleteDialog : TripsAction
}
