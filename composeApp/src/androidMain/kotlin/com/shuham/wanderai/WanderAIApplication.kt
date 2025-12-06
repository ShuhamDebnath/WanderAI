package com.shuham.wanderai

import android.app.Application
import com.shuham.wanderai.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class WanderAIApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        initKoin {
            androidLogger()
            androidContext(this@WanderAIApplication)
        }
    }
}
