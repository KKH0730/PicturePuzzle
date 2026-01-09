package com.seno.game.manager

import android.content.Context
import com.google.firebase.auth.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.seno.game.App
import com.seno.game.R
import com.seno.game.data.network.ApiConstants
import com.seno.game.data.network.FirebaseRequest
import com.seno.game.extensions.createRandomNickname
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
private const val LOGIN_TYPE_NAVER = "naver.com"
private const val LOGIN_TYPE_KAKAO = "kakao.com"
private const val LOGIN_TYPE_UNKNOWN = "unknown"
const val UNKNOWN_UID = "unknownUid"

enum class PlatForm(val value: String) {
    KAKAO(value = "kakao"),
    GOOGLE(value = "google"),
    NAVER(value = "naver")
}

object AccountManager {
    private var firebaseRequest: FirebaseRequest = FirebaseRequest()
    private val currentUser: FirebaseUser?
        get() = firebaseRequest.currentUser

    val firebaseUid: String
        get() = firebaseRequest.currentUser?.uid ?: UNKNOWN_UID

    val isSignedIn: Boolean
        get() = currentUser != null

    val isAnonymous: Boolean
        get() = currentUser?.isAnonymous == true

    val isUser: Boolean
        get() = firebaseUid.isNotEmpty() && firebaseUid != UNKNOWN_UID

    val profileColRef = FirebaseFirestore.getInstance().collection("profile")

    private fun getSaveGameInfoColRef(uid: String): CollectionReference {
        return FirebaseFirestore.getInstance()
            .collection("profile")
            .document(uid)
            .collection("save_game_info")
    }

