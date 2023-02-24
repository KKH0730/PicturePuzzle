package com.seno.game.ui.main.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seno.game.R
import com.seno.game.extensions.createRandomNickname
import com.seno.game.extensions.startActivity
import com.seno.game.extensions.toast
import com.seno.game.manager.*
import com.seno.game.model.SavedGameInfo
import com.seno.game.prefs.PrefsManager
import com.seno.game.ui.account.my_profile.MyProfileActivity
import com.seno.game.ui.account.sign_gate.SignGateActivity
import com.seno.game.ui.component.LoadingView
import com.seno.game.ui.main.LifecycleEventListener
import com.seno.game.ui.main.MainActivity
import com.seno.game.ui.main.home.component.*
import com.seno.game.ui.main.home.game.diff_picture.list.DPSinglePlayListActivity
import com.seno.game.util.MusicPlayUtil

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun HomeScreen() {
    val homeViewModel = hiltViewModel<HomeViewModel>()
    HomeUI(
        savedGameInfo = homeViewModel.savedGameInfoToLocalDB.collectAsStateWithLifecycle().value,
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
        onChangedPush = { homeViewModel.reqUpdatePushOnOff(AccountManager.firebaseUid, isPushOn = it) }
    )
}

@Composable
fun HomeUI(
    savedGameInfo: SavedGameInfo,
    onChangedBackgroundVolume: (Float) -> Unit,
    onChangeFinishedBackgroundVolume: (Float) -> Unit,
    onChangedEffectVolume: (Float) -> Unit,
    onChangeFinishedEffectVolume: (Float) -> Unit,
    onChangedVibration: (Boolean) -> Unit,
    onChangedPush: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    val facebookAccountManager = FacebookAccountManager(activity = context as MainActivity)
    val googleAccountManager = GoogleAccountManager(activity = context as MainActivity)
    val naverAccountManager = NaverAccountManager()
    val kakaoAccountManager = KakaoAccountManager(context = context)

    var isShowQuitDialog by remember { mutableStateOf(false) }
    var isShowLogoutDialog by remember { mutableStateOf(false) }
    var isShowSettingDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var nickname by remember { mutableStateOf(PrefsManager.nickname) }
    var profile by remember { mutableStateOf("") }

    (context as MainActivity).LifecycleEventListener {
        when (it) {
            Lifecycle.Event.ON_CREATE -> {}
            Lifecycle.Event.ON_START -> {}
            Lifecycle.Event.ON_RESUME -> {
                nickname = PrefsManager.nickname
                profile = PrefsManager.profileUri
                MusicPlayUtil.restart(isBackgroundSound = true)
            }
            Lifecycle.Event.ON_PAUSE -> {
            }
            Lifecycle.Event.ON_STOP -> {
                MusicPlayUtil.pause(isBackgroundSound = true)
            }
            Lifecycle.Event.ON_DESTROY -> {
                MusicPlayUtil.release(isBackgroundSound = true)
            }
            else -> return@LifecycleEventListener
        }
    }

    if (isShowQuitDialog) {
        QuitDialog(
            onClickYes = { (context as MainActivity).finish() },
            onClickNo = { isShowQuitDialog = false },
            onDismissed = { isShowQuitDialog = false }
        )
    }

    if (isShowLogoutDialog) {
        LogoutDialog(
            onClickYes = {
                isLoading = true
                AccountManager.startLogout(
                    context = context,
                    facebookAccountManager = facebookAccountManager,
                    googleAccountManager = googleAccountManager,
                    naverAccountManager = naverAccountManager,
                    kakaoAccountManager = kakaoAccountManager,
                    isCompleteLogout = {
                        isLoading = false
                        isShowLogoutDialog = false

                        PrefsManager.apply {
                            PrefsManager.nickname = context.resources.createRandomNickname()
                            profileUri = ""
                        }
                        nickname = PrefsManager.nickname
                        profile = ""

                        context.toast("로그아웃 성공")
                    }
                )
            },
            onClickNo = { isShowLogoutDialog = false },
            onDismissed = { isShowLogoutDialog = false }
        )
    }

    if (isShowSettingDialog) {
        SettingDialog(
            onClickClose = { isShowSettingDialog = false },
            backgroundVolume = savedGameInfo.backgroundVolume,
            onChangedBackgroundVolume = onChangedBackgroundVolume,
            onChangedFinishedBackgroundVolume = onChangeFinishedBackgroundVolume,
            effectVolume = savedGameInfo.effectVolume,
            onChangedEffectVolume = onChangedEffectVolume,
            onChangedFinishedEffectVolume = onChangeFinishedEffectVolume,
            onChangedVibration = onChangedVibration,
            onChangedPush = onChangedPush,
            onClickLogin = { context.startActivity(SignGateActivity::class.java) },
            onClickLogout = {
                isShowSettingDialog = false
                isShowLogoutDialog = true
            },
            onClickManageProfile = {},
            onDismissed = { isShowSettingDialog = false }
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
                    nickname = nickname,
                    profile = profile,
                    onClick = { context.startActivity(MyProfileActivity::class.java) }
                )
                Spacer(modifier = Modifier.weight(weight = 1f))
                HomeQuickMenuContainer(
                    onToggledSound = {
                        val isPlaying = MusicPlayUtil.isPlaying
                        if (isPlaying == null || !isPlaying) {
                            MusicPlayUtil.restart(isBackgroundSound = true)
                        } else {
                            MusicPlayUtil.pause(isBackgroundSound = true)
                        }
                    },
                    onClickSetting = { isShowSettingDialog = true }
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
                    (context as MainActivity).overridePendingTransition(
                        R.anim.slide_right_enter,
                        R.anim.slide_right_exit
                    )
                },
                onClickMultiPlay = {},
                onClickQuit = { isShowQuitDialog = true },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(height = 56.dp))
        }

        if (isLoading) {
            LoadingView()
        }
    }
}