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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.seno.game.R
import com.seno.game.extensions.*
import com.seno.game.theme.AppTheme
import com.seno.game.ui.game.diff_picture.list.screen.DPSinglePlayListScreen
import com.seno.game.ui.game.diff_picture.single.DPSinglePlayActivity
import kotlinx.coroutines.launch

class DPSinglePlayListActivity : AppCompatActivity() {
    private val viewModel by viewModels<DiffPictureSingleGameViewModel>()
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                result.data?.let { intent ->
                    val isStartNextGame = intent.getBooleanExtra("isStartNextGame", false)
                    val currentRoundPosition = intent.getIntExtra(DPSinglePlayActivity.CURRENT_ROUND_POSITION, -1)
                    val finalRoundPosition = intent.getIntExtra(DPSinglePlayActivity.FINAL_ROUND_POSITION, -1)
                    viewModel.setNextStage(currentRoundPosition = currentRoundPosition, finalRoundPosition = finalRoundPosition)

                    if (isStartNextGame) {
                        if (currentRoundPosition != -1 && finalRoundPosition != -1) {
                            viewModel.startNextGame(
                                currentRoundPosition = currentRoundPosition,
                                finalRoundPosition = finalRoundPosition
                            )
                        }
                    } else {
                        viewModel.refreshGameList()
                    }
                }
            }
        }

    @SuppressLint("StateFlowValueCalledInComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startObserve()

        setContent {
            AppTheme {
                Surface(Modifier.fillMaxSize()) {
                    DPSinglePlayListScreen(
                        stageInfos = viewModel.gameList.collectAsState().value,
                        stage = viewModel.currentStage.collectAsState().value,
                        onChangedStage = viewModel::onChangedPage,
                        onClickBack = { finish() },
                        onClickGameItem = { dPSingleGame -> viewModel.syncGameItem(selectedItem = dPSingleGame) },
                        onClickPlayButton = { viewModel.startGame() },
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
                        context = this@DPSinglePlayListActivity,
                        stagePosition = it.currentStagePosition,
                        currentRoundPosition = it.currentRoundPosition,
                        finalRoundPosition = it.finalRoundPosition,
                        launcher = launcher
                    )
                    overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit)
                }
            }
        }
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(DPSinglePlayListActivity::class.java)
        }
    }
}