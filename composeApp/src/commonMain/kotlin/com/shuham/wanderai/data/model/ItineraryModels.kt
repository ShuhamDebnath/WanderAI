package com.shuham.wanderai.data.model

import kotlinx.serialization.Serializable

// --- App Data Models (To use in your UI) ---
@Serializable
data class TripItinerary(
    val tripName: String,
    val dailyPlan: List<ItineraryDayPlan>
)

@Serializable
data class ItineraryDayPlan(
    val day: Int,                 // Matches JSON "day"
    val activities: List<Activity> // Matches JSON "activities" directly
)

@Serializable
data class Activity(
    val time: String,
    val title: String,
    val description: String,
    val type: String
)