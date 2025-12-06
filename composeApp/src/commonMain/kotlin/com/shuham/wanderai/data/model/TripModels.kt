package com.shuham.wanderai.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TripRequest(
    val destination: String,
    val budget: String,
    val travelers: String,
    val duration: Int,
    val interests: List<String>,
    val diet: List<String>
)

@Serializable
data class TripResponse(
    val tripName: String,
    val destinations: List<String>,
    val days: List<DayPlan>
)

@Serializable
data class DayPlan(
    val dayNumber: Int,
    val city: String,
    val narrative: String,
    val sections: List<DaySection>
)

@Serializable
data class DaySection(
    val timeOfDay: String, // Morning, Afternoon, Evening
    val activities: List<ActivityItem>
)

@Serializable
data class ActivityItem(
    val type: String, // SIGHTSEEING, FOOD_OPTION, etc.
    val title: String? = null, // Only for options
    val placeName: String? = null, // For single items
    val description: String,
    val estimatedDuration: String? = null,
    val coordinates: GeoPoint? = null,
    val options: List<PlaceOption>? = null // For Food/Hotel choices
)

@Serializable
data class PlaceOption(
    val name: String,
    val tag: String, // Best Match, Cheapest, etc.
    val priceLevel: String,
    val description: String,
    val isRecommended: Boolean
)

@Serializable
data class GeoPoint(
    val lat: Double,
    val lng: Double
)
