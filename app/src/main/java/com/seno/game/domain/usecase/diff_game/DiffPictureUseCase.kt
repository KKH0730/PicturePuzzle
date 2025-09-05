package com.seno.game.domain.usecase.diff_game

import com.seno.game.data.diff_picture.DiffPictureRepository
import com.seno.game.data.network.model.MultiGameRoom
import com.seno.game.di.coroutine.IoDispatcher
import com.seno.game.model.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DiffPictureUseCase @Inject constructor(
    private val diffPictureRepository: DiffPictureRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun reqUpdateSavedGameInfo(
        uid: String,
        stage: Int,
        completeGameRound: String,
        heartCount: Int,
        heartChargedTime: Long
    ): Result<Unit> =
        diffPictureRepository.updateSavedGameInfo(
            uid = uid,
            stage = stage,
            completeGameRound = completeGameRound,
            heartCount = heartCount,
            heartChargedTime = heartChargedTime
        )


    suspend fun reqRoundDiffPicture(stage: String, round: String): Result<Pair<String, String>> {
        return diffPictureRepository.getRoundDiffPicture(stage = stage, round = round)
    }

    suspend fun checkWaitingRoom(path: String, hostUid: String): Result<Boolean> =
        diffPictureRepository.checkWaitingRoom(path = path, hostUid = hostUid)

    suspend fun createMultiGame(
        path: String,
        hostUid: String,
        hostNickname: String,
        hostProfileUri: String,
    ): Result<Boolean> {
        return diffPictureRepository.createMultiGame(
            path = path, hostUid = hostUid,
            hostNickname = hostNickname,
            hostProfileUri = hostProfileUri
        )
    }

    suspend fun updateMultiGamePlayer(
        path: String,
        uid: String,
        nickname: String,
        profileUri: String,
        isAdd: Boolean
    ): Result<Boolean> {
        return diffPictureRepository.updateMultiGamePlayer(
            path = path,
            uid = uid,
            nickname = nickname,
            profileUri = profileUri,
            isAdd = isAdd,
        )
    }

    suspend fun updateMultiGameStart(path: String): Result<Boolean> {
        return diffPictureRepository.updateMultiGameStart(path = path)
    }

    fun observeMultiGameSnapshot(path: String): Flow<MultiGameRoom>{
        return diffPictureRepository.observeMultiGameSnapshot(path = path)
    }
}