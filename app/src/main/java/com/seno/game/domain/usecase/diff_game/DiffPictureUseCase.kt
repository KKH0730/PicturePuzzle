package com.seno.game.domain.usecase.diff_game

import com.seno.game.data.diff_picture.DiffPictureRepository
import com.seno.game.di.coroutine.IoDispatcher
import com.seno.game.model.DiffPictureGame
import com.seno.game.model.Result
import com.seno.game.ui.main.home.game.diff_picture.multi.model.MultiGameProfileInfo
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

    suspend fun createMultiGame(
        path: String,
        hostUid: String,
        hostNickName: String,
        hostProfileUri: String,
        guestUid: String,
        guestNickName: String,
        guestProfileUri: String
    ): Result<Boolean> {
        return diffPictureRepository.createMultiGame(
            path = path, hostUid = hostUid,
            hostNickName = hostNickName,
            hostProfileUri = hostProfileUri,
            guestUid = guestUid,
            guestNickName = guestNickName,
            guestProfileUri = guestProfileUri
        )
    }

    suspend fun updateMultiGame(path: String): Result<Boolean> {
        return diffPictureRepository.updateMultiGame(path = path)
    }

    suspend fun observeMultiGameSnapshot(path: String): Flow<Result<Pair<Boolean, MultiGameProfileInfo>>>{
        return diffPictureRepository.observeMultiGameSnapshot(path = path)
    }

    suspend fun createRoom(
        date: String,
        uid: String,
        roomUid: String,
        nickName: String,
    ): Result<DiffPictureGame>? {
        return diffPictureRepository.createRoom(date = date, uid = uid, roomUid = roomUid, nickName = nickName)
    }

    suspend fun enterRoom(
        date: String,
        uid: String,
        roomUid: String,
        nickName: String,
    ): Result<DiffPictureGame>? = diffPictureRepository.enterRoom(date = date, uid = uid, roomUid = roomUid, nickName = nickName)

    suspend fun readyGamePlay(
        date: String,
        uid: String,
        roomUid: String,
    ): Result<Unit>? = diffPictureRepository.readyGamePlay(date = date, uid = uid, roomUid = roomUid)

    suspend fun exitRoom(
        date: String,
        uid: String,
        roomUid: String,
    ): Result<DiffPictureGame>? = diffPictureRepository.exitRoom(date = date, uid = uid, roomUid = roomUid)
}