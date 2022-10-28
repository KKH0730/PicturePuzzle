package com.seno.game.data.diff_picture

import android.net.Uri
import com.google.firebase.firestore.DocumentReference
import com.seno.game.model.DiffPictureGame
import com.seno.game.model.Player
import com.seno.game.model.Result

interface DiffPictureRepository {
    suspend fun getDiffPictures(): Result<List<Pair<Uri, Uri>>>

    suspend fun createRoom(
        date: String,
        uid: String,
        roomUid: String,
        nickName: String,
    ): Result<DiffPictureGame>?

    suspend fun enterRoom(
        date: String,
        uid: String,
        roomUid: String,
        nickName: String,
    ): Result<DiffPictureGame>?
}