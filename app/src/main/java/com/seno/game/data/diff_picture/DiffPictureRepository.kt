package com.seno.game.data.diff_picture

import com.seno.game.model.DiffPictureGame
import com.seno.game.model.Result
import com.seno.game.ui.main.home.game.diff_picture.multi.model.MultiGameProfileInfo
import kotlinx.coroutines.flow.Flow

interface DiffPictureRepository {
    suspend fun updateSavedGameInfo(
        uid: String,
        stage: Int,
        completeGameRound: String,
        heartCount: Int,
        heartChargedTime: Long
    ): Result<Unit>

    suspend fun getRoundDiffPicture(stage: String, round: String): Result<Pair<String, String>>

    suspend fun createMultiGame(
        path: String,
        hostUid: String,
        hostNickName: String,
        hostProfileUri: String,
        guestUid: String,
        guestNickName: String,
        guestProfileUri: String
    ): Result<Boolean>

    suspend fun updateMultiGame(path: String): Result<Boolean>

    suspend fun observeMultiGameSnapshot(path: String): Flow<Result<Pair<Boolean, MultiGameProfileInfo>>>

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