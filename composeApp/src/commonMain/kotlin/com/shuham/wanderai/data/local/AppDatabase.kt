package com.shuham.wanderai.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TripEntity::class], version = 1, exportSchema = false) // Version incremented to 2
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
}
