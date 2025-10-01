package com.seno.game.data.config

import com.google.firebase.firestore.FirebaseFirestore
import com.seno.game.data.mapper.ConfigMapper
import com.seno.game.data.mapper.DiffPictureSavedGameInfoMapper
import com.seno.game.data.network.ApiConstants
import com.seno.game.extensions.getTodayDate
import com.seno.game.model.Result
import com.seno.game.model.SavedGameInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ConfigImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val configMapper: ConfigMapper,
    private val diffPictureSavedGameInfoMapper: DiffPictureSavedGameInfoMapper
): ConfigRepository {
    override suspend fun getSavedGameInfo(uid: String): SavedGameInfo {
        val userInfoTask = suspendCoroutine { continuation ->
            db.collection(ApiConstants.Collection.PROFILE)
                .document(uid)
                .get()
                .addOnCompleteListener { task -> continuation.resume(task) }
        }

        if (!userInfoTask.isSuccessful || !userInfoTask.result.exists()) {
            return SavedGameInfo()
        }

        val diffPictureGameInfoTask = suspendCoroutine { continuation ->
            db.collection(ApiConstants.Collection.PROFILE)
                .document(uid)
                .collection(ApiConstants.Collection.SAVE_GAME_INFO)
                .document(ApiConstants.Document.DIFF_PICTURE)
                .get()
                .addOnCompleteListener { task -> continuation.resume(task) }
        }

        if (!diffPictureGameInfoTask.isSuccessful || !diffPictureGameInfoTask.result.exists()) {
            return SavedGameInfo()
        }

        return diffPictureSavedGameInfoMapper.fromRemote(
            param1 = diffPictureGameInfoTask.result,
            param2 = configMapper.fromRemote(model = userInfoTask.result)
        )
    }

    override suspend fun resetAndGetSavedGameInfo(uid: String, currentTimeMillis: Long): Flow<Result<SavedGameInfo>> =
        flow {
            try {
                val userInfoSnapshot = db.collection(ApiConstants.Collection.PROFILE)
                    .document(uid)
                    .get()
                    .await()

                if (!userInfoSnapshot.exists()) {
                    emit(Result.Success(SavedGameInfo())) // 기본값 반환
                    return@flow
                }

                val diffPictureDocRef = db.collection(ApiConstants.Collection.PROFILE)
                    .document(uid)
                    .collection(ApiConstants.Collection.SAVE_GAME_INFO)
                    .document(ApiConstants.Document.DIFF_PICTURE)

                db.runTransaction { transaction ->
                    val diffSnapshot = transaction.get(diffPictureDocRef)

                    val defaultSavedGameInfoMap = mapOf(
                        ApiConstants.FirestoreKey.DIFF_PICTURE_GAME_CURRENT_STATE to 0,
                        ApiConstants.FirestoreKey.COMPLETE_GAME_ROUND to "",
                        ApiConstants.FirestoreKey.DIFF_PICTURE_GAME_HEART_COUNT to 5,
                        ApiConstants.FirestoreKey.DIFF_PICTURE_GAME_HEART_CHARGED_TIME to currentTimeMillis,
                        ApiConstants.FirestoreKey.RECENT_SINGLE_PLAY_DATE to getTodayDate()
                    )

                    if (diffSnapshot.exists()) {
                        transaction.update(diffPictureDocRef, defaultSavedGameInfoMap)
                    } else {
                        transaction.set(diffPictureDocRef, defaultSavedGameInfoMap)
                    }

                    diffSnapshot
                }.await()

                val latestSnapshot = diffPictureDocRef.get().await()
                val userInfo = ConfigMapper().fromRemote(userInfoSnapshot)
                val savedGameInfo = diffPictureSavedGameInfoMapper.fromRemote(latestSnapshot, userInfo)

                emit(Result.Success(savedGameInfo))
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Result.Error(e))
            }
        }

    override suspend fun updateBackgroundVolume(uid: String, volume: String): Flow<Result<Float>> = flow {
        val updateBackgroundVolumeTask = suspendCoroutine { continuation ->
            db.collection(ApiConstants.Collection.PROFILE)
                .document(uid)
                .update("backgroundVolume", volume)
                .addOnCompleteListener { task -> continuation.resume(task) }
        }
        emit(
            if (updateBackgroundVolumeTask.isSuccessful) {
                Result.Success(volume.toFloat())
            } else {
                Result.Error(updateBackgroundVolumeTask.exception?.cause)
            }
        )
    }

    override suspend fun updateEffectVolume(uid: String, volume: String): Flow<Result<Float>> =
        flow {
            val updateEffectVolumeTask = suspendCoroutine { continuation ->
                db.collection(ApiConstants.Collection.PROFILE)
                    .document(uid)
                    .update("effectVolume", volume)
                    .addOnCompleteListener { task -> continuation.resume(task) }
            }

            emit(
                if (updateEffectVolumeTask.isSuccessful) {
                    Result.Success(volume.toFloat())
                } else {
                    Result.Error(updateEffectVolumeTask.exception?.cause)
                }
            )
        }

    override suspend fun updateVibrationOnOff(uid: String, isVibrationOn: Boolean): Flow<Result<Boolean>> =
        flow {
            val updateVibrationOnOffTask = suspendCoroutine { continuation ->
                db.collection(ApiConstants.Collection.PROFILE)
                    .document(uid)
                    .update("isVibrationOn", isVibrationOn)
                    .addOnCompleteListener { task -> continuation.resume(task) }
            }

            emit(
                if (updateVibrationOnOffTask.isSuccessful) {
                    Result.Success(isVibrationOn)
                } else {
                    Result.Error(updateVibrationOnOffTask.exception?.cause)
                }
            )
        }

    override suspend fun updatePushOnOff(uid: String, isPushOn: Boolean): Flow<Result<Boolean>> =
        flow {
            val updatePushOnOffTask = suspendCoroutine { continuation ->
                db.collection(ApiConstants.Collection.PROFILE)
                    .document(uid)
                    .update("isPushOn", isPushOn)
                    .addOnCompleteListener { task -> continuation.resume(task) }
            }

            emit(
                if (updatePushOnOffTask.isSuccessful) {
                    Result.Success(isPushOn)
                } else {
                    Result.Error(updatePushOnOffTask.exception?.cause)
                }
            )
        }

    override suspend fun updateADOnOff(uid: String, isShowAD: Boolean): Flow<Result<Boolean>> =
        flow {
            val updateADOnOffTask = suspendCoroutine { continuation ->
                db.collection(ApiConstants.Collection.PROFILE)
                    .document(uid)
                    .update("isShowAD", isShowAD)
                    .addOnCompleteListener { task -> continuation.resume(task) }
            }

            emit(
                if (updateADOnOffTask.isSuccessful) {
                    Result.Success(isShowAD)
                } else {
                    Result.Error(updateADOnOffTask.exception?.cause)
                }
            )
        }

    override suspend fun updateNickname(uid: String, nickname: String): Flow<Result<String>> =
        flow {
            val updateNicknameTask = suspendCoroutine { continuation ->
                db.collection(ApiConstants.Collection.PROFILE)
                    .document(uid)
                    .update("nickname", nickname)
                    .addOnCompleteListener { task -> continuation.resume(task) }
            }

            emit(
                if (updateNicknameTask.isSuccessful) {
                    Result.Success(nickname)
                } else {
                    Result.Error(updateNicknameTask.exception?.cause)
                }
            )
        }
}
