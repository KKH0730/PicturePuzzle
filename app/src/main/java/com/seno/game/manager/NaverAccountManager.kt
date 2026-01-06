package com.seno.game.manager

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.functions.functions
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

//firebase deploy --only functions

class NaverAccountManager(private val context: Context) {

    suspend fun login(
        onSignInSucceed: () -> Unit,
        onSignInFailed: (Exception?) -> Unit
    ) {
        val accessToken = loginWithNaver() ?: run {
            onSignInFailed.invoke(Exception("naver access token is null"))
            return
        }

        val naverUser = getFirebaseCustomToken(accessToken = accessToken) ?: run {
            onSignInFailed.invoke(Exception("kakao custom token error"))
            return
        }

        AccountManager.signInWithCustomToken(
            customToken = naverUser.customToken,
            platform = PlatForm.NAVER,
            email = naverUser.email ?: "",
            nickname = naverUser.nickname ?: "",
            profileUri = naverUser.profileUri ?: "",
            onSignInSucceed = onSignInSucceed,
            onSignInFailed = onSignInFailed
        )
    }

    suspend fun loginWithNaver(): String? = suspendCoroutine { continuation ->
        NaverIdLoginSDK.authenticate(context, object : OAuthLoginCallback {
            override fun onSuccess() {
                val accessToken = NaverIdLoginSDK.getAccessToken()
                continuation.resume(accessToken)
            }

            override fun onFailure(httpStatus: Int, message: String) {
                continuation.resume(null)
            }

            override fun onError(errorCode: Int, message: String) {
                continuation.resume(null)
            }
        })
    }

    suspend fun getFirebaseCustomToken(accessToken: String) = suspendCoroutine { continuation ->
        Firebase.functions(regionOrCustomDomain = "us-central1")
            .getHttpsCallable("naver_auth")
            .call(mapOf("accessToken" to accessToken))
            .addOnSuccessListener { result ->
                val data = result.data as Map<*, *>
                val customToken = data["customToken"] as String
                val email = data["email"] as? String?
                val nickname = data["nickname"] as? String?
                val profileUri = data["profileUri"] as? String?

                continuation.resume(NaverUser(customToken = customToken, email = email, nickname = nickname, profileUri = profileUri))
            }
            .addOnFailureListener { e ->
                Timber.e("getFirebaseCustomToken Custom Token 호출 실패 : $e")
                continuation.resume(null)
            }
    }

    suspend fun reauthenticate(): Boolean = withContext(Dispatchers.IO) {
        try {
            val accessToken = loginWithNaver() ?: return@withContext false
            val naverUser = getFirebaseCustomToken(accessToken = accessToken) ?: return@withContext false
            AccountManager.signInWithCustomToken(customToken = naverUser.customToken)
        } catch (e: Exception) {
            Timber.e(e)
            false
        }
    }

    fun logout() {
        NaverIdLoginSDK.logout()
    }

    data class NaverUser(
        val customToken: String,
        val email: String?,
        val nickname: String?,
        val profileUri: String?
    )
}