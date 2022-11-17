package com.seno.game.manager

import android.content.Context
import com.kakao.sdk.user.UserApiClient
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

const val KAKAO_PACKAGE = "com.kakao.talk"

class KakaoManager(private val context: Context) {
    var disposables = CompositeDisposable()

    private val isKakaoTalkInstalled: Boolean
        get() {
            val intent = context.packageManager.getLaunchIntentForPackage(KAKAO_PACKAGE)
            return intent != null
        }

    fun login() {
        if (isKakaoTalkInstalled) {
            // 카카오톡으로 로그인
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    Timber.e("로그인 실패 error")
                } else if (token != null) {
                    Timber.e("로그인 성공 ${token.accessToken}")
                }
            }
        } else {

        }
    }

}