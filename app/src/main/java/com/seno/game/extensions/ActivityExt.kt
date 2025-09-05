package com.seno.game.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import androidx.annotation.RawRes

@SuppressLint("ResourceType")
fun Activity.startActivityAnimation(
    isOpen: Boolean,
    @RawRes openEnterAnim: Int = 0,
    @RawRes openExitAnim: Int = 0,
    @RawRes closeEnterAnim: Int = 0,
    @RawRes closeExitAnim: Int = 0,
) {
    if (isOpen) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            this.overrideActivityTransition(Activity.OVERRIDE_TRANSITION_OPEN, openEnterAnim, openExitAnim)
        } else {
            overridePendingTransition(openEnterAnim, openExitAnim)
        }
    } else {
        // Activity 닫을 때
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            this.overrideActivityTransition(Activity.OVERRIDE_TRANSITION_CLOSE, closeEnterAnim, closeExitAnim)
        } else {
            overridePendingTransition(closeEnterAnim, closeExitAnim)
        }
    }
}