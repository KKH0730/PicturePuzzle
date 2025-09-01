package com.seno.game.ui.view

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDialog
import com.seno.game.databinding.DialogNewMonthBinding
import androidx.core.graphics.drawable.toDrawable

class NewMonthAlertDialog(
    context: Context,
    private val onConfirm: (() -> Unit)? = null
) : AppCompatDialog(context) {
    private val binding: DialogNewMonthBinding = DialogNewMonthBinding.inflate(LayoutInflater.from(context))

    init {
        binding.dialog = this@NewMonthAlertDialog
        setContentView(binding.root)
        changeDisplay()
        setCancelable(false) // 외부 터치로 닫기 방지
    }

    private fun changeDisplay() {
        // 다이얼로그 외부 배경 투명
        window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        // optional: 패딩 제거
        window?.decorView?.setPadding(0, 0, 0, 0)
    }

    fun clickPositiveButton() { onConfirm?.invoke() }
}