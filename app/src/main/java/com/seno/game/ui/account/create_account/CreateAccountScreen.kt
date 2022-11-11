package com.seno.game.ui.account.create_account

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.google.firebase.auth.FacebookAuthProvider
import com.seno.game.R
import com.seno.game.extensions.toast
import com.seno.game.manager.*
import com.seno.game.prefs.PrefsManager
import com.seno.game.ui.account.create_account.component.SnsLoginButton
import timber.log.Timber

@Composable
fun CreateAccountScreen(
    facebookAccountManager: FacebookAccountManager,
    googleAccountManager: GoogleAccountManager
) {
    Column(modifier = Modifier.fillMaxSize()) {
        FaceBookLoginModule(facebookAccountManager = facebookAccountManager)
        GoogleLoginModule(googleAccountManager = googleAccountManager)
    }
}

@Composable
fun FaceBookLoginModule(facebookAccountManager: FacebookAccountManager) {
    val context = LocalContext.current

    SnsLoginButton(
        snsImage = painterResource(id = R.drawable.ic_launcher_foreground),
        text = stringResource(id = R.string.account_facebook_login)
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
fun GoogleLoginModule(googleAccountManager: GoogleAccountManager) {
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
        }
    }

    SnsLoginButton(
        snsImage = painterResource(id = R.drawable.ic_launcher_foreground),
        text = stringResource(id = R.string.account_google_login)
    ) {
        googleAccountManager.login(launcher = launcher)
    }
}