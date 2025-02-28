package com.seno.game.manager

import android.net.Uri
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.seno.game.R
import com.seno.game.data.network.ApiConstants
import com.seno.game.data.network.AccountRequest
import com.seno.game.extensions.getString
import com.seno.game.extensions.saveDiskCacheData
import com.seno.game.prefs.PrefsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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
    private var accountRequest: AccountRequest = AccountRequest()
    private val currentUser: FirebaseUser?
        get() = accountRequest.currentUser

    val firebaseUid: String?
        get() = accountRequest.currentUser?.uid

    val isSignedIn: Boolean
        get() = currentUser != null

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
        CoroutineScope(Dispatchers.IO).launch {
            val signInTask = accountRequest.signInWithCredential(credential = credential)
            if (!signInTask.isSuccessful) {
                onSignInFailed.invoke()
                return@launch
            }

            val uid = signInTask.result.user?.uid
            if (uid == null) {
                onSignInFailed.invoke()
                return@launch
            } else {
                val isProfileDocExist = isExistProfileDocument(uid = uid)
                if (isProfileDocExist) {
                    // SNS 로그인
                    getProfileInfo(
                        uid = uid,
                        onSignInSucceed = onSignInSucceed,
                        onSignInFailed = onSignInFailed
                    )
                } else {
                    // SNS 회원가입
                    setProfileInfo(
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

    private suspend fun isExistProfileDocument(uid: String) = suspendCoroutine { continuation ->
        profileColRef.document(uid).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(task.result.exists())
                } else {
                    continuation.resume(false)
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
        CoroutineScope(Dispatchers.IO).launch {
            when (val createUserWithEmailAndPasswordResult = accountRequest.createUserWithEmailAndPassword(email, password)) {
                null -> {
                    onSignInFailed.invoke()
                }
                is FirebaseAuthUserCollisionException -> {
                    val signInUserWithEmailAndPasswordResult = accountRequest.signInWithEmailAndPassword(
                        email = email,
                        password = password,
                    )

                    if (signInUserWithEmailAndPasswordResult == null || signInUserWithEmailAndPasswordResult is Exception) {
                        onSignInFailed.invoke()
                    } else {
                        val user = (signInUserWithEmailAndPasswordResult as AuthResult).user
                        getProfileInfo(
                            uid = user?.uid,
                            onSignInSucceed = onSignInSucceed,
                            onSignInFailed = onSignInFailed
                        )
                    }
                }
                else -> {
                    val user = (createUserWithEmailAndPasswordResult as AuthResult).user
                    setProfileInfo(
                        uid = user?.uid,
                        platform = platform,
                        nickname = nickname,
                        profileUri = profileUri,
                        onSignInSucceed = onSignInSucceed,
                        onSignInFailed = onSignInFailed
                    )
                }
            }
        }
    }

    private suspend fun setUserInfo(
        uid: String,
        platform: PlatForm,
        nickname: String?,
    ) = suspendCoroutine { continuation ->
        val userNickname = nickname ?: PrefsManager.nickname
        val userProfileUri = profileUri ?: PrefsManager.profileUri
        val map = mutableMapOf(
            ApiConstants.UserInfo.UID to uid,
            ApiConstants.UserInfo.NICKNAME to userNickname,
            ApiConstants.UserInfo.PLATFORM to platform.name,
            ApiConstants.UserInfo.PROFILE_URI to userProfileUri,
            ApiConstants.UserInfo.BACKGROUND_VOLUME to PrefsManager.backgroundVolume.toString(),
            ApiConstants.UserInfo.EFFECT_VOLUME to PrefsManager.effectVolume.toString(),
            ApiConstants.UserInfo.IS_VIBRATION_ON to PrefsManager.isVibrationOn,
            ApiConstants.UserInfo.IS_PUSH_ON to PrefsManager.isPushOn,
            ApiConstants.UserInfo.IS_SHOW_AD to PrefsManager.isShowAD
        )

        profileColRef.document(uid)
            .set(map)
            .addOnCompleteListener { task -> continuation.resume(task) }
    }

    private suspend fun setDiffPictureGameInfo(uid: String) = suspendCoroutine { continuation ->
        val savedGameInfoMap = mutableMapOf<String, Any>(
            ApiConstants.FirestoreKey.DIFF_PICTURE_GAME_CURRENT_STATE to PrefsManager.diffPictureStage,
            ApiConstants.FirestoreKey.COMPLETE_GAME_ROUND to PrefsManager.diffPictureCompleteGameRound,
            ApiConstants.FirestoreKey.DIFF_PICTURE_GAME_HEART_COUNT to PrefsManager.diffPictureHeartCount,
            ApiConstants.FirestoreKey.DIFF_PICTURE_GAME_HEART_CHANGE_TIME to PrefsManager.diffPictureHeartChangedTime
        )

        profileColRef
            .document(uid)
            .collection(ApiConstants.Collection.SAVE_GAME_INFO)
            .document(ApiConstants.Document.DIFF_PICTURE)
            .set(savedGameInfoMap)
            .addOnCompleteListener { task -> continuation.resume(task) }
    }

    private suspend fun setProfileInfo(
        uid: String?,
        platform: PlatForm,
        nickname: String?,
        profileUri: String?,
        onSignInSucceed: () -> Unit,
        onSignInFailed: () -> Unit,
    ) {
        uid?.let {
            profileUri?.saveDiskCacheData(size = null)

            val userInfoTask = setUserInfo(uid = uid, platform = platform, nickname = nickname)
            if (!userInfoTask.isSuccessful) {
                onSignInFailed.invoke()
                return
            }

            val savedGameInfoTask = setDiffPictureGameInfo(uid = uid)
            if (!savedGameInfoTask.isSuccessful) {
                onSignInFailed.invoke()
                return
            }

            PrefsManager.apply {
                nickname?.let { this.nickname = it }
                this.platform = platform.name
                profileUri?.let { this.profileUri = it }
            }

            onSignInSucceed.invoke()
        } ?: onSignInFailed.invoke()
    }

    private suspend fun getUserInfo(
        uid: String,
    ) = suspendCoroutine { continuation ->
        profileColRef.document(uid)
            .get()
            .addOnCompleteListener { task -> continuation.resume(task) }
    }

    private suspend fun getDiffPictureGameInfo(uid: String) = suspendCoroutine { continuation ->
        profileColRef
            .document(uid)
            .collection(ApiConstants.Collection.SAVE_GAME_INFO)
            .document(ApiConstants.Document.DIFF_PICTURE)
            .get()
            .addOnCompleteListener { task -> continuation.resume(task) }
    }

    private suspend fun getProfileInfo(
        uid: String?,
        onSignInSucceed: () -> Unit,
        onSignInFailed: () -> Unit,
    ) {
        uid?.let {
            val userInfoTask = getUserInfo(uid = uid)
            if (!userInfoTask.isSuccessful) {
                onSignInFailed.invoke()
                return
            }

            val userDoc = userInfoTask.result as DocumentSnapshot
            if (userDoc.exists()) {
                PrefsManager.apply {
                    this.nickname = userDoc.getString(ApiConstants.UserInfo.NICKNAME) ?: nickname
                    this.platform = userDoc.getString(ApiConstants.UserInfo.PLATFORM) ?: platform
                    this.profileUri = userDoc.getString(ApiConstants.UserInfo.PROFILE_URI) ?: profileUri
                    this.backgroundVolume = userDoc.getString(ApiConstants.UserInfo.BACKGROUND_VOLUME)?.toFloat() ?: backgroundVolume
                    this.effectVolume = userDoc.getString(ApiConstants.UserInfo.EFFECT_VOLUME)?.toFloat() ?: effectVolume
                    this.isVibrationOn = userDoc.getBoolean(ApiConstants.UserInfo.IS_VIBRATION_ON) ?: isVibrationOn
                    this.isPushOn = userDoc.getBoolean(ApiConstants.UserInfo.IS_PUSH_ON) ?: isPushOn
                    this.isShowAD = userDoc.getBoolean(ApiConstants.UserInfo.IS_SHOW_AD) ?: isShowAD
                    this.profileUri.saveDiskCacheData(size = null)
                }
            }
            val savedGameInfoTask = getDiffPictureGameInfo(uid = uid)
            if (!savedGameInfoTask.isSuccessful) {
                onSignInFailed.invoke()
                return
            }

            val savedGameInfoDoc = savedGameInfoTask.result as DocumentSnapshot
            if (savedGameInfoDoc.exists()) {
                PrefsManager.apply {
                    (savedGameInfoDoc.getString(ApiConstants.FirestoreKey.COMPLETE_GAME_ROUND) ?: diffPictureCompleteGameRound)
                        .split(",")
                        .forEach { round ->
                            this.diffPictureCompleteGameRound = round
                        }
                    this.diffPictureStage =
                        savedGameInfoDoc.getLong(ApiConstants.FirestoreKey.DIFF_PICTURE_GAME_CURRENT_STATE)?.toInt() ?: diffPictureStage
                    this.diffPictureHeartCount =
                        savedGameInfoDoc.getLong(ApiConstants.FirestoreKey.DIFF_PICTURE_GAME_HEART_COUNT)?.toInt() ?: diffPictureHeartCount
                    this.diffPictureHeartChangedTime =
                        savedGameInfoDoc.getLong(ApiConstants.FirestoreKey.DIFF_PICTURE_GAME_HEART_CHANGE_TIME) ?: diffPictureHeartChangedTime
                }
            }
            onSignInSucceed.invoke()
        } ?: onSignInFailed.invoke()
    }

    fun startLogout(
        facebookAccountManager: FacebookAccountManager?,
        googleAccountManager: GoogleAccountManager?,
        naverAccountManager: NaverAccountManager?,
        kakaoAccountManager: KakaoAccountManager?,
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

            override fun onSignOutNaver() {
                naverAccountManager?.logout()
                signOutFirebase(isCompleteLogout = isCompleteLogout)
            }

            override fun onSignOutKakao() {
                kakaoAccountManager?.logout()
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
            LOGIN_TYPE_NAVER -> onSignOutCallbackListener.onSignOutNaver()
            else -> onSignOutCallbackListener.onSignOutKakao()
        }
    }

    fun signOutFirebase(isCompleteLogout: () -> Unit) {
        accountRequest.signOut()
        signInAnonymous(
            onSuccess = isCompleteLogout,
            onFail = isCompleteLogout
        )
    }

    fun signInAnonymous(
        onSuccess: () -> Unit,
        onFail: () -> Unit,
    ) {
        accountRequest.signInAnonymous()
            .addOnFailureListener { onFail.invoke() }
            .addOnSuccessListener { onSuccess.invoke() }
    }

    suspend fun signInAnonymous(): AuthResult = accountRequest.signInAnonymous().await()

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