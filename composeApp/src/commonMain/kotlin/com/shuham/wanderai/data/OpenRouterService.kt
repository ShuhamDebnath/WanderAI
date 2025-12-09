package com.shuham.wanderai.data

import com.shuham.wanderai.BuildConfig
import com.shuham.wanderai.data.model.TripResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class OpenRouterService(private val client: HttpClient) {

    private val apiKey = BuildConfig.OPENROUTER_API_KEY
    private val url = "https://openrouter.ai/api/v1/chat/completions"

    private val model = "amazon/nova-2-lite-v1:free"

    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    suspend fun getTripData(
        destination: String,
        budget: String,
        days: Int,
        travelers: String,
        interests: List<String>,
        diet: List<String>
    ): TripResponse? {

        val prompt = """
            Plan a $days-day trip to $destination.
            - Budget: $budget
            - Travelers: $travelers
            - Interests: ${interests.joinToString(", ")}
            - Diet: ${diet.joinToString(", ")}

            You MUST return a JSON object matching exactly this structure:
            {
              "tripName": "String",
              "destinations": ["String"],
              "days": [
                {
                  "dayNumber": 1,
                  "city": "String",
                  "narrative": "A brief summary of the day",
                  "sections": [
                    {
                      "timeOfDay": "Morning",
                      "activities": [
                        {
                          "type": "SIGHTSEEING", 
                          "time": "09:00 AM",
                          "placeName": "Name of place",
                          "coordinates": { "lat": 0.0, "lng": 0.0 },
                          "description": "Short description",
                          "estimatedDuration": "2 hours"
                        },
                        {
                          "type": "FOOD_OPTION",
                          "time": "01:00 PM",
                          "title": "Lunch Recommendation",
                          "options": [
                            {
                              "name": "Restaurant Name",
                              "tag": "Best Match", 
                              "priceLevel": "$$",
                              "description": "Why it fits",
                              "isRecommended": true
                            }
                          ]
                        }
                      ]
                    }
                  ]
                }
              ]
            }
            
            Valid 'type' values: SIGHTSEEING, TRANSPORT, CHECK_IN, FOOD_OPTION, HOTEL_OPTION.
            Valid 'timeOfDay' values: Morning, Afternoon, Evening.
            Provide 3 options for FOOD_OPTION/HOTEL_OPTION (Best Match, Cheapest, Luxury).
            Do not include markdown formatting. Just raw JSON.
        """.trimIndent()

        val requestBody = OpenRouterRequest(
            model = model,
            messages = listOf(
                Message(role = "user", content = prompt)
            ),
            response_format = ResponseFormat(type = "json_object") // Force JSON
        )

        return try {
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $apiKey")
                header("HTTP-Referer", "https://wanderai.app")
                header("X-Title", "WanderAI")
                setBody(requestBody)
            }

            val rawString = response.bodyAsText()
            if (!response.status.isSuccess()) {
                return null
            }

            val apiResponse = jsonParser.decodeFromString<OpenRouterResponse>(rawString)
            var content = apiResponse.choices.firstOrNull()?.message?.content ?: "{}"

            val jsonStartIndex = content.indexOfFirst { it == '{' || it == '[' }
            val jsonEndIndex = content.indexOfLast { it == '}' || it == ']' }
            if (jsonStartIndex != -1 && jsonEndIndex != -1) {
                content = content.substring(jsonStartIndex, jsonEndIndex + 1)
            }

            if (content.startsWith("[")) {
                jsonParser.decodeFromString<List<TripResponse>>(content).firstOrNull()
            } else {
                jsonParser.decodeFromString<TripResponse>(content)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

@Serializable
data class OpenRouterRequest(
    val model: String,
    val messages: List<Message>,
    val response_format: ResponseFormat
)

@Serializable
data class Message(
    val role: String,
    val content: String
)

@Serializable
data class ResponseFormat(
    val type: String
)

@Serializable
data class OpenRouterResponse(
    val choices: List<Choice>
)

@Serializable
data class Choice(
    val message: Message
)
