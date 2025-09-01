package com.seno.game.binding_adapter

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("onLongClick")
fun View.setOnLongClick(listener: (() -> Unit)?) {
    this.setOnLongClickListener {
        listener?.invoke()
        true
    }
}