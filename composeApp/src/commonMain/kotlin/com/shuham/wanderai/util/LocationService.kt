package com.shuham.wanderai.util

import com.shuham.wanderai.data.model.Coordinates

interface LocationService {
    suspend fun getCoordinates(placeName: String): Coordinates?
}