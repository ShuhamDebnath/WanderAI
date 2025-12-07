package com.shuham.wanderai.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.shuham.wanderai.data.local.AppDatabase
import org.koin.core.scope.Scope
import platform.Foundation.NSHomeDirectory

actual fun getDatabase(scope: Scope): AppDatabase {
    val dbFile = NSHomeDirectory() + "/wanderai.db"
    return Room.databaseBuilder<AppDatabase>(dbFile)
        .setDriver(BundledSQLiteDriver())
        .build()
}


//actual fun getDatabaseAgain(): AppDatabase{
//    val dbFile = NSHomeDirectory() + "/wanderai.db"
//    return Room.databaseBuilder<AppDatabase>(
//        dbFile,
//        { AppDatabase::class.instantiateImpl()}
//    )
//        .setDriver(BundledSQLiteDriver())
//        .build()
//}

