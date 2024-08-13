package com.seno.game.data.config

import com.google.firebase.firestore.FirebaseFirestore
import com.seno.game.data.mapper.ConfigMapper
import com.seno.game.data.mapper.DiffPictureSavedGameInfoMapper
import com.seno.game.data.network.ApiConstants
import com.seno.game.model.Result
import com.seno.game.model.SavedGameInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ConfigImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val configMapper: ConfigMapper,
    private val diffPictureSavedGameInfoMapper: DiffPictureSavedGameInfoMapper,
): ConfigRepository {
    override suspend fun getSavedGameInfo(uid: String): Flow<Result<SavedGameInfo>> =
        flow {
            val userInfoTask = suspendCoroutine { continuation ->
                db.collection(ApiConstants.Collection.PROFILE)
                    .document(uid)
                    .get()
                    .addOnCompleteListener { task -> continuation.resume(task) }
            }

            if (!userInfoTask.isSuccessful || !userInfoTask.result.exists()) {
                emit(Result.Success(SavedGameInfo()))
                return@flow
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
                emit(Result.Success(SavedGameInfo()))
                return@flow
            }

            diffPictureSavedGameInfoMapper.fromRemote(
                param1 = diffPictureGameInfoTask.result,
                param2 = configMapper.fromRemote(model = userInfoTask.result)
            ).run {
                emit(Result.Success(this))
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
