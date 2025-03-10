package com.seno.game.ui.account.my_profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.seno.game.extensions.startActivity
import com.seno.game.theme.AppTheme
import com.seno.game.ui.account.sign_gate.SignGateActivity

class MyProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                Surface(Modifier.fillMaxSize()) {
                    MyProfileScreen(
                        onClickClose = { finish() },
                        onClickLogin = { startActivity(SignGateActivity::class.java) },
                        onClickCreateAccount = {}
                    )
                }
            }
        }
    }
}