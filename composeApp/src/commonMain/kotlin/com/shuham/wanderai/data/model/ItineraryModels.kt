package com.shuham.wanderai.data.model

import kotlinx.serialization.Serializable

// --- App Data Models (To use in your UI) ---
@Serializable
data class TripItinerary(
    val tripName: String,
    val dailyPlan: List<DayPlan>
)

@Serializable
data class DayPlan(
    val day: Int,
    val activities: List<Activity>
)

@Serializable
data class Activity(
    val time: String,
    val title: String,
    val description: String,
    val type: String // e.g., "food", "sightseeing"
)
