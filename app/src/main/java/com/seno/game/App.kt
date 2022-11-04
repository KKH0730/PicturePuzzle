package com.seno.game

import android.app.Application
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
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

        FacebookSdk.sdkInitialize(applicationContext)
        Timber.plant(Timber.DebugTree())

        OpenCVLoader.initDebug()
        if (!BuildConfig.DEBUG) {
            AppEventsLogger.activateApp(application = getInstance())
        }
    }

}