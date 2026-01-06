package com.seno.game.manager

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.functions.functions
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.rx
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class KakaoAccountManager(private val context: Context) {

    private val isKakaoTalkInstalled: Boolean
        get() {
            return UserApiClient.instance.isKakaoTalkLoginAvailable(context = context)
        }

    suspend fun login(
        onSignInSucceed: () -> Unit,
        onSignInFailed: (Exception?) -> Unit
    ) {
        val accessToken = loginWithKakao(context = context)?.accessToken  ?: run {
            onSignInFailed.invoke(Exception("kakao access token error"))
            return
        }
        val kakaoUser = getFirebaseCustomToken(accessToken = accessToken) ?: run {
            onSignInFailed.invoke(Exception("kakao custom token error"))
            return
        }

        AccountManager.signInWithCustomToken(
            customToken = kakaoUser.customToken,
            platform = PlatForm.KAKAO,
            email = kakaoUser.email ?: "",
            nickname = kakaoUser.nickname ?: "",
            profileUri = kakaoUser.profileUri ?: "",
            onSignInSucceed = onSignInSucceed,
            onSignInFailed = onSignInFailed
        )
    }

    private suspend fun loginWithKakao(context: Context): OAuthToken? =
        suspendCoroutine { continuation ->
            if (isKakaoTalkInstalled) {
                UserApiClient().loginWithKakaoTalk(context) { token, error ->
                    if (error != null) {
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            continuation.resumeWith(Result.failure(Exception("Unknown error")))
                        } else {
                            UserApiClient.rx.loginWithKakaoAccount(context)
                        }
                        continuation.resume(null)
                    } else {
                        if (token != null) continuation.resume(token)
                        else continuation.resume(null)
                    }
                }
            } else {
                UserApiClient().loginWithKakaoAccount(context) { token, error ->
                    if (error != null) continuation.resume(null)
                    else if (token != null) continuation.resume(token)
                    else continuation.resume(null)
                }
            }
        }


    private suspend fun getFirebaseCustomToken(accessToken: String) = suspendCoroutine { continuation ->
        Firebase.functions(regionOrCustomDomain = "us-central1")
            .getHttpsCallable("kakao_auth")
            .call(mapOf("accessToken" to accessToken))
            .addOnSuccessListener { result ->
                val data = result.data as Map<*, *>
                val customToken = data["customToken"] as String
                val email = data["email"] as? String?
                val nickname = data["nickname"] as? String?
                val profileUri = data["profileUri"] as? String?

                continuation.resume(KakaoUser(customToken = customToken, email = email, nickname = nickname, profileUri = profileUri))
            }
            .addOnFailureListener { e ->
                Timber.e("getFirebaseCustomToken Custom Token 호출 실패 : $e")
                continuation.resume(null)
            }
    }

    suspend fun reauthenticate(): Boolean = withContext(Dispatchers.IO) {
        try {
            val accessToken = loginWithKakao(context)?.accessToken ?: return@withContext false
            val kakaoUser = getFirebaseCustomToken(accessToken = accessToken) ?: return@withContext false
            AccountManager.signInWithCustomToken(customToken = kakaoUser.customToken)
        } catch (e: Exception) {
            Timber.e(e)
            false
        }
    }

    fun logout() {
        UserApiClient.rx.logout()
    }

    data class KakaoUser(
        val customToken: String,
        val email: String?,
        val nickname: String?,
        val profileUri: String?
    )
}