package com.shuham.wanderai.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class PlacesService(private val client: HttpClient) {

    // Free OpenStreetMap Geocoding API (Photon)
    private val photonUrl = "https://photon.komoot.io/api/"

    // Free Wikipedia API for Images
    private val wikiUrl = "https://en.wikipedia.org/w/api.php"

    suspend fun searchCities(query: String): List<CitySuggestion> {
        if (query.length < 3) return emptyList()

        return try {
            val response: PhotonResponse = client.get(photonUrl) {
                parameter("q", query)
                parameter("limit", 5)
                parameter("lang", "en")
                // Filter for places (cities, towns, villages)
                parameter("osm_tag", "place:city")
                parameter("osm_tag", "place:town")
            }.body()

            response.features.map {
                CitySuggestion(
                    name = it.properties.name,
                    country = it.properties.country ?: "",
                    state = it.properties.state ?: ""
                )
            }.distinct() // Remove duplicates
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Fetches a relevant image URL for a place name using Wikipedia's Media API.
     * Useful for showing a hero image in the details bottom sheet.
     */
    suspend fun getPlaceImageUrl(placeName: String): String? {
        return try {
            val response = client.get(wikiUrl) {
                parameter("action", "query")
                parameter("format", "json")
                parameter("generator", "search")
                parameter("gsrsearch", placeName)
                parameter("gsrlimit", 1) // Get top 1 result
                parameter("prop", "pageimages")
                parameter("piprop", "original") // Request original image URL
            }.body<String>()

            // Manually parse the dynamic JSON tree to find the "source" URL
            val json = Json { ignoreUnknownKeys = true }.parseToJsonElement(response)
            val pages = json.jsonObject["query"]?.jsonObject?.get("pages")?.jsonObject

            // Wikipedia returns pages with dynamic IDs (e.g. "12345": {...}), so we take the first key
            val firstPageKey = pages?.keys?.firstOrNull()

            val originalImage = pages?.get(firstPageKey)?.jsonObject?.get("original")?.jsonObject
            val imageUrl = originalImage?.get("source")?.jsonPrimitive?.content

            imageUrl
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

@Serializable
data class CitySuggestion(
    val name: String,
    val state: String,
    val country: String
) {
    fun displayName(): String {
        return if (state.isNotEmpty()) "$name, $state, $country" else "$name, $country"
    }
}

// --- Internal API Models ---
@Serializable
data class PhotonResponse(val features: List<PhotonFeature>)

@Serializable
data class PhotonFeature(val properties: PhotonProperties)

@Serializable
data class PhotonProperties(
    val name: String,
    val country: String? = null,
    val state: String? = null
)