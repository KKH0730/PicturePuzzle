package com.seno.game.data.diff_picture

import android.net.Uri
import com.seno.game.model.DiffPictureGame
import com.seno.game.model.Result
import kotlinx.coroutines.flow.Flow

interface DiffPictureRepository {
    suspend fun updateSavedGameInfo(
        uid: String,
        stage: Int,
        completeGameRound: String,
        heartCount: Int,
        heartChangedTime: Long
    ): Flow<Result<Unit>>

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

    suspend fun readyGamePlay(
        date: String,
        uid: String,
        roomUid: String,
    ): Result<Unit>?

    suspend fun exitRoom(
        date: String,
        uid: String,
        roomUid: String,
    ): Result<DiffPictureGame>?
}