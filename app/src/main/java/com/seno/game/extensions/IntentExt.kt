package com.seno.game.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat

fun Context.startActivity(action: String, builder: (Intent.() -> Unit)) {
    startActivity(Intent(action).apply(builder))
}

fun <T> Context.startActivity(
    activityClass: Class<T>,
    builder: (Intent.() -> Unit)
) {
    startActivity(Intent(this, activityClass).apply(builder))
}

fun <T> Context.startActivity(
    activityClass: Class<T>
) {
    startActivity(Intent(this, activityClass))
}

fun <T> Context.startActivity(
    activityClass: Class<T>,
    launcher: ActivityResultLauncher<Intent?>,
    builder: (Intent.() -> Unit)
) {
    launcher.launch(Intent(this, activityClass).apply(builder))
}

fun <T> Context.startActivity(
    activityClass: Class<T>,
    launcher: ActivityResultLauncher<Intent?>
) {
    launcher.launch(Intent(this, activityClass))
}

fun Activity.restartApp() {
    ActivityCompat.finishAffinity(this)
    val intent: Intent? = packageManager.getLaunchIntentForPackage(packageName)
    if (intent != null) {
        startActivity(intent)
    }
}