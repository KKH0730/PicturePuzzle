package com.seno.game.ui.main.home.game.diff_picture.list.model

import androidx.annotation.DrawableRes

data class DPSingleGame(
    val id: Int,
    val stage: Int,
    var isComplete: Boolean = false,
    var isSelect: Boolean = false
)