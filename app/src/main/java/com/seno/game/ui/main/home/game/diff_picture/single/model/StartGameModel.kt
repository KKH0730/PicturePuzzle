package com.seno.game.ui.main.home.game.diff_picture.single.model

import com.seno.game.data.model.DPSingleGame

data class StartGameModel(
    val currentGameModel: DPSingleGame,
    val currentStagePosition: Int,
    val currentRoundPosition: Int,
    val finalRoundPosition: Int,
)