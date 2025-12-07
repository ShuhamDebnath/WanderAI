package com.shuham.wanderai.data.repository

import com.shuham.wanderai.data.OpenRouterService
import com.shuham.wanderai.data.local.TripDao
import com.shuham.wanderai.data.local.TripEntity
import com.shuham.wanderai.data.model.TripRequest
import com.shuham.wanderai.data.model.TripResponse
import com.shuham.wanderai.domain.repository.TripRepository
import com.shuham.wanderai.util.NetworkResult
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TripRepositoryImpl(
    private val openRouterService: OpenRouterService,
    private val tripDao: TripDao
) : TripRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun generateTrip(request: TripRequest): NetworkResult<TripResponse> {
        val networkResult = try {
            openRouterService.generateItinerary(
                destination = request.destination,
                budget = request.budget,
                days = request.duration,
                travelers = request.travelers,
                interests = request.interests,
                diet = request.diet
            )?.let {
                NetworkResult.Success(it)
            } ?: NetworkResult.Error("Failed to generate itinerary. The response from the AI was empty.")
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkResult.Error(e.message ?: "An unknown error occurred while calling the AI service.")
        }

        if (networkResult is NetworkResult.Success) {
            networkResult.data?.let { saveTrip(it) }
        }

        return networkResult
    }

    override suspend fun saveTrip(trip: TripResponse): String {
        val tripJson = json.encodeToString(trip)
        val uniqueId = (trip.tripName.take(10) + "_" + System.currentTimeMillis()).filter { it.isLetterOrDigit() }
        
        val entity = TripEntity(
            id = uniqueId,
            tripName = trip.tripName,
            destinations = trip.destinations.joinToString(", "),
            createdAt = System.currentTimeMillis(),
            tripDataJson = tripJson
        )
        
        tripDao.insertTrip(entity)
        return uniqueId
    }

    override suspend fun getTrip(tripId: String): TripResponse? {
        val entity = tripDao.getTripById(tripId)
        return if (entity != null) {
            try {
                json.decodeFromString<TripResponse>(entity.tripDataJson)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }

    override suspend fun getAllTrips(): List<Pair<String, TripResponse>> {
        return tripDao.getAllTrips().mapNotNull { entity ->
            try {
                val tripResponse = json.decodeFromString<TripResponse>(entity.tripDataJson)
                entity.id to tripResponse
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
