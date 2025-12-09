package com.shuham.wanderai.presentation.map

data class MapState(
    val markers: List<MapMarker> = emptyList(),
    val isLoading: Boolean = true
)
