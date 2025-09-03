package com.seno.game.data.diff_picture

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.seno.game.data.network.ApiConstants
import com.seno.game.di.DiffPictureStorageRef
import com.seno.game.di.network.DiffDocRef
import com.seno.game.model.DiffPictureGame
import com.seno.game.model.Player
import com.seno.game.model.Result
import com.seno.game.ui.main.home.game.diff_picture.multi.model.MultiGameProfileInfo
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

    override suspend fun createMultiGame(
        path: String,
        hostUid: String,
        hostNickName: String,
        hostProfileUri: String,
        guestUid: String,
        guestNickName: String,
        guestProfileUri: String
    ): Result<Boolean> {
        val data = mapOf(
            "hostUid" to hostUid,
            "hostNickName" to hostNickName,
            "hostProfileUri" to hostProfileUri,
            "guestUid" to guestUid,
            "guestNickName" to guestNickName,
            "guestProfileUri" to guestProfileUri,
            "ready" to false
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

    override suspend fun updateMultiGame(path: String): Result<Boolean> {
        return try {
            diffGameDocRef
                .collection("multi")
                .document(path)
                .update("ready", true)
                .await()

            Result.Success(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(exception = e)
        }
    }

    override suspend fun observeMultiGameSnapshot(path: String): Flow<Result<Pair<Boolean ,MultiGameProfileInfo>>> =
        callbackFlow {
            val registration = diffGameDocRef
                .collection("multi")
                .document(path)
                .addSnapshotListener { snapshot, error ->
                    try {
                        if (error != null) {
                            trySend(Result.Error(error))
                            return@addSnapshotListener
                        }

                        if (snapshot != null && snapshot.exists()) {
                            val data = MultiGameProfileInfo(
                                path = path,
                                hostUid = snapshot.getString("hostUid") ?: "",
                                hostNickName = snapshot.getString("hostNickName") ?: "",
                                hostProfileUri = snapshot.getString("hostProfileUri") ?: "",
                                guestUid = snapshot.getString("guestUid") ?: "",
                                guestNickName = snapshot.getString("guestNickName") ?: "",
                                guestProfileUri = snapshot.getString("guestProfileUri") ?: "",
                            )

                            if (data.isNotEmpty()) {
                                val ready = snapshot.getBoolean("ready") ?: false
                                trySend(Result.Success(ready to data))
                            } else {
                                trySend(Result.Error(error))
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        trySend(Result.Error(error))
                    }
                }
            awaitClose { registration.remove() }
        }

    override suspend fun createRoom(
        date: String, //20220730
        uid: String,
        roomUid: String,
        nickName: String,
    ): Result<DiffPictureGame>? {
        return try {
            val player = Player(
                uid = uid,
                nickName = nickName,
                profileUri = ""
            )

            val map = HashMap<String, Any>()
            map["date"] = date
            map["roomUid"] = roomUid
            map["playerList"] = arrayListOf(player)

            var result: Result<DiffPictureGame>? = null
            val roomDocRef = diffGameDocRef.collection(date).document(roomUid)
            db.runBatch { batch ->
                batch.set(roomDocRef, map)
            }.addOnSuccessListener {
                result = Result.Success(
                    DiffPictureGame(
                        date = date,
                        roomUid = roomUid,
                        playerList = arrayListOf(
                            Player(uid = uid, nickName = nickName, profileUri = "")
                        )
                    )
                )
            }.addOnFailureListener {
                result = Result.Error(exception = it)
            }.await()
            result
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.e(e)
            Result.Error(exception = e)
        }
    }

    override suspend fun enterRoom(date: String, uid: String, roomUid: String, nickName: String): Result<DiffPictureGame>? {
        return try {
            var result: Result<DiffPictureGame>? = null
            var tempDiffPictureGame: DiffPictureGame? = null
            val roomDocRef = diffGameDocRef.collection(date).document(roomUid)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(roomDocRef)
                val playerList = playerListMapper(playerList = (snapshot.get("playerList") as ArrayList<HashMap<String, Any>>)).toMutableList()
                playerList.add(
                    Player(
                        uid = uid,
                        nickName = nickName,
                        profileUri = ""
                    )
                )

                val diffPictureGame = diffPictureGameMapper(documentSnapshot = snapshot).apply {
                    this.playerList = playerList as ArrayList<Player>
                }
                tempDiffPictureGame = diffPictureGame

                transaction.update(roomDocRef, "playerList", playerList)
            }.addOnSuccessListener {
                tempDiffPictureGame?.let {
                    result = Result.Success(data = it)
                } ?: run {
                    result = Result.Error(exception = NullPointerException())
                }
            }.addOnFailureListener {
                result = Result.Error(exception = it)
            }.await()
            result
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.e(e)
            Result.Error(exception = e)
        }
    }

    override suspend fun readyGamePlay(
        date: String,
        uid: String,
        roomUid: String,
    ): Result<Unit>? {
        return try {
            var result: Result<Unit>? = null
            val roomDocRef = diffGameDocRef.collection(date).document(roomUid)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(roomDocRef)

                val playerList = playerListMapper(playerList = (snapshot.get("playerList") as ArrayList<HashMap<String, Any>>))
                playerList.forEach {

                }

                transaction.update(roomDocRef, "playerList", playerList)
            }.addOnSuccessListener {
                result = Result.Success(data = Unit)
            }.addOnFailureListener {
                result = Result.Error(exception = it)
            }.await()
            result
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.e(e)
            Result.Error(exception = e)
        }
    }

    override suspend fun exitRoom(
        date: String,
        uid: String,
        roomUid: String,
    ): Result<DiffPictureGame>? {
        return try {
            var result: Result<DiffPictureGame>? = null
            var tempDiffPictureGame: DiffPictureGame? = null
            val roomDocRef = diffGameDocRef.collection(date).document(roomUid)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(roomDocRef)

                val playerList =
                    playerListMapper(uid = uid, playerList = (snapshot.get("playerList") as ArrayList<HashMap<String, Any>>)).toMutableList()
                val diffPictureGame = diffPictureGameMapper(documentSnapshot = snapshot).apply {
                    this.playerList = playerList as ArrayList<Player>
                }
                tempDiffPictureGame = diffPictureGame

                transaction.update(roomDocRef, "playerList", playerList)
            }.addOnSuccessListener {
                tempDiffPictureGame?.let {
                    result = Result.Success(data = it)
                } ?: run {
                    result = Result.Error(exception = NullPointerException())
                }
            }.addOnFailureListener {
                result = Result.Error(exception = it)
            }.await()

            result
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.e(e)
            Result.Error(exception = e)
        }
    }
}

private fun diffPictureGameMapper(documentSnapshot: DocumentSnapshot): DiffPictureGame {
    return DiffPictureGame(
        date = documentSnapshot.getString("date"),
        roomUid = documentSnapshot.getString("roomUid"),
        playerList = (playerListMapper(playerList = documentSnapshot.get("playerList") as ArrayList<HashMap<String, Any>>)) as ArrayList<Player>
    )
}

private fun playerListMapper(playerList: ArrayList<HashMap<String, Any>>): List<Player> {
    return playerList.map {
        Player(
            uid = (it["uid"] as String),
            nickName = (it["nickName"] as String),
            profileUri = "",
        )
    }
}

private fun playerListMapper(uid: String, playerList: ArrayList<HashMap<String, Any>>): List<Player> {
    val list = ArrayList<Player>()
    playerList.forEach { hashMap ->
        if (hashMap["uid"] == uid) {
            return@forEach
        }

        list.add(
            Player(
                uid = (hashMap["uid"] as String),
                nickName = (hashMap["nickName"] as String),
                profileUri = "",
            )
        )
    }
    return list
}