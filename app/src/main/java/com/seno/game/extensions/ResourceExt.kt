package com.seno.game.extensions

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.seno.game.App

@SuppressLint("UseCompatLoadingForDrawables")
fun getDrawable(@DrawableRes drawableRes: Int) = App.getInstance().getDrawable(drawableRes)

//fun getString(@StringRes stringRes: Int) = App.getInstance().getString(stringRes)
