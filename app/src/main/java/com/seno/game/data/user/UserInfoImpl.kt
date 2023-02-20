package com.seno.game.data.user

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.seno.game.data.network.ApiConstants
import com.seno.game.model.SavedGameInfo
import com.seno.game.model.response.UserInfoResponse
import com.seno.game.prefs.PrefsManager
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class UserInfoImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val ref: StorageReference,
    private val userInfoMapper: UserInfoMapper,
    private val savedGameInfoMapper: SavedGameInfoMapper,
) : UserInfoRepository {
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
        }.getOrDefault(SavedGameInfo())
    }

    override suspend fun updateBackgroundVolume(uid: String, volume: String): String {
        return kotlin.runCatching {
            db.collection(ApiConstants.Collection.PROFILE)
                .document(uid)
                .update("backgroundVolume", volume)
                .await()
            volume
        }.onFailure {
            it.printStackTrace()
            Timber.e(it)
        }.getOrDefault(PrefsManager.backgroundVolume.toString())
    }

    override suspend fun updateEffectVolume(uid: String, volume: String): String {
        return kotlin.runCatching {
            db.collection(ApiConstants.Collection.PROFILE)
                .document(uid)
                .update("effectVolume", volume)
                .await()
            volume
        }.onFailure {
            it.printStackTrace()
            Timber.e(it)
        }.getOrDefault(PrefsManager.effectVolume.toString())
    }
}