package com.seno.game.data.model

data class DiffPictureGame(
    val date: String?,
    val roomUid: String?, // room document 이름
    var playerList: ArrayList<Player>
)