package com.seno.game.ui.account.my_profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.seno.game.core.ResultConstants
import com.seno.game.extensions.safeStartActivity
import com.seno.game.extensions.toast
import com.seno.game.theme.AppTheme
import com.seno.game.ui.account.AccountViewModel
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
                        onCompleteWithdrawal = {
                            setResult(ResultConstants.RESULT_WITHDRAWAL)
                            finish()
                        }
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

    companion object {

        fun start(context: Context, launcher: ActivityResultLauncher<Intent>) {
            context.safeStartActivity(MyProfileActivity::class.java, launcher)
        }
    }
}