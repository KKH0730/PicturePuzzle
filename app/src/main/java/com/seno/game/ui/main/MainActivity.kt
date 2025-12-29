package com.seno.game.ui.main

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.seno.game.App
import com.seno.game.R
import com.seno.game.extensions.clearDiskCache
import com.seno.game.extensions.clearMemoryCache
import com.seno.game.extensions.createRandomNickname
import com.seno.game.extensions.getImageDate
import com.seno.game.extensions.getTodayDate
import com.seno.game.extensions.parseImageDate
import com.seno.game.extensions.restartApp
import com.seno.game.extensions.safeStartActivity
import com.seno.game.manager.AccountManager
import com.seno.game.model.SavedGameInfo
import com.seno.game.prefs.PrefsManager
import com.seno.game.ui.component.CommonAlertDialog
import com.seno.game.ui.main.home.screen.HomeLoadingScreen
import com.seno.game.ui.main.screen.MainScreen
import com.seno.game.ui.main.screen.MainViewModel
import com.seno.game.util.MusicPlayUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createRandomNickname()

        enableEdgeToEdge()
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = false
        }

        setContent {
            Surface(Modifier.fillMaxSize()) {
                val savedGameInfo = mainViewModel.savedGameInfoToLocalDB.collectAsStateWithLifecycle(initialValue = null, minActiveState = Lifecycle.State.CREATED).value
                val isNetworkError = mainViewModel.showNetworkErrorEvent.collectAsStateWithLifecycle(initialValue = false, minActiveState = Lifecycle.State.CREATED).value

                if (!AccountManager.isUser) {
                    if (App.isServeMonthService && PrefsManager.recentSinglePlayDate.parseImageDate() != getImageDate()) {
                        PrefsManager.clearSinglePlayData(currentTimeMillis = System.currentTimeMillis())
                        PrefsManager.recentSinglePlayDate = getTodayDate()
                        clearMemoryCache()
                        clearDiskCache()
                    }

                    HomeLoadingScreen()
                    MainScreen()
                    return@Surface
                }

                if (AccountManager.isUser && savedGameInfo == null) {
                    mainViewModel.getSavedGameInfo(uid = AccountManager.firebaseUid)
                }

                if (isNetworkError) {
                    CommonAlertDialog(
                        title = getString(R.string.network_error_title),
                        content = getString(R.string.network_error),
                        confirmText = getString(R.string.alert_dialog_restart),
                        onClickConfirm = { this@MainActivity.restartApp() }
                    )
                } else {
                    if (savedGameInfo != null) {
                        // 저장된 게임 데이터 Load
                        savedGameInfo.savedGameInfoToLocalDB()

                        // MainScreen을 띄울 때, 화면이 깜빡임으로 인해 보기 안좋아 하단에 LoadingScreen을 띄워두어 깜빡임이 보이지 않도록 함
                        HomeLoadingScreen()
                        MainScreen()
                    } else {
                        HomeLoadingScreen()
                    }
                }
            }
        }
    }

    private fun createRandomNickname() {
        if (PrefsManager.nickname.isEmpty() && AccountManager.isUser || !AccountManager.isUser) {
            PrefsManager.nickname = resources.createRandomNickname()
        }
    }

    override fun onDestroy() {
        MusicPlayUtil.release(isBackgroundSound = true)
        super.onDestroy()
    }

    private fun reqAuthentication(callback: (Boolean) -> Unit) {
        if (AccountManager.isUser) {
            callback(true)
        } else {
            AccountManager.signInAnonymous(
                onSuccess = { callback(true) },
                onFail = { callback(false) }
            )
        }
    }

    private fun printHashKey() {
        try {
            val info: PackageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            info.signatures?.let {
                for (signature in it) {
                    val md: MessageDigest = MessageDigest.getInstance("SHA")
                    md.update(signature.toByteArray())
                    val hashKey: String = String(Base64.encode(md.digest(), 0))
                    Timber.e("Hash Key: $hashKey")
                }
            }

        } catch (e: NoSuchAlgorithmException) {
            Timber.e("Hash Key: ${e.message}")
        } catch (e: Exception) {
            Timber.e("Hash Key: ${e.message}")
        }
    }

    companion object {
        fun start(context: Context) {
            context.safeStartActivity(MainActivity::class.java) {
                putExtra("isSplashFinish", true)
            }
        }
    }
}

fun SavedGameInfo?.savedGameInfoToLocalDB() {
    this?.let {
        PrefsManager.apply {
            nickname = it.nickname
            platform = it.platform
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
            diffPictureHeartChargedTime = it.diffPictureHeartChargedTime
        }
    }
}