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
    val activities: List<Activity>
)

@Serializable
data class Activity(
    val type: String, // SIGHTSEEING, FOOD_OPTION, etc.
    // Fields for Sightseeing/Transport
    val placeName: String? = null,
    val coordinates: Coordinates? = null,
    val description: String? = null,
    val estimatedDuration: String? = null,
    // Fields for Options (Food/Hotel)
    val title: String? = null,
    val options: List<ActivityOption>? = null
)




@Serializable
data class Coordinates(
    val lat: Double,
    val lng: Double
)

@Serializable
data class ActivityOption(
    val name: String,
    val tag: String, // Best Match, Cheapest, etc.
    val priceLevel: String, // $, $$, $$$
    val description: String,
    val isRecommended: Boolean
)
