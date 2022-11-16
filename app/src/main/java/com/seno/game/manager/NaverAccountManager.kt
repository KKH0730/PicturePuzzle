package com.seno.game.manager

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import timber.log.Timber

class NaverAccountManager {

    fun login(
        context: Context,
        launcher: ActivityResultLauncher<Intent>,
        onSignInSucceed: () -> Unit,
        onSigInFailed: () -> Unit
    ) {
        NaverIdLoginSDK.authenticate(context, launcher, object : OAuthLoginCallback {
            override fun onSuccess() {
                // 네이버 로그인 인증이 성공했을 때 수행할 코드 추가
                NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
                    override fun onSuccess(result: NidProfileResponse) {
                        // 네이버 유저 정보 가져오기
                        val email = result.profile?.email
                        val id = result.profile?.id
                        val name = result.profile?.name
                        val profileImage = result.profile?.profileImage

                        if (email != null && id != null) {
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
                        val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                        val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                        onSigInFailed.invoke()
                        Timber.e("kkh profileCallback errorCode:$errorCode, errorDesc:$errorDescription")
                    }

                    override fun onError(errorCode: Int, message: String) {
                        onFailure(errorCode, message)
                    }
                })
            }

            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Timber.e("kkh oauthLoginCallback errorCode:$errorCode, errorDesc:$errorDescription")
            }

            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        })
    }

    fun logout() {
        NaverIdLoginSDK.logout()
    }

    fun onActivityResult(
        onSignInSucceed: () -> Unit,
        onSigInFailed: () -> Unit
    ) {
        NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
            override fun onSuccess(result: NidProfileResponse) {
                // 네이버 유저 정보 가져오기
                val email = result.profile?.email
                val id = result.profile?.id
                val name = result.profile?.name
                val profileImage = result.profile?.profileImage

                if (email != null && id != null) {
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
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                onSigInFailed.invoke()
                Timber.e("kkh profileCallback errorCode:$errorCode, errorDesc:$errorDescription")
            }

            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        })
    }
}