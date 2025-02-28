package com.seno.game.data.mapper

import com.google.firebase.firestore.DocumentSnapshot
import com.seno.game.data.model.DiffPictureGame
import com.seno.game.data.model.Player

fun diffPictureGameMapper(documentSnapshot: DocumentSnapshot): DiffPictureGame {
    return DiffPictureGame(
        date = documentSnapshot.getString("date"),
        roomUid = documentSnapshot.getString("roomUid"),
        playerList = (playerListMapper(playerList = documentSnapshot.get("playerList") as ArrayList<HashMap<String, Any>>)) as ArrayList<Player>
    )
}

fun playerListMapper(playerList: ArrayList<HashMap<String, Any>>): List<Player> {
    return playerList.map {
        Player(
            uid = (it["uid"] as String),
            nickName = (it["nickName"] as String),
            isReady = (it["ready"] as Boolean)
        )
    }
}

fun playerListMapper(uid: String, playerList: ArrayList<HashMap<String, Any>>): List<Player> {
    val list = ArrayList<Player>()
    playerList.forEach { hashMap ->
        if (hashMap["uid"] == uid) {
            return@forEach
        }

        list.add(
            Player(
                uid = (hashMap["uid"] as String),
                nickName = (hashMap["nickName"] as String),
                isReady = (hashMap["ready"] as Boolean)
            )
        )
    }
    return list
}