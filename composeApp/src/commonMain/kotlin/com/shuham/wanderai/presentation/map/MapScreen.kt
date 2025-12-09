package com.shuham.wanderai.presentation.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MapRoute(
    viewModel: MapViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    MapScreen(state = state)
}

@Composable
fun MapScreen(state: MapState) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (state.markers.isEmpty()) {
            Text("No locations available for this day.", modifier = Modifier.align(Alignment.Center))
        } else {
            MapView(
                modifier = Modifier.fillMaxSize(),
                markers = state.markers,
                onMapControllerReady = { controller ->
                    // The MapView implementation will handle zooming to fit markers
                }
            )
        }
    }
}
