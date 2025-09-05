package com.seno.game.ui.main.home.game.diff_picture.multi.entry.lobby

import android.content.Context
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.seno.game.ui.base.BaseComposeActivity
import com.seno.game.extensions.createQRCode
import com.seno.game.extensions.safeStartActivity
import com.seno.game.ui.main.home.game.diff_picture.multi.entry.lobby.screen.LobbyRoomScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LobbyActivity : BaseComposeActivity(
    isLightStatusBar = true,
    isLightNavigationBar = false
) {
    private val viewModel by viewModels<LobbyViewModel>()
    private val path: String by lazy { intent.getStringExtra(PATH) ?: "" }

    @Composable
    override fun ComposeContent() {
        val qrBitmap = path.createQRCode()
        val ownerUid = try {
            path.split("_")[0]
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }

        if (qrBitmap == null || ownerUid.isEmpty()) return

        LobbyRoomScreen(
            ownerUid = ownerUid,
            qrBitmap = qrBitmap,
            players = viewModel.players.collectAsStateWithLifecycle().value?.players ?: listOf(),
            isShowQuitDialog = viewModel.isShowQuitDialog.collectAsStateWithLifecycle().value,
            onClickQuit = { finish() },
            onDismissQuitDialog = {},
            onClickBack = {
                viewModel.updateMultiGamePlayer(isAdd = false)
                finish()
            }
        )

        startObserve()
        setOnBackPressedEvent()
    }

    private fun startObserve() {
        lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.CREATED) {
                launch { viewModel.startMultiGame.collectLatest {  } }
            }
        }
    }

    private fun setOnBackPressedEvent() {
        onBackPressedDispatcher.addCallback(this@LobbyActivity, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        })
    }

    companion object {
        const val PATH = "path"

        fun start(context: Context, path: String) {
            context.safeStartActivity(LobbyActivity::class.java) {
                putExtra(PATH, path)
            }
        }
    }
}
