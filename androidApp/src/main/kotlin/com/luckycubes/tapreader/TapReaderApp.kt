package com.luckycubes.tapreader

import android.app.Application
import com.luckycubes.tapreader.core.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class TapReaderApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@TapReaderApp)
        }
    }
}
