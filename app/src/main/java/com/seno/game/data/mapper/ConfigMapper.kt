package com.seno.game.data.mapper

import com.google.firebase.firestore.DocumentSnapshot
import com.seno.game.data.Mapper
import com.seno.game.data.network.ApiConstants
import com.seno.game.manager.AccountManager
import com.seno.game.model.response.UserInfoResponse
import com.seno.game.prefs.PrefsManager
import javax.inject.Inject

class ConfigMapper @Inject constructor(): Mapper<DocumentSnapshot, UserInfoResponse> {
    override fun fromRemote(model: DocumentSnapshot): UserInfoResponse {
        return with(model) {
            UserInfoResponse(
                getString(ApiConstants.UserInfo.UID) ?: AccountManager.firebaseUid ?: "",
                getString(ApiConstants.UserInfo.NICKNAME) ?: PrefsManager.nickname,
                getString(ApiConstants.UserInfo.PLATFORM) ?: PrefsManager.platform,
                getString(ApiConstants.UserInfo.PROFILE_URI) ?: PrefsManager.profileUri,
                getString(ApiConstants.UserInfo.BACKGROUND_VOLUME)?.toFloat() ?: PrefsManager.backgroundVolume,
                getString(ApiConstants.UserInfo.EFFECT_VOLUME)?.toFloat() ?: PrefsManager.effectVolume,
                getBoolean(ApiConstants.UserInfo.IS_VIBRATION_ON) ?: PrefsManager.isVibrationOn,
                getBoolean(ApiConstants.UserInfo.IS_PUSH_ON) ?: PrefsManager.isPushOn,
                getBoolean(ApiConstants.UserInfo.IS_SHOW_AD) ?: PrefsManager.isShowAD
            )
        }
    }
}