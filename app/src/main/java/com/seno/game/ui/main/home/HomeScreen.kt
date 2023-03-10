package com.seno.game.ui.main.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seno.game.R
import com.seno.game.extensions.createRandomNickname
import com.seno.game.extensions.showSnackBar
import com.seno.game.manager.*
import com.seno.game.model.SavedGameInfo
import com.seno.game.prefs.PrefsManager
import com.seno.game.ui.account.sign_gate.SignGateActivity
import com.seno.game.ui.component.LoadingView
import com.seno.game.ui.main.LifecycleEventListener
import com.seno.game.ui.main.MainActivity
import com.seno.game.ui.main.home.component.*
import com.seno.game.ui.main.home.game.diff_picture.list.DPSinglePlayListActivity
import com.seno.game.util.SoundUtil

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val homeViewModel = hiltViewModel<HomeViewModel>()
    val homeState = rememberHomeState(
        facebookAccountManager = FacebookAccountManager(activity = context as MainActivity),
        googleAccountManager = GoogleAccountManager(activity = context),
        naverAccountManager = NaverAccountManager(),
        kakaoAccountManager = KakaoAccountManager(context = context)
    )

    HomeUI(
        savedGameInfo = homeViewModel.savedGameInfoToLocalDB.collectAsStateWithLifecycle().value,
        homeState = homeState,
        onChangedBackgroundVolume = homeViewModel::updateBackgroundVolume,
        onChangeFinishedBackgroundVolume = {
            homeViewModel.reqUpdateBackgroundVolume(
                uid = AccountManager.firebaseUid,
                volume = it.toString()
            )
        },
        onChangedEffectVolume = homeViewModel::updateEffectVolume,
        onChangeFinishedEffectVolume = {
            homeViewModel.reqUpdateEffectVolume(
                uid = AccountManager.firebaseUid,
                volume = it.toString()
            )
        },
        onChangedVibration = { homeViewModel.reqUpdateVibrationOnOff(AccountManager.firebaseUid, isVibrationOn = it) },
        onChangedPush = { homeViewModel.reqUpdatePushOnOff(AccountManager.firebaseUid, isPushOn = it) },
    )
}

@Composable
fun HomeUI(
    savedGameInfo: SavedGameInfo,
    homeState: HomeState,
    onChangedBackgroundVolume: (Float) -> Unit,
    onChangeFinishedBackgroundVolume: (Float) -> Unit,
    onChangedEffectVolume: (Float) -> Unit,
    onChangeFinishedEffectVolume: (Float) -> Unit,
    onChangedVibration: (Boolean) -> Unit,
    onChangedPush: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    (context as MainActivity).LifecycleEventListener {
        when (it) {
            Lifecycle.Event.ON_CREATE -> {}
            Lifecycle.Event.ON_START -> {}
            Lifecycle.Event.ON_RESUME -> {
                homeState.nickname.value = PrefsManager.nickname
                homeState.profile.value = PrefsManager.profileUri
                SoundUtil.restartBackgroundBGM()
            }
            Lifecycle.Event.ON_PAUSE -> {}
            Lifecycle.Event.ON_STOP -> {}
            Lifecycle.Event.ON_DESTROY -> {}
            else -> return@LifecycleEventListener
        }
    }

    if (homeState.isShowQuitDialog.value) {
        QuitDialog(
            onClickYes = { context.finish() },
            onClickNo = { homeState.isShowQuitDialog.value = false },
            onDismissed = { homeState.isShowQuitDialog.value = false }
        )
    }

    if (homeState.isShowLogoutDialog.value) {
        LogoutDialog(
            onClickYes = {
                homeState.isLoading.value = true
                homeState.isShowLogoutDialog.value = false

                AccountManager.startLogout(
                    facebookAccountManager = homeState.facebookAccountManager,
                    googleAccountManager = homeState.googleAccountManager,
                    naverAccountManager = homeState.naverAccountManager,
                    kakaoAccountManager = homeState.kakaoAccountManager,
                    isCompleteLogout = {
                        homeState.isLoading.value = false
                        homeState.isShowLogoutDialog.value = false

                        PrefsManager.apply {
                            nickname = context.resources.createRandomNickname()
                            profileUri = ""
                        }
                        homeState.nickname.value= PrefsManager.nickname
                        homeState.profile.value = ""

                        context.window.decorView.rootView.showSnackBar(message = "로그아웃 성공")
                    }
                )
            },
            onClickNo = { homeState.isShowLogoutDialog.value = false },
            onDismissed = { homeState.isShowLogoutDialog.value = false }
        )
    }

    if (homeState.isShowSettingDialog.value) {
        SettingDialog(
            onClickClose = { homeState.isShowSettingDialog.value = false },
            backgroundVolume = savedGameInfo.backgroundVolume,
            onChangedBackgroundVolume = onChangedBackgroundVolume,
            onChangedFinishedBackgroundVolume = onChangeFinishedBackgroundVolume,
            effectVolume = savedGameInfo.effectVolume,
            onChangedEffectVolume = onChangedEffectVolume,
            onChangedFinishedEffectVolume = onChangeFinishedEffectVolume,
            onChangedVibration = onChangedVibration,
            onChangedPush = onChangedPush,
            onClickLogin = { SignGateActivity.start(context = context) },
            onClickLogout = {
                homeState.isShowSettingDialog.value = false
                homeState.isShowLogoutDialog.value = true
            },
            onClickManageProfile = {},
            onDismissed = { homeState.isShowSettingDialog.value = false }
        )
    }

    if (homeState.isShowPrepareDialog.value) {
        PrepareDialog(
            onDismissed = { homeState.isShowPrepareDialog.value = false },
            onClickConfirm = {  homeState.isShowPrepareDialog.value = false }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.ic_home_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(14.dp))
            Row {
                ProfileContainer(
                    nickname = homeState.nickname.value,
                    profile = homeState.profile.value,
                    onClick = {
//                        context.startActivity(MyProfileActivity::class.java)
                    }
                )
                Spacer(modifier = Modifier.weight(weight = 1f))
                HomeQuickMenuContainer(
                    onToggledSound = {
                        val isPlaying = SoundUtil.isBGMPlaying
                        if (isPlaying == null || !isPlaying) {
                            SoundUtil.restartBackgroundBGM()
                            PrefsManager.isActiveBackgroundBGM = true
                        } else {
                            SoundUtil.pause(isBackgroundSound = true)
                            PrefsManager.isActiveBackgroundBGM = false
                        }
                    },
                    onClickSetting = { homeState.isShowSettingDialog.value = true }
                )
                Spacer(modifier = Modifier.width(width = 6.dp))
            }
            Spacer(modifier = Modifier.height(height = 92.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_splash),
                contentDescription = null,
                modifier = Modifier
                    .width(width = 216.dp)
                    .aspectRatio(ratio = 2.37f)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.weight(weight = 1f))
            GamePlayContainer(
                onClickSoloPlay = {
                    DPSinglePlayListActivity.start(context = context)
                    context.overridePendingTransition(
                        R.anim.slide_right_enter,
                        R.anim.slide_right_exit
                    )
                },
                onClickMultiPlay = { homeState.isShowPrepareDialog.value = true },
                onClickQuit = { homeState.isShowQuitDialog.value = true },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(height = 56.dp))
        }

        if (homeState.isLoading.value) {
            LoadingView()
        }
    }
}