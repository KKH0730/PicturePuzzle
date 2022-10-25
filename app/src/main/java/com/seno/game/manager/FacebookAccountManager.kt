package com.seno.game.manager

import android.app.Activity
import android.content.Intent
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import timber.log.Timber

class FacebookAccountManager(private val activity: Activity) {
    private var loginManager: LoginManager = LoginManager.getInstance()
    private var callbackManager: CallbackManager = CallbackManager.Factory.create()

    fun login(onSocialLoginCallbackListener: OnSocialSignInCallbackListener) {
        loginManager.run {
            logInWithReadPermissions(activity, listOf("public_profile"))
            registerCallback(
                callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult) {
                        onSocialLoginCallbackListener.signInWithCredential(idToken = result.accessToken.token)
                    }

                    override fun onCancel() {
                        onSocialLoginCallbackListener.onError(e = null)
                    }

                    override fun onError(error: FacebookException) {
                        onSocialLoginCallbackListener.onError(e = error)
                    }
                }
            )
        }
    }

    fun logout() {
//        val accessToken = AccessToken.getCurrentAccessToken()
//        val isLoggedIn = accessToken != null && !accessToken.isExpired
//
//        if (!isLoggedIn) {
//            return
//        }

        loginManager.run {
            unregisterCallback(callbackManager)
            this.logOut()
        }
    }

    fun removeCallback() {
        loginManager.unregisterCallback(callbackManager)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        const val LOGIN_FACEBOOK_REQUEST_CODE = 64206
    }
}