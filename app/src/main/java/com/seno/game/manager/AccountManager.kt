package com.seno.game.manager

import android.net.Uri
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.seno.game.App
import com.seno.game.R
import com.seno.game.data.network.FirebaseRequest
import com.seno.game.extensions.getString
import com.seno.game.extensions.toast
import com.seno.game.prefs.PrefsManager
import timber.log.Timber

private const val LOGIN_TYPE_GOOGLE = "google.com"
private const val LOGIN_TYPE_FACEBOOK = "facebook.com"

enum class PlatForm(name: String) {
    KAKAO(name = "kakao"),
    GOOGLE(name = "google"),
    FACEBOOK(name = "facebook"),
    NAVER(name = "naver")
}

object AccountManager {
    private var firebaseRequest: FirebaseRequest = FirebaseRequest()
    var firebaseAuthToken: String = ""

    val currentUser: FirebaseUser?
        get() = firebaseRequest.currentUser

    val firebaseUid: String?
        get() = firebaseRequest.currentUser?.uid

    val isSignedIn: Boolean
        get() = currentUser != null

    val isAnonymous: Boolean
        get() = currentUser?.isAnonymous == true

    val isSignedOut: Boolean
        get() = currentUser == null

    val isUser: Boolean
        get() = currentUser != null

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
        get() {
            Timber.e("kkh displayname : ${FirebaseAuth.getInstance().currentUser?.displayName}")
            return FirebaseAuth.getInstance().currentUser?.displayName
        }

    val profileUri: Uri?
        get() {
            Timber.e("kkh profileUri : ${FirebaseAuth.getInstance().currentUser?.photoUrl}")
            return FirebaseAuth.getInstance().currentUser?.photoUrl
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
        platform: PlatForm,
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

                saveProfile(
                    uid = it.result.user?.uid,
                    platform = platform,
                    nickname = displayName,
                    profileUri = profileUri?.toString(),
                    onSignInSucceed = onSignInSucceed,
                    onSigInFailed = onSigInFailed
                )
            }
    }

    fun signInWithCustomToken(
        token: String,
        platform: PlatForm,
        nickname: String?,
        profileUri: String?,
        onSignInSucceed: () -> Unit,
        onSigInFailed: () -> Unit,
    ) {
        firebaseRequest.signInWithCustomToken(fCredentialToken = token)
            .addOnFailureListener { e ->
                e.message?.let { message -> App.getInstance().applicationContext.toast(message) }
                onSigInFailed.invoke()
            }
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    return@addOnCompleteListener
                }

                saveProfile(
                    uid = it.result.user?.uid,
                    platform = platform,
                    nickname = nickname,
                    profileUri = profileUri,
                    onSignInSucceed = onSignInSucceed,
                    onSigInFailed = onSigInFailed
                )
            }
    }

    private fun saveProfile(
        uid: String?,
        platform: PlatForm,
        nickname: String?,
        profileUri: String?,
        onSignInSucceed: () -> Unit,
        onSigInFailed: () -> Unit,
    ) {
        uid?.let {
            val map = HashMap<String, Any>()
            map["platform"] = platform.name
            map["nickname"] = nickname ?: PrefsManager.nickname
            map["profileUri"] = profileUri ?: PrefsManager.nickname

            val db = FirebaseFirestore.getInstance()
            db.collection("profile")
                .document(uid)
                .set(map)
                .addOnCompleteListener {
                    if (!it.isSuccessful) {
                        return@addOnCompleteListener
                    }
                    onSignInSucceed.invoke()
                }.addOnFailureListener { e ->
                    e.message?.let { message -> App.getInstance().applicationContext.toast(message) }
                    onSigInFailed.invoke()
                }
        } ?: onSigInFailed.invoke()
    }

    fun startLogout(
        facebookAccountManager: FacebookAccountManager?,
        googleAccountManager: GoogleAccountManager?,
        isCompleteLogout: () -> Unit,
    ) {
        signOut(object : OnSignOutCallbackListener {
            override fun onSignOutFacebook() {
                facebookAccountManager?.logout()
                signOutFirebase(isCompleteLogout = isCompleteLogout)
            }

            override fun onSignOutGoogle() {
                googleAccountManager?.logout(
                    logoutListener = object : GoogleAccountManager.LogoutListener {
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

    private fun signOut(onSignOutCallbackListener: OnSignOutCallbackListener?) {
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
        signInAnonymous(
            onSuccess = { isCompleteLogout.invoke() },
            onFail = {}
        )
    }

    fun signInAnonymous(
        onSuccess: () -> Unit,
        onFail: () -> Unit
    ) {
        firebaseRequest.signInAnonymous()
            .addOnSuccessListener { onSuccess.invoke() }
            .addOnFailureListener { onFail.invoke() }
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