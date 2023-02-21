package com.seno.game.manager

import android.app.Activity
import android.content.Intent
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.seno.game.R

const val LOGIN_FACEBOOK_REQUEST_CODE = 64206

class FacebookAccountManager(private val activity: Activity) {

    private var loginManager: LoginManager = LoginManager.getInstance()
    private var callbackManager: CallbackManager = CallbackManager.Factory.create()

    fun login(onSocialLoginCallbackListener: OnSocialSignInCallbackListener) {
        loginManager.run {
            logInWithReadPermissions(activity, arrayListOf(*activity.resources.getStringArray(R.array.login_fbreadpermissions)))
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
}