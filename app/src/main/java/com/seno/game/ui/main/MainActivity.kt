package com.seno.game.ui.main

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.seno.game.R
import com.seno.game.extensions.restartApp
import com.seno.game.extensions.startActivity
import com.seno.game.manager.AccountManager
import com.seno.game.data.model.SavedGameInfo
import com.seno.game.prefs.PrefsManager
import com.seno.game.theme.AppTheme
import com.seno.game.ui.common.RestartDialog
import com.seno.game.ui.main.home.HomeLoadingScreen
import com.seno.game.ui.splash.SplashActivity
import com.seno.game.util.SoundUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        if (!intent.getBooleanExtra("isSplashFinish", false)) {
            SplashActivity.start(context = this@MainActivity)
            finish()
        } else {
            setContent {
                AppTheme {
                    Surface(Modifier.fillMaxSize()) {
                        MainScreen()
                    }
                }
            }
        }
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(MainActivity::class.java) {
                putExtra("isSplashFinish", true)
            }
        }
    }
}

fun SavedGameInfo?.savedGameInfoToLocalDB() {
    this?.let {
        PrefsManager.apply {
            nickname = it.nickname
            profileUri = it.profileUri
            backgroundVolume = it.backgroundVolume
            effectVolume = it.effectVolume
            isVibrationOn = it.isVibrationOn
            isPushOn = it.isPushOn
            isShowAD = it.isShowAD
            diffPictureStage = it.diffPictureGameCurrentStage
            it.completeGameRound.split(",").forEach { round ->
                diffPictureCompleteGameRound = round
            }
            diffPictureHeartCount = it.diffPictureHeartCount
            diffPictureHeartChangedTime = it.diffPictureHeartChangedTime
        }
    }
}