package com.shuham.wanderai.presentation.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.shuham.wanderai.data.model.Coordinates
import kotlinx.serialization.Serializable

@Serializable
data class MapMarker(
    val id: String,
    val position: Coordinates,
    val title: String,
    val snippet: String,
    val icon: MarkerIcon
)

@Serializable
enum class MarkerIcon {
    SIGHTSEEING,
    FOOD,
    HOTEL
}

interface MapController {
    fun setMarkers(markers: List<MapMarker>)
    fun clearMarkers()
    fun moveCamera(coordinates: Coordinates, zoom: Float)
    fun animateCameraToFit(positions: List<Coordinates>)
}

@Composable
expect fun MapView(
    modifier: Modifier,
    markers: List<MapMarker>,
    onMapControllerReady: (MapController) -> Unit
)
