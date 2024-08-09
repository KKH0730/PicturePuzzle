package com.seno.game.data.network

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseRequest {

    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val firebaseMessaging: FirebaseMessaging = FirebaseMessaging.getInstance()

    val messagingToken: Task<String>
        get() = firebaseMessaging.token

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    suspend fun createUserWithEmailAndPassword(email: String, password: String) = suspendCoroutine { continuation ->
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                continuation.resume(task.result)
            } else {
                continuation.resume(task.exception)
            }
        }
    }

    suspend fun signInWithEmailAndPassword(email: String, password: String) = suspendCoroutine { continuation ->
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(task.result)
                } else {
                    continuation.resume(task.exception)
                }
            }
    }

    suspend fun signInWithCredential(credential: AuthCredential) = suspendCoroutine { continuation ->
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task -> continuation.resume(task) }
            .addOnFailureListener {
                Timber.e("exception : $it")
            }
    }

    fun reauthenticate(credential: AuthCredential): Task<Void>? =
        currentUser?.reauthenticate(credential)

    fun updatePassword(password: String): Task<Void>? =
        currentUser?.updatePassword(password)

    fun signInWithCustomToken(fCredentialToken: String): Task<AuthResult> =
        firebaseAuth.signInWithCustomToken(fCredentialToken)

    fun sendPasswordResetEmail(email: String): Task<Void> =
        firebaseAuth.sendPasswordResetEmail(email)

    fun signOut() = firebaseAuth.signOut()


    fun signInAnonymous(): Task<AuthResult> = firebaseAuth.signInAnonymously()

}