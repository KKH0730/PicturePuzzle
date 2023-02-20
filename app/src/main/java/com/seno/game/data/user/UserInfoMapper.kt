package com.seno.game.data.user

import com.google.firebase.firestore.DocumentSnapshot
import com.seno.game.data.Mapper
import com.seno.game.data.MapperType2
import com.seno.game.data.network.ApiConstants
import com.seno.game.model.SavedGameInfo
import com.seno.game.model.response.UserInfoResponse
import com.seno.game.prefs.PrefsManager
import javax.inject.Inject

class UserInfoMapper @Inject constructor(): Mapper<DocumentSnapshot, UserInfoResponse> {
    override fun fromRemote(model: DocumentSnapshot): UserInfoResponse {
        return with(model) {
            UserInfoResponse(
                getString("uid") ?: "",
                getString("nickname") ?: "",
                getString("platform") ?: "",
                getString("profileUri") ?: "",
                getString("backgroundVolume")?.toFloat() ?: PrefsManager.backgroundVolume,
                getString("effectVolume")?.toFloat() ?: PrefsManager.effectVolume,
                getBoolean("isVibrationOn") ?: PrefsManager.isVibrationOn,
                getBoolean("isPushOn") ?: PrefsManager.isPushOn,
                getBoolean("isShowAD") ?: PrefsManager.isShowAD
            )
        }
    }
}

class SavedGameInfoMapper @Inject constructor(): MapperType2<DocumentSnapshot, UserInfoResponse, SavedGameInfo> {
    override fun fromRemote(param1: DocumentSnapshot, param2: UserInfoResponse): SavedGameInfo {
        return SavedGameInfo(
            uid = param2.uid,
            nickname = param2.nickname,
            platform = param2.platform,
            profileUri = param2.profileUri,
            backgroundVolume = param2.backgroundVolume,
            effectVolume = param2.effectVolume,
            isVibrationOn = param2.isVibrationOn,
            isPushOn = param2.isPushOn,
            isShowAD = param2.isShowAD,
            diffPictureGameCurrentStage = param1.getLong(ApiConstants.FirestoreKey.DIFF_PICTURE_GAME_CURRENT_STATE)?.toInt() ?: 0,
            completeGameRound = param1.getString(ApiConstants.FirestoreKey.COMPLETE_GAME_ROUND) ?: ""
        )
    }

}