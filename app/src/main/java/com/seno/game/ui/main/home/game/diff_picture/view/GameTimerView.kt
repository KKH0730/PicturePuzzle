package com.seno.game.ui.main.home.game.diff_picture.view

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.seno.game.R
import com.seno.game.databinding.LayoutTimerViewBinding
import com.seno.game.extensions.dpToPx
import com.seno.game.extensions.screenWidth
import com.seno.game.util.AnimationUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.*

class GameTimerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = LayoutTimerViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    var maxTime = 120 // 120초(2분)
    val decreasePerSecond = 1 // 1초씩 감소
    var penaltyValueSecond = 10 // 10초

    private var remainingTime = maxTime
    private var decreaseTime = MutableSharedFlow<Int>()

    private var timer: Timer? = null
    private var timerTask: TimerTask? = null

    var onTimerOver: (() -> Unit)? = null
    var onTimerChanged: ((Int) -> Unit)? = null
    var onStartWrongAnswerAnimation: ((decreasedTime: Int, prevTime: Int) -> Unit)? = null
    var onClickWrongAnswer: () -> Unit = {
        CoroutineScope(Dispatchers.IO).launch { decreaseTime.emit(penaltyValueSecond) }
    }

    private var animatorSet: AnimatorSet? = null

    init {
        initView()
        startObserve()
    }

    private fun initView() {
        binding.progressBar.apply {
            max = maxTime
            progress = maxTime
        }
    }

    private fun startObserve() {
        CoroutineScope(Dispatchers.Main).launch {
            decreaseTime.collect {
                val prevTime = remainingTime
                remainingTime -= it

                when (it) {
                    penaltyValueSecond -> {
                        if (remainingTime < 0) {
                            onStartWrongAnswerAnimation?.invoke(0, prevTime)
                        } else {
                            onStartWrongAnswerAnimation?.invoke(remainingTime, prevTime)
                        }
                    }
                    else -> {
                        if (remainingTime < 0) {
                            binding.progressBar.progress = 0
                            releaseTimer()
                            onTimerOver?.invoke()
                        } else {
                            binding.progressBar.progress = remainingTime
                        }
                    }
                }
                onTimerChanged?.invoke(remainingTime)
            }
        }
    }

    private fun setTimer() {
        timer = Timer()
        timerTask = object : TimerTask() {
            override fun run() {
                try {
                    CoroutineScope(Dispatchers.IO).launch { decreaseTime.emit(decreasePerSecond) }
                } catch (e: Exception) {
                    e.printStackTrace()
                    releaseTimer()
                }
            }
        }
    }

    fun stopTimer() {
        timer?.cancel()
    }

    fun timerStart() {
        setTimer()
        timer?.schedule(timerTask, 0, 1000)
    }

    fun timerRestart() {
        setTimer()
        timerStart()
    }

    fun resetTimer() {
        remainingTime = maxTime
    }

    @SuppressLint("Recycle")
    fun playPenaltyAnimation(
        rootView: ConstraintLayout,
        yPosition: Float,
        decreasedTime: Int,
        prevTime: Int,
    ) {
        val prevPosition = (prevTime.toFloat() / maxTime.toFloat()) * screenWidth.toFloat()
        val decreasedPosition = (decreasedTime.toFloat() / maxTime.toFloat()) * screenWidth.toFloat()

        val frameLayout = FrameLayout(context).apply {
            val params = LayoutParams((prevPosition - decreasedPosition).toInt(), 7.dpToPx())
            layoutParams = params
            setBackgroundResource(R.drawable.bg_timer_wrong_answer)
            x = decreasedPosition
            y = yPosition
        }.also { rootView.addView(it) }

        val animationListener = object: Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                if (remainingTime < 0) {
                    binding.progressBar.progress = 0
                    releaseTimer()
                    onTimerOver?.invoke()
                } else {
                    binding.progressBar.progress = remainingTime
                }
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                rootView.children.forEach {
                    if (it == frameLayout) {
                        rootView.removeView(it)
                    }
                }
            }
        }

        val fadeOutAnimator = ObjectAnimator
            .ofFloat(frameLayout, "alpha", 1f, 0f)
            .apply {
                duration = 600
                startDelay = 300
            }

        val translateYAnimator = ObjectAnimator
            .ofFloat(
                frameLayout,
                "translationY",
                yPosition,
                yPosition - 50f
            )
            .apply {
                duration = 600
                startDelay = 200
            }

        val rotateAnimator = ObjectAnimator.ofFloat(
            frameLayout,
            "rotation",
            13f, -13f, 13f, -13f, 13f, -13f, 0f
        ).apply {
            duration = 400
        }

        animatorSet = AnimatorSet().apply {
            playTogether(fadeOutAnimator, translateYAnimator, rotateAnimator)
        }.also {
            it.addListener(animationListener)
            it.start()
        }
    }

    private fun releaseTimer() {
        timer?.cancel()
        timer = null

        timerTask?.cancel()
        timerTask = null
    }

    fun release() {
        releaseTimer()
        AnimationUtils.stopAnimation(animatorSet)
    }
}