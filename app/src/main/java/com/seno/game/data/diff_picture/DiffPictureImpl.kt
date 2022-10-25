package com.seno.game.data.diff_picture

import android.net.Uri
import com.google.firebase.storage.StorageReference
import com.seno.game.model.Result
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class DiffPictureImpl @Inject constructor(
    private val ref: StorageReference,
) : DiffPictureRepository {

    override suspend fun getDiffPictures(): Result<List<Pair<Uri, Uri>>> {
        return try {
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


//            ref.child("halloween").listAll()
//                .addOnSuccessListener { listResult ->
//                    Timber.e("kkh aaaaa : ${listResult.items.size}")
//                    listResult.items.map { ref ->
//                        ref.downloadUrl.addOnSuccessListener {
//                        }
//                    }
//                }.addOnFailureListener {
//                    Timber.e("kkh exception: ${it.message}")
//                }.await()
            Result.Success(uriPairList)
        } catch (e: Exception) {
            return Result.Error(exception = e)
        }
    }

//    override suspend fun getDiffPictures(): Result<List<Pair<Uri, Uri>>> {
//        return try {
//            val uriPairList = arrayListOf<Pair<Uri, Uri>>()
//            ref.child("halloween").storage.reference.listAll()
//                .addOnSuccessListener { listResult ->
//                    Timber.e("kkh 111 : ${listResult.items.size}")
//                    // 이미지 두개 들어있는 폴더들
//                    listResult.items.forEach { pictureFolderRef ->
//                        var firstUri: Uri? = null
//                        pictureFolderRef.listAll().addOnSuccessListener { pictureListResult ->
//                            Timber.e("kkh 222 : ${pictureListResult.items.size}")
//                            pictureListResult.items.forEach { pictureRef ->
//                                pictureRef.downloadUrl.addOnSuccessListener { uri ->
//                                    if (!pictureRef.name.contains("copy")) {
//                                        firstUri = uri
//                                    } else {
//                                        firstUri?.let {
//                                            uriPairList.add(it to uri)
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            Result.Success(uriPairList)
//        } catch (e: Exception) {
//            return Result.Error(exception = e)
//        }
//    }
}
