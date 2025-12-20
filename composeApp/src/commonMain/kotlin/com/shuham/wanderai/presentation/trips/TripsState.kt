package com.shuham.wanderai.presentation.trips

import com.shuham.wanderai.data.model.TripResponse

enum class SortBy(val displayName: String) {
    NEWEST_FIRST("Newest First"),
    OLDEST_FIRST("Oldest First"),
    NAME_A_Z("By Name (A-Z)")
}

data class TripsState(
    val trips: List<TripItem> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val sortBy: SortBy = SortBy.NEWEST_FIRST,
    val showSortMenu: Boolean = false,
    val tripToDelete: TripItem? = null,
    val showDeleteConfirmation: Boolean = false
)

data class TripItem(
    val id: String,
    val tripData: TripResponse,
    val imageUrl: String = "https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?q=80&w=1000&auto=format&fit=crop",
    val status: String = "Completed",
    val dateRange: String = "Jan 12 - Jan 15, 2024"
)

sealed interface TripsAction {
    data class OnSearchQueryChanged(val query: String) : TripsAction
    data class OnTripClicked(val tripId: String) : TripsAction
    
    // Sorting Actions
    object OnSortClicked : TripsAction
    object OnDismissSortMenu : TripsAction
    data class OnSortSelected(val sortBy: SortBy) : TripsAction

    // Deletion Actions
    data class OnDeleteTripClicked(val trip: TripItem) : TripsAction
    object OnConfirmDelete : TripsAction
    object OnDismissDeleteDialog : TripsAction
}
