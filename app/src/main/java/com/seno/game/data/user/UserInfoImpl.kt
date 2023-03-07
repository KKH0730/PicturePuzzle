package com.seno.game.data.user

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import javax.inject.Inject

class UserInfoImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val ref: StorageReference,
) : UserInfoRepository {

}