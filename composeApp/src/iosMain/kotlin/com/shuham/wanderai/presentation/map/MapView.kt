package com.shuham.wanderai.presentation.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import com.shuham.wanderai.data.model.Coordinates
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKAnnotationProtocol
import platform.MapKit.MKCoordinateRegionMakeWithDistance
import platform.MapKit.MKMapView
import platform.MapKit.MKPointAnnotation
import platform.darwin.NSObject

// --- iOS Actual Implementation ---

@OptIn(ExperimentalForeignApi::class)
class MapControllerImpl(private val mapView: MKMapView) : MapController {

    override fun setMarkers(markers: List<MapMarker>) {
        // Clear existing before adding new ones
        mapView.removeAnnotations(mapView.annotations)
        val annotations = markers.map { it.toMKPointAnnotation() }
        mapView.addAnnotations(annotations)
    }

    override fun clearMarkers() {
        mapView.removeAnnotations(mapView.annotations)
    }

    override fun moveCamera(coordinates: Coordinates, zoom: Double) {
        val region = MKCoordinateRegionMakeWithDistance(
            centerCoordinate = coordinates.toCLLocationCoordinate2D(),
            latitudinalMeters = zoom,
            longitudinalMeters = zoom
        )
        mapView.setRegion(region, animated = true)
    }

    override fun animateCameraToFit(positions: List<Coordinates>) {
        // MKMapView can do this automatically with showAnnotations
        mapView.showAnnotations(mapView.annotations, animated = true)
    }
}

// Helper to convert our common data class to the iOS-specific one
@OptIn(ExperimentalForeignApi::class)
private fun MapMarker.toMKPointAnnotation(): MKPointAnnotation {
    val annotation = MKPointAnnotation()
    annotation.setCoordinate(this.position.toCLLocationCoordinate2D())
    annotation.setTitle(this.title)
    annotation.setSubtitle(this.snippet)
    return annotation
}

@OptIn(ExperimentalForeignApi::class)
private fun Coordinates.toCLLocationCoordinate2D(): CLocationCoordinate2D {
    return CLLocationCoordinate2DMake(latitude = this.lat, longitude = this.lng)
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun MapView(
    modifier: Modifier,
    markers: List<MapMarker>,
    onMapControllerReady: (MapController) -> Unit
) {
    val mapView = remember { MKMapView() }
    val mapController = remember { MapControllerImpl(mapView) }

    UIKitView(
        factory = { mapView },
        modifier = modifier,
        update = { view ->
            // This is where we imperatively update the map view
            // For this implementation, we will use the controller.
        }
    )

    // Handle markers and controller setup
    LaunchedEffect(Unit, markers) {
        onMapControllerReady(mapController)
        mapController.setMarkers(markers)
        mapController.animateCameraToFit(markers.map { it.position })
    }
}
