package com.shuham.wanderai.data.repository

import com.shuham.wanderai.data.OpenRouterService
import com.shuham.wanderai.data.PlacesService
import com.shuham.wanderai.data.local.TripDao
import com.shuham.wanderai.data.local.TripEntity
import com.shuham.wanderai.data.model.Activity
import com.shuham.wanderai.data.model.TripRequest
import com.shuham.wanderai.data.model.TripResponse
import com.shuham.wanderai.domain.repository.TripRepository
import com.shuham.wanderai.util.LocationService
import com.shuham.wanderai.util.NetworkResult
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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
            )?.let { tripResponse ->
                // Enrich with coordinates AND images before saving
                val enrichedTrip = enrichTripData(tripResponse)
                val savedTripId = saveTrip(enrichedTrip)
                NetworkResult.Success(enrichedTrip.copy(id = savedTripId))
            } ?: NetworkResult.Error("Failed to generate itinerary. The response from the AI was empty.")
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkResult.Error(e.message ?: "An unknown error occurred while calling the AI service.")
        }
        
        return networkResult
    }
    
    // Updated to enrich both Coordinates and Images in parallel
    private suspend fun enrichTripData(trip: TripResponse): TripResponse = coroutineScope {
        val updatedDays = trip.days.map { day ->
            async {
                val updatedSections = day.sections.map { section ->
                    val updatedActivities = section.activities.map { activity ->
                        enrichActivity(activity, day.city)
                    }
                    section.copy(activities = updatedActivities)
                }
                day.copy(sections = updatedSections)
            }
        }
        trip.copy(days = updatedDays.awaitAll())
    }

    private suspend fun enrichActivity(activity: Activity, city : String): Activity = coroutineScope {
        // 1. Coordinates
        val coordsDeferred = async {
            if (activity.coordinates == null && activity.placeName != null) {
                locationService.getCoordinates(activity.placeName)
            } else {
                activity.coordinates
            }
        }

        // 2. Image URL (Primary Activity)
        val imageDeferred = async {
            if (activity.imageUrl == null) {
                val query = activity.placeName ?: activity.title
                if (query != null) placesService.getPlaceImageUrl(query) else null
            } else {
                activity.imageUrl
            }
        }
        
        // 3. Image URLs for Options (if any)
        val optionsDeferred = activity.options?.map { option ->
            async {
                if (option.imageUrl == null) {
                    val url = placesService.getPlaceImageUrl(option.name)
                    option.copy(imageUrl = url)
                } else {
                    option
                }
            }
        }

        val newCoords = coordsDeferred.await()
        val newImage = imageDeferred.await()
        val newOptions = optionsDeferred?.awaitAll()

        activity.copy(
            coordinates = newCoords,
            imageUrl = newImage,
            options = newOptions
        )
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

    override suspend fun updateTrip(trip: TripResponse) {
        val tripJson = json.encodeToString(trip)
        val existingEntity = tripDao.getTripById(trip.id)
        
        if (existingEntity != null) {
            val updatedEntity = existingEntity.copy(tripDataJson = tripJson)
            tripDao.updateTrip(updatedEntity)
        } else {
            // Fallback: Re-create minimal entity if somehow missing, though rare
             val entity = TripEntity(
                id = trip.id,
                tripName = trip.tripName,
                destinations = trip.destinations.joinToString(", "),
                createdAt = System.currentTimeMillis(),
                tripDataJson = tripJson
            )
            tripDao.insertTrip(entity)
        }
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
