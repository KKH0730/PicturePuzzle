package com.seno.game.ui.main.home.game.diff_picture.list

import android.content.Context
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.seno.game.R
import com.seno.game.base.BaseComposeActivity
import com.seno.game.extensions.clearMemoryCache
import com.seno.game.extensions.getImageDate
import com.seno.game.extensions.getTodayDate
import com.seno.game.extensions.parseImageDate
import com.seno.game.extensions.saveDiskCacheData
import com.seno.game.extensions.snackbar
import com.seno.game.extensions.startActivity
import com.seno.game.prefs.PrefsManager
import com.seno.game.ui.main.home.game.diff_picture.list.screen.DPSinglePlayListScreen
import com.seno.game.ui.main.home.game.diff_picture.single.DPSinglePlayActivity
import com.seno.game.ui.view.NewMonthAlertDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DPSinglePlayListActivity : BaseComposeActivity(
    isLightStatusBar = true,
    isLightNavigationBar = false
) {
    private val viewModel by viewModels<DiffPictureSingleGameViewModel>()
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let { intent ->
                    lifecycleScope.launch {
                        viewModel.reqUpdateSavedGameInfo()

                        val isStartNextGame = intent.getBooleanExtra("isStartNextGame", false)
                        val currentRoundPosition = intent.getIntExtra(DPSinglePlayActivity.CURRENT_ROUND_POSITION, -1)
                        val finalRoundPosition = intent.getIntExtra(DPSinglePlayActivity.FINAL_ROUND_POSITION, -1)

                        if (currentRoundPosition != -1 && finalRoundPosition != -1) {
                            if (currentRoundPosition == finalRoundPosition) {
                                viewModel.setNextStage()
                            }
                            if (isStartNextGame) {
                                viewModel.startNextGame(
                                    currentRoundPosition = currentRoundPosition,
                                    finalRoundPosition = finalRoundPosition
                                )
                            }
                            viewModel.refreshGameList()
                        }
                    }
                }
            }
        }

    @Composable
    override fun ComposeContent() {
        startObserve()

        Surface(Modifier.fillMaxSize()) {
            DPSinglePlayListScreen(
                stageInfos = viewModel.gameList.collectAsState().value,
                stage = viewModel.currentStage.collectAsState().value,
                enablePlayButton = viewModel.enablePlayButton.collectAsState().value,
                onChangedStage = viewModel::onChangedPage,
                onClickBack = { finish() },
                onClickGameItem = { dPSingleGame -> viewModel.syncGameItem(selectedItem = dPSingleGame) },
                onClickPlayButton = { viewModel.startGame() },
                onChangedHeartTime = { viewModel.reqUpdateSavedGameInfo() }
            )
        }
    }

    private fun startObserve() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.currentGameRound.collect {
                        it.images.saveDiskCacheData()

                        if (PrefsManager.recentSinglePlayDate.parseImageDate() != getImageDate()) {
                            onResume()
                            return@collect
                        } else {
                            PrefsManager.recentSinglePlayDate = getTodayDate()
                        }

                        DPSinglePlayActivity.start(
                            context = this@DPSinglePlayListActivity,
                            stagePosition = it.currentStagePosition,
                            currentRoundPosition = it.currentRoundPosition,
                            finalRoundPosition = it.finalRoundPosition,
                            image1 = it.images?.first ?: "",
                            image2 = it.images?.second ?: "",
                            launcher = launcher
                        )
                        overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit)

                        viewModel.updateEnableUpdateButton(enable = true)
                    }
                }

                launch {
                    viewModel.message.collect {
                        viewModel.updateEnableUpdateButton(enable = true)
                        snackbar(it)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (PrefsManager.recentSinglePlayDate.parseImageDate() != getImageDate()) {
            NewMonthAlertDialog (
                context = this@DPSinglePlayListActivity,
                onConfirm = {
                    PrefsManager.clearSinglePlayData(currentTimeMillis = System.currentTimeMillis())
                    clearMemoryCache()
                    restartApp(this@DPSinglePlayListActivity)
                }).show()
        }
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(DPSinglePlayListActivity::class.java)
        }
    }
}