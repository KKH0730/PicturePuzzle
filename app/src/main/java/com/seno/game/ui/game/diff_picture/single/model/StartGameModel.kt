package com.seno.game.ui.game.diff_picture.single.model

import com.seno.game.ui.game.diff_picture.list.model.DPSingleGame

data class StartGameModel(
    val currentGameModel: DPSingleGame,
    val currentStagePosition: Int,
    val currentRoundPosition: Int,
    val finalRoundPosition: Int,
)