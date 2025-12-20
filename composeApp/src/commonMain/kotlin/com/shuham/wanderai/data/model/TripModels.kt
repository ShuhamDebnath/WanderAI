package com.shuham.wanderai.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TripRequest(
    val destination: String,
    val travelers: String,
    val duration: Int,
    val budget: String,
    val pace : Float,
    val diet: List<String>,
    val interests: List<String>
)


@Serializable
data class TripResponse(
    var id: String = "",
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
    val type: String,
    val time: String? = null,
    val placeName: String? = null,
    val coordinates: Coordinates? = null,
    val description: String? = null,
    val estimatedDuration: String? = null,
    val priceLevel: String? = null,
    val insiderTip: String? = null,
    val title: String? = null,
    val options: List<ActivityOption>? = null,
    var imageUrl: String? = null // REMOVED @Transient
)

@Serializable
data class Coordinates(
    val lat: Double,
    val lng: Double
)

@Serializable
data class ActivityOption(
    val name: String,
    val tag: String,
    val priceLevel: String,
    val description: String,
    val isRecommended: Boolean,
    var imageUrl: String? = null // REMOVED @Transient
)
