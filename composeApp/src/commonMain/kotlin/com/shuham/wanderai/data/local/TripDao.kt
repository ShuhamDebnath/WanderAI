package com.shuham.wanderai.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TripDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripEntity)

    @Update
    suspend fun updateTrip(trip: TripEntity)

    @Query("SELECT * FROM trips WHERE id = :tripId")
    suspend fun getTripById(tripId: String): TripEntity?
    
    @Query("SELECT * FROM trips ORDER BY createdAt DESC")
    suspend fun getAllTrips(): List<TripEntity>
    
    @Query("SELECT * FROM trips WHERE tripDataJson = :json LIMIT 1")
    suspend fun getTripByJson(json: String): TripEntity?
    
    @Query("DELETE FROM trips WHERE id = :tripId")
    suspend fun deleteTrip(tripId: String)
}
