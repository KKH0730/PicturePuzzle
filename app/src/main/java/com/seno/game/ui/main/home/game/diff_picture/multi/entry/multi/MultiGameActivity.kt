package com.seno.game.ui.main.home.game.diff_picture.multi.entry.multi

import android.content.Context
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.rememberNavController
import com.seno.game.extensions.safeStartActivity
import com.seno.game.navigation.NavigationGraph
import com.seno.game.navigation.NavigationRoute
import com.seno.game.ui.base.BaseComposeActivity
import com.seno.game.ui.component.BannerADView
import com.seno.game.ui.component.CommonNetworkErrorDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LobbyActivity : BaseComposeActivity(
    isLightStatusBar = true,
    isLightNavigationBar = false
) {
    private val viewModel by viewModels<MultiGameViewModel>()
    private val path: String by lazy { intent.getStringExtra(PATH) ?: "" }

    @Composable
    override fun ComposeContent() {
        val navController = rememberNavController()

        if (viewModel.networkErrorDialog.collectAsStateWithLifecycle().value) {
            CommonNetworkErrorDialog(
                onClickQuit = { finish() },
                onDismissed = {}
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            NavigationGraph(
                startRoute = NavigationRoute.LOBBY_SCREEN,
                navController = navController,
            )
            BottomBannerView(modifier = Modifier.align(alignment = Alignment.BottomCenter))
        }

        startObserve()
        setOnBackPressedEvent()
    }

    @Composable
    fun BottomBannerView(modifier: Modifier = Modifier) {
        val insets = WindowInsets.systemBars.asPaddingValues()

        Column(
            modifier = modifier.fillMaxWidth()
                .height(intrinsicSize = IntrinsicSize.Min)
                .padding(bottom = insets.calculateBottomPadding())
        ) {
            Spacer(modifier = Modifier.height(height = 20.dp))
            BannerADView(modifier = Modifier.height(height = 50.dp))
            Spacer(modifier = Modifier.height(height = 16.dp))
        }
    }

    private fun startObserve() {
        lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.CREATED) {
                launch { viewModel.startMultiGame.collectLatest {  } }

                launch { viewModel.finish.collectLatest { finish() } }
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
