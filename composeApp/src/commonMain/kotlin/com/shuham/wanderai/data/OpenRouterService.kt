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
        explicitNulls = false
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


    suspend fun getTripData(
        destination: String,
        budget: String,
        days: Int,
        travelers: String,
        interests: List<String>,
        diet: List<String>,
        pace: Float // Added Pace parameter
    ): TripResponse? {

        // Interpret Pace
        val paceDescription = when {
            pace < 0.33f -> "Relaxed (Low intensity, plenty of free time)"
            pace < 0.66f -> "Moderate (Balanced activities and rest)"
            else -> "Packed (High intensity, maximize sightseeing)"
        }

        // UPDATED PROMPT: Includes Pace and dynamic section logic
        val prompt = """
            Act as a Senior Luxury Travel Agent with 20 years of experience. Plan a $days-day trip to $destination.
            
            **Client Profile:**
            - Budget: $budget
            - Group: $travelers
            - Pace: $paceDescription
            - Interests: ${interests.joinToString(", ")}
            - Diet: ${diet.joinToString(", ")}

            **Critical Planning Rules (Follow these or be fired):**
            1. **Geo-Clustering:** Group Morning/Afternoon activities by NEIGHBORHOOD to minimize travel time. Don't make the user crisscross the city.
            2. **Insider Tips:** For every SIGHTSEEING spot, provide a specific "Insider Tip" (e.g., "Best photo spot is behind the cafe", "Go before 9 AM to avoid crowds").
            3. **Food Specifics:** For restaurants, mention a specific "Must Try Dish" in the description that fits the ${diet.joinToString(", ")} diet.
            4. **Transport:** If moving between cities, use type 'TRANSPORT'.
            5. **Hotels:** Suggest 3 hotels every night.
            6. **Pace Logic:** Adjust the number of activities based on the requested Pace ($paceDescription). If Relaxed, allow for leisure. If Packed, fit more in.
            7. **Activity Types:** Use these types appropriately:
               - 'TRANSPORT': For moving between cities or long distances within a city.
               - 'CHECK_IN': Upon arrival or hotel change.
               - 'SIGHTSEEING': For attractions.
               - 'FOOD_OPTION': For Lunch/Dinner (Provide 3 choices).
               - 'HOTEL_OPTION': For Night rest (Provide 3 choices).
            8. **Transit Logic:** In the 'description' of each SIGHTSEEING spot, explicitly mention how to get there from the previous spot (e.g., "A quick 10-min walk from the park").
            9. **Timestamps:** Assign a realistic 'time' (e.g., "09:00 AM") to every activity.
            10.**Structure:** The day SHOULD typically cover Morning, Afternoon, Evening, and Night, but you may rearrange sections if the logistics require it (e.g., late arrival).
            
            **JSON Structure (Strict compliance required):**
            {
              "tripName": "Creative & Catchy Trip Title",
              "destinations": ["City 1", "City 2"],
              "days": [
                {
                  "dayNumber": 1,
                  "city": "Name of City",
                  "narrative": "Arrive in City and explore...",
                  "sections": [
                    {
                      "timeOfDay": "Morning",
                      "activities": [
                        {
                          "type": "TRANSPORT",
                          "time": "08:00 AM",
                          "title": "Flight to City",
                          "description": "Arrive at Airport.",
                          "estimatedDuration": "N/A",
                          "priceLevel": "N/A"
                        },
                        {
                          "type": "CHECK_IN",
                          "time": "10:00 AM",
                          "placeName": "Hotel Check-in",
                          "description": "Drop bags.",
                          "estimatedDuration": "1 hour"
                        }
                      ]
                    },
                    {
                      "timeOfDay": "Afternoon",
                      "activities": [
                        {
                          "type": "SIGHTSEEING", 
                          "time": "01:00 PM",
                          "placeName": "Name of Place",
                          "coordinates": { "lat": 0.0, "lng": 0.0 },
                          "description": "Description of place.",
                          "insiderTip": "Specific advice (e.g. 'Enter via the East Gate')",
                          "estimatedDuration": "1.5 hours"
                        },
                        {
                          "type": "FOOD_OPTION",
                          "time": "02:30 PM",
                          "title": "Lunch in Neighborhood",
                          "options": [
                            {
                              "name": "Restaurant Name",
                              "tag": "Best Match", 
                              "priceLevel": "$$",
                              "description": "Cozy vibe. Must try: Specific Dish.",
                              "isRecommended": true
                            },
                            { "name": "Option 2", "tag": "Budget", "priceLevel": "$", "description": "...", "isRecommended": false },
                            { "name": "Option 3", "tag": "Luxury", "priceLevel": "$$$", "description": "...", "isRecommended": false }
                          ]
                        }
                      ]
                    },
                    {
                      "timeOfDay": "Evening",
                      "activities": [
                         {
                          "type": "SIGHTSEEING",
                          "time": "06:00 PM",
                          "placeName": "Name of Place",
                          "description": "Description.",
                          "estimatedDuration": "1 hour"
                        },
                        {
                          "type": "FOOD_OPTION",
                          "time": "08:00 PM",
                          "title": "Dinner with a View",
                          "options": [ ... ]
                        }
                      ]
                    },
                    {
                      "timeOfDay": "Night",
                      "activities": [
                        {
                          "type": "HOTEL_OPTION",
                          "time": "10:00 PM",
                          "title": "Where to Stay",
                          "options": [ ... ]
                        }
                      ]
                    }
                  ]
                }
              ]
            }
            
            Return ONLY valid JSON. No markdown.
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
