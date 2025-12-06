package com.shuham.wanderai.data

import com.shuham.wanderai.BuildConfig
import com.shuham.wanderai.data.model.TripItinerary
import io.ktor.client.HttpClient
import io.ktor.client.call.body
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
    private val model = "deepseek/deepseek-chat-v3.1"




    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    suspend fun generateItinerary(
        destination: String,
        budget: String,
        days: Int,
        travelers: String,
        interests: List<String>,
        diet: List<String>
    ): TripItinerary? {

        val prompt = """
            Plan a $days-day trip to $destination.
            - Budget: $budget
            - Travelers: $travelers
            - Interests: ${interests.joinToString(", ")}
            - Diet: ${diet.joinToString(", ")}

            You MUST return a JSON object matching exactly this structure:
            {
              "tripName": "String",
              "dailyPlan": [
                {
                  "day": 1,
                  "activities": [
                    { "time": "09:00 AM", "title": "Place Name", "description": "Short details", "type": "sightseeing" }
                  ]
                }
              ]
            }
            Do not include markdown formatting. Just raw JSON.
        """.trimIndent()

        // OpenAI Standard Request Body
        val requestBody = OpenRouterRequest(
            model = model,
            messages = listOf(
                Message(role = "user", content = prompt)
            ),
            response_format = ResponseFormat(type = "json_object") // Force JSON
        )

        println("pass 1: Sending request to OpenRouter")

        return try {
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $apiKey")
                // OpenRouter requires these headers
                header("HTTP-Referer", "https://wanderai.app")
                header("X-Title", "WanderAI")
                setBody(requestBody)
            }

            val rawString = response.bodyAsText()
            println("pass 2: Raw Response: $rawString")

            if (!response.status.isSuccess()) {
                println("API Error: ${response.status}")
                return null
            }

            val apiResponse = jsonParser.decodeFromString<OpenRouterResponse>(rawString)
            var content = apiResponse.choices.firstOrNull()?.message?.content ?: "{}"

            // Cleanup Logic
            val jsonStartIndex = content.indexOfFirst { it == '{' || it == '[' }
            val jsonEndIndex = content.indexOfLast { it == '}' || it == ']' }
            if (jsonStartIndex != -1 && jsonEndIndex != -1) {
                content = content.substring(jsonStartIndex, jsonEndIndex + 1)
            }

            println("pass 3: Parsed Content: $content")

            // Parse final Itinerary
            if (content.startsWith("[")) {
                jsonParser.decodeFromString<List<TripItinerary>>(content).firstOrNull()
            } else {
                jsonParser.decodeFromString<TripItinerary>(content)
            }

        } catch (e: Exception) {
            println("fail e : $e")
            e.printStackTrace()
            null
        }
    }
}

// --- OpenAI Standard Data Models ---

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
