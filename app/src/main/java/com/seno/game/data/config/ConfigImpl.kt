package com.seno.game.data.config

import com.google.firebase.firestore.FirebaseFirestore
import com.seno.game.data.network.ApiConstants
import com.seno.game.data.user.SavedGameInfoMapper
import com.seno.game.data.user.UserInfoMapper
import com.seno.game.extensions.onResponseWithDefaultValue
import com.seno.game.model.SavedGameInfo
import com.seno.game.prefs.PrefsManager
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ConfigImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val userInfoMapper: UserInfoMapper,
    private val savedGameInfoMapper: SavedGameInfoMapper,
): ConfigRepository {
    override suspend fun getSavedGameInfo(uid: String): SavedGameInfo {
        return kotlin.runCatching {
            val userInfoDocRef = db.collection(ApiConstants.Collection.PROFILE)
                .document(uid)
                .get()
                .await()

            val userInfoResponse = if (userInfoDocRef.exists()) {
                userInfoMapper.fromRemote(model = userInfoDocRef)
            } else {
                null
            }

            var savedGameInfo = SavedGameInfo()
            if (userInfoResponse != null) {
                val savedGameInfDocRef = db.collection(ApiConstants.Collection.PROFILE)
                    .document(uid)
                    .collection(ApiConstants.Collection.SAVE_GAME_INFO)
                    .document(ApiConstants.Document.DIFF_PICTURE)
                    .get()
                    .await()

                if (savedGameInfDocRef.exists()) {
                    savedGameInfo = savedGameInfoMapper.fromRemote(
                        param1 = savedGameInfDocRef,
                        param2 = userInfoResponse
                    )
                }
            }
            savedGameInfo
        }.onResponseWithDefaultValue(defaultValue = SavedGameInfo())
    }

    override suspend fun updateBackgroundVolume(uid: String, volume: String): String {
        return kotlin.runCatching {
            db.collection(ApiConstants.Collection.PROFILE)
                .document(uid)
                .update("backgroundVolume", volume)
                .await()
            volume
        }.onResponseWithDefaultValue(defaultValue = PrefsManager.backgroundVolume.toString())
    }

    override suspend fun updateEffectVolume(uid: String, volume: String): String {
        return kotlin.runCatching {
            db.collection(ApiConstants.Collection.PROFILE)
                .document(uid)
                .update("effectVolume", volume)
                .await()
            volume
        }.onResponseWithDefaultValue(defaultValue = PrefsManager.effectVolume.toString())
    }

    override suspend fun updateVibrationOnOff(uid: String, isVibrationOn: Boolean): Boolean {
        return kotlin.runCatching {
            db.collection(ApiConstants.Collection.PROFILE)
                .document(uid)
                .update("isVibrationOn", isVibrationOn)
                .await()
            isVibrationOn
        }.onResponseWithDefaultValue(defaultValue = PrefsManager.isVibrationOn)
    }

    override suspend fun updatePushOnOff(uid: String, isPushOn: Boolean): Boolean {
        return kotlin.runCatching {
            db.collection(ApiConstants.Collection.PROFILE)
                .document(uid)
                .update("isPushOn", isPushOn)
                .await()
            isPushOn
        }.onResponseWithDefaultValue(defaultValue = PrefsManager.isPushOn)
    }

    override suspend fun updateADOnOff(uid: String, isShowAD: Boolean): Boolean {
        return kotlin.runCatching {
            db.collection(ApiConstants.Collection.PROFILE)
                .document(uid)
                .update("isShowAD", isShowAD)
                .await()
            isShowAD
        }.onResponseWithDefaultValue(defaultValue = PrefsManager.isShowAD)
    }
}