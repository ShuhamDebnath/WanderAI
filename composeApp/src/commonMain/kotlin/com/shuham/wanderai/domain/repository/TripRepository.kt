package com.shuham.wanderai.domain.repository

import com.shuham.wanderai.data.TripItinerary
import com.shuham.wanderai.data.model.TripRequest
import com.shuham.wanderai.util.NetworkResult

interface TripRepository {
    suspend fun generateTrip(request: TripRequest): NetworkResult<TripItinerary>
    suspend fun getTrip(tripId: String): TripItinerary?
    suspend fun saveTrip(trip: TripItinerary): String // Returns the ID
}
