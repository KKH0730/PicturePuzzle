package com.seno.game.manager

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.functions.functions
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import com.seno.game.extensions.isNotNullAndNotEmpty
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

//firebase deploy --only functions

class NaverAccountManager {

    fun login(
        context: Context,
        onSignInSucceed: () -> Unit,
        onSigInFailed: (Exception?) -> Unit
    ) {
        val scopes = arrayOf("profile", "email", "name")

        NaverIdLoginSDK.authenticate(context, object : OAuthLoginCallback {
            override fun onSuccess() {
                val accessToken = NaverIdLoginSDK.getAccessToken()

                if (accessToken.isNotNullAndNotEmpty()) {
                    getFirebaseCustomToken(
                        naverAccessToken = accessToken,
                        onSignInSucceed = onSignInSucceed,
                        onSignInFailed = onSigInFailed
                    )
                } else {
                    onSigInFailed.invoke(Exception("accessToken is null"))
                }
            }

            override fun onFailure(httpStatus: Int, message: String) {
                onSigInFailed.invoke(Exception(message))
            }

            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        })
    }

    fun getFirebaseCustomToken(
        naverAccessToken: String,
        onSignInSucceed: () -> Unit,
        onSignInFailed: (Exception?) -> Unit
    ) {
        val functions = Firebase.functions(regionOrCustomDomain = "us-central1")
        functions
            .getHttpsCallable("naver_auth")
            .call(mapOf("accessToken" to naverAccessToken))
            .addOnSuccessListener { result ->
                val data = result.data as Map<*, *>
                val customToken = data["customToken"] as String
                val email = data["email"] as? String?
                val nickname = data["nickname"] as? String?
                val profileUri = data["profileUri"] as? String?

                AccountManager.signInWithCustomToken(
                    customToken = customToken,
                    platform = PlatForm.NAVER,
                    email = email ?: "",
                    nickname = nickname ?: "",
                    profileUri = profileUri ?: "",
                    onSignInSucceed = onSignInSucceed,
                    onSignInFailed = onSignInFailed
                )
            }
            .addOnFailureListener { e ->
                Timber.e("getFirebaseCustomToken Custom Token 호출 실패 : $e")
            }
    }

    suspend fun suspendLogin(context: Context): AuthCredential? {
        return suspendCoroutine { continuation ->
            NaverIdLoginSDK.authenticate(context, object : OAuthLoginCallback {
                override fun onSuccess() {
                    // 네이버 로그인 인증이 성공했을 때 수행할 코드 추가
                    NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
                        override fun onSuccess(result: NidProfileResponse) {
                            // 네이버 유저 정보 가져오기
                            val email = if (result.profile?.email.isNullOrEmpty()) "" else  "naver_${result.profile?.email}"
                            val id = result.profile?.id

                            if (email.isNotNullAndNotEmpty() && id.isNotNullAndNotEmpty()) {
                                continuation.resume(EmailAuthProvider.getCredential(email, id))
                            } else {
                                continuation.resume(null)
                            }
                        }

                        override fun onFailure(httpStatus: Int, message: String) {
                            continuation.resume(null)
                        }

                        override fun onError(errorCode: Int, message: String) {
                            continuation.resume(null)
                        }
                    })
                }

                override fun onFailure(httpStatus: Int, message: String) {
                    continuation.resume(null)
                }

                override fun onError(errorCode: Int, message: String) {
                    continuation.resume(null)
                }
            })
        }
    }

    fun logout() {
        NaverIdLoginSDK.logout()
    }
}