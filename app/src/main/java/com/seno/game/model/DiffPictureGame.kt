package com.seno.game.model

data class DiffPictureGame(
    val date: String?,
    val roomUid: String?, // room document 이름
    val playerList: ArrayList<Player>
)