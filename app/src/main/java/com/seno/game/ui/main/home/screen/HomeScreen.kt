package com.seno.game.ui.main.home.screen

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gun0912.tedpermission.coroutine.TedPermission
import com.seno.game.R
import com.seno.game.core.ResultConstants
import com.seno.game.extensions.LifecycleEventListener
import com.seno.game.extensions.createRandomNickname
import com.seno.game.extensions.safeStartActivity
import com.seno.game.extensions.toast
import com.seno.game.manager.AccountManager
import com.seno.game.manager.FacebookAccountManager
import com.seno.game.manager.GoogleAccountManager
import com.seno.game.manager.KakaoAccountManager
import com.seno.game.manager.NaverAccountManager
import com.seno.game.model.SavedGameInfo
import com.seno.game.prefs.PrefsManager
import com.seno.game.ui.account.my_profile.MyProfileActivity
import com.seno.game.ui.account.sign_gate.SignGateActivity
import com.seno.game.ui.component.BannerADView
import com.seno.game.ui.component.CommonAlertDialog
import com.seno.game.ui.component.CommonCustomDialog
import com.seno.game.ui.component.LoadingView
import com.seno.game.ui.main.MainActivity
import com.seno.game.ui.main.home.HomeViewModel
import com.seno.game.ui.main.home.component.GamePlayContainer
import com.seno.game.ui.main.home.component.HomeProfileContainer
import com.seno.game.ui.main.home.component.HomeQuickMenuContainer
import com.seno.game.ui.main.home.component.QuitDialog
import com.seno.game.ui.main.home.component.SettingDialog
import com.seno.game.ui.main.home.game.diff_picture.list.DPSinglePlayListActivity
import com.seno.game.ui.main.home.game.diff_picture.multi.entry.EntryActivity
import com.seno.game.ui.main.home.game.diff_picture.multi.entry.join.QRScanActivity
import com.seno.game.ui.main.home.game.diff_picture.multi.entry.multi.LobbyActivity
import com.seno.game.util.MusicPlayUtil
import kotlinx.coroutines.launch


