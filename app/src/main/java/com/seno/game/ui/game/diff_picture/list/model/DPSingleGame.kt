package com.seno.game.ui.game.diff_picture.list.model

import androidx.annotation.DrawableRes

data class DPSingleGame(
    val id: Int,
    var isComplete: Boolean = false,
    var isSelect: Boolean = false
)