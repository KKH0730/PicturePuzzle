package com.seno.game.ui.game.diff_picture.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.seno.game.R
import com.seno.game.databinding.DialogGameCompleteBinding

class GameCompleteDialog @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = DialogGameCompleteBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    var onClickPositiveButton: (() -> Unit)? = null
    var onClickNegativeButton: (() -> Unit)? = null

    init {
        binding.apply {
            dialog = this@GameCompleteDialog
        }
        initView()
    }

    private fun initView() {
        setBackgroundColor(context.getColor(R.color.color_804D4C4C))
        visibility = View.GONE
    }

    fun clickBackground() {
        visibility = View.GONE
    }

    fun clickPositiveButton() {
        onClickPositiveButton?.invoke()
    }

    fun clickNegativeButton() {
        dismiss()
        onClickNegativeButton?.invoke()
    }

    fun show() {
        visibility = View.VISIBLE
    }

    fun dismiss() {
        visibility = View.GONE
    }
}