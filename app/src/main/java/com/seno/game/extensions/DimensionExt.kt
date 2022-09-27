package com.seno.game.extensions

import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.seno.game.App

fun Int.textDp(density: Density): TextUnit = with(density) {
    this@textDp.dp.toSp()
}

val Int.textDp: TextUnit
    @Composable get() = this.textDp(density = LocalDensity.current)

fun Int.toPixel(): Float {
    return this * (Resources.getSystem().displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun Int.dpToPx(): Int = toFloat().dpToPx()

fun Float.dpToPx(): Int =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        App.getInstance().resources.displayMetrics
    ).toInt()

fun Float.pxToDp(): Int  {
    var density =  App.getInstance().resources.displayMetrics.density

    when (density) {
        1.0f -> {
            density *= 4.0f
        }
        1.5f -> {
            density *= (8 / 3)
        }
        2.0f -> {
            density *= 2.0f
        }
    }
    return (this / density).toInt()
}