package com.seno.game.manager

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.seno.game.R
import com.seno.game.extensions.getString
import timber.log.Timber


class GoogleAccountManager() {

    val googleIdOption = GetGoogleIdOption.Builder()
        // Your server's client ID, not your Android client ID.
        .setServerClientId(getString(R.string.default_web_client_id))
        // Only show accounts previously used to sign in.
        .setFilterByAuthorizedAccounts(true)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    suspend fun login(
        context: Context,
        onSignInSucceed: () -> Unit,
        onSignInFailed: (java.lang.Exception?) -> Unit
    ) {
        val credentialManager = CredentialManager.create(context)
        try {
            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = result.credential
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                val authCredential = getAuthCredential(googleIdTokenCredential.idToken)
                AccountManager.signInWithCredential(
                    credential = authCredential,
                    platform = PlatForm.GOOGLE,
                    email = googleIdTokenCredential.id,
                    nickname = googleIdTokenCredential.displayName ?: "",
                    profileUri = googleIdTokenCredential.profilePictureUri?.toString() ?: "",
                    onSignInSucceed = onSignInSucceed,
                    onSignInFailed = onSignInFailed
                )
            } else {
                Timber.e("Credential is not of type Google ID!")
            }
        } catch (e: GetCredentialException) {
            onSignInFailed(e)
        }
    }

    suspend fun suspendLogin(context: Context): AuthCredential? {
        return try {
            val credentialManager = CredentialManager.create(context)
            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = result.credential
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                getAuthCredential(googleIdTokenCredential.idToken)
            } else {
                Timber.e("Credential is not of type Google ID!")
                null
            }
        } catch (e: GetCredentialException) {
            Timber.e(e)
            null
        }
    }

    suspend fun logout(context: Context) {
        val credentialManager = CredentialManager.create(context)
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
    }

    fun getAuthCredential(token: String?): AuthCredential {
        return GoogleAuthProvider.getCredential(token, null)
    }
}