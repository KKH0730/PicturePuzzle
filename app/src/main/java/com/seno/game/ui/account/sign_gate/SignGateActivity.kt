package com.seno.game.ui.account.sign_gate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.core.os.postDelayed
import com.seno.game.extensions.showSnackBar
import com.seno.game.extensions.startActivity
import com.seno.game.extensions.toast
import com.seno.game.manager.*
import com.seno.game.theme.AppTheme
import kotlinx.coroutines.delay

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
                    kakaoAccountManager = KakaoAccountManager(context = this@SignGateActivity)

                    SignGateScreen(
                        googleAccountManager = googleAccountManager,
                        facebookAccountManager = facebookAccountManager,
                        naverAccountManager = naverAccountManager,
                        kakaoAccountManager = kakaoAccountManager,
                        onSignInSucceed = {
                            runOnUiThread {
                                window.decorView.rootView.showSnackBar(message = "로그인 성공")
                                Handler(Looper.getMainLooper()).postDelayed(500) {
                                    finish()
                                }
                            }
                        },
                        onSignInFailed = {
                            runOnUiThread {
                                window.decorView.rootView.showSnackBar(message = "로그인 실패")
                            }
                        },
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

    companion object {
        fun start(context: Context) {
            context.startActivity(SignGateActivity::class.java)
        }
    }
}