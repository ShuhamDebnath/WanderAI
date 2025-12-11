package com.shuham.wanderai.util

import android.content.Context
import android.location.Geocoder
import com.shuham.wanderai.data.model.Coordinates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class AndroidLocationService(private val context: Context) : LocationService {
    override suspend fun getCoordinates(placeName: String): Coordinates? {
        return withContext(Dispatchers.IO) {
            try {
                // Use Android's built-in Geocoder
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocationName(placeName, 1)
                if (!addresses.isNullOrEmpty()) {
                    Coordinates(addresses[0].latitude, addresses[0].longitude)
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