    const val DIFF_PICTURE_DOC = "diff_picture"

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
        email: String,
        nickname: String,
        profileUri: String,
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
                        email = email,
                        nickname = nickname,
                        profileUri = profileUri,
                        onSignInSucceed = onSignInSucceed,
                        onSignInFailed = onSignInFailed
                    )
                }
            }
        }
    }

    fun signInWithCustomToken(
        customToken: String,
        platform: PlatForm,
        email: String,
        nickname: String?,
        profileUri: String,
        onSignInSucceed: () -> Unit,
        onSignInFailed: (Exception?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val signInTask = firebaseRequest.signInWithCustomToken(fCredentialToken = customToken)
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
                        email = email,
                        nickname = nickname,
                        profileUri = profileUri,
                        onSignInSucceed = onSignInSucceed,
                        onSignInFailed = onSignInFailed
                    )
                }
            }
        }
    }

    suspend fun signInWithCustomToken(customToken: String): Boolean = withContext(Dispatchers.IO) {
        val signInTask = firebaseRequest.signInWithCustomToken(fCredentialToken = customToken)
        signInTask.isSuccessful
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

    suspend fun reauthenticate(credential: AuthCredential?) = suspendCoroutine { continuation ->
        if (credential == null) {
            continuation.resume(false)
            return@suspendCoroutine
        }

        firebaseRequest.currentUser?.reauthenticate(credential)
            ?.addOnSuccessListener { continuation.resume(true) }
            ?.addOnFailureListener { e -> continuation.resume(false) }
    }

    private suspend fun setProfileInfo(
        uid: String,
        platform: PlatForm,
        email: String,
        nickname: String?,
        profileUri: String?,
        onSignInSucceed: () -> Unit,
        onSignInFailed: (Exception?) -> Unit,
    ) {
        profileUri?.saveDiskCacheData(size = null)

        val userInfoTask = setUserInfo(uid = uid, platform = platform, email = email, nickname = nickname, profileUri = profileUri)
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
            this.nickname = nickname ?: ""
            this.email = email
            this.platform = platform.value
            this.profileUri = profileUri ?: ""
        }

        onSignInSucceed.invoke()
    }

    private suspend fun setProfileInfo(
        uid: String,
        platform: PlatForm,
        email: String,
        nickname: String?,
        profileUri: String?
    ): Boolean {
        profileUri?.saveDiskCacheData(size = null)

        val userInfoTask = setUserInfo(uid = uid, platform = platform, email = email, nickname = nickname, profileUri = profileUri)
        if (!userInfoTask.isSuccessful) {
            return false
        }

        val savedGameInfoTask = setDiffPictureGameInfo(uid = uid)
        if (!savedGameInfoTask.isSuccessful) {
            return false
        }

        PrefsManager.apply {
            this.nickname = nickname ?: ""
            this.email = email
            this.platform = platform.value
            this.profileUri = profileUri ?: ""
        }

        return true
    }

    private suspend fun setUserInfo(
        uid: String,
        platform: PlatForm,
        email: String,
        nickname: String?,
        profileUri: String?,
    ) = suspendCoroutine { continuation ->
        val userNickname = nickname ?: ""
        val userProfileUri = profileUri ?: ""
        val map = mutableMapOf(
            ApiConstants.UserInfo.UID to uid,
            ApiConstants.UserInfo.PLATFORM to platform.value,
            ApiConstants.UserInfo.EMAIL to email,
            ApiConstants.UserInfo.NICKNAME to userNickname,
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
                    this.email = userDoc.getString(ApiConstants.UserInfo.EMAIL) ?: email
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
                val heartChargedTime = savedGameInfoDoc.getLong(ApiConstants.FirestoreKey.DIFF_PICTURE_GAME_HEART_CHARGED_TIME) ?: PrefsManager.diffPictureHeartChargedTime
                val localHeartChargedTime = PrefsManager.diffPictureHeartChargedTime
                val recentChargedTime = heartChargedTime.coerceAtMost(localHeartChargedTime)
                val recentHeartCount = if (recentChargedTime == localHeartChargedTime) {
                    PrefsManager.diffPictureHeartCount
                } else {
                    savedGameInfoDoc.getLong(ApiConstants.FirestoreKey.DIFF_PICTURE_GAME_HEART_COUNT)?.toInt() ?: 0
                }
                val recentStage = if (recentChargedTime == localHeartChargedTime) {
                    savedGameInfoDoc.getLong(ApiConstants.FirestoreKey.DIFF_PICTURE_GAME_CURRENT_STATE)?.toInt() ?: 0
                } else {
                    PrefsManager.diffPictureStage
                }
                val recentCompleteRound = if (recentChargedTime == localHeartChargedTime) {
                    savedGameInfoDoc.getString(ApiConstants.FirestoreKey.COMPLETE_GAME_ROUND) ?: ""
                } else {
                    PrefsManager.diffPictureCompleteGameRound
                }

                PrefsManager.apply {
                    recentCompleteRound
                        .split(",")
                        .forEach { round -> this.diffPictureCompleteGameRound = round }
                    this.diffPictureStage = recentStage
                    this.diffPictureHeartCount = recentHeartCount
                    this.diffPictureHeartChargedTime = recentChargedTime
                }

            }
            onSignInSucceed.invoke()
        } ?: onSignInFailed.invoke(Exception("uid is null"))
    }

    private suspend fun getProfileInfo(uid: String): Boolean {
        val userInfoTask = getUserInfo(uid = uid)
        if (!userInfoTask.isSuccessful) return false

        val userDoc = userInfoTask.result as DocumentSnapshot
        if (userDoc.exists()) {
            PrefsManager.apply {
                this.email = userDoc.getString(ApiConstants.UserInfo.EMAIL) ?: email
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
            return false
        }

        val savedGameInfoDoc = savedGameInfoTask.result as DocumentSnapshot
        if (savedGameInfoDoc.exists()) {
            val heartChargedTime = savedGameInfoDoc.getLong(ApiConstants.FirestoreKey.DIFF_PICTURE_GAME_HEART_CHARGED_TIME) ?: PrefsManager.diffPictureHeartChargedTime
            val localHeartChargedTime = PrefsManager.diffPictureHeartChargedTime
            val recentChargedTime = heartChargedTime.coerceAtMost(localHeartChargedTime)
            val recentHeartCount = if (recentChargedTime == localHeartChargedTime) {
                PrefsManager.diffPictureHeartCount
            } else {
                savedGameInfoDoc.getLong(ApiConstants.FirestoreKey.DIFF_PICTURE_GAME_HEART_COUNT)?.toInt() ?: 0
            }
            val recentStage = if (recentChargedTime == localHeartChargedTime) {
                savedGameInfoDoc.getLong(ApiConstants.FirestoreKey.DIFF_PICTURE_GAME_CURRENT_STATE)?.toInt() ?: 0
            } else {
                PrefsManager.diffPictureStage
            }
            val recentCompleteRound = if (recentChargedTime == localHeartChargedTime) {
                savedGameInfoDoc.getString(ApiConstants.FirestoreKey.COMPLETE_GAME_ROUND) ?: ""
            } else {
                PrefsManager.diffPictureCompleteGameRound
            }

            PrefsManager.apply {
                recentCompleteRound
                    .split(",")
                    .forEach { round -> this.diffPictureCompleteGameRound = round }
                this.diffPictureStage = recentStage
                this.diffPictureHeartCount = recentHeartCount
                this.diffPictureHeartChargedTime = recentChargedTime
            }
        }
        return true
    }

    suspend fun startLogout(
        context: Context,
        googleAccountManager: GoogleAccountManager?,
        naverAccountManager: NaverAccountManager?,
        kakaoAccountManager: KakaoAccountManager?,
        isCompleteLogout: () -> Unit,
    ) {
        signOut(object : OnSignOutCallbackListener {

            override suspend fun onSignOutGoogle() {
                googleAccountManager?.logout(context = context)
                signOutFirebase(isCompleteLogout = isCompleteLogout)
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
        context: Context,
        googleAccountManager: GoogleAccountManager,
        naverAccountManager: NaverAccountManager,
        kakaoAccountManager: KakaoAccountManager,
        isCompleteWithdrawal: () -> Unit,
        onFail: () -> Unit
    ) {
        val uid = firebaseUid
        if (uid.isEmpty()) {
            withContext(Dispatchers.Main) { onFail.invoke() }
            return
        }

        if (uid == UNKNOWN_UID) {
            withContext(Dispatchers.Main) { isCompleteWithdrawal.invoke() }
            return
        }

        val deleteAuthTask = withdrawalFirebase()
        if (!deleteAuthTask.isSuccessful) {
            withContext(Dispatchers.Main) {
                if (deleteAuthTask.exception is FirebaseAuthRecentLoginRequiredException) {
                    reauthenticate(
                        uid = uid,
                        googleAccountManager = googleAccountManager,
                        naverAccountManager = naverAccountManager,
                        kakaoAccountManager = kakaoAccountManager,
                        isCompleteWithdrawal = isCompleteWithdrawal,
                        onFail = onFail
                    )
                } else {
                    onFail.invoke()
                }
            }
            return
        }

        deleteUserInfo(uid = uid)
        isCompleteWithdrawal.invoke()
    }

    private suspend fun reauthenticate(
        uid: String,
        googleAccountManager: GoogleAccountManager,
        naverAccountManager: NaverAccountManager,
        kakaoAccountManager: KakaoAccountManager,
        isCompleteWithdrawal: () -> Unit,
        onFail: () -> Unit
    ) {
        val isAuthenticated = when (PrefsManager.platform) {
            PlatForm.GOOGLE.value -> reauthenticate(credential = googleAccountManager.reauthenticate())
            PlatForm.NAVER.value -> naverAccountManager.reauthenticate()
            else -> kakaoAccountManager.reauthenticate()
        }

        if (isAuthenticated) {
            deleteUserInfo(uid = uid)
            isCompleteWithdrawal.invoke()
        } else {
            onFail.invoke()
        }
    }

    private suspend fun signOut(onSignOutCallbackListener: OnSignOutCallbackListener?) {
        if (onSignOutCallbackListener == null) {
            return
        }

        when (PrefsManager.platform) {
            PlatForm.GOOGLE.value -> onSignOutCallbackListener.onSignOutGoogle()
            PlatForm.NAVER.value -> onSignOutCallbackListener.onSignOutNaver()
            else -> onSignOutCallbackListener.onSignOutKakao()
        }
    }

    fun signOutFirebase(isCompleteLogout: () -> Unit) {
        firebaseRequest.signOut()
        isCompleteLogout.invoke()
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

    fun initData() {
        PrefsManager.apply {
            this.email = ""
            this.nickname = App.getInstance().resources.createRandomNickname()
            this.platform = ""
            this.profileUri = ""
        }
    }
}

interface OnSignOutCallbackListener {
    suspend fun onSignOutGoogle()
    fun onSignOutNaver()
    fun onSignOutKakao()
}