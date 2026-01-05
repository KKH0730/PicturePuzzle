package com.seno.game.data.mapper

import com.google.firebase.firestore.DocumentSnapshot
import com.seno.game.data.MapperType2
import com.seno.game.data.network.ApiConstants
import com.seno.game.model.SavedGameInfo
import com.seno.game.model.response.UserInfoResponse
import com.seno.game.prefs.PrefsManager
import javax.inject.Inject

class DiffPictureSavedGameInfoMapper @Inject constructor(): MapperType2<DocumentSnapshot, UserInfoResponse, SavedGameInfo> {
    override fun fromRemote(param1: DocumentSnapshot, param2: UserInfoResponse): SavedGameInfo {

        val heartChargedTime = param1.getLong(ApiConstants.FirestoreKey.DIFF_PICTURE_GAME_HEART_CHARGED_TIME) ?: PrefsManager.diffPictureHeartChargedTime
        val localHeartChargedTime = PrefsManager.diffPictureHeartChargedTime
        val recentChargedTime = heartChargedTime.coerceAtMost(localHeartChargedTime)
        val recentHeartCount = if (recentChargedTime == localHeartChargedTime) {
            PrefsManager.diffPictureHeartCount
        } else {
            param1.getLong(ApiConstants.FirestoreKey.DIFF_PICTURE_GAME_HEART_COUNT)?.toInt() ?: 0
        }
        val recentStage = if (recentChargedTime == localHeartChargedTime) {
            param1.getLong(ApiConstants.FirestoreKey.DIFF_PICTURE_GAME_CURRENT_STATE)?.toInt() ?: 0
        } else {
            PrefsManager.diffPictureStage
        }
        val recentCompleteRound = if (recentChargedTime == localHeartChargedTime) {
            param1.getString(ApiConstants.FirestoreKey.COMPLETE_GAME_ROUND) ?: ""
        } else {
            PrefsManager.diffPictureCompleteGameRound
        }

        return SavedGameInfo(
            uid = param2.uid,
            email = param2.email,
            nickname = param2.nickname,
            platform = param2.platform,
            profileUri = param2.profileUri,
            backgroundVolume = param2.backgroundVolume,
            effectVolume = param2.effectVolume,
            isVibrationOn = param2.isVibrationOn,
            isPushOn = param2.isPushOn,
            isShowAD = param2.isShowAD,
            diffPictureGameCurrentStage = recentStage,
            completeGameRound = recentCompleteRound,
            diffPictureHeartCount = recentHeartCount,
            diffPictureHeartChargedTime = recentChargedTime,
            recentSinglePlayDate = param1.getString(ApiConstants.FirestoreKey.RECENT_SINGLE_PLAY_DATE) ?: PrefsManager.recentSinglePlayDate
        )
    }
}