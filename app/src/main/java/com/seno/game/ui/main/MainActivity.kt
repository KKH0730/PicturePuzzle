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
import com.seno.game.R
import com.seno.game.extensions.checkNetworkConnectivityForComposable
import com.seno.game.extensions.restartApp
import com.seno.game.extensions.startActivity
import com.seno.game.manager.AccountManager
import com.seno.game.theme.AppTheme
import com.seno.game.ui.common.RestartDialog
import com.seno.game.ui.game.diff_picture.list.DPSinglePlayListActivity
import com.seno.game.ui.main.home.HomeDummyScreen
import com.seno.game.ui.splash.SplashActivity
import com.seno.game.util.MusicPlayUtil
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        printHashKey()

        if (!intent.getBooleanExtra("isSplashFinish", false)) {
//            SplashActivity.start(context = this@MainActivity)
//            finish()
            DPSinglePlayListActivity.start(context = this)
        } else {
            setContent {
                AppTheme {
                    Surface(Modifier.fillMaxSize()) {
                        if (AccountManager.isUser) {
                            MainUI(isAuthentication = true)
                        } else {
                            var isAuthentication by remember { mutableStateOf(false) }
                            reqAuthentication { isAuthentication = it }
                            MainUI(isAuthentication = isAuthentication)
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun MainUI(isAuthentication: Boolean) {
        HomeDummyScreen()
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

    fun printHashKey() {
        try {
            val info: PackageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
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
