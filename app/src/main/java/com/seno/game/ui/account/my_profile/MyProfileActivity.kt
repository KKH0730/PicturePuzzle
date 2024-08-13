package com.seno.game.ui.account.my_profile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.seno.game.extensions.startActivity
import com.seno.game.extensions.toast
import com.seno.game.theme.AppTheme
import com.seno.game.ui.account.AccountViewModel
import com.seno.game.ui.account.sign_gate.SignGateActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyProfileActivity : ComponentActivity() {
    private val accountViewModel by viewModels<AccountViewModel>()

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

        observe()
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                launch {
                    accountViewModel.message.collectLatest { toast(it) }
                }
            }
        }
    }
}