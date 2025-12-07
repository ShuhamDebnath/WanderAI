package com.shuham.wanderai.di

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.shuham.wanderai.data.local.AppDatabase
import org.koin.core.scope.Scope

actual fun getDatabase(scope: Scope): AppDatabase {
    val context = scope.get<Context>()
    val dbFile = context.getDatabasePath("wanderai.db")
    return Room.databaseBuilder<AppDatabase>(context, dbFile.absolutePath)
        .setDriver(BundledSQLiteDriver())
        .build()
}

//actual fun getDatabaseAgain(context: Context): AppDatabase{
//    val dbFile = context.getDatabasePath("wanderai.db")
//    return Room.databaseBuilder<AppDatabase>(
//        context,
//        AppDatabase::class.java,
//        dbFile.absolutePath
//    )
//        .setDriver(BundledSQLiteDriver())
//        .build()
//}
