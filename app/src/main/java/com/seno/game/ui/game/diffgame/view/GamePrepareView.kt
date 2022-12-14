package com.seno.game.ui.game.diffgame.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import com.seno.game.R
import com.seno.game.extensions.getString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.*

class GamePrepareView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var tvCountDown: AppCompatTextView
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
        setTimer()
    }

    private fun startObserve() {
        CoroutineScope(Dispatchers.Main).launch {
            prepareCount.collect {
                if (it < 0) {
                    this@GamePrepareView.visibility = View.GONE
                    releaseTimer()
                    onGameStart?.invoke()
                } else if(it == 0) {
                    tvCountDown.text = getString(R.string.game_start)
                } else {
                    tvCountDown.text = it.toString()
                }
            }
        }
    }

    private fun setCountDownText() {
        tvCountDown = AppCompatTextView(context)
        tvCountDown.apply {
            textSize = 48f
            text = prepareCount.value.toString()
            setTextColor(context.getColor(R.color.white))
            typeface = Typeface.DEFAULT_BOLD
        }.also { textView ->
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER)
                .also {
                    this@GamePrepareView.addView(textView, it)
                }
        }
    }
    
    private fun setTimer() {
        timer = Timer()
        timerTask = object: TimerTask() {
            override fun run() {
                try {
                    prepareCount.value -= 1
                } catch (e: Exception) {
                    e.printStackTrace()
                    releaseTimer()
                }
            }
        }
        timer?.schedule(timerTask, 1500, 1000)
    }

    private fun releaseTimer() {
        timer?.cancel()
        timer = null

        timerTask?.cancel()
        timerTask = null
    }
}