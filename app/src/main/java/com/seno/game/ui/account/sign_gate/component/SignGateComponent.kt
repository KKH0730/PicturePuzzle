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
import com.seno.game.R
import com.seno.game.extensions.noRippleClickable
import com.seno.game.extensions.toast
import com.seno.game.manager.*
import com.seno.game.prefs.PrefsManager
import com.seno.game.ui.account.sign_gate.SignGateActivity
import timber.log.Timber

@Composable
fun SocialLoginContainer(
    googleAccountManager: GoogleAccountManager,
    facebookAccountManager: FacebookAccountManager
) {

    Row(
        horizontalArrangement = Arrangement.spacedBy(space = 22.dp),
    ) {
        GoogleLoginButton(googleAccountManager = googleAccountManager)
        KakaoLoginButton()
        NaverLoginButton()
        FaceBookLoginButton(facebookAccountManager = facebookAccountManager)
    }
}

@Composable
fun GoogleLoginButton(
    googleAccountManager: GoogleAccountManager
) {
    val context = LocalContext.current
    val launcher: ManagedActivityResultLauncher<Intent, ActivityResult> = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            googleAccountManager.onActivityResult(
                data = it.data,
                onSocialSignInCallbackListener = object: OnSocialSignInCallbackListener {
                    override fun signInWithCredential(idToken: String?) {
                        val authCredential = googleAccountManager.getAuthCredential(idToken)
                        try {
                            AccountManager.signInWithCredential(
                                credential = authCredential,
                                platform = PlatForm.GOOGLE,
                                onSignInSucceed = {
                                    context.toast("로그인 성공")
                                },
                                onSigInFailed = {
                                    context.toast("로그인 실패")
                                }
                            )
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
        } else {
            Timber.e("kkh e")
        }
    }

    SnsLoginButton(snsImage = painterResource(id = R.drawable.ic_sns_google),)
    {
        googleAccountManager.login(launcher = launcher)
    }
}

@Composable
fun KakaoLoginButton() {
    SnsLoginButton(
        snsImage = painterResource(id = R.drawable.ic_sns_kakao)
    ) {
    }
}

@Composable
fun NaverLoginButton() {
    SnsLoginButton(
        snsImage = painterResource(id = R.drawable.ic_sns_naver)
    ) {
    }
}

@Composable
fun FaceBookLoginButton(facebookAccountManager: FacebookAccountManager) {
    val context = LocalContext.current

    SnsLoginButton(
        snsImage = painterResource(id = R.drawable.ic_sns_facebook)
    ) {
        facebookAccountManager.login(
            onSocialLoginCallbackListener = object : OnSocialSignInCallbackListener {
                override fun signInWithCredential(idToken: String?) {
                    try {
                        idToken?.let { token ->
                            val credential = FacebookAuthProvider.getCredential(token)
                            AccountManager.signInWithCredential(
                                credential = credential,
                                platform = PlatForm.FACEBOOK,
                                onSignInSucceed = {
                                    AccountManager.displayName?.let { name ->
                                        PrefsManager.nickname = name
                                    }
                                    context.toast("로그인 성공")
                                },
                                onSigInFailed = {
                                    context.toast("로그인 실패")
                                }
                            )
                        }
                    } catch (e: Exception) {
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


@Composable
fun SnsLoginButton(
    snsImage: Painter,
    onClick: () -> Unit
) {
    Image(
        painter = snsImage,
        contentDescription = null,
        modifier = Modifier
            .size(size = 40.dp)
            .noRippleClickable { onClick.invoke() }
    )
}

