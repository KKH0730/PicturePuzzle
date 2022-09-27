package com.seno.game.extensions

import androidx.annotation.StringRes
import com.seno.game.App

fun getString(@StringRes res: Int) = App.getInstance().getString(res)