package com.seno.game.data.diff_picture

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Transaction
import com.google.firebase.storage.StorageReference
import com.seno.game.data.network.ApiConstants
import com.seno.game.di.coroutine.IoDispatcher
import com.seno.game.di.network.DiffDocRef
import com.seno.game.extensions.onResponseWithDefaultValue
import com.seno.game.extensions.onResponseWithNull
import com.seno.game.model.DiffPictureGame
import com.seno.game.model.Player
import com.seno.game.model.Result
import com.seno.game.model.SavedGameInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class DiffPictureImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val ref: StorageReference,
) : DiffPictureRepository {

    @DiffDocRef
    @Inject
    lateinit var diffGameDocRef: DocumentReference
    override suspend fun updateSavedGameInfo(
        uid: String,
        stage: Int,
        completeGameRound: String,
        heartCount: Int,
        heartChangedTime: Long
    ) {
        Timber.e("updateSavedGameInfo uid : $uid")
        val map = mutableMapOf<String, Any>(
            ApiConstants.FirestoreKey.DIFF_PICTURE_GAME_CURRENT_STATE to stage,
            ApiConstants.FirestoreKey.COMPLETE_GAME_ROUND to completeGameRound,
            ApiConstants.FirestoreKey.DIFF_PICTURE_GAME_HEART_COUNT to heartCount,
            ApiConstants.FirestoreKey.DIFF_PICTURE_GAME_HEART_CHANGE_TIME to heartChangedTime
        )
        kotlin.runCatching {
            db.collection(ApiConstants.Collection.PROFILE)
                .document(uid)
                .collection(ApiConstants.Collection.SAVE_GAME_INFO)
                .document(ApiConstants.Document.DIFF_PICTURE)
                .set(map)
                .addOnSuccessListener {
                    Timber.e("updateSavedGameInfo addOnSuccessListener")
                }
                .addOnFailureListener {
                    Timber.e("updateSavedGameInfo addOnFailureListener : ${it.message}")
                }

        }
    }

    override suspend fun getDiffPictures(): Result<List<Pair<Uri, Uri>>> {
        return withContext(Dispatchers.IO) {
            try {
                val uriPairList = arrayListOf<Pair<Uri, Uri>>()
                ref.child("halloween").list(100)
                    .addOnSuccessListener { listResult ->

                        // 이미지 두개 들어있는 폴더들
                        listResult.items.forEach { pictureFolderRef ->
                            var firstUri: Uri? = null
                            pictureFolderRef.listAll().addOnSuccessListener { pictureListResult ->
                                pictureListResult.items.forEach { pictureRef ->
                                    pictureRef.downloadUrl.addOnSuccessListener { uri ->
                                        if (!pictureRef.name.contains("copy")) {
                                            firstUri = uri
                                        } else {
                                            firstUri?.let {
                                                uriPairList.add(it to uri)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }.await()
                Result.Success(uriPairList)
            } catch (e: Exception) {
                Result.Error(exception = e)
            }
        }
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
                isReady = true
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
                result = Result.Success(DiffPictureGame(
                    date = date,
                    roomUid = roomUid,
                    playerList = arrayListOf(Player(uid = uid, nickName = nickName, isReady = true))
                ))
            }.addOnFailureListener {
                result = Result.Error(exception = it)
            }.await()
            result
        } catch (e: Exception) {
            Result.Error(exception = e)
        }
    }

    override suspend fun enterRoom(date: String, uid: String, roomUid: String, nickName: String): Result<DiffPictureGame>? {
        return try {
            var result: Result<DiffPictureGame>? = null
            var tempDiffPictureGame: DiffPictureGame ? = null
            val roomDocRef = diffGameDocRef.collection(date).document(roomUid)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(roomDocRef)
                val playerList = playerListMapper(playerList = (snapshot.get("playerList") as ArrayList<HashMap<String, Any>>)).toMutableList()
                playerList.add(Player(
                    uid = uid,
                    nickName = nickName,
                    isReady = false
                ))

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
                    if (uid == it.uid) {
                        it.isReady = !it.isReady
                    }
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
            var tempDiffPictureGame: DiffPictureGame ? = null
            val roomDocRef = diffGameDocRef.collection(date).document(roomUid)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(roomDocRef)

                val playerList = playerListMapper(uid = uid, playerList = (snapshot.get("playerList") as ArrayList<HashMap<String, Any>>)).toMutableList()
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
            isReady = (it["ready"] as Boolean)
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
                isReady = (hashMap["ready"] as Boolean)
            )
        )
    }
    return list
}