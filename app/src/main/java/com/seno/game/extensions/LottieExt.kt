package com.seno.game.extensions

import android.animation.Animator
import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RawRes
import com.airbnb.lottie.LottieAnimationView

fun Context.drawLottieAnswerCircle(
    centerX: Float,
    centerY: Float,
    imageContainerX: Int = 0,
    imageContainerY: Int = 0,
    @RawRes rawRes: Int,
    speed: Float,
    maxProgress: Float,
    radius: Int,
    onAnimationStart: (animator: Animator?) -> Unit = {},
    onAnimationEnd: (animator: Animator?, view: LottieAnimationView) -> Unit = { _, _ -> },
    onAnimationRepeat: (animator: Animator?) -> Unit = {},
    onAnimationCancel: (animator: Animator?) -> Unit = {},
): LottieAnimationView {
    return LottieAnimationView(this).apply {
        this.x = centerX + imageContainerX - radius
        this.y = centerY + imageContainerY - radius
        this.setAnimation(rawRes)
        this.setMaxProgress(maxProgress)
        this.speed = speed
        this.scaleType = ImageView.ScaleType.FIT_XY

//        this.layoutParams = LinearLayout.LayoutParams(radius, radius)
        this.layoutParams = LinearLayout.LayoutParams(radius * 2, radius * 2)
        this.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationCancel(animator: Animator) {
                onAnimationCancel.invoke(animator)
            }

            override fun onAnimationRepeat(animator: Animator) {
                onAnimationRepeat.invoke(animator)
            }

            override fun onAnimationStart(animator: Animator) {
                onAnimationStart.invoke(animator)
            }

            override fun onAnimationEnd(animator: Animator) {
                onAnimationEnd.invoke(animator,this@apply)
            }
        })
    }
}