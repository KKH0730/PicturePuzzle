package com.seno.game.manager

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.seno.game.App
import com.seno.game.R
import com.seno.game.data.network.FirebaseRequest
import com.seno.game.extensions.createRandomNickname
import com.seno.game.extensions.getString
import com.seno.game.extensions.saveDiskCacheData
import com.seno.game.extensions.toast
import com.seno.game.prefs.PrefsManager
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import timber.log.Timber

private const val LOGIN_TYPE_GOOGLE = "google.com"
private const val LOGIN_TYPE_FACEBOOK = "facebook.com"
private const val LOGIN_TYPE_NAVER = "naver.com"
private const val LOGIN_TYPE_KAKAO = "kakao.com"

enum class PlatForm(name: String) {
    KAKAO(name = "kakao"),
    GOOGLE(name = "google"),
    FACEBOOK(name = "facebook"),
    NAVER(name = "naver")
}

object AccountManager {
    private var firebaseRequest: FirebaseRequest = FirebaseRequest()
    private val currentUser: FirebaseUser?
        get() = firebaseRequest.currentUser

    val firebaseUid: String?
        get() = firebaseRequest.currentUser?.uid

    val isSignedIn: Boolean
        get() = currentUser != null

    val isAnonymous: Boolean
        get() = currentUser?.isAnonymous == true

    val isUser: Boolean
        get() = currentUser != null

    val profileColRef = FirebaseFirestore.getInstance().collection("profile")

    private val authProviderId: String
        get() {
            var providerId = ""
            FirebaseAuth.getInstance().currentUser?.providerData?.forEach {
                providerId = when (it.providerId) {
                    "facebook.com" -> LOGIN_TYPE_FACEBOOK
                    "google.com" -> LOGIN_TYPE_GOOGLE
                    "password" -> {
                        if (currentUser?.email?.contains("naver.com") == true) {
                            LOGIN_TYPE_NAVER
                        } else {
                            LOGIN_TYPE_KAKAO
                        }
                    }
                    else -> ""
                }
            }
            return providerId
        }

    val authProviderName: String
        get() {
            var provider = ""
            currentUser?.providerData?.forEach {
                when (it.providerId) {
                    "facebook.com" -> provider = getString(R.string.facebook)
                    "google.com" -> provider = getString(R.string.google)
                    "password" -> provider = if (currentUser?.email?.contains("naver.com") == true) {
                        getString(R.string.naver)
                    } else {
                        getString(R.string.kakao)
                    }
                }
            }
            return provider
        }

    private val displayName: String?
        get() {
            return FirebaseAuth.getInstance().currentUser?.displayName
        }

    private val profileUri: Uri?
        get() {
            return FirebaseAuth.getInstance().currentUser?.photoUrl
        }


