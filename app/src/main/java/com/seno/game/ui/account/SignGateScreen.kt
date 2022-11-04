package com.seno.game.ui.account

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.viewinterop.AndroidView
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.seno.game.R
import com.seno.game.extensions.noRippleClickable
import com.seno.game.extensions.textDp
import com.seno.game.extensions.toast
import com.seno.game.manager.AccountManager
import com.seno.game.manager.FacebookAccountManager
import com.seno.game.manager.GoogleAccountManager
import com.seno.game.manager.OnSocialSignInCallbackListener
import timber.log.Timber

@Composable
fun SignGateScreen(facebookAccountManager: FacebookAccountManager) {
    val accountState = rememberAccountState()
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize()) {
        FaceBookLoginModule(
            facebookAccountManager = facebookAccountManager,
            context = context
        )
        GoogleLoginModule(
            context = context
        )
    }
}

@Composable
fun FaceBookLoginModule(facebookAccountManager: FacebookAccountManager, context: Context) {
    SnsLoginButton(
        snsImage = painterResource(id = R.drawable.ic_launcher_foreground),
        text = stringResource(id = R.string.account_facebook_login)
    ) {
        facebookAccountManager.login(
            onSocialLoginCallbackListener = object : OnSocialSignInCallbackListener {
                override fun signInWithCredential(idToken: String?) {
                    Timber.e("kkh 11")
                    try {
                        idToken?.let { token ->
                            val credential = FacebookAuthProvider.getCredential(token)
                            AccountManager.signInWithCredential(
                                credential = credential,
                                onSignInSucceed = {
                                    Timber.e("kkh 22")
                                    context.toast("로그인 성공")
                                },
                                onSigInFailed = {
                                    Timber.e("kkh 33")
                                    context.toast("로그인 실패")
                                }
                            )
                        }
                    } catch (e: Exception) {
                        Timber.e("kkh 44 : ${e.message}")
                    }
                }

                override fun onError(e: Exception?) {
                    Timber.e("kkh onError : ${e?.message}")
                    Timber.e(e)
                }
            }
        )
    }
}

@Composable
fun GoogleLoginModule(context: Context) {
    val googleAccountManager = GoogleAccountManager(activity = context as SignGateActivity)
    val launcher: ManagedActivityResultLauncher<Intent, ActivityResult> = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            googleAccountManager.onActivityResult(
                data = it.data,
                onSocialSignInCallbackListener = object: OnSocialSignInCallbackListener {
                    override fun signInWithCredential(idToken: String?) {
                        val authCredential = googleAccountManager.getAuthCredential(idToken)
                        try {
                            AccountManager.signInWithCredential(
                                credential = authCredential,
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

                    }
                }
            )
        }
    }

    SnsLoginButton(
        snsImage = painterResource(id = R.drawable.ic_launcher_foreground),
        text = stringResource(id = R.string.account_google_login)
    ) {
        googleAccountManager.login(launcher)
    }
}


@Composable
fun SnsLoginButton(
    snsImage: Painter,
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(fraction = 0.8f)
            .noRippleClickable(onClick = onClick)
    ) {
        Icon(
            painter = snsImage,
            contentDescription = null,
            modifier = Modifier.align(Alignment.CenterStart)
        )
        Text(
            text = text,
            style = TextStyle(
                fontSize = 14.textDp,
                color = Color.Black
            ),
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

