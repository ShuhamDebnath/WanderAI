package com.shuham.wanderai.domain.repository

import com.shuham.wanderai.data.model.TripRequest
import com.shuham.wanderai.data.model.TripResponse
import com.shuham.wanderai.util.NetworkResult

interface TripRepository {
    suspend fun generateTrip(request: TripRequest): NetworkResult<TripResponse>
    suspend fun getTrip(tripId: String): TripResponse?
    suspend fun getAllTrips(): List<Pair<String, TripResponse>> // Returns List of (ID, Trip)
    suspend fun saveTrip(trip: TripResponse): String // Returns the ID
}
