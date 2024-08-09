package com.seno.game.ui.account.sign_gate.component

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FacebookAuthProvider
import com.navercorp.nid.NaverIdLoginSDK
import com.seno.game.R
import com.seno.game.extensions.noRippleClickable
import com.seno.game.extensions.toast
import com.seno.game.manager.*
import timber.log.Timber

@Composable
fun SocialLoginContainer(
    googleAccountManager: GoogleAccountManager,
    facebookAccountManager: FacebookAccountManager,
    naverAccountManager: NaverAccountManager,
    kakaoAccountManager: KakaoAccountManager,
    onClickSocialLogin: () -> Unit,
    onSignInSucceed: () -> Unit,
    onSignInFailed: (java.lang.Exception?) -> Unit
) {

    Row(
        horizontalArrangement = Arrangement.spacedBy(space = 22.dp),
    ) {
        GoogleLoginButton(
            googleAccountManager = googleAccountManager,
            onClickSocialLogin = onClickSocialLogin,
            onSignInSucceed = onSignInSucceed,
            onSignInFailed = onSignInFailed,
        )
        KakaoLoginButton(
            kakaoAccountManager = kakaoAccountManager,
            onClickSocialLogin = onClickSocialLogin,
            onSignInSucceed = onSignInSucceed,
            onSignInFailed = onSignInFailed
        )

        NaverLoginButton(
            naverAccountManager = naverAccountManager,
            onClickSocialLogin = onClickSocialLogin,
            onSignInSucceed = onSignInSucceed,
            onSignInFailed = onSignInFailed
        )
        FaceBookLoginButton(
            facebookAccountManager = facebookAccountManager,
            onClickSocialLogin = onClickSocialLogin,
            onSignInSucceed = onSignInSucceed,
            onSignInFailed = onSignInFailed
        )
    }
}

@Composable
fun GoogleLoginButton(
    googleAccountManager: GoogleAccountManager,
    onClickSocialLogin: () -> Unit,
    onSignInSucceed: () -> Unit,
    onSignInFailed: (java.lang.Exception?) -> Unit
) {
    val launcher: ManagedActivityResultLauncher<Intent, ActivityResult> =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                googleAccountManager.onActivityResult(
                    data = it.data,
                    onSocialSignInCallbackListener = object : OnSocialSignInCallbackListener {
                        override fun signInWithCredential(idToken: String?) {
                            try {
                                idToken?.let { token ->
                                    val authCredential = googleAccountManager.getAuthCredential(token)
                                    AccountManager.signInWithCredential(
                                        credential = authCredential,
                                        platform = PlatForm.GOOGLE,
                                        onSignInSucceed = onSignInSucceed,
                                        onSignInFailed = onSignInFailed
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Timber.e(e)
                            }
                        }

                        override fun onError(e: Exception?) {
                            Timber.e(e)
                        }
                    }
                )
            }
        }

    SnsLoginButton(snsImage = painterResource(id = R.drawable.ic_sns_google)) {
        onClickSocialLogin.invoke()
        googleAccountManager.login(launcher = launcher)
    }
}

@Composable
fun KakaoLoginButton(
    kakaoAccountManager: KakaoAccountManager,
    onClickSocialLogin: () -> Unit,
    onSignInSucceed: () -> Unit,
    onSignInFailed: (java.lang.Exception?) -> Unit
) {
    SnsLoginButton(
        snsImage = painterResource(id = R.drawable.ic_sns_kakao)
    ) {
        onClickSocialLogin.invoke()
        kakaoAccountManager.login(
            onSignInSucceed = onSignInSucceed,
            onSignInFailed = onSignInFailed
        )
    }
}

@Composable
fun NaverLoginButton(
    naverAccountManager: NaverAccountManager,
    onClickSocialLogin: () -> Unit,
    onSignInSucceed: () -> Unit,
    onSignInFailed: (java.lang.Exception?) -> Unit
) {
    val context = LocalContext.current
    val launcher: ManagedActivityResultLauncher<Intent, ActivityResult> =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            when (it.resultCode) {
                Activity.RESULT_OK -> {
                    naverAccountManager.onActivityResult(
                        onSignInSucceed = onSignInSucceed,
                        onSignInFailed = onSignInFailed
                    )
                }
                Activity.RESULT_CANCELED -> {
                    // 실패 or 에러
                    val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                    val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                }
            }
        }


    SnsLoginButton(
        snsImage = painterResource(id = R.drawable.ic_sns_naver)
    ) {
        onClickSocialLogin.invoke()
        naverAccountManager.login(
            context = context,
            launcher = launcher,
            onSignInSucceed = {
                context.toast("로그인 성공")
            },
            onSigInFailed = {
                context.toast("로그인 실패")
            },
        )
    }
}

@Composable
fun FaceBookLoginButton(
    facebookAccountManager: FacebookAccountManager,
    onClickSocialLogin: () -> Unit,
    onSignInSucceed: () -> Unit,
    onSignInFailed: (java.lang.Exception?) -> Unit
) {
    SnsLoginButton(
        snsImage = painterResource(id = R.drawable.ic_sns_facebook)
    ) {
        onClickSocialLogin.invoke()
        facebookAccountManager.login(
            onSocialLoginCallbackListener = object : OnSocialSignInCallbackListener {
                override fun signInWithCredential(idToken: String?) {
                    try {
                        idToken?.let { token ->
                            val credential = FacebookAuthProvider.getCredential(token)
                            AccountManager.signInWithCredential(
                                credential = credential,
                                platform = PlatForm.FACEBOOK,
                                onSignInSucceed = onSignInSucceed,
                                onSignInFailed = onSignInFailed
                            )
                        }
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                }

                override fun onError(e: Exception?) {
                    Timber.e(e)
                    onSignInFailed.invoke(e)
                }
            }
        )
    }
}

@Composable
fun SnsLoginButton(
    snsImage: Painter,
    onClick: () -> Unit,
) {
    Image(
        painter = snsImage,
        contentDescription = null,
        modifier = Modifier
            .size(size = 40.dp)
            .noRippleClickable { onClick.invoke() }
    )
}

