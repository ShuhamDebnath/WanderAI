package com.shuham.wanderai.presentation.trip_details

import com.shuham.wanderai.data.model.Activity
import com.shuham.wanderai.data.model.ActivityOption
import com.shuham.wanderai.data.model.TripResponse

data class TripDetailsState(
    val trip: TripResponse? = null,
    val selectedDay: Int = 1,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    // For Bottom Sheet Details
    val selectedActivity: Activity? = null,
    val selectedOption: ActivityOption? = null
)

sealed interface TripDetailsAction {
    data class OnDaySelected(val day: Int) : TripDetailsAction
    object OnAddActivityClicked : TripDetailsAction
    // New Actions for Enhanced UI
    data class OnActivityClicked(val activity: Activity) : TripDetailsAction
    data class OnOptionClicked(val option: ActivityOption) : TripDetailsAction
    object OnDismissBottomSheet : TripDetailsAction
    object OnNavigateToMap : TripDetailsAction
}
