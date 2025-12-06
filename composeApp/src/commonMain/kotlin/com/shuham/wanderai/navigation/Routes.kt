package com.shuham.wanderai.navigation

import kotlinx.serialization.Serializable

// --- Top Level Destinations ---

@Serializable
object Splash 

@Serializable
object Login

@Serializable
object SignUp

@Serializable
object Main 


// --- Main Screen Bottom Nav Destinations ---

@Serializable
object Home

@Serializable
object Trips

@Serializable
object Profile

// --- Trip Generation Flow ---

// Note: Loading is now a state within Home, not a route.

@Serializable
data class Itinerary(val tripId: String)
