package com.shuham.wanderai.presentation.home

import com.shuham.wanderai.data.CitySuggestion

// Enums for UI State
enum class TravelerType(val label: String) { SOLO("Solo"), COUPLE("Couple"), FRIENDS("Friends"), FAMILY("Family") }
enum class BudgetTier(val label: String) { BUDGET("Budget"), MID_RANGE("Mid-range"), LUXURY("Luxury") }
enum class DietOption(val label: String) { VEGETARIAN("Vegetarian"), VEGAN("Vegan"), GLUTEN_FREE("Gluten-Free"), HALAL("Halal") }
enum class Interest(val label: String) { ART("Art"), HISTORY("History"), NATURE("Nature"), NIGHTLIFE("Nightlife"), FOODIE("Foodie") }

data class HomeState(
    val destinations: List<String> = listOf(""),
    val citySuggestions: List<CitySuggestion> = emptyList(),
    val activeSuggestionField: Int? = null, // Index of the destination field being edited
    val selectedTravelerType: TravelerType = TravelerType.SOLO,
    val isFlexibleDate: Boolean = false,
    val tripDurationDays: Int = 3,
    val selectedBudget: BudgetTier = BudgetTier.MID_RANGE,
    val pace: Float = 0.5f, // 0.0 (relaxed) to 1.0 (fast-paced)
    val selectedInterests: List<Interest> = emptyList(),
    val selectedDiet: List<DietOption> = emptyList(),
    val userName: String = "Wanderer",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
