package com.seno.game.data.diff_picture

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.seno.game.data.network.ApiConstants
import com.seno.game.data.network.model.MultiGameRoom
import com.seno.game.di.DiffPictureStorageRef
import com.seno.game.di.network.DiffDocRef
import com.seno.game.model.Player
import com.seno.game.model.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DiffPictureImpl @Inject constructor(
    private val db: FirebaseFirestore,
    @DiffPictureStorageRef private val ref: StorageReference,
) : DiffPictureRepository {

    @DiffDocRef
    @Inject
    lateinit var diffGameDocRef: DocumentReference

    override suspend fun updateSavedGameInfo(
        uid: String,
        stage: Int,
        completeGameRound: String,
        heartCount: Int,
        heartChargedTime: Long,
    ): Result<Unit> {
        return try {
            val updateSavedGameInfoTask = suspendCoroutine { continuation ->
                val map = mutableMapOf<String, Any>(
                    ApiConstants.FirestoreKey.DIFF_PICTURE_GAME_CURRENT_STATE to stage,
                    ApiConstants.FirestoreKey.COMPLETE_GAME_ROUND to completeGameRound,
                    ApiConstants.FirestoreKey.DIFF_PICTURE_GAME_HEART_COUNT to heartCount,
                    ApiConstants.FirestoreKey.DIFF_PICTURE_GAME_HEART_CHARGED_TIME to heartChargedTime
                )
                kotlin.runCatching {
                    db.collection(ApiConstants.Collection.PROFILE)
                        .document(uid)
                        .collection(ApiConstants.Collection.SAVE_GAME_INFO)
                        .document(ApiConstants.Document.DIFF_PICTURE)
                        .update(map)
                        .addOnCompleteListener { task -> continuation.resume(task) }
                        .addOnSuccessListener {
                            Timber.e("updateSavedGameInfo addOnSuccessListener")
                        }
                        .addOnFailureListener {
                            Timber.e("updateSavedGameInfo addOnFailureListener : ${it.message}")
                        }
                }
            }
            if (updateSavedGameInfoTask.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(updateSavedGameInfoTask.exception)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.e(e)
            Result.Error(e)
        }
    }

    override suspend fun getRoundDiffPicture(stage: String, round: String): Result<Pair<String, String>> {
        val today = LocalDate.now()
        val formattedDate = today.format(DateTimeFormatter.ofPattern("yyyyMM"))

        return withContext(Dispatchers.IO) {
            try {
                val originImage = ref.child(formattedDate).child("${formattedDate}_${stage}_${round}_1.png").downloadUrl.await()
                val otherImage = ref.child(formattedDate).child("${formattedDate}_${stage}_${round}_2.png").downloadUrl.await()
                Result.Success(originImage.toString() to otherImage.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                Timber.e(e)
                Result.Error(exception = e)
            }
        }
    }

    override suspend fun checkWaitingRoom(path: String, hostUid: String): Result<Boolean> {
        return try {
            val multiDocRef = diffGameDocRef.collection("multi").document(path).get().await()

            if (multiDocRef.exists()) {
                val uid = multiDocRef.get("hostUid") as? String? ?: ""
                if (uid == hostUid) return Result.Success(true)
            }

            Result.Success(false)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }

    override suspend fun createMultiGame(
        path: String,
        hostUid: String,
        hostNickname: String,
        hostProfileUri: String
    ): Result<Boolean> {
        val players = listOf(
            mapOf(
                "uid" to hostUid,
                "nickname" to hostNickname,
                "profileUri" to hostProfileUri
            )
        )

        val data = mapOf(
            "hostUid" to hostUid,
            "hostNickname" to hostNickname,
            "hostProfileUri" to hostProfileUri,
            "players" to players,
            "start" to false
        )

        return try {
            diffGameDocRef
                .collection("multi")
                .document(path)
                .set(data)
                .await()
            Result.Success(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(exception = e)
        }
    }

    override suspend fun updateMultiGamePlayer(path: String, uid: String, nickname: String, profileUri: String, isAdd: Boolean): Result<Boolean> {
        return try {
            val multiDocRef = diffGameDocRef.collection("multi").document(path)

            db.runTransaction { transaction ->
                val snapshot = transaction.get(multiDocRef)
                val playersFromSnapshot = (snapshot.get("players") as? List<*>)
                    ?.mapNotNull { it as? Map<*, *> }
                    ?.map { hashMap ->
                        Player(
                            uid = hashMap["uid"] as? String ?: "",
                            nickname = hashMap["nickname"] as? String ?: "",
                            profileUri = hashMap["profileUri"] as? String ?: ""
                        )
                    } ?: emptyList()

                val updateList = playersFromSnapshot.toMutableList()

                val existingIndex = updateList.indexOfFirst { it.uid == uid }
                if (isAdd) {
                    if (existingIndex != -1) {
                        updateList[existingIndex] = Player(uid, nickname, profileUri)
                    } else {
                        updateList.add(Player(uid, nickname, profileUri))
                    }
                } else {
                    if (existingIndex != -1) {
                        updateList.removeAt(existingIndex)
                    }
                }

                val players = updateList.map { player ->
                    mapOf(
                        "uid" to player.uid,
                        "nickname" to player.nickname,
                        "profileUri" to player.profileUri
                    )
                }

                transaction.update(multiDocRef, "players", players)
                snapshot
            }.await()
            Result.Success(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(exception = e)
        }
    }

    override suspend fun updateMultiGameStart(path: String): Result<Boolean> {
        return try {
            diffGameDocRef.collection("multi").document(path).update("start", true).await()

            Result.Success(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(exception = e)
        }
    }

    override fun observeMultiGameSnapshot(path: String): Flow<MultiGameRoom> =
        callbackFlow {
            val uniqueId = createUniqueRoomId(path = path)

            val registration = diffGameDocRef
                .collection("multi")
                .document(path)
                .addSnapshotListener { snapshot, error ->
                    try {
                        if (error != null) {
                            return@addSnapshotListener
                        }

                        if (snapshot != null && snapshot.exists()) {
                            val players = (snapshot.get("players") as? List<*>)
                                ?.mapNotNull { it as? Map<*, *> }
                                ?.map { hashMap ->
                                    Player(
                                        uid = hashMap["uid"] as? String ?: "",
                                        nickname = hashMap["nickname"] as? String ?: "",
                                        profileUri = hashMap["profileUri"] as? String ?: ""
                                    )
                                } ?: emptyList()

                            if (players.isNotEmpty()) {
                                trySend(
                                    MultiGameRoom(
                                        uniqueId = uniqueId,
                                        start =  snapshot.getBoolean("start") ?: false,
                                        players = players
                                    )
                                )
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            awaitClose { registration.remove() }
        }

    suspend fun createUniqueRoomId(
        path: String
    ): String {
        val colRef = diffGameDocRef.collection("unique_room_id")
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"

        return db.runTransaction { transaction ->
            var id: String
            var exists: Boolean

            do {
                id = (1..6)
                    .map { chars.random() }
                    .joinToString("")

                val docRef = colRef.document(id)
                exists = transaction.get(docRef).exists()
            } while (exists)

            // 방 문서 생성
            val docRef = colRef.document(id)
            transaction.set(docRef, mapOf("path" to path))
            id
        }.await()
    }
}