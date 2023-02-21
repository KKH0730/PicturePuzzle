package com.seno.game.util

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AnimationSet
import androidx.core.view.children

object AnimationUtils {
    fun stopAnimation(animatorSet: AnimatorSet?) {
        animatorSet?.let {
            it.childAnimations.forEach { animator ->
                stopAnimation(animator)
            }

            it.run {
                cancel()
                removeAllListeners()
                end()
            }
        }
    }

    fun stopAnimation(animator: Animator?) {
        animator?.let {
            it.run {
                cancel()
                removeAllListeners()
                end()
            }
        }
    }

    fun startShakeAnimation(
        targetView: View?,
        remainingTime: Int
    ): AnimatorSet {
        val rotateAnimator = ObjectAnimator.ofFloat(
            targetView,
            "rotation",
            5f,
            -5f,
            5f,
            -5f,
            0f
        ).apply {
            repeatCount = ValueAnimator.INFINITE
            duration = if (remainingTime <= 10) {
                100
            } else if (remainingTime <= 20) {
                200
            } else  {
                300
            }
        }

        val animatorSet = AnimatorSet().apply {
            play(rotateAnimator)
        }.also {
            it.start()
        }

        return animatorSet
    }
}