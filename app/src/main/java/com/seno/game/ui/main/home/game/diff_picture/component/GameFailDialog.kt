package com.seno.game.ui.main.home.game.diff_picture.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.seno.game.R
import com.seno.game.databinding.DialogGameFailBinding

class GameFailDialog @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = DialogGameFailBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    var onClickGiveUp: (() -> Unit)? = null
    var onClickShowAD: (() -> Unit)? = null

    init {
        binding.apply {
            dialog = this@GameFailDialog
        }
        initView()
    }

    private fun initView() {
        setBackgroundColor(context.getColor(R.color.color_804D4C4C))
        visibility = View.GONE
    }
    fun clickGiveUpButton() {
        onClickGiveUp?.invoke()
    }

    fun clickShowAD() {
        onClickShowAD?.invoke()
    }

    fun show() {
        visibility = View.VISIBLE
    }

    fun dismiss() {
        visibility = View.GONE
    }
}