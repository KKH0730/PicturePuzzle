package com.seno.game.data.model

data class DPSingleGame(
    val id: Int,
    val stage: Int,
    var isComplete: Boolean = false,
    var isSelect: Boolean = false
)