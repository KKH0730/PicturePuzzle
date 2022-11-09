package com.seno.game.extensions

import android.animation.Animator
import android.content.Context
import android.widget.LinearLayout
import com.airbnb.lottie.LottieAnimationView
import com.seno.game.R

fun Context.drawLottieAnswerCircle(
    x: Float,
    y: Float,
    speed: Float,
    maxProgress: Float,
    radius: Int,
    onAnimationStart: (animator: Animator?) -> Unit = {},
    onAnimationEnd: (animator: Animator?) -> Unit = {},
    onAnimationRepeat: (animator: Animator?) -> Unit = {},
    onAnimationCancel: (animator: Animator?) -> Unit = {},
): LottieAnimationView {
    return LottieAnimationView(this).apply {
        this.x = x
        this.y = y
        this.setAnimation(R.raw.lt_circle_mark)
        this.setMaxProgress(maxProgress)
        this.speed = speed
        this.layoutParams = LinearLayout.LayoutParams(radius, radius)
        this.addAnimatorListener(object: Animator.AnimatorListener {
            override fun onAnimationCancel(animator: Animator?) { onAnimationCancel.invoke(animator) }
            override fun onAnimationRepeat(animator: Animator?) { onAnimationRepeat.invoke(animator) }
            override fun onAnimationStart(animator: Animator?) { onAnimationStart.invoke(animator) }
            override fun onAnimationEnd(animator: Animator?) { onAnimationEnd.invoke(animator) }
        })
    }
}