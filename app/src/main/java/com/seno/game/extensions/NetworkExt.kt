package com.seno.game.extensions

import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat

/**
 * 네트워크 연결 상태 확인
 */
@Composable
fun Context.checkNetworkConnectivityForComposable(): Boolean {
    val connectivityManager = this.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
    return connectivityManager.activeNetwork != null
}

fun Context.checkNetworkConnectivity(): Boolean {
    val connectivityManager = this.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
    return connectivityManager.activeNetwork != null
}