package com.seno.game.ui.main.home

import android.content.Context
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.seno.game.R
import com.seno.game.extensions.createRandomNickname
import com.seno.game.extensions.startActivity
import com.seno.game.manager.AccountManager
import com.seno.game.manager.FacebookAccountManager
import com.seno.game.manager.GoogleAccountManager
import com.seno.game.manager.NaverAccountManager
import com.seno.game.prefs.PrefsManager
import com.seno.game.ui.account.my_profile.MyProfileActivity
import com.seno.game.ui.account.sign_gate.SignGateActivity
import com.seno.game.ui.game.diffgame.DiffPictureGameActivity
import com.seno.game.ui.main.LifecycleEventListener
import com.seno.game.ui.main.MainActivity
import com.seno.game.ui.main.home.component.*
import com.seno.game.util.MusicPlayUtil
import timber.log.Timber

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val facebookAccountManager = FacebookAccountManager(context as MainActivity)
    val googleAccountManager = GoogleAccountManager(context as MainActivity)
    val naverAccountManager = NaverAccountManager()
    val homeViewModel = hiltViewModel<HomeViewModel>()
    var isShowQuitDialog by remember { mutableStateOf(false) }
    var isShowLogoutDialog by remember { mutableStateOf(false) }
    var isShowSettingDialog by remember { mutableStateOf(false) }
    var nickname by remember { mutableStateOf(PrefsManager.nickname) }

    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibrator = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibrator.defaultVibrator
    } else {
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    (context as MainActivity).LifecycleEventListener {
        when (it) {
            Lifecycle.Event.ON_CREATE -> {}
            Lifecycle.Event.ON_START -> {}
            Lifecycle.Event.ON_RESUME -> {
                nickname = PrefsManager.nickname
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
                AccountManager.startLogout(
                    facebookAccountManager = facebookAccountManager,
                    googleAccountManager = googleAccountManager,
                    naverAccountManager = naverAccountManager,
                    isCompleteLogout = {
                        isShowLogoutDialog = false
                        nickname = context.resources.createRandomNickname()
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
            onValueChangeBackgroundSoundSlider = {
                PrefsManager.backgroundVolume = it
                MusicPlayUtil.setVol(leftVol = it, rightVol = it, isBackgroundSound = true)
            },
            onValueChangeEffectSoundSlider = {
                PrefsManager.effectVolume = it
                MusicPlayUtil.setVol(leftVol = it, rightVol = it, isBackgroundSound = false)
            },
            onCheckChangeVibration = { PrefsManager.isVibrationOn = it },
            onCheckChangePush = { PrefsManager.isPushOn = it },
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

        Column(
            modifier = Modifier.fillMaxSize()
//            .pointerInteropFilter {
//                when (it.action) {
//                    MotionEvent.ACTION_DOWN -> {
//                        if(Build.VERSION.SDK_INT >= 26) {
//                            vibrator.vibrate(VibrationEffect.createOneShot(50, 50))
//                        } else {    //26보다 낮으면
//                            vibrator.vibrate(50)
//                        }
//                    }
//                    MotionEvent.ACTION_MOVE -> {}
//                    MotionEvent.ACTION_UP -> {}
//                    else ->  false
//                }
//                true
//            }
        ) {
            Spacer(modifier = Modifier.height(14.dp))
            Row() {
                ProfileContainer(
                    nickname = nickname,
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
                    context.startActivity(DiffPictureGameActivity::class.java)
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
    }
}

//    Column(Modifier.fillMaxSize()) {
//        Text(text = if (isUser) {
//            "로그인 O"
//        } else {
//            "로그인 X"
//        })
//        Button(
//            onClick = {
//                context.startActivity(SignGateActivity::class.java)
//                (context as MainActivity).overridePendingTransition(
//                    R.anim.slide_right_enter,
//                    R.anim.slide_right_exit
//                )
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text(text = "로그인 화면")
//        }
//
//        Button(
//            onClick = {
//                context.startActivity(HunMinJeongEumActivity::class.java)
//                (context as MainActivity).overridePendingTransition(
//                    R.anim.slide_right_enter,
//                    R.anim.slide_right_exit
//                )
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text(text = "훈민정음")
//        }
//
//        Button(
//            onClick = {
//                context.startActivity(AreaGameActivity::class.java)
//                (context as MainActivity).overridePendingTransition(R.anim.slide_right_enter,
//                    R.anim.slide_right_exit)
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text(text = "땅따먹기")
//        }
//
//        Button(
//            onClick = {
//                context.startActivity(DiffPictureGameActivity::class.java)
//                (context as MainActivity).overridePendingTransition(R.anim.slide_right_enter,
//                    R.anim.slide_right_exit)
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text(text = "틀린그림찾기")
//        }
//
//        Button(
//            onClick = {
//                AccountManager.firebaseUid?.let { uid ->
//                    val date = Date(Calendar.getInstance().timeInMillis)
//                    context.startActivity(CreateGameActivity::class.java) {
//                        putExtra("date", date.getTodayDate())
//                        putExtra("uid", uid)
//                        putExtra("roomUid", "$uid${date.time}")
//                        putExtra("isChief", true)
//                    }
//                }
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text(text = "방 생성")
//        }
//
//        Button(
//            onClick = {
//                AccountManager.firebaseUid?.let {
//                    context.startActivity(FindGameActivity::class.java)
//                }
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text(text = "방 찾기")
//        }
//    }