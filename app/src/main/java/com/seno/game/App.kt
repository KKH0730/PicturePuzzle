package com.seno.game

import android.app.Application
import android.content.ContextWrapper
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.ads.MobileAds
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NaverIdLoginSDK
import com.pixplicity.easyprefs.library.Prefs
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

        Prefs.Builder()
            .setContext(this)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName(packageName)
            .setUseDefaultSharedPreference(true)
            .build()

        MobileAds.initialize(this)

        NaverIdLoginSDK.initialize(
            context = this,
            clientId = getString(R.string.social_login_info_naver_client_id),
            clientSecret = getString(R.string.social_login_info_naver_client_secret),
            clientName = getString(R.string.social_login_info_naver_client_name)
        )

        KakaoSdk.init(this, getString(R.string.kakao_native_app_key))

        OpenCVLoader.initDebug()
        if (!BuildConfig.DEBUG) {
            AppEventsLogger.activateApp(application = getInstance())
        }
    }
}