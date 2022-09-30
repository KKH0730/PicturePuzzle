package com.seno.game

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.opencv.android.OpenCVLoader
import timber.log.Timber

@HiltAndroidApp
class App : Application() {

    companion object {
        private lateinit var instance: App
        fun getInstance(): App = instance
    }


    override fun onCreate() {
        super.onCreate()
        instance = this
        Timber.plant(Timber.DebugTree())

        OpenCVLoader.initDebug()
    }

}