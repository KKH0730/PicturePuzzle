package com.seno.game.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat

fun Context.safeStartActivity(intent: Intent) {
    try {
        startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.safeStartActivity(action: String, builder: (Intent.() -> Unit)) {
    try {
        startActivity(Intent(action).apply(builder))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun <T> Context.safeStartActivity(
    activityClass: Class<T>,
    builder: (Intent.() -> Unit)
) {
    try {
        startActivity(Intent(this, activityClass).apply(builder))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun <T> Context.safeStartActivity(
    activityClass: Class<T>
) {
    try {
        startActivity(Intent(this, activityClass))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun <T> Context.safeStartActivity(
    activityClass: Class<T>,
    launcher: ActivityResultLauncher<Intent>,
    options: ActivityOptionsCompat? = null,
    builder: (Intent.() -> Unit)
) {
    try {
        launcher.launch(Intent(this, activityClass).apply(builder), options)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun <T> Context.safeStartActivity(
    activityClass: Class<T>,
    launcher: ActivityResultLauncher<Intent>,
    options: ActivityOptionsCompat? = null
) {
    try {
        launcher.launch(Intent(this, activityClass), options)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Activity.restartApp() {
    ActivityCompat.finishAffinity(this)
    val intent: Intent? = packageManager.getLaunchIntentForPackage(packageName)
    if (intent != null) {
        startActivity(intent)
    }
}