package com.seno.game.ui.main.home.game.diff_picture.game_level_list

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.seno.game.R
import com.seno.game.extensions.showSnackBar
import com.seno.game.extensions.startActivity
import com.seno.game.prefs.PrefsManager
import com.seno.game.theme.AppTheme
import com.seno.game.ui.main.home.game.diff_picture.game_level_list.screen.DPSinglePlayListScreen
import com.seno.game.ui.main.home.game.diff_picture.single.DPSinglePlayActivity
import com.seno.game.util.SoundUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DPSinglePlayListActivity : AppCompatActivity() {
    private val viewModel by viewModels<DPSinglePlayListViewModel>()
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                result.data?.let { intent ->
                    if (PrefsManager.isActiveBackgroundBGM) {
                        SoundUtil.restartBackgroundBGM()
                    }

                    viewModel.reqUpdateSavedGameInfo()

                    val isStartNextGame = intent.getBooleanExtra("isStartNextGame", false)
                    val currentRoundPosition = intent.getIntExtra(DPSinglePlayActivity.CURRENT_ROUND_POSITION, -1)
                    val finalRoundPosition = intent.getIntExtra(DPSinglePlayActivity.FINAL_ROUND_POSITION, -1)

                    if (currentRoundPosition != -1 && finalRoundPosition != -1) {
                        if (currentRoundPosition == finalRoundPosition) {
                            viewModel.setNextStage()
                        }
                        if (isStartNextGame) {
                            if (PrefsManager.diffPictureHeartCount == 0) {
                                window.decorView.rootView.showSnackBar(message = getString(R.string.diff_game_no_heart))
                            } else {
                                viewModel.startNextGame(
                                    currentRoundPosition = currentRoundPosition,
                                    finalRoundPosition = finalRoundPosition
                                )
                            }
                        }
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
                        stageInfos = viewModel.gameList.collectAsStateWithLifecycle().value,
                        stage = viewModel.currentStage.collectAsStateWithLifecycle().value,
                        enablePlayButton = viewModel.enablePlayButton.collectAsStateWithLifecycle().value,
                        onChangedStage = viewModel::onChangedPage,
                        onClickBack = { finish() },
                        onClickGameItem = { dPSingleGame -> viewModel.syncGameItem(selectedItem = dPSingleGame) },
                        onClickPlayButton = { viewModel.startGame() },
                        onChangedHeartTime = { viewModel.reqUpdateSavedGameInfo() }
                    )
                }
            }
        }
    }

    @SuppressLint("ShowToast")
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

                    viewModel.updateEnableUpdateButton(enable = true)
                }
            }
            launch {
                viewModel.message.collect {
                    viewModel.updateEnableUpdateButton(enable = true)
                    window.decorView.rootView.showSnackBar(message = it)
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