package com.shuham.wanderai.data.repository

import com.shuham.wanderai.data.OpenRouterService
import com.shuham.wanderai.data.local.TripDao
import com.shuham.wanderai.data.local.TripEntity
import com.shuham.wanderai.data.model.TripRequest
import com.shuham.wanderai.data.model.TripResponse
import com.shuham.wanderai.domain.repository.TripRepository
import com.shuham.wanderai.util.NetworkResult
import kotlinx.serialization.json.Json

class TripRepositoryImpl(
    private val openRouterService: OpenRouterService,
    private val tripDao: TripDao
) : TripRepository {

    private val json = Json { 
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    override suspend fun generateTrip(request: TripRequest): NetworkResult<TripResponse> {
        return try {
            openRouterService.getTripData(
                destination = request.destination,
                budget = request.budget,
                days = request.duration,
                travelers = request.travelers,
                interests = request.interests,
                diet = request.diet
            )?.let { tripResponse ->
                // Save to DB immediately on success and get the ID

                val savedTripId = saveTrip(tripResponse)
                // Return the response now including the ID
                NetworkResult.Success(tripResponse.copy(id = savedTripId))
            } ?: NetworkResult.Error("Failed to generate itinerary. The response from the AI was empty.")
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkResult.Error(e.message ?: "An unknown error occurred while calling the AI service.")
        }
    }

    override suspend fun saveTrip(trip: TripResponse): String {
        // Generate a new ID here. This is the single source of truth for the ID.
        val uniqueId = (trip.tripName.take(10) + "_" + System.currentTimeMillis()).filter { it.isLetterOrDigit() }

        // Create a new TripResponse instance that includes the generated ID.
        val tripWithId = trip.copy(id = uniqueId)

        val tripJson = json.encodeToString(tripWithId)
        
        val entity = TripEntity(
            id = uniqueId, // Use the newly generated ID
            tripName = tripWithId.tripName,
            destinations = tripWithId.destinations.joinToString(", "),
            createdAt = System.currentTimeMillis(),
            tripDataJson = tripJson
        )

        println("entity $entity")
        
        tripDao.insertTrip(entity)
        return uniqueId
    }

    override suspend fun getTrip(tripId: String): TripResponse? {
        return tripDao.getTripById(tripId)?.let { tripEntity ->


            try {
                val tripResponse  = json.decodeFromString<TripResponse>(tripEntity.tripDataJson)
                tripResponse.id = tripEntity.id
                println("tripResponse $tripResponse")
                tripResponse
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    override suspend fun getAllTrips(): List<Pair<String, TripResponse>> {
        return tripDao.getAllTrips().mapNotNull { entity ->
            try {
                val tripResponse = json.decodeFromString<TripResponse>(entity.tripDataJson)
                // The ID from the entity is the source of truth
                entity.id to tripResponse.copy(id = entity.id)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    override suspend fun deleteTrip(tripId: String) {
        tripDao.deleteTrip(tripId)
    }
}
