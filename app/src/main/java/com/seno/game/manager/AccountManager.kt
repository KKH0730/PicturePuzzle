package com.seno.game.manager

import android.app.Activity
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.seno.game.App
import com.seno.game.R
import com.seno.game.data.network.FirebaseRequest
import com.seno.game.extensions.getString
import com.seno.game.extensions.isNotNullAndNotEmpty
import com.seno.game.extensions.toast
import timber.log.Timber

private const val LOGIN_TYPE_GOOGLE = "google.com"
private const val LOGIN_TYPE_FACEBOOK = "facebook.com"

object AccountManager {
    private var firebaseRequest: FirebaseRequest = FirebaseRequest()
    var firebaseAuthToken: String = ""

    val currentUser: FirebaseUser?
        get() = firebaseRequest.currentUser

    val firebaseUid: String?
        get() = firebaseRequest.currentUser?.uid

    val isSignedIn: Boolean
        get() = currentUser != null

    val isSignedOut: Boolean
        get() = currentUser == null

    val isUser: Boolean
        get() = currentUser == null

    private val authProviderId: String
        get() {
            FirebaseAuth.getInstance().currentUser?.providerData?.forEach {
                when (val providerId = it.providerId) {
                    "facebook.com", "google.com", "password" -> return providerId
                }
            }
            return ""
        }

    val displayName: String?
        get()  {
            Timber.e("kkh displayname : ${FirebaseAuth.getInstance().currentUser?.displayName}")
            return FirebaseAuth.getInstance().currentUser?.displayName
        }

    val authProviderName: String
        get() {
            var provider = ""
            FirebaseAuth.getInstance().currentUser?.providerData?.forEach {
                Timber.e("kkh providerId : ${it.providerId} size : ${FirebaseAuth.getInstance().currentUser?.providerData?.size}")
                when (it.providerId) {
                    "facebook.com" -> provider = getString(R.string.facebook)
                    "google.com" -> provider = getString(R.string.google)
                }
            }
            return provider
        }

    fun signInWithCredential(
        credential: AuthCredential,
        onSignInSucceed: () -> Unit,
        onSigInFailed: () -> Unit,
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

    fun startLogout(
        activity: Activity,
        isCompleteLogout: () -> Unit,
    ) {
        signOut(object : OnSignOutCallbackListener {
            override fun onSignOutFacebook() {
                FacebookAccountManager.logout()
                signOutFirebase(isCompleteLogout = isCompleteLogout)
            }

            override fun onSignOutGoogle() {
                GoogleAccountManager.logout(
                    activity = activity,
                    object : GoogleAccountManager.LogoutListener {
                    override fun onSuccessLogout() {
                        signOutFirebase(isCompleteLogout = isCompleteLogout)
                    }
                })
            }

            override fun onSignOutEmail() {
                signOutFirebase(isCompleteLogout = isCompleteLogout)
            }
        })
    }

    fun signOut(onSignOutCallbackListener: OnSignOutCallbackListener?) {
        if (onSignOutCallbackListener == null) {
            return
        }

        when (authProviderId) {
            LOGIN_TYPE_FACEBOOK -> onSignOutCallbackListener.onSignOutFacebook()
            LOGIN_TYPE_GOOGLE -> onSignOutCallbackListener.onSignOutGoogle()
            else -> onSignOutCallbackListener.onSignOutEmail()
        }
    }

    fun signOutFirebase(isCompleteLogout: () -> Unit) {
        firebaseRequest.signOut()
        isCompleteLogout.invoke()
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