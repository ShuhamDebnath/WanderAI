package com.shuham.wanderai.presentation.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.shuham.wanderai.data.model.Coordinates
import kotlinx.coroutines.flow.MutableSharedFlow

// --- Android Actual Implementation ---

// 1. Controller Implementation
class MapControllerImpl : MapController {
    val commandFlow = MutableSharedFlow<MapCommand>()

    override fun setMarkers(markers: List<MapMarker>) {
        commandFlow.tryEmit(MapCommand.SetMarkers(markers))
    }

    override fun clearMarkers() {
        commandFlow.tryEmit(MapCommand.ClearMarkers)
    }

    override fun moveCamera(coordinates: Coordinates, zoom: Float) {
        commandFlow.tryEmit(MapCommand.MoveCamera(coordinates, zoom))
    }

    override fun animateCameraToFit(positions: List<Coordinates>) {
        commandFlow.tryEmit(MapCommand.AnimateCameraToFit(positions))
    }
}

// Internal commands to decouple controller from Composable state
sealed interface MapCommand {
    data class SetMarkers(val markers: List<MapMarker>) : MapCommand
    object ClearMarkers : MapCommand
    data class MoveCamera(val coordinates: Coordinates, val zoom: Float) : MapCommand
    data class AnimateCameraToFit(val positions: List<Coordinates>) : MapCommand
}

// 2. The `actual` MapView
@Composable
actual fun MapView(
    modifier: Modifier,
    markers: List<MapMarker>,
    onMapControllerReady: (MapController) -> Unit
) {
    val controller = remember { MapControllerImpl() }
    val cameraPositionState = rememberCameraPositionState()

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = MapProperties(),
        uiSettings = MapUiSettings(zoomControlsEnabled = false)
    ) {
        markers.forEach { marker ->
            Marker(
                state = rememberUpdatedMarkerState(position = LatLng(marker.position.lat, marker.position.lng)),
                title = marker.title,
                snippet = marker.snippet,
                // Here you can map your enum to actual drawables
                icon = BitmapDescriptorFactory.defaultMarker(
                    when (marker.icon) {
                        MarkerIcon.SIGHTSEEING -> BitmapDescriptorFactory.HUE_AZURE
                        MarkerIcon.FOOD -> BitmapDescriptorFactory.HUE_ORANGE
                        MarkerIcon.HOTEL -> BitmapDescriptorFactory.HUE_VIOLET
                    }
                )
            )
        }
    }

    // Handle commands from the controller
    LaunchedEffect(controller) {
        onMapControllerReady(controller)
        controller.commandFlow.collect {
            when (it) {
                is MapCommand.SetMarkers -> { /* Markers are handled declaratively now */ }
                is MapCommand.ClearMarkers -> { /* Markers are handled declaratively */ }
                is MapCommand.MoveCamera -> {
                    // cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(LatLng(it.coordinates.lat, it.coordinates.lng), it.zoom))
                }
                is MapCommand.AnimateCameraToFit -> {
                    if (it.positions.isNotEmpty()) {
                        val boundsBuilder = LatLngBounds.builder()
                        it.positions.forEach { pos ->
                            boundsBuilder.include(LatLng(pos.lat, pos.lng))
                        }
                        // cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100))
                    }
                }
            }
        }
    }
}
