package com.seno.game.domain.usecase.diff_game

import android.net.Uri
import com.seno.game.data.diff_picture.DiffPictureRepository
import com.seno.game.model.DiffPictureGame
import com.seno.game.model.Player
import com.seno.game.model.Result
import javax.inject.Inject

class DiffPictureUseCase @Inject constructor(
    private val diffPictureRepository: DiffPictureRepository
) {

    suspend fun reqDiffPictures(): Result<List<Pair<Uri, Uri>>>  {
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