package com.seno.game.manager

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.seno.game.R
import com.seno.game.extensions.getString
import timber.log.Timber

class GoogleAccountManager(private val activity: Activity) {

    private val signInClient: GoogleSignInClient = GoogleSignIn.getClient(
        activity,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()
    )

    fun login(launcher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
        launcher.launch(signInClient.signInIntent)
    }

    fun logout(logoutListener: LogoutListener) {
        signInClient.signOut().addOnSuccessListener { logoutListener.onSuccessLogout() }
    }

    fun onActivityResult(
        data: Intent?,
        onSocialSignInCallbackListener: OnSocialSignInCallbackListener?,
    ) {
        try {
            GoogleSignIn
                .getSignedInAccountFromIntent(data)
                .getResult(ApiException::class.java)
                ?.idToken
                ?.let { onSocialSignInCallbackListener?.signInWithCredential(it) }
                ?: onSocialSignInCallbackListener?.onError(null)
        } catch (e: ApiException) {
            Timber.e(e)
            onSocialSignInCallbackListener?.onError(e)
        }
    }

    fun getAuthCredential(token: String?): AuthCredential {
        return GoogleAuthProvider.getCredential(token, null)
    }

    interface LogoutListener {
        fun onSuccessLogout()
    }

    companion object {
        const val LOGIN_GOOGLE_REQUEST_CODE = 1818
    }
}