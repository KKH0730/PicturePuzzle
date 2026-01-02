package com.seno.game.manager

import android.content.Context
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import com.seno.game.extensions.isNotNullAndNotEmpty
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class NaverAccountManager {

    fun login(
        context: Context,
        onSignInSucceed: () -> Unit,
        onSigInFailed: (Exception?) -> Unit
    ) {
        NaverIdLoginSDK.authenticate(context, object : OAuthLoginCallback {
            override fun onSuccess() {
                // 네이버 로그인 인증이 성공했을 때 수행할 코드 추가
                NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
                    override fun onSuccess(result: NidProfileResponse) {
                        // 네이버 유저 정보 가져오기
                        val email = if (result.profile?.email.isNullOrEmpty()) "" else  "naver_${result.profile?.email}"
                        val id = result.profile?.id
                        val name = result.profile?.name
                        val profileImage = result.profile?.profileImage

                        if (email.isNotNullAndNotEmpty() && id.isNotNullAndNotEmpty()) {
                            AccountManager.createUserWithEmailAndPassword(
                                email = email,
                                password = id,
                                platform = PlatForm.NAVER,
                                nickname = name,
                                profileUri = profileImage,
                                onSignInSucceed = onSignInSucceed,
                                onSignInFailed = onSigInFailed
                            )
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

            override fun onFailure(httpStatus: Int, message: String) {
                onSigInFailed.invoke(Exception(message))
            }

            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        })
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
                                OAuthProvider.getCredential("","","")
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