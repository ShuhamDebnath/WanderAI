package com.shuham.wanderai.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey
    val id: String,
    val tripName: String,
    val destinations: String, // e.g., "Tokyo, Kyoto"
    val createdAt: Long = System.currentTimeMillis(), // Unix timestamp
    val isSaved: Boolean = false, // To mark user-saved trips vs. just history
    val tripDataJson: String // The entire TripResponse object as a JSON string
)
