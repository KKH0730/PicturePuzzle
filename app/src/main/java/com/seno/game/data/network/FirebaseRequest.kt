package com.seno.game.data.network

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import timber.log.Timber

class FirebaseRequest {

    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val firebaseMessaging: FirebaseMessaging = FirebaseMessaging.getInstance()

    val messagingToken: Task<String>
        get() = firebaseMessaging.token

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    fun createUserWithEmailAndPassword(email: String, password: String): Task<AuthResult> =
        firebaseAuth.createUserWithEmailAndPassword(email, password)

    fun signInWithEmailAndPassword(email: String, password: String): Task<AuthResult> =
        firebaseAuth.signInWithEmailAndPassword(email, password)

    fun signInWithCredential(credential: AuthCredential): Task<AuthResult> =
        firebaseAuth.signInWithCredential(credential)

    fun reauthenticate(credential: AuthCredential): Task<Void> =
        currentUser!!.reauthenticate(credential)

    fun updatePassword(password: String): Task<Void> =
        currentUser!!.updatePassword(password)

    fun signInWithCustomToken(fCredentialToken: String): Task<AuthResult> =
        firebaseAuth.signInWithCustomToken(fCredentialToken)

    fun sendPasswordResetEmail(email: String): Task<Void> =
        firebaseAuth.sendPasswordResetEmail(email)

    fun signOut() {
        firebaseAuth.signOut()
    }

    fun signInAnonymous(): Task<AuthResult> = firebaseAuth.signInAnonymously()

}