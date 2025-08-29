package com.seno.game.ui.main.home.game.diff_picture.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.seno.game.R
import com.seno.game.databinding.DialogGameCompleteBinding
import com.seno.game.databinding.DialogGameFinishBinding

class GameFinishDialog @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = DialogGameFinishBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    var onClickPositiveButton: (() -> Unit)? = null
    var onClickNegativeButton: (() -> Unit)? = null

    init {
        binding.apply {
            dialog = this@GameFinishDialog
        }
        initView()
    }

    private fun initView() {
        setBackgroundColor(context.getColor(R.color.color_804D4C4C))
        visibility = GONE
    }

    fun clickPositiveButton() {
        onClickPositiveButton?.invoke()
    }

    fun show() {
        visibility = VISIBLE
    }

    fun dismiss() {
        visibility = GONE
    }
}