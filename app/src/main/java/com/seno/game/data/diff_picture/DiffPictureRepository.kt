package com.seno.game.data.diff_picture

import com.seno.game.data.network.model.MultiGameRoom
import com.seno.game.model.Player
import com.seno.game.model.Result
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

    suspend fun checkWaitingRoom(path: String, hostUid: String): Result<Boolean>

    suspend fun createMultiGame(
        path: String,
        hostUid: String,
        hostNickname: String,
        hostProfileUri: String
    ): Result<Boolean>

    suspend fun updateMultiGamePlayer(
        path: String,
        uid: String,
        nickname: String,
        profileUri: String,
        isAdd: Boolean
    ): Result<Boolean>

    suspend fun updateMultiGameStart(path: String): Result<Boolean>

    fun observeMultiGameSnapshot(path: String): Flow<MultiGameRoom>
}