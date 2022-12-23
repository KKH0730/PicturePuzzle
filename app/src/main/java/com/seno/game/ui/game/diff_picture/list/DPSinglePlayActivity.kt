package com.seno.game.ui.game.diff_picture.list

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
import com.seno.game.ui.game.diff_picture.list.component.GridGameLevelList
import com.seno.game.ui.game.diff_picture.list.model.DPSingleGame
import com.seno.game.ui.game.diff_picture.single.DPSinglePlayActivity
import kotlinx.coroutines.launch

class DiffPictureSingleGameListActivity : AppCompatActivity() {
    private val viewModel by viewModels<DiffPictureSingleGameViewModel>()
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            viewModel.refreshGameList()

            result.data
                .takeIf { it != null }
                .let { intent.getIntExtra("roundPosition", -1) }
                .takeIf { it != -1 }
                ?.run { viewModel.startNextGame(currentGameRound = this) }
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
                        gameList = viewModel.gameList.collectAsState().value,
                        onClickItem = {
                            viewModel.startGame(selectedItem = it)
                        }
                    )
                }
            }
        }
    }

    private fun startObserve() {
        lifecycleScope.launch {
            launch {
                viewModel.currentGameRound.collect {
                    DPSinglePlayActivity.start(
                        context = this@DiffPictureSingleGameListActivity,
                        roundPosition = it.id,
                        launcher = launcher
                    )
                    overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit)
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
fun DiffPictureSingleGameListScreen(gameList: List<DPSingleGame>, onClickItem: (DPSingleGame) -> Unit) {
    GridGameLevelList(gameList = gameList, onClickItem = onClickItem)
}