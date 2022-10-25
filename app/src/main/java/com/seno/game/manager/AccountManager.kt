package com.seno.game.manager

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.seno.game.App
import com.seno.game.data.network.FirebaseRequest
import com.seno.game.extensions.isNotNullAndNotEmpty
import com.seno.game.extensions.toast

object AccountManager {
    private var firebaseRequest: FirebaseRequest = FirebaseRequest()
    var firebaseUid: String = ""
    var firebaseAuthToken: String = ""

    val currentUser: FirebaseUser?
        get() = firebaseRequest.currentUser

    val isSignedIn: Boolean
        get() = currentUser != null

    val isSignedOut: Boolean
        get() = currentUser == null

    fun signInWithCredential(
        credential: AuthCredential,
        onSignInSucceed: () -> Unit,
        onSigInFailed: () -> Unit
    ) {
        firebaseRequest.signInWithCredential(credential = credential)
            .addOnFailureListener { e ->
                e.message?.let { message -> App.getInstance().applicationContext.toast(message) }
                onSigInFailed.invoke()
            }
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    return@addOnCompleteListener
                }
                onSignInSucceed.invoke()
            }
    }

}

interface OnSocialSignInCallbackListener {
    fun signInWithCredential(idToken: String?)
    fun onError(e: Exception?)
}

interface OnSignOutCallbackListener {
    fun onSignOutFacebook()
    fun onSignOutGoogle()
    fun onSignOutEmail()
}