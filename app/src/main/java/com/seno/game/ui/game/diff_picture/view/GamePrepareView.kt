package com.seno.game.ui.game.diff_picture.view

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.seno.game.R
import com.seno.game.extensions.dpToPx
import com.seno.game.extensions.getString
import com.seno.game.util.AnimationUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.*

@SuppressLint("Recycle", "CustomViewStyleable")
class GamePrepareView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var tvCountDown: AppCompatTextView
    private lateinit var tvStage: AppCompatTextView
    private lateinit var stageContainer: LinearLayoutCompat

    private var translateYAnimator: ObjectAnimator? = null
    private var animatorSet: AnimatorSet? = null
    private var prepareCount = MutableStateFlow(3)

    private var timer: Timer? = null
    private var timerTask: TimerTask? = null
    var onGameStart: (() -> Unit)? = null

    init {
        initView()
        startObserve()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        setOnTouchListener { _, _ -> true }
        setBackgroundColor(context.getColor(R.color.color_804D4C4C))
        setCountDownText()
    }

    private fun startObserve() {
        CoroutineScope(Dispatchers.Main).launch {
            prepareCount.collect { count ->
                if (count < 0) {
                    startTopMoveAndFadeOutAnimation()
                } else if (count == 0) {
                    tvCountDown.text = getString(R.string.game_prepare_start)
                } else {
                    tvCountDown.text = count.toString()
                }
            }
        }
    }

    private fun setCountDownText() {
        tvStage = AppCompatTextView(context).apply {
            setBackgroundResource(R.drawable.bg_28dp_rounding_2dp_stroke_fffef1)
            setPadding(25.dpToPx(), 7.dpToPx(), 25.dpToPx(), 7.dpToPx())
            setTextColor(context.getColor(R.color.color_fffef1))
            textSize = 24f
            typeface = Typeface.DEFAULT
            layoutParams = ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        }

        tvCountDown = AppCompatTextView(context).apply {
            textSize = 64f
            text = prepareCount.value.toString()
            setTextColor(context.getColor(R.color.color_fffef1))
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, 55.dpToPx(),0,0)
            layoutParams = ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        }

        stageContainer = LinearLayoutCompat(context).apply {
            orientation = LinearLayoutCompat.VERTICAL
            visibility = View.GONE
            setHorizontalGravity(Gravity.CENTER)
            layoutParams = LinearLayoutCompat.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
        }.also { stageContainer ->
            stageContainer.addView(tvStage)
            stageContainer.addView(tvCountDown)
        }

        this@GamePrepareView.addView(
            stageContainer,
            LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_HORIZONTAL
            )
        )
    }

    fun setStage(stage: String) {
        tvStage.text = stage
    }

    fun startMoveBottomAnimation() {
        translateYAnimator = ObjectAnimator
            .ofFloat(
                stageContainer,
                "translationY",
                -400f,
                181.dpToPx().toFloat()
            )
            .apply {
                startDelay = 500
                duration = 1000
                addListener(
                    object: Animator.AnimatorListener {
                        override fun onAnimationStart(p0: Animator?) {
                            stageContainer.visibility = View.VISIBLE
                        }
                        override fun onAnimationCancel(p0: Animator?) {}
                        override fun onAnimationRepeat(p0: Animator?) {}
                        override fun onAnimationEnd(p0: Animator?) {
                            startTimer()
                        }
                    }
                )
            }
        translateYAnimator?.start()
    }

    private fun startTopMoveAndFadeOutAnimation() {
        animatorSet = AnimatorSet()

        val translateYAnimator = ObjectAnimator
            .ofFloat(stageContainer, "translationY", 181.dpToPx().toFloat(), -581.dpToPx().toFloat())
            .apply {
                startDelay = 500
                duration = 700
                addListener(
                    object: Animator.AnimatorListener {
                        override fun onAnimationStart(p0: Animator?) {}
                        override fun onAnimationCancel(p0: Animator?) {}
                        override fun onAnimationRepeat(p0: Animator?) {}
                        override fun onAnimationEnd(p0: Animator?) {
                            stageContainer.visibility = View.GONE
                        }
                    }
                )
            }


        val fadeOutAnimator = ObjectAnimator
            .ofFloat(this@GamePrepareView, "alpha", 1f, 0f)
            .apply {
                startDelay = 800
                duration = 700
                addListener(
                    object: Animator.AnimatorListener {
                        override fun onAnimationStart(p0: Animator?) {}
                        override fun onAnimationCancel(p0: Animator?) {}
                        override fun onAnimationRepeat(p0: Animator?) {}
                        override fun onAnimationEnd(p0: Animator?) {
                            this@GamePrepareView.visibility = View.GONE
                            onGameStart?.invoke()
                            release()
                        }
                    }
                )
            }

        animatorSet?.run {
            playTogether(translateYAnimator, fadeOutAnimator)
            start()
        }
    }

    private fun startTimer() {
        timer = Timer()
        timerTask = object : TimerTask() {
            override fun run() {
                try {
                    prepareCount.value
                        ?.takeIf { prepareCount.value != -1 }
                        ?.run {
                            prepareCount.value -= 1
                        } ?: run {
                        releaseTimer()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    releaseTimer()
                }
            }
        }
        timer?.schedule(timerTask, 500, 1000)
    }

    private fun releaseTimer() {
        timer?.cancel()
        timer = null

        timerTask?.cancel()
        timerTask = null
    }

    fun release() {
        releaseTimer()
        AnimationUtils.stopAnimation(translateYAnimator)
        AnimationUtils.stopAnimation(animatorSet)
    }
}