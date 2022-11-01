package com.seno.game.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.seno.game.databinding.LoadingViewAllReadyBinding

class AllReadyLoadingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = LoadingViewAllReadyBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

        initView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        setOnTouchListener { _, _ -> true }
    }
}