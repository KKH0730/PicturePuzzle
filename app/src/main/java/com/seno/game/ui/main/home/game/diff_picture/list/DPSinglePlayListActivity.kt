package com.seno.game.ui.main.home.game.diff_picture.list

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.seno.game.R
import com.seno.game.extensions.clearDiskCache
import com.seno.game.ui.base.BaseComposeActivity
import com.seno.game.extensions.clearMemoryCache
import com.seno.game.extensions.getImageDate
import com.seno.game.extensions.getTodayDate
import com.seno.game.extensions.parseImageDate
import com.seno.game.extensions.saveDiskCacheData
import com.seno.game.extensions.snackbar
import com.seno.game.extensions.safeStartActivity
import com.seno.game.extensions.startActivityAnimation
import com.seno.game.prefs.PrefsManager
import com.seno.game.ui.component.CommonAlertDialog
import com.seno.game.ui.main.home.game.diff_picture.list.screen.DPSinglePlayListScreen
import com.seno.game.ui.main.home.game.diff_picture.single.DPSinglePlayActivity
import com.seno.game.ui.view.NewMonthAlertDialog
import com.seno.game.util.ad.AdmobRewardedAdUtil
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

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this@DPSinglePlayListActivity.startActivityAnimation(isOpen = true, openEnterAnim = R.anim.slide_right_enter, openExitAnim = R.anim.slide_right_exit)
        startObserve()

    }

    @Composable
    override fun ComposeContent() {
        val admobRewardedAdUtil by remember { mutableStateOf(AdmobRewardedAdUtil(activity = this@DPSinglePlayListActivity)) }
        val isShowAdPopup = viewModel.isShowAdPopup.collectAsStateWithLifecycle().value

        Surface(Modifier.fillMaxSize()) {
            if (isShowAdPopup) {
                CommonAlertDialog(
                    title = getString(R.string.diff_game_no_heart_ad_popup_title),
                    content = getString(R.string.diff_game_no_heart_ad_popup_content),
                    dismissText = getString(R.string.home_logout_n),
                    confirmText = getString(R.string.home_logout_y),
                    onClickDismiss = {
                        viewModel.showAdPopup(isShow = false)
                        viewModel.showSnackMessage(getString(R.string.diff_game_no_heart))
                    },
                    onClickConfirm = {
                        viewModel.showAdPopup(isShow = false)
                        admobRewardedAdUtil.loadRewardedAd(
                            onAdFailedToLoad = {},
                            onAdLoaded = { admobRewardedAdUtil.showRewardedAd(onAdDismissedFullScreenContent = { viewModel.startGame(isCheckHeartCount = false) }) }
                        )
                    }
                )
            }

            DPSinglePlayListScreen(
                stageInfos = viewModel.gameList.collectAsStateWithLifecycle().value,
                stage = viewModel.currentStage.collectAsStateWithLifecycle().value,
                onChangedStage = viewModel::onChangedPage,
                onClickBack = { finish() },
                onClickGameItem = { viewModel.startGame(isCheckHeartCount = false) },
                onChangedHeartTime = { viewModel.reqUpdateSavedGameInfo() }
            )
        }
    }

    @SuppressLint("ResourceType")
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
                        this@DPSinglePlayListActivity.startActivityAnimation(isOpen = true, openEnterAnim = R.anim.slide_right_enter, openExitAnim = R.anim.slide_right_exit)
                    }
                }

                launch {
                    viewModel.message.collect {
                        snackbar(it)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (PrefsManager.recentSinglePlayDate.parseImageDate() != getImageDate()) {
            NewMonthAlertDialog(
                context = this@DPSinglePlayListActivity,
                onConfirm = {
                    PrefsManager.clearSinglePlayData(currentTimeMillis = System.currentTimeMillis())
                    clearMemoryCache()
                    clearDiskCache()
                    restartApp(this@DPSinglePlayListActivity)
                }).show()
        }
    }

    companion object {
        fun start(context: Context) {
            context.safeStartActivity(DPSinglePlayListActivity::class.java)
        }
    }
}