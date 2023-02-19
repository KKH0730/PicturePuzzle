package com.seno.game.data.user

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.seno.game.data.network.ApiConstants
import com.seno.game.model.SavedGameInfo
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class UserInfoImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val ref: StorageReference,
) : UserInfoRepository {
    override suspend fun getSavedGameInfo(uid: String): SavedGameInfo {
        return kotlin.runCatching {
            var savedGameInfo = SavedGameInfo(
                diffPictureGameCurrentStage = 0,
                completeGameRound = ""
            )
            db.collection(ApiConstants.Collection.PROFILE)
                .document(uid)
                .collection(ApiConstants.Collection.SAVE_GAME_INFO)
                .document(ApiConstants.Document.DIFF_PICTURE)
                .get()
                .addOnSuccessListener {
                    savedGameInfo = SavedGameInfo(
                        diffPictureGameCurrentStage = it.getLong(ApiConstants.FirestoreKey.DIFF_PICTURE_GAME_CURRENT_STATE)?.toInt() ?: 0,
                        completeGameRound = it.getString(ApiConstants.FirestoreKey.COMPLETE_GAME_ROUND) ?: ""
                    )
                }.addOnFailureListener { Timber.e(it) }
                .await()
            savedGameInfo
        }.getOrDefault(
            SavedGameInfo(
                diffPictureGameCurrentStage = 0,
                completeGameRound = ""
            )
        )
    }
}