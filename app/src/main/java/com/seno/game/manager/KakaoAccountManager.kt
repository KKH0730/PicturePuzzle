package com.seno.game.manager

import android.content.Context
import com.kakao.sdk.auth.network.RxAuthOperations
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.rx
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

class KakaoAccountManager(private val context: Context) {
    var disposables = CompositeDisposable()

    private val isKakaoTalkInstalled: Boolean
        get() {
            return UserApiClient.instance.isKakaoTalkLoginAvailable(context = context)
        }

    fun login(
        onSignInSucceed: () -> Unit,
        onSignInFailed: (Exception?) -> Unit
    ) {
        if (isKakaoTalkInstalled) {
            loginKakaoTalk(
                onSignInSucceed = onSignInSucceed,
                onSignInFailed = onSignInFailed
            )
        } else {
            loginKakaoAccount(
                onSignInSucceed = onSignInSucceed,
                onSigInFailed = onSignInFailed
            )
        }
    }

    private fun loginKakaoTalk(
        onSignInSucceed: () -> Unit,
        onSignInFailed: (Exception?) -> Unit
    ) {
        // 카카오톡으로 로그인
        UserApiClient.rx.loginWithKakaoTalk(context)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { error ->
                // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                    Single.error(error)
                } else {
                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.rx.loginWithKakaoAccount(context)
                }
            }
            .subscribe({ token ->
                getClientProfileInfo(
                    onSignInSucceed = onSignInSucceed,
                    onSignInFailed = onSignInFailed,
                )
            }, { error ->
                onSignInFailed.invoke(Exception(error.message))
            })
            .addTo(disposables)
    }

    private fun loginKakaoAccount(
        onSignInSucceed: () -> Unit,
        onSigInFailed: (Exception?) -> Unit
    ) {
        UserApiClient.rx.loginWithKakaoAccount(context)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                getClientProfileInfo(
                    onSignInSucceed = onSignInSucceed,
                    onSignInFailed = onSigInFailed,
                )
            }, { error -> onSigInFailed.invoke(Exception(error.message)) })
            .addTo(disposables)
    }

    private fun getClientProfileInfo(
        onSignInSucceed: () -> Unit,
        onSignInFailed: (Exception?) -> Unit
    ){
        UserApiClient.rx.me()
            .flatMap { user ->
                val kakaoAccount = user.kakaoAccount
                if (kakaoAccount == null) {
                    Single.just(null)
                } else {
                    val email = kakaoAccount.email
                    val nickname = user.kakaoAccount?.profile?.nickname
                    val kakaoUid = user.id
                    val profileUri = kakaoAccount.profile?.thumbnailImageUrl

                    Single.just(KakaoUser(email, kakaoUid.toString(), nickname, profileUri))
                }
            }
            .retryWhen(
                // InsufficientScope 에러에 대해 추가 동의 후 재요청
                RxAuthOperations.instance.incrementalAuthorizationRequired(context)
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it != null) {
                    if (it.email != null && it.kakaoUid != null) {
                        val email = it.email
                        val kakaoUid = it.kakaoUid
                        val nickname = it.nickname ?: ""
                        val profileUri = it.profileUri ?: ""

                        AccountManager.createUserWithEmailAndPassword(
                            email = email,
                            password = kakaoUid,
                            platform = PlatForm.KAKAO,
                            nickname = nickname,
                            profileUri = profileUri,
                            onSignInSucceed = onSignInSucceed,
                            onSignInFailed = onSignInFailed
                        )
                    } else {
                        onSignInFailed.invoke(Exception("email or kakaoUid is null"))
                    }
                } else {
                    onSignInFailed.invoke(Exception("kakaoUser is null"))
                }
            }, { onSignInFailed.invoke(Exception(it.message)) })
            .addTo(disposables)
    }

    fun release() {
        disposables.dispose()
    }

    fun logout() {
        UserApiClient.rx.logout()
    }

    data class KakaoUser(
        val email: String?,
        val kakaoUid: String?,
        val nickname: String?,
        val profileUri: String?
    )
}