package com.seno.game.ui.main

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.firebase.auth.FirebaseAuth
import com.seno.game.R
import com.seno.game.extensions.checkNetworkConnectivityForComposable
import com.seno.game.extensions.restartApp
import com.seno.game.theme.AppTheme
import com.seno.game.ui.common.RestartDialog
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
        setContent {
            AppTheme {
                Surface(Modifier.fillMaxSize()) {
                    if (checkNetworkConnectivityForComposable()) {
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
//        setAuthentication {
//            if (it) {
//                setContent {
//                    AppTheme {
//                        Surface(Modifier.fillMaxSize()) {
//                            if (checkNetworkConnectivityForComposable()) {
//                                MainScreen()
//                            } else {
//                                RestartDialog(
//                                    title = getString(R.string.network_error_title),
//                                    content = getString(R.string.network_error),
//                                    confirmText = getString(R.string.alert_dialog_restart),
//                                    onClickConfirm = { this@MainActivity.restartApp() }
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }

    private fun setAuthentication(callback: (Boolean) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            callback(true)
        } else {
            auth.signInAnonymously()
                .addOnSuccessListener { callback(true) }
                .addOnFailureListener { callback(false) }
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
