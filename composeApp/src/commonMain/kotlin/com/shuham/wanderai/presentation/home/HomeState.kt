package com.shuham.wanderai.presentation.home

import com.shuham.wanderai.data.model.TripRequest

data class HomeState(
    val userName: String = "Shuham",
    val destinations: List<String> = listOf("New York", "Los Angeles"),
    
    val selectedTravelerType: TravelerType = TravelerType.Solo,
    
    val isFlexibleDate: Boolean = true, // Toggle state
    val tripDurationDays: Int = 2,
    
    val selectedBudget: BudgetTier = BudgetTier.Standard,
    
    val selectedDiet: List<DietOption> = listOf(DietOption.NonVeg),
    val pace: Float = 0.5f, // 0.0 (Relaxed) to 1.0 (Packed)
    val selectedInterests: List<Interest> = listOf(Interest.History, Interest.Food, Interest.Nature),
    
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

// --- Actions (Unidirectional Data Flow) ---
sealed interface HomeAction {
    data class OnDestinationChanged(val index: Int, val newValue: String) : HomeAction
    object OnAddDestination : HomeAction
    data class OnRemoveDestination(val index: Int) : HomeAction
    data class OnTravelerTypeSelected(val type: TravelerType) : HomeAction
    data class OnDateTypeToggle(val isFlexible: Boolean) : HomeAction
    data class OnDurationChanged(val newDuration: Int) : HomeAction
    data class OnBudgetSelected(val budget: BudgetTier) : HomeAction
    data class OnDietSelected(val diet: DietOption) : HomeAction
    data class OnPaceChanged(val newPace: Float) : HomeAction
    data class OnInterestSelected(val interest: Interest) : HomeAction
    object OnPlanTripClicked : HomeAction
    object OnErrorDismissed : HomeAction
}

enum class TravelerType(val label: String) {
    Solo("Solo"), Couple("Couple"), Family("Family"), Friends("Friends")
}

enum class BudgetTier(val label: String, val description: String, val priceLevel: String) {
    Low("Low", "Backpacker, Hostels, Public Transport", "$"),
    Standard("Standard", "3-Star Hotels, Taxis", "$$"),
    High("High", "Luxury, Resorts, Private Chauffeur", "$$$")
}

enum class DietOption(val label: String) {
    Vegetarian("Vegetarian"), NonVeg("Non-Veg"), Vegan("Vegan"), Halal("Halal")
}

enum class Interest(val label: String) {
    History("History"), Food("Food"), Nature("Nature"), Anime("Anime"), Shopping("Shopping")
}
