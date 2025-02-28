package com.seno.game.domain.usecase.diff_game

import android.net.Uri
import com.seno.game.data.network.repository.DiffPictureRepository
import com.seno.game.di.coroutine.IoDispatcher
import com.seno.game.extensions.catchError
import com.seno.game.data.model.DiffPictureGame
import com.seno.game.data.model.Result
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
        heartChangedTime: Long
    ): Flow<Result<Unit>> =
        diffPictureRepository.updateSavedGameInfo(
            uid = uid,
            stage = stage,
            completeGameRound = completeGameRound,
            heartCount = heartCount,
            heartChangedTime = heartChangedTime
        ).catchError(dispatcher = ioDispatcher)


    suspend fun reqDiffPictures(): Result<List<Pair<Uri, Uri>>> {
        return diffPictureRepository.getDiffPictures()
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