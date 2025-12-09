package com.shuham.wanderai.navigation

import com.shuham.wanderai.presentation.map.MapMarker
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

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

// --- Trip Generation and Details Flow ---

@Serializable
data class TripDetails(val tripId: String)


@Serializable
data class Map(
    val tripId: String,
    val dayNumber: Int = -1 // -1 means "Show Whole Trip"
)



//@Serializable
//data class Map(
//    // Custom serializer is needed for lists of complex objects in navigation
//    @Serializable(with = MapMarkerListSerializer::class)
//    val markers: List<MapMarker>
//)
//
//// --- Custom Serializer for List<MapMarker> ---
//object MapMarkerListSerializer : KSerializer<List<MapMarker>> {
//    private val listSerializer = ListSerializer(MapMarker.serializer())
//
//    override val descriptor: SerialDescriptor = listSerializer.descriptor
//
//    override fun serialize(encoder: Encoder, value: List<MapMarker>) {
//        listSerializer.serialize(encoder, value)
//    }
//
//    override fun deserialize(decoder: Decoder): List<MapMarker> {
//        return listSerializer.deserialize(decoder)
//    }
//}
