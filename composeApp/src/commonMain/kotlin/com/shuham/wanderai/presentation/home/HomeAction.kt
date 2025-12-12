package com.shuham.wanderai.presentation.home

import com.shuham.wanderai.data.CitySuggestion

sealed interface HomeAction {
    // Destination input
    data class OnDestinationChanged(val index: Int, val newValue: String) : HomeAction
    data class OnDestinationFieldFocused(val index: Int) : HomeAction
    data class OnCitySelected(val index: Int, val city: CitySuggestion) : HomeAction
    object OnDismissSuggestions : HomeAction
    object OnAddDestination : HomeAction
    data class OnRemoveDestination(val index: Int) : HomeAction

    // Other form actions
    data class OnTravelerTypeSelected(val type: TravelerType) : HomeAction
    data class OnDateTypeToggle(val isFlexible: Boolean) : HomeAction
    data class OnDurationChanged(val newDuration: Int) : HomeAction
    data class OnBudgetSelected(val budget: BudgetTier) : HomeAction
    data class OnPaceChanged(val newPace: Float) : HomeAction
    data class OnInterestSelected(val interest: Interest) : HomeAction
    data class OnDietSelected(val diet: DietOption) : HomeAction

    // Final action
    object OnPlanTripClicked : HomeAction
    object OnErrorDismissed : HomeAction
}
