package com.seno.game.ui.splash

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import com.seno.game.R
import com.seno.game.extensions.createRandomNickname
import com.seno.game.extensions.startActivity
import com.seno.game.manager.AccountManager
import com.seno.game.prefs.PrefsManager
import com.seno.game.theme.AppTheme
import com.seno.game.ui.main.MainActivity
import hideNavigationBar

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.hideNavigationBar()
        createRandomNickname()

        setContent {
            AppTheme {
                Surface(Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = colorResource(id = R.color.area_blue))
                    ) {
                        SplashScreen {
                            MainActivity.start(context = this@SplashActivity)
                            overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit)
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun createRandomNickname() {
        if (PrefsManager.nickname.isEmpty() && AccountManager.isUser || !AccountManager.isUser) {
            resources.createRandomNickname()
        }
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(SplashActivity::class.java)
        }
    }
}