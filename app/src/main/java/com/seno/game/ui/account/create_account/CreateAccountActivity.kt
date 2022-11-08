package com.seno.game.ui.account.create_account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.seno.game.manager.FacebookAccountManager
import com.seno.game.manager.GoogleAccountManager
import com.seno.game.manager.LOGIN_FACEBOOK_REQUEST_CODE
import com.seno.game.theme.AppTheme

class CreateAccountActivity : AppCompatActivity() {
    private val facebookAccountManager = FacebookAccountManager(activity = this@CreateAccountActivity)
    private val googleAccountManager = GoogleAccountManager(activity = this@CreateAccountActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                Surface(Modifier.fillMaxSize()) {
                    CreateAccountScreen(
                        facebookAccountManager = facebookAccountManager,
                        googleAccountManager = googleAccountManager
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        facebookAccountManager.removeCallback()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == LOGIN_FACEBOOK_REQUEST_CODE) {
            facebookAccountManager.onActivityResult(requestCode, resultCode, data)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}