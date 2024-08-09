package com.seno.game.extensions

//noinspection SuspiciousImport
import android.R
import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.widget.ContentFrameLayout
import com.google.android.material.snackbar.Snackbar


fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.toast(@StringRes messageResId: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, messageResId, duration).show()
}

fun Context.snackbar(message: String) {
    val rootView = (this as Activity).window.decorView.findViewById<ContentFrameLayout>(R.id.content)
    Snackbar
        .make(rootView, message, Snackbar.LENGTH_SHORT)
        .setAction(getString(com.seno.game.R.string.confirm)) {}
        .setTextColor(getColor(com.seno.game.R.color.white))
        .setActionTextColor(getColor(com.seno.game.R.color.white))
        .show()
}