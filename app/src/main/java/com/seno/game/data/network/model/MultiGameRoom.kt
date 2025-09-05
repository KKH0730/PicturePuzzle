package com.seno.game.data.network.model

import com.seno.game.model.Player

data class MultiGameRoom(
    val uniqueId: String,
    val start: Boolean,
    val players: List<Player>,
)