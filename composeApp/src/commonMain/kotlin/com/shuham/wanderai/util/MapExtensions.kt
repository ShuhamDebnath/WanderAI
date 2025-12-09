package com.shuham.wanderai.util

import com.shuham.wanderai.data.model.Activity
import com.shuham.wanderai.data.model.TripResponse
import com.shuham.wanderai.presentation.map.MapMarker
import com.shuham.wanderai.presentation.map.MarkerIcon

/**
 * Converts a TripResponse into a list of MapMarkers.
 * @param selectedDay If a specific day is provided (not -1), only markers from that day are returned.
 * Otherwise, all markers for the entire trip are returned.
 */
fun TripResponse.toMapMarkers(selectedDay: Int = -1): List<MapMarker> {
    val daysToProcess = if (selectedDay != -1) {
        this.days.filter { it.dayNumber == selectedDay }
    } else {
        this.days
    }

    return daysToProcess.flatMap { day ->
        day.sections.flatMap { section ->
            section.activities.mapNotNull { activity ->
                activity.coordinates?.let {
                    MapMarker(
                        id = activity.placeName ?: activity.title ?: "",
                        position = it,
                        title = activity.placeName ?: activity.title ?: "Unknown Place",
                        snippet = activity.description ?: "",
                        icon = activity.getMarkerIcon()
                    )
                }
            }
        }
    }
}

private fun Activity.getMarkerIcon(): MarkerIcon {
    return when (this.type) {
        "FOOD_OPTION" -> MarkerIcon.FOOD
        "HOTEL_OPTION" -> MarkerIcon.HOTEL
        else -> MarkerIcon.SIGHTSEEING
    }
}
