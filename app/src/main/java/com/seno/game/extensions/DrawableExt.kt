package com.seno.game.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.graphics.drawable.toBitmap
import com.seno.game.R

@SuppressLint("UseCompatLoadingForDrawables")
fun Context.drawAnswerCircle(radius: Float): ImageView {
    val drawable = getDrawable(R.drawable.img_circle_answer)
    val bitmap = drawable?.toBitmap()
    return ImageView(this).apply {
        setImageBitmap(bitmap)
        scaleType = ImageView.ScaleType.FIT_XY
        layoutParams = LinearLayout.LayoutParams(radius.toInt(), radius.toInt())
    }
}