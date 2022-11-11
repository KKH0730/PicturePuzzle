package com.seno.game.extensions

import android.animation.Animator
import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RawRes
import com.airbnb.lottie.LottieAnimationView

fun Context.drawLottieAnswerCircle(
    x: Float,
    y: Float,
    @RawRes rawRes: Int,
    speed: Float,
    maxProgress: Float,
    radius: Int,
    isWrongAnswer: Boolean,
    onAnimationStart: (animator: Animator?) -> Unit = {},
    onAnimationEnd: (animator: Animator?) -> Unit = {},
    onAnimationRepeat: (animator: Animator?) -> Unit = {},
    onAnimationCancel: (animator: Animator?) -> Unit = {},
): LottieAnimationView {
    return LottieAnimationView(this).apply {
        this.x = if (isWrongAnswer) {
            x - (radius / 2)
        } else {
            x
        }
        this.y = if (isWrongAnswer) {
            y - (radius / 2)
        } else {
            y
        }
        this.scaleX = 2.5f
        this.scaleY = 2.5f
        this.setAnimation(rawRes)
        this.setMaxProgress(maxProgress)
        this.speed = speed
        this.scaleType = ImageView.ScaleType.FIT_XY
        this.layoutParams = LinearLayout.LayoutParams(radius, radius)
        this.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationCancel(animator: Animator?) {
                onAnimationCancel.invoke(animator)
            }

            override fun onAnimationRepeat(animator: Animator?) {
                onAnimationRepeat.invoke(animator)
            }

            override fun onAnimationStart(animator: Animator?) {
                onAnimationStart.invoke(animator)
            }

            override fun onAnimationEnd(animator: Animator?) {
                onAnimationEnd.invoke(animator)
            }
        })
    }
}