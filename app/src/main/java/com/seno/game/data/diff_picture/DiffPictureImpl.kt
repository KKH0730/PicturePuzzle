package com.seno.game.data.diff_picture

import android.net.Uri
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Transaction
import com.google.firebase.storage.StorageReference
import com.seno.game.di.coroutine.IoDispatcher
import com.seno.game.di.network.DiffDocRef
import com.seno.game.model.DiffPictureGame
import com.seno.game.model.Player
import com.seno.game.model.Result
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

    override suspend fun getDiffPictures(): Result<List<Pair<Uri, Uri>>> {
        return withContext(Dispatchers.IO) {
            try {
                val uriPairList = arrayListOf<Pair<Uri, Uri>>()
                ref.child("halloween").list(100)
                    .addOnSuccessListener { listResult ->

                        Timber.e("kkh 111 : ${listResult.items.size}")
                        // 이미지 두개 들어있는 폴더들
                        listResult.items.forEach { pictureFolderRef ->
                            var firstUri: Uri? = null
                            pictureFolderRef.listAll().addOnSuccessListener { pictureListResult ->
                                Timber.e("kkh 222 : ${pictureListResult.items.size}")
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
        val player = Player(
            uid = uid,
            nickName = nickName,
            isReady = false
        )

        val map = HashMap<String, Any>()
        map["roomUid"] = roomUid
        map["playerList"] = arrayListOf(player)

        return try {
            var result: Result<DiffPictureGame>? = null
            diffGameDocRef.collection(date).document(roomUid)
                .set(map)
                .addOnSuccessListener {
                    result = Result.Success(DiffPictureGame(
                        date = date,
                        roomUid = uid,
                        playerList = arrayListOf(Player(uid = uid, nickName = nickName, isReady = false))
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
            val roomDocRef = diffGameDocRef.collection(date).document(roomUid)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(roomDocRef)

                val playList = snapshot.get("playList") as ArrayList<Player>
                playList.add(Player(
                    uid = uid,
                    nickName = nickName,
                    isReady = false
                ))
                transaction.update(roomDocRef, "playerList", playList)
            }.addOnSuccessListener {
                result = Result.Success(data = diffPictureGameMapper(documentSnapshot = it.get(diffGameDocRef)))
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
        playerList = documentSnapshot.get("playerList") as ArrayList<Player>
    )
}