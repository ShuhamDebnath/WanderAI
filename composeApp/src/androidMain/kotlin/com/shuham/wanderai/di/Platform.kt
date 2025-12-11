package com.shuham.wanderai.di

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.shuham.wanderai.data.local.AppDatabase
import com.shuham.wanderai.util.AndroidLocationService
import com.shuham.wanderai.util.LocationService
import org.koin.core.scope.Scope

actual fun getDatabase(scope: Scope): AppDatabase {
    val context = scope.get<Context>()
    val dbFile = context.getDatabasePath("wanderai.db")
    return Room.databaseBuilder<AppDatabase>(context, dbFile.absolutePath)
        .setDriver(BundledSQLiteDriver())
        .fallbackToDestructiveMigration(false) // Use this for easier schema changes during dev
        .build()
}

actual fun getLocationService(scope: Scope): LocationService {
    return AndroidLocationService(scope.get())
}