    fun signInWithCredential(
        credential: AuthCredential,
        platform: PlatForm,
        onSignInSucceed: () -> Unit,
        onSignInFailed: () -> Unit,
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            val uid = withContext(Dispatchers.IO) {
                var userUid: String? = null

                firebaseRequest.signInWithCredential(credential = credential)
                    .addOnFailureListener { onSignInFailed.invoke() }
                    .addOnCompleteListener {
                        if (!it.isSuccessful) {
                            onSignInFailed.invoke()
                            return@addOnCompleteListener
                        }

                        userUid = it.result.user?.uid
                    }.await()
                userUid
            }

            val isProfileDocExist = withContext(Dispatchers.IO) {
                var isDocumentExist = false
                if (uid == null) {
                    onSignInFailed.invoke()
                } else {
                    profileColRef.document(uid).get()
                        .addOnFailureListener { onSignInFailed.invoke() }
                        .addOnCompleteListener {
                            if (!it.isSuccessful) {
                                onSignInFailed.invoke()
                                return@addOnCompleteListener
                            }
                            isDocumentExist = it.result.exists()
                        }.await()
                }
                isDocumentExist
            }

            withContext(Dispatchers.IO) {
                if (isProfileDocExist) {
                    getProfileInfo(
                        uid = uid,
                        onSignInSucceed = onSignInSucceed,
                        onSignInFailed = onSignInFailed
                    )
                } else {
                    saveProfileInfo(
                        uid = uid,
                        platform = platform,
                        nickname = displayName,
                        profileUri = profileUri?.toString(),
                        onSignInSucceed = onSignInSucceed,
                        onSignInFailed = onSignInFailed
                    )
                }
            }
        }
    }

    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        platform: PlatForm,
        nickname: String?,
        profileUri: String?,
        onSignInSucceed: () -> Unit,
        onSignInFailed: () -> Unit,
    ) {
        firebaseRequest.createUserWithEmailAndPassword(email, password)
            .addOnFailureListener { e ->
                if (e is FirebaseAuthUserCollisionException) {
                    // 로그인이니까 프로필 가져오기
                    signInUserWithEmailAndPassword(
                        email = email,
                        password = password,
                        onSignInSucceed = onSignInSucceed,
                        onSignInFailed = onSignInFailed
                    )
                } else {
                    onSignInFailed.invoke()
                }
            }

            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    if (it.exception !is FirebaseAuthUserCollisionException) {
                        onSignInFailed.invoke()
                    }
                    return@addOnCompleteListener
                }

                saveProfileInfo(
                    uid = it.result.user?.uid,
                    platform = platform,
                    nickname = nickname,
                    profileUri = profileUri,
                    onSignInSucceed = onSignInSucceed,
                    onSignInFailed = onSignInFailed
                )
            }
    }

    private fun signInUserWithEmailAndPassword(
        email: String,
        password: String,
        onSignInSucceed: () -> Unit,
        onSignInFailed: () -> Unit,
    ) {
        firebaseRequest.signInWithEmailAndPassword(email, password)
            .addOnFailureListener { onSignInFailed.invoke() }
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    onSignInFailed.invoke()
                    return@addOnCompleteListener
                }


                getProfileInfo(
                    uid = it.result.user?.uid,
                    onSignInSucceed = onSignInSucceed,
                    onSignInFailed = onSignInFailed
                )
            }
    }

    private fun saveProfileInfo(
        uid: String?,
        platform: PlatForm,
        nickname: String?,
        profileUri: String?,
        onSignInSucceed: () -> Unit,
        onSignInFailed: () -> Unit,
    ) {

        uid?.let {
            profileUri?.saveDiskCacheData(size = null)

            val userNickname = nickname ?: PrefsManager.nickname
            val userProfileUri = profileUri ?: PrefsManager.nickname
            val map = mutableMapOf(
                "platform" to platform.name,
                "uid" to uid,
                "nickname" to userNickname,
                "profileUri" to userProfileUri
            )

            profileColRef.document(uid)
                .set(map)
                .addOnFailureListener { onSignInFailed.invoke() }
                .addOnCompleteListener {
                    if (!it.isSuccessful) {
                        onSignInFailed.invoke()
                        return@addOnCompleteListener
                    }

                    platform.let { PrefsManager.platform = it.name }
                    nickname?.let { PrefsManager.nickname = it }
                    profileUri?.let { PrefsManager.profileUri = it}

                    onSignInSucceed.invoke()
                }
        } ?: onSignInFailed.invoke()
    }

    private fun getProfileInfo(
        uid: String?,
        onSignInSucceed: () -> Unit,
        onSignInFailed: () -> Unit,
    ) {
        uid?.let {
            profileColRef.document(uid)
                .get()
                .addOnFailureListener { onSignInFailed.invoke() }
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        onSignInFailed.invoke()
                        return@addOnCompleteListener
                    }

                    val documentSnapshot = task.result
                    PrefsManager.apply {
                        this.platform = documentSnapshot.getString("platform") ?: ""
                        this.nickname = documentSnapshot.getString("nickname") ?: ""
                        this.profileUri = documentSnapshot.getString("profileUri") ?: ""
                        this.profileUri.saveDiskCacheData(size = null)
                    }
                    onSignInSucceed.invoke()
                }
        } ?: onSignInFailed.invoke()
    }

    fun startLogout(
        context: Context,
        facebookAccountManager: FacebookAccountManager?,
        googleAccountManager: GoogleAccountManager?,
        naverAccountManager: NaverAccountManager?,
        kakaoAccountManager: KakaoAccountManager?,
        isCompleteLogout: () -> Unit,
    ) {
        signOut(object : OnSignOutCallbackListener {
            override fun onSignOutFacebook() {
                facebookAccountManager?.logout()
                signOutFirebase(context = context, isCompleteLogout = isCompleteLogout)
            }

            override fun onSignOutGoogle() {
                googleAccountManager?.logout(
                    logoutListener = object : GoogleAccountManager.LogoutListener {
                        override fun onSuccessLogout() {
                            signOutFirebase(context = context, isCompleteLogout = isCompleteLogout)
                        }
                    })
            }

            override fun onSignOutNaver() {
                naverAccountManager?.logout()
                signOutFirebase(context = context, isCompleteLogout = isCompleteLogout)
            }

            override fun onSignOutKakao() {
                kakaoAccountManager?.logout()
                signOutFirebase(context = context, isCompleteLogout = isCompleteLogout)
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
            LOGIN_TYPE_NAVER -> onSignOutCallbackListener.onSignOutNaver()
            else -> onSignOutCallbackListener.onSignOutKakao()
        }
    }

    fun signOutFirebase(context: Context, isCompleteLogout: () -> Unit) {
        firebaseRequest.signOut()
        signInAnonymous(
            onSuccess = isCompleteLogout,
            onFail = isCompleteLogout
        )
    }

    fun signInAnonymous(
        onSuccess: () -> Unit,
        onFail: () -> Unit
    ) {
        firebaseRequest.signInAnonymous()
            .addOnFailureListener { onFail.invoke() }
            .addOnSuccessListener { onSuccess.invoke() }
    }
}

interface OnSocialSignInCallbackListener {
    fun signInWithCredential(idToken: String?)
    fun onError(e: Exception?)
}

interface OnSignOutCallbackListener {
    fun onSignOutFacebook()
    fun onSignOutGoogle()
    fun onSignOutNaver()
    fun onSignOutKakao()
}