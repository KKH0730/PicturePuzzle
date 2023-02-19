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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.seno.game.R
import com.seno.game.extensions.checkNetworkConnectivityForComposable
import com.seno.game.extensions.restartApp
import com.seno.game.extensions.startActivity
import com.seno.game.manager.AccountManager
import com.seno.game.model.SavedGameInfo
import com.seno.game.prefs.PrefsManager
import com.seno.game.theme.AppTheme
import com.seno.game.ui.common.RestartDialog
import com.seno.game.ui.main.home.HomeLoadingScreen
import com.seno.game.ui.splash.SplashActivity
import com.seno.game.util.MusicPlayUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        printHashKey()


        if (!intent.getBooleanExtra("isSplashFinish", false)) {
            SplashActivity.start(context = this@MainActivity)
            finish()
        } else {
            setContent {
                AppTheme {
                    Surface(Modifier.fillMaxSize()) {
                        var savedGameInfo by remember { mutableStateOf<SavedGameInfo?>(null) }
                        var isNetworkError by remember { mutableStateOf(false) }
                        var isAuthentication by remember { mutableStateOf(false) }


                        if (AccountManager.isUser) {
                            isAuthentication = true
                        } else {
                            reqAuthentication { isAuthentication = it }
                        }

                        if (isAuthentication && savedGameInfo == null) {
                            AccountManager.firebaseUid?.let {
                                mainViewModel.getSavedGameInfo(uid = it)
                            } ?: run {
                                isNetworkError = true
                            }
                        }

                        MainUI(
                            isAuthentication = isAuthentication,
                            savedGameInfo = savedGameInfo,
                            isNetworkError = isNetworkError
                        )

                        startObserve(
                            onCallbackSavedGameInfo = { savedGameInfo = it },
                            onCallbackNetworkError = { isNetworkError = it }
                        )
                    }
                }
            }
        }
    }

    private fun startObserve(
        onCallbackSavedGameInfo: (SavedGameInfo) -> Unit,
        onCallbackNetworkError: (Boolean) -> Unit
    ) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { mainViewModel.savedGameInfoToLocalDB.collect(onCallbackSavedGameInfo::invoke)}
                launch { mainViewModel.showNetworkErrorEvent.collect(onCallbackNetworkError::invoke)}
            }
        }
    }

    @Composable
    private fun MainUI(
        isAuthentication: Boolean,
        savedGameInfo: SavedGameInfo?,
        isNetworkError: Boolean
    ) {
        if (savedGameInfo == null) {
            HomeLoadingScreen()
            return
        }

        if (isNetworkError) {
            RestartDialog(
                title = getString(R.string.network_error_title),
                content = getString(R.string.network_error),
                confirmText = getString(R.string.alert_dialog_restart),
                onClickConfirm = { this@MainActivity.restartApp() }
            )
        } else {
            // 저장된 게임 데이터 Load
            savedGameInfoToLocalDB(savedGameInfo = savedGameInfo)

            HomeLoadingScreen()
            if (isAuthentication) {
                if (checkNetworkConnectivityForComposable()) {
                    MusicPlayUtil.startBackgroundSound(context = this@MainActivity)
                    MainScreen()
                } else {
                    RestartDialog(
                        title = getString(R.string.network_error_title),
                        content = getString(R.string.network_error),
                        confirmText = getString(R.string.alert_dialog_restart),
                        onClickConfirm = { this@MainActivity.restartApp() }
                    )
                }
            }
        }
    }

    private fun savedGameInfoToLocalDB(savedGameInfo: SavedGameInfo?) {
        savedGameInfo?.let {
            PrefsManager.apply {
                diffPictureStage = it.diffPictureGameCurrentStage
                diffPictureCompleteGameRound = it.completeGameRound
            }
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
            val info: PackageInfo =
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey: String = String(Base64.encode(md.digest(), 0))
                Timber.e("Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Timber.e("Hash Key: ${e.message}")
        } catch (e: Exception) {
            Timber.e("Hash Key: ${e.message}")
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

@Composable
fun ComponentActivity.LifecycleEventListener(event: (Lifecycle.Event) -> Unit) {
    val eventHandler by rememberUpdatedState(newValue = event)
    val lifecycle = this@LifecycleEventListener.lifecycle
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            eventHandler(event)
        }

        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}
