package com.seno.game.ui.account.sign_gate

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.seno.game.extensions.toast
import com.seno.game.manager.*
import com.seno.game.theme.AppTheme
import timber.log.Timber

class SignGateActivity : AppCompatActivity() {
    private lateinit var googleAccountManager: GoogleAccountManager
    private lateinit var facebookAccountManager: FacebookAccountManager
    private lateinit var naverAccountManager: NaverAccountManager
    private lateinit var kakaoAccountManager: KakaoAccountManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                Surface(Modifier.fillMaxSize()) {
                    googleAccountManager = GoogleAccountManager(activity = this@SignGateActivity)
                    facebookAccountManager = FacebookAccountManager(activity = this@SignGateActivity)
                    naverAccountManager = NaverAccountManager()
                    kakaoAccountManager = KakaoAccountManager(context  = this@SignGateActivity)

                   SignGateScreen(
                       googleAccountManager = googleAccountManager,
                       facebookAccountManager = facebookAccountManager,
                       naverAccountManager = naverAccountManager,
                       kakaoAccountManager = kakaoAccountManager,
                       onClickClose = { finish() }
                   )
                }
            }
        }
    }

    override fun onDestroy() {
        facebookAccountManager.removeCallback()
        kakaoAccountManager.release()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == LOGIN_FACEBOOK_REQUEST_CODE) {
            facebookAccountManager.onActivityResult(requestCode, resultCode, data)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}