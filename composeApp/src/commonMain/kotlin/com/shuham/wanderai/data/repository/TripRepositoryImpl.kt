package com.shuham.wanderai.data.repository

import com.shuham.wanderai.data.OpenRouterService
import com.shuham.wanderai.data.PlacesService
import com.shuham.wanderai.data.local.TripDao
import com.shuham.wanderai.data.local.TripEntity
import com.shuham.wanderai.data.model.TripRequest
import com.shuham.wanderai.data.model.TripResponse
import com.shuham.wanderai.domain.repository.TripRepository
import com.shuham.wanderai.util.LocationService
import com.shuham.wanderai.util.NetworkResult
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TripRepositoryImpl(
    private val openRouterService: OpenRouterService,
    private val tripDao: TripDao,
    private val locationService: LocationService,
    private val placesService: PlacesService
) : TripRepository {

    private val json = Json { 
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    override suspend fun generateTrip(request: TripRequest): NetworkResult<TripResponse> {
        val networkResult = try {
            openRouterService.getTripData(
                destination = request.destination,
                budget = request.budget,
                days = request.duration,
                pace = request.pace,
                travelers = request.travelers,
                interests = request.interests,
                diet = request.diet
            )?.let {
                // Enrich with coordinates before saving
                val enrichedTrip = enrichTripWithCoordinates(it)
                val savedTripId = saveTrip(enrichedTrip)
                NetworkResult.Success(enrichedTrip.copy(id = savedTripId))
            } ?: NetworkResult.Error("Failed to generate itinerary. The response from the AI was empty.")
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkResult.Error(e.message ?: "An unknown error occurred while calling the AI service.")
        }
        
        return networkResult
    }
    
    private suspend fun enrichTripWithCoordinates(trip: TripResponse): TripResponse {
        val updatedDays = trip.days.map { day ->
            val updatedSections = day.sections.map { section ->
                val updatedActivities = section.activities.map { activity ->
                    if (activity.coordinates == null && activity.placeName != null) {
                        val coords = locationService.getCoordinates(activity.placeName)
                        activity.copy(coordinates = coords)
                    } else {
                        activity
                    }
                }
                section.copy(activities = updatedActivities)
            }
            day.copy(sections = updatedSections)
        }
        return trip.copy(days = updatedDays)
    }

    override suspend fun saveTrip(trip: TripResponse): String {
        val uniqueId = (trip.tripName.take(10) + "_" + System.currentTimeMillis()).filter { it.isLetterOrDigit() }
        val tripWithId = trip.copy(id = uniqueId)
        val tripJson = json.encodeToString(tripWithId)
        
        val entity = TripEntity(
            id = uniqueId,
            tripName = tripWithId.tripName,
            destinations = tripWithId.destinations.joinToString(", "),
            createdAt = System.currentTimeMillis(),
            tripDataJson = tripJson
        )
        
        tripDao.insertTrip(entity)
        return uniqueId
    }

    override suspend fun getTrip(tripId: String): TripResponse? {
        return tripDao.getTripById(tripId)?.let {
            try {
                json.decodeFromString<TripResponse>(it.tripDataJson)
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

    override suspend fun getPlaceImageUrl(placeName: String): String? {
        return placesService.getPlaceImageUrl(placeName)
    }


}
