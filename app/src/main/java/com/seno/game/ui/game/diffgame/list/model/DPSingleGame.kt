package com.seno.game.ui.game.diffgame.list.model

data class DPSingleGame(
    val id: Int,
    val level: DPSingleGameLevel,
    var isComplete: Boolean = false
)