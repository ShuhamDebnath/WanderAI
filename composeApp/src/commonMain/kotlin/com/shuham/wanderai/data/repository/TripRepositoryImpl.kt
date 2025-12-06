package com.shuham.wanderai.data.repository

import com.shuham.wanderai.data.OpenRouterService
import com.shuham.wanderai.data.model.TripItinerary
import com.shuham.wanderai.data.model.TripRequest
import com.shuham.wanderai.domain.repository.TripRepository
import com.shuham.wanderai.util.NetworkResult

class TripRepositoryImpl(
    private val openRouterService: OpenRouterService,
) : TripRepository {

    private val memoryCache = mutableMapOf<String, TripItinerary>()

    override suspend fun generateTrip(request: TripRequest): NetworkResult<TripItinerary> {
        return try {


            println("request Success : $request")

            val result = openRouterService.generateItinerary(
                destination = request.destination,
                budget = request.budget,
                days = request.duration,
                travelers = request.travelers,
                interests = request.interests,
                diet = request.diet
            )
            if (result != null) {
                println("result Success : $result")
                NetworkResult.Success(result)
            } else {
                println("result Error : $result")
                NetworkResult.Error("Failed to generate itinerary. Response was empty.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkResult.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun saveTrip(trip: TripItinerary): String {
        val id = (trip.tripName.take(10) + "_" + System.currentTimeMillis()).filter { it.isLetterOrDigit() }
        memoryCache[id] = trip
        return id
    }

    override suspend fun getTrip(tripId: String): TripItinerary? {
        return memoryCache[tripId]
    }
}
