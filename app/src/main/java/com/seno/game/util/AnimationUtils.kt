package com.seno.game.util

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AnimationSet

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

    fun startShuffleCoachMarAndHeartAnimation(
        bigCircle: View?,
        smallCircle: View?,
        tapImage: View?,
        heartTailImage: View?,
        keytalkContainer: View?,
        coachMarkStartAnimationListener: Animator.AnimatorListener,
        coachMarkEndAnimationListener: Animator.AnimatorListener,
        heartTailFadeInAnimationListener: Animator.AnimatorListener,
    ): AnimatorSet {
        val animatorSet = AnimatorSet()

        if (bigCircle != null && smallCircle != null && tapImage != null) {
            val bigCircleFadeIn = ObjectAnimator.ofFloat(
                bigCircle,
                "alpha",
                0f,
                0.4f
            ).apply { duration = 500 }

            val tapImageFadeIn = ObjectAnimator.ofFloat(
                tapImage,
                "alpha",
                0f,
                1f
            ).apply { duration = 500 }

            val smallCircleFadeInOut = ObjectAnimator.ofFloat(
                smallCircle,
                "alpha",
                0f,
                0.3f,
                0.6f,
                0f
            ).apply {
                duration = 500
                startDelay = 500
            }

            val bigCircleFadeOut = ObjectAnimator.ofFloat(
                bigCircle,
                "alpha",
                0.4f,
                0f
            ).apply {
                duration = 300
                startDelay = 1000
            }

            val tapImageFadeOut = ObjectAnimator.ofFloat(
                tapImage,
                "alpha",
                1f,
                0f
            ).apply {
                duration = 300
                startDelay = 1000
                addListener(coachMarkEndAnimationListener)
            }

            val keytalkAnimator = ObjectAnimator.ofFloat(
                keytalkContainer,
                "rotationX",
                90f,
                -15f,
                15f,
                0f
            ).apply {
                duration = 700
                startDelay = 1500
            }

            val keytalkFade = ObjectAnimator.ofFloat(
                keytalkContainer,
                "alpha",
                0.25f,
                0.5f,
                0.75f,
                1f
            ).apply {
                duration = 700
                startDelay = 1500
            }

            val heartTailImageFadeIn = ObjectAnimator.ofFloat(
                heartTailImage,
                "alpha",
                0f,
                1f
            ).apply {
                duration = 200
                startDelay = 1500
                addListener(heartTailFadeInAnimationListener)
            }

            val heartTailImageFadeOut = ObjectAnimator.ofFloat(
                heartTailImage,
                "alpha",
                1f,
                0f
            ).apply {
                duration = 200
                startDelay = 2350
            }

            animatorSet.run {
                playTogether(
                    bigCircleFadeIn,
                    tapImageFadeIn,
                    smallCircleFadeInOut,
                    bigCircleFadeOut,
                    tapImageFadeOut,
                    keytalkAnimator,
                    keytalkFade,
                    heartTailImageFadeIn,
                    heartTailImageFadeOut
                )
                addListener(coachMarkStartAnimationListener)
                start()
            }
        }

        return animatorSet
    }
}