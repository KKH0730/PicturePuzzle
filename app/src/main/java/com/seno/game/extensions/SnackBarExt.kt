package com.seno.game.extensions

import android.view.View
import android.widget.FrameLayout
import com.google.android.material.snackbar.Snackbar

fun View.showSnackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).apply {
        val params = (view.layoutParams as FrameLayout.LayoutParams).apply {
            setMargins(10.dpToPx(), 0, 10.dpToPx(), 60.dpToPx())
        }
        view.layoutParams = params
    }.run { show() }
}