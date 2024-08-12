package com.seno.game.ui.account.my_profile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.seno.game.extensions.startActivity
import com.seno.game.manager.FacebookAccountManager
import com.seno.game.manager.GoogleAccountManager
import com.seno.game.manager.KakaoAccountManager
import com.seno.game.manager.NaverAccountManager
import com.seno.game.theme.AppTheme
import com.seno.game.ui.account.sign_gate.SignGateActivity

class MyProfileActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                Surface(Modifier.fillMaxSize()) {
                    MyProfileScreen(
                        onClickClose = { finish() },
                        onClickLogin = { startActivity(SignGateActivity::class.java) }
                    )
                }
            }
        }
    }
}