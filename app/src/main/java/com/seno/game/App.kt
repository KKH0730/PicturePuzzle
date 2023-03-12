package com.seno.game

import android.app.Activity
import android.app.Application
import android.content.ContextWrapper
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LifecycleObserver
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.ads.MobileAds
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NaverIdLoginSDK
import com.pixplicity.easyprefs.library.Prefs
import com.seno.game.prefs.PrefsManager
import com.seno.game.ui.main.home.game.diff_picture.single.DPSinglePlayActivity
import com.seno.game.ui.splash.SplashActivity
import com.seno.game.util.SoundUtil
import dagger.hilt.android.HiltAndroidApp
import org.opencv.android.OpenCVLoader
import timber.log.Timber

@HiltAndroidApp
class App : Application(), LifecycleObserver, Application.ActivityLifecycleCallbacks {
    private var activityCount = 0
    private var isInBackground = false
    private var isInitialStart = true
    
    companion object {
        private lateinit var instance: App
        fun getInstance(): App = instance
    }


    override fun onCreate() {
        super.onCreate()
        instance = this

        registerActivityLifecycleCallbacks(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

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

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        if (isInitialStart) {
            isInitialStart = false

            if (!isInBackground && PrefsManager.isActiveBackgroundBGM) {
                SoundUtil.startBackgroundSound(this)
            }
        }
    }

    override fun onActivityStarted(activity: Activity) {
        // 액티비티가 시작되었을 때의 처리
        activityCount++
        if (isInBackground) {
            // 앱이 foreground로 이동한 경우 처리할 내용
            isInBackground = false

            if (activity !is DPSinglePlayActivity) {
                if (!isInBackground && PrefsManager.isActiveBackgroundBGM) {
                    SoundUtil.restartBackgroundBGM()
                }
            }
        }
    }

    override fun onActivityResumed(activity: Activity) {
        // 액티비티가 재개되었을 때의 처리
    }

    override fun onActivityPaused(activity: Activity) {
        // 액티비티가 일시 중지될 때의 처리
    }

    override fun onActivityStopped(activity: Activity) {
        // 액티비티가 중지되었을 때의 처리
        activityCount--
        if (activityCount == 0) {
            // 앱이 background로 이동한 경우 처리할 내용
            isInBackground = true

            if (activity !is SplashActivity) {
                SoundUtil.pause(isBackgroundSound = true)
            }
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        // 액티비티의 상태를 저장할 때의 처리
    }

    override fun onActivityDestroyed(activity: Activity) {
        // 액티비티가 소멸될 때의 처리
    }

    fun isInBackground(): Boolean {
        return isInBackground
    }

    override fun onTerminate() {
        SoundUtil.release(isBackgroundSound = true)
        super.onTerminate()
    }
}