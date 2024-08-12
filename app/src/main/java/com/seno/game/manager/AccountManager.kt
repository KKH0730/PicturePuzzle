package com.seno.game.manager

import android.net.Uri
import com.google.firebase.auth.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.seno.game.R
import com.seno.game.data.network.ApiConstants
import com.seno.game.data.network.FirebaseRequest
import com.seno.game.extensions.getString
import com.seno.game.extensions.isNotNullAndNotEmpty
import com.seno.game.extensions.saveDiskCacheData
import com.seno.game.prefs.PrefsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val LOGIN_TYPE_GOOGLE = "google.com"
private const val LOGIN_TYPE_FACEBOOK = "facebook.com"
private const val LOGIN_TYPE_NAVER = "naver.com"
private const val LOGIN_TYPE_KAKAO = "kakao.com"

enum class PlatForm(val value: String) {
    KAKAO(value = "kakao"),
    GOOGLE(value = "google"),
    FACEBOOK(value = "facebook"),
    NAVER(value = "naver")
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

    private fun getSaveGameInfoColRef(uid: String): CollectionReference {
        return FirebaseFirestore.getInstance()
            .collection("profile")
            .document(uid)
            .collection("save_game_info")
    }

    const val DIFF_PICTURE_DOC = "diff_picture"

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

    @JvmStatic
    fun addAuthStateListener(onSignedIn: () -> Unit, onSignedOut: () -> Unit) {
        val authStateListener = FirebaseAuth.AuthStateListener {
            getFirebaseUserIdToken(
                onSuccess = { onSignedIn.invoke() },
                onFailure = {
                    onSignedOut.invoke()
                    Timber.e(it)
                }
            )
        }
        firebaseRequest.firebaseAuth.addAuthStateListener(authStateListener)
    }

    private fun getFirebaseUserIdToken(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        currentUser?.let { firebaseUser ->
            firebaseUser.getIdToken(true).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.let { onSuccess.invoke() }
                } else {
                    task.exception?.let { onFailure.invoke(it) }
                        ?: onFailure.invoke(Exception("Unknown FirebaseUser Id Token Error"))
                }
            }
        } ?: onFailure(Exception("FirebaseUser is null"))
    }

    fun signInWithCredential(
        credential: AuthCredential,
        platform: PlatForm,
        onSignInSucceed: () -> Unit,
        onSignInFailed: (Exception?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val signInTask = firebaseRequest.signInWithCredential(credential = credential)
            if (!signInTask.isSuccessful) {
                onSignInFailed.invoke(signInTask.exception)
                return@launch
            }

            val uid = signInTask.result.user?.uid
            if (uid == null) {
                onSignInFailed.invoke(Exception("uid is null"))
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
        onSignInFailed: (Exception?) -> Unit,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            when (val createUserWithEmailAndPasswordResult = firebaseRequest.createUserWithEmailAndPassword(email, password)) {
                null -> {
                    onSignInFailed.invoke(Exception("Result is null"))
                }
                is FirebaseAuthUserCollisionException -> {
                    val signInUserWithEmailAndPasswordResult = firebaseRequest.signInWithEmailAndPassword(
                        email = email,
                        password = password,
                    )

                    if (signInUserWithEmailAndPasswordResult == null || signInUserWithEmailAndPasswordResult is Exception) {
                        onSignInFailed.invoke(Exception("FirebaseAuthUserCollisionException"))
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

    private suspend fun setProfileInfo(
        uid: String?,
        platform: PlatForm,
        nickname: String?,
        profileUri: String?,
        onSignInSucceed: () -> Unit,
        onSignInFailed: (Exception?) -> Unit,
    ) {
        uid?.let {
            profileUri?.saveDiskCacheData(size = null)

            val userInfoTask = setUserInfo(uid = uid, platform = platform, nickname = nickname)
            if (!userInfoTask.isSuccessful) {
                onSignInFailed.invoke(userInfoTask.exception)
                return
            }

            val savedGameInfoTask = setDiffPictureGameInfo(uid = uid)
            if (!savedGameInfoTask.isSuccessful) {
                onSignInFailed.invoke(savedGameInfoTask.exception)
                return
            }

            PrefsManager.apply {
                nickname?.let { this.nickname = it }
                this.platform = platform.value
                profileUri?.let { this.profileUri = it }
            }

            onSignInSucceed.invoke()
        } ?: onSignInFailed.invoke(Exception("uid is null"))
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
            ApiConstants.UserInfo.PLATFORM to platform.value,
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
            ApiConstants.FirestoreKey.DIFF_PICTURE_GAME_HEART_CHARGED_TIME to PrefsManager.diffPictureHeartChargedTime
        )

        profileColRef
            .document(uid)
            .collection(ApiConstants.Collection.SAVE_GAME_INFO)
            .document(ApiConstants.Document.DIFF_PICTURE)
            .set(savedGameInfoMap)
            .addOnCompleteListener { task -> continuation.resume(task) }
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
        onSignInFailed: (Exception?) -> Unit,
    ) {
        uid?.let {
            val userInfoTask = getUserInfo(uid = uid)
            if (!userInfoTask.isSuccessful) {
                onSignInFailed.invoke(userInfoTask.exception)
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
                onSignInFailed.invoke(savedGameInfoTask.exception)
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
                    this.diffPictureHeartChargedTime =
                        savedGameInfoDoc.getLong(ApiConstants.FirestoreKey.DIFF_PICTURE_GAME_HEART_CHARGED_TIME) ?: diffPictureHeartChargedTime
                }
            }
            onSignInSucceed.invoke()
        } ?: onSignInFailed.invoke(Exception("uid is null"))
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

    suspend fun startWithdrawal(
        isCompleteWithdrawal: () -> Unit,
        onFail: () -> Unit
    ) {
        val uid = firebaseUid
        if (uid.isNullOrEmpty()) {
            withContext(Dispatchers.Main) { onFail.invoke() }
            return
        }

        val deleteAuthTask = withdrawalFirebase()
        if (!deleteAuthTask.isSuccessful) {
            withContext(Dispatchers.Main) { onFail.invoke() }
            return
        }

        deleteUserInfo(uid = uid)
        withContext(Dispatchers.Main) { isCompleteWithdrawal.invoke() }
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
        firebaseRequest.signOut()
        isCompleteLogout.invoke()
//        signInAnonymous(
//            onSuccess = isCompleteLogout,
//            onFail = isCompleteLogout
//        )
    }

    fun signInAnonymous(
        onSuccess: () -> Unit,
        onFail: () -> Unit,
    ) {
        firebaseRequest.signInAnonymous()
            .addOnFailureListener { onFail.invoke() }
            .addOnSuccessListener { onSuccess.invoke() }
    }

    private suspend fun withdrawalFirebase() = suspendCoroutine { continuation ->
        firebaseRequest.currentUser?.delete()
            ?.addOnCompleteListener { task -> continuation.resume(task) }
    }

    private suspend fun deleteUserInfo(
        uid: String,
    ) = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance().runTransaction { transaction ->
            transaction.delete(getSaveGameInfoColRef(uid = uid).document(DIFF_PICTURE_DOC))
            transaction.delete(profileColRef.document(uid))
        }.addOnCompleteListener { task -> continuation.resume(task) }
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