@Composable
fun HomeScreen() {
    val homeViewModel = hiltViewModel<HomeViewModel>()

    HomeUI(
        savedGameInfo = homeViewModel.savedGameInfoToLocalDB.collectAsStateWithLifecycle().value,
        onChangedBackgroundVolume = homeViewModel::updateBackgroundVolume,
        onChangeFinishedBackgroundVolume = {
            if (AccountManager.isUser) {
                homeViewModel.reqUpdateBackgroundVolume(
                    uid = AccountManager.firebaseUid,
                    volume = it.toString()
                )
            }
        },
        onChangedEffectVolume = homeViewModel::updateEffectVolume,
        onChangeFinishedEffectVolume = {
            if (AccountManager.isUser) {
                homeViewModel.reqUpdateEffectVolume(
                    uid = AccountManager.firebaseUid,
                    volume = it.toString()
                )
            }
        },
        onChangedVibration = {
            if (AccountManager.isUser) {
                homeViewModel.reqUpdateVibrationOnOff(AccountManager.firebaseUid, isVibrationOn = it)
            }
        },
        onChangedPush = {
            if (AccountManager.isUser) {
                homeViewModel.reqUpdatePushOnOff(AccountManager.firebaseUid, isPushOn = it)
            }
        },
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
    val scope = rememberCoroutineScope()

    val facebookAccountManager = FacebookAccountManager(activity = context as ComponentActivity)
    val googleAccountManager = GoogleAccountManager(activity = context)
    val naverAccountManager = NaverAccountManager()
    val kakaoAccountManager = KakaoAccountManager(context = context)

    var isShowPermissionAlertDialog by remember { mutableStateOf(false) }
    var isShowQuitDialog by remember { mutableStateOf(false) }
    var isShowLogoutDialog by remember { mutableStateOf(false) }
    var isShowSettingDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var isUser by remember { mutableStateOf(AccountManager.isUser) }
    var nickname by remember { mutableStateOf(PrefsManager.nickname) }
    var profileUri by remember { mutableStateOf("") }

    val insets = WindowInsets.systemBars.asPaddingValues()

    var loginLauncher: ActivityResultLauncher<Intent>? = null

    val qrScanLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val path = result.data?.getStringExtra("path") ?: ""
            if (path.isNotEmpty() && AccountManager.isUser) {
                LobbyActivity.start(context = context, path = path)
            } else {
                loginLauncher?.let { SignGateActivity.start(context = context, path = path, resultCode = ResultConstants.RESULT_CREATE_LOBBY, launcher = it) }
            }
        }
    }

    val entryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            ResultConstants.RESULT_CREATE_LOBBY -> LobbyActivity.start(context = context, path = "${AccountManager.firebaseUid}_${System.currentTimeMillis()}")
            ResultConstants.RESULT_JOIN_LOBBY -> {
                scope.launch {
                    val permissionResult = TedPermission.create()
                        .setPermissions(Manifest.permission.CAMERA)
                        .check()

                    if (permissionResult.isGranted) {
                        QRScanActivity.start(context = context, launcher = qrScanLauncher)
                    } else {
                        isShowPermissionAlertDialog = true
                    }
                }
            }
        }
    }

    loginLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == ResultConstants.RESULT_LOGIN ||
            result.resultCode == ResultConstants.RESULT_ENTRY ||
            result.resultCode == ResultConstants.RESULT_CREATE_LOBBY) {
            isUser = true
        }

        when (result.resultCode) {
            ResultConstants.RESULT_ENTRY -> EntryActivity.start(context = context, launcher = entryLauncher)
            ResultConstants.RESULT_CREATE_LOBBY -> {
                val path = result.data?.getStringExtra("path") ?: ""
                if (path.isNotEmpty()) {
                    LobbyActivity.start(context = context, path = "${AccountManager.firebaseUid}_${System.currentTimeMillis()}")
                }
            }
        }
    }

    BackHandler {
        isShowQuitDialog = true
    }

    context.LifecycleEventListener {
        when (it) {
            Lifecycle.Event.ON_CREATE -> {}
            Lifecycle.Event.ON_START -> {}
            Lifecycle.Event.ON_RESUME -> {
                nickname = PrefsManager.nickname
                profileUri = PrefsManager.profileUri
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

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.ic_home_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = insets.calculateTopPadding(), bottom = insets.calculateBottomPadding())
        ) {
            Spacer(modifier = Modifier.height(14.dp))
            Row {
                HomeProfileContainer(
                    nickname = nickname,
                    profileUri = profileUri,
                    onClick = { context.safeStartActivity(MyProfileActivity::class.java) }
                )
                Spacer(modifier = Modifier.weight(weight = 1f))
                HomeQuickMenuContainer(
                    onClickSetting = { isShowSettingDialog = true },
                    onToggledSound = {
                        val isPlaying = MusicPlayUtil.isPlaying
                        if (isPlaying == null || !isPlaying) {
                            MusicPlayUtil.restart(isBackgroundSound = true)
                        } else {
                            MusicPlayUtil.pause(isBackgroundSound = true)
                        }
                    },
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
                onClickSoloPlay = { DPSinglePlayListActivity.start(context = context) },
                onClickMultiPlay = {
                    if (AccountManager.isUser) {
                        EntryActivity.start(context = context, launcher = entryLauncher)
                    } else {
                        SignGateActivity.start(
                            context = context,
                            path = "${AccountManager.firebaseUid}_${System.currentTimeMillis()}",
                            resultCode = ResultConstants.RESULT_ENTRY,
                            launcher = loginLauncher
                        )
                    }
                },
                onClickQuit = { isShowQuitDialog = true },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(height = 20.dp))
            BannerADView(modifier = Modifier.height(height = 50.dp))
            Spacer(modifier = Modifier.height(height = 16.dp))
        }

        if (isLoading) {
            LoadingView()
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
        CommonCustomDialog(
            image = painterResource(R.drawable.ic_dialog_cat_wow),
            mainDescription = stringResource(id = R.string.home_logout_message1),
            subDescription = stringResource(id = R.string.home_logout_message2),
            leftButtonText = stringResource(id = R.string.home_logout_n),
            rightButtonText = stringResource(id = R.string.home_logout_y),
            onClickLeft = { isShowLogoutDialog = false },
            onClickRight = {
                isLoading = true
                AccountManager.startLogout(
                    facebookAccountManager = facebookAccountManager,
                    googleAccountManager = googleAccountManager,
                    naverAccountManager = naverAccountManager,
                    kakaoAccountManager = kakaoAccountManager,
                    isCompleteLogout = {
                        isLoading = false
                        isShowLogoutDialog = false
                        isUser = false

                        PrefsManager.apply {
                            this.nickname = context.resources.createRandomNickname()
                            this.platform = ""
                            this.profileUri = ""
                            this.isShowAD = true
                        }
                        nickname = PrefsManager.nickname
                        profileUri = ""

                        context.toast(context.getString(R.string.my_profile_logout_success))
                    }
                )
            },
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
            isUser = isUser,
            onChangedEffectVolume = onChangedEffectVolume,
            onChangedFinishedEffectVolume = onChangeFinishedEffectVolume,
            onChangedVibration = onChangedVibration,
            onChangedPush = onChangedPush,
            onClickLogin = { SignGateActivity.start(context = context, resultCode = ResultConstants.RESULT_LOGIN, launcher = loginLauncher) },
            onClickLogout = {
                isShowSettingDialog = false
                isShowLogoutDialog = true
            },
            onClickManageProfile = {},
            onDismissed = { isShowSettingDialog = false }
        )
    }

    if (isShowPermissionAlertDialog) {
        CommonAlertDialog(
            title = stringResource(id = R.string.camera_permission_title),
            content = stringResource(id = R.string.camera_permission_content),
            confirmText = stringResource(id = R.string.camera_permission_confirm),
            dismissText = stringResource(id = R.string.camera_permission_dismiss),
            onClickConfirm = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.safeStartActivity(intent)
            }
        )
    }
}