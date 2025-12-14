package com.shuham.wanderai.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TripEntity::class], version = 4, exportSchema = false) // Incremented to 4
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
}
