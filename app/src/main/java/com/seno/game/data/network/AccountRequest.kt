package com.seno.game.data.network

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AccountRequest {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

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
    }

    fun signOut() = firebaseAuth.signOut()


    fun signInAnonymous(): Task<AuthResult> = firebaseAuth.signInAnonymously()

}