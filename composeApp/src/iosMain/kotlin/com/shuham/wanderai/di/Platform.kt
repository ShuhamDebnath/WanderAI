package com.shuham.wanderai.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.shuham.wanderai.data.local.AppDatabase
import com.shuham.wanderai.data.model.Coordinates
import com.shuham.wanderai.util.LocationService
import org.koin.core.scope.Scope
import platform.Foundation.NSHomeDirectory

actual fun getDatabase(scope: Scope): AppDatabase {
    val dbFile = NSHomeDirectory() + "/wanderai.db"
    return Room.databaseBuilder<AppDatabase>(dbFile)
        .setDriver(BundledSQLiteDriver())
        .fallbackToDestructiveMigration(false)
        .build()
}

// Dummy implementation for iOS
actual fun getLocationService(scope: Scope): LocationService {
    return object : LocationService {
        override suspend fun getCoordinates(placeName: String): Coordinates? {
            println("LocationService on iOS is not implemented yet.")
            return null
        }
    }
}
