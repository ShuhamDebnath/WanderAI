package com.shuham.wanderai.presentation.map

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.shuham.wanderai.data.model.Coordinates

@Composable
private fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    val lifecycle = androidx.lifecycle.compose.LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle, mapView) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }
    return mapView
}

class MapControllerImpl(private val googleMap: GoogleMap) : MapController {
    override fun setMarkers(markers: List<MapMarker>) {
        googleMap.clear()
        markers.forEach { marker ->
            val latLng = LatLng(marker.position.lat, marker.position.lng)
            googleMap.addMarker(
                com.google.android.gms.maps.model.MarkerOptions()
                    .position(latLng)
                    .title(marker.title)
                    .snippet(marker.snippet)
                    .icon(
                        BitmapDescriptorFactory.defaultMarker(
                            when (marker.icon) {
                                MarkerIcon.SIGHTSEEING -> BitmapDescriptorFactory.HUE_AZURE
                                MarkerIcon.FOOD -> BitmapDescriptorFactory.HUE_ORANGE
                                MarkerIcon.HOTEL -> BitmapDescriptorFactory.HUE_VIOLET
                            }
                        )
                    )
            )
        }
    }

    override fun clearMarkers() {
        googleMap.clear()
    }

    override fun moveCamera(coordinates: Coordinates, zoom: Float) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(coordinates.lat, coordinates.lng), zoom))
    }

    override fun animateCameraToFit(positions: List<Coordinates>) {
        if (positions.isNotEmpty()) {
            val boundsBuilder = LatLngBounds.builder()
            positions.forEach { boundsBuilder.include(LatLng(it.lat, it.lng)) }
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100))
        }
    }
}

@Composable
actual fun MapView(
    modifier: Modifier,
    markers: List<MapMarker>,
    onMapControllerReady: (MapController) -> Unit
) {
    val mapView = rememberMapViewWithLifecycle()

    AndroidView({ mapView }, modifier) { view ->
        view.getMapAsync { googleMap ->
            onMapControllerReady(MapControllerImpl(googleMap))
            val controller = MapControllerImpl(googleMap)
            controller.setMarkers(markers)
            controller.animateCameraToFit(markers.map { it.position })
        }
    }
}
