package com.shuham.wanderai.presentation.trip_details

import com.shuham.wanderai.data.model.Activity
import com.shuham.wanderai.data.model.ActivityOption
import com.shuham.wanderai.data.model.TripResponse

data class TripDetailsState(
    val trip: TripResponse? = null,
    val selectedDay: Int = 0,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val selectedActivity: Activity? = null,
    val selectedOption: ActivityOption? = null,
    val selectedImage: String? = null // To hold the image URL for the bottom sheet
)

sealed interface TripDetailsAction {
    data class OnDaySelected(val index: Int) : TripDetailsAction
    object OnAddActivityClicked : TripDetailsAction
    data class OnActivityClicked(val activity: Activity) : TripDetailsAction
    data class OnOptionClicked(val option: ActivityOption) : TripDetailsAction
    object OnDismissBottomSheet : TripDetailsAction
    object OnNavigateToMap : TripDetailsAction
}

sealed interface TripDetailsEvent {
    data class NavigateToMap(val tripId: String, val dayNumber: Int) : TripDetailsEvent
}
