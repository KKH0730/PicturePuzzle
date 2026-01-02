package com.seno.game.ui.account.sign_gate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.seno.game.R
import com.seno.game.core.ResultConstants
import com.seno.game.extensions.snackbar
import com.seno.game.extensions.safeStartActivity
import com.seno.game.extensions.toast
import com.seno.game.manager.*
import com.seno.game.theme.AppTheme

class SignGateActivity : AppCompatActivity() {
    private lateinit var googleAccountManager: GoogleAccountManager
    private lateinit var naverAccountManager: NaverAccountManager
    private lateinit var kakaoAccountManager: KakaoAccountManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                Surface(Modifier.fillMaxSize()) {
                    googleAccountManager = GoogleAccountManager()
                    naverAccountManager = NaverAccountManager()
                    kakaoAccountManager = KakaoAccountManager(context = this@SignGateActivity)

                    SignGateScreen(
                        googleAccountManager = googleAccountManager,
                        naverAccountManager = naverAccountManager,
                        kakaoAccountManager = kakaoAccountManager,
                        onSignInSucceed = {
                            runOnUiThread {
                                toast("로그인 성공")
                                val resultIntent = Intent().apply {
                                    putExtra(PATH, intent.getStringExtra(PATH) ?: "")
                                }
                                setResult(intent.getIntExtra(RESULT_CODE, ResultConstants.RESULT_LOGIN), resultIntent)
                                finish()
                            }
                        },
                        onSignInFailed = { exception ->
                            runOnUiThread {
                                if (exception is FirebaseAuthUserCollisionException) {
                                    snackbar(message = getString(R.string.alert_duplicated_id))
                                } else {
                                    snackbar(message = "로그인 실패")
                                }
                            }
                        },
                        onClickClose = { finish() }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        kakaoAccountManager.release()
        super.onDestroy()
    }

    companion object {
        const val PATH = "path"
        const val RESULT_CODE = "resultCode"

        fun start(context: Context) {
            context.safeStartActivity(SignGateActivity::class.java)
        }

        fun start(context: Context, path: String = "") {
            context.safeStartActivity(SignGateActivity::class.java) {
                putExtra(PATH, path)
            }
        }

        fun start(context: Context, path: String = "", resultCode: Int, launcher: ActivityResultLauncher<Intent>) {
            context.safeStartActivity(SignGateActivity::class.java, launcher) {
                putExtra(PATH, path)
                putExtra(RESULT_CODE, resultCode)
            }
        }
    }
}