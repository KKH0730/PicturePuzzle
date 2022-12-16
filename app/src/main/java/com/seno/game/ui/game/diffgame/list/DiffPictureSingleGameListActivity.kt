package com.seno.game.ui.game.diffgame.list

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.seno.game.R
import com.seno.game.extensions.startActivity
import com.seno.game.theme.AppTheme
import com.seno.game.ui.game.diffgame.list.component.GridGameLevelList
import com.seno.game.ui.game.diffgame.list.model.DPSingleGame
import com.seno.game.ui.game.diffgame.single.DiffPictureSingleGameActivity
import kotlinx.coroutines.launch

class DiffPictureSingleGameListActivity : AppCompatActivity() {
    private val viewModel by viewModels<DiffPictureSingleGameViewModel>()
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            viewModel.startNextGame()
        }
    }

    @SuppressLint("StateFlowValueCalledInComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startObserve()

        setContent {
            AppTheme {
                Surface(Modifier.fillMaxSize()) {
                    DiffPictureSingleGameListScreen(
                        gameList = viewModel.gameList.value,
                        onClickItem = { viewModel.startGame(position = it) }
                    )
                }
            }
        }
    }

    private fun startObserve() {
        lifecycleScope.launch {
            launch {
                viewModel.currentGameRound.collect {
                    if (it != null) {
                        DiffPictureSingleGameActivity.start(
                            context = this@DiffPictureSingleGameListActivity,
                            position = it,
                            launcher = launcher
                        )
                        overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit)
                    }
                }
            }
        }
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(DiffPictureSingleGameListActivity::class.java)
        }
    }
}


@Composable
fun DiffPictureSingleGameListScreen(gameList: List<DPSingleGame>, onClickItem: (position: Int) -> Unit) {
    GridGameLevelList(gameList = gameList, onClickItem = onClickItem)
}