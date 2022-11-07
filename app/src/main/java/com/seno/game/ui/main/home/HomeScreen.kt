package com.seno.game.ui.main.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.seno.game.R
import com.seno.game.extensions.startActivity
import com.seno.game.manager.AccountManager
import com.seno.game.ui.account.sign_gate.SignGateActivity
import com.seno.game.ui.game.diffgame.DiffPictureGameActivity
import com.seno.game.ui.main.LifecycleEventListener
import com.seno.game.ui.main.MainActivity
import com.seno.game.ui.main.home.component.*
import com.seno.game.util.MusicPlayUtil

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val homeViewModel = hiltViewModel<HomeViewModel>()
    var isUser by remember { mutableStateOf(false) }
    var isShowQuitDialog by remember { mutableStateOf(false) }
    var isShowSettingDialog by remember { mutableStateOf(false) }

    (context as MainActivity).LifecycleEventListener {
        when (it) {
            Lifecycle.Event.ON_CREATE -> {}
            Lifecycle.Event.ON_START -> {}
            Lifecycle.Event.ON_RESUME -> {
                isUser = AccountManager.currentUser != null
                MusicPlayUtil.restart()
            }
            Lifecycle.Event.ON_PAUSE -> {
            }
            Lifecycle.Event.ON_STOP -> { MusicPlayUtil.pause() }
            Lifecycle.Event.ON_DESTROY -> { MusicPlayUtil.release() }
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

    if (isShowSettingDialog) {
        SettingDialog(
            onClickClose = { isShowSettingDialog = false },
            onValueChangeBackgroundSlider = {
              MusicPlayUtil.setVol(leftVol = it, rightVol = it)
            },
            onDismissed = { isShowSettingDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.color_e7c6ff))
    ) {
        Spacer(modifier = Modifier.height(14.dp))
        Row() {
            ProfileContainer(onClick = {
                context.startActivity(SignGateActivity::class.java)
                (context as MainActivity).overridePendingTransition(
                    R.anim.slide_right_enter,
                    R.anim.slide_right_exit
                )
            })
            Spacer(modifier = Modifier.weight(weight = 1f))
            HomeQuickMenuContainer(
                onToggledSound = {
                    val isPlaying = MusicPlayUtil.isPlaying
                    if (isPlaying == null || !isPlaying) {
                        MusicPlayUtil.restart()
                    } else {
                        MusicPlayUtil.pause()
                    }
                },
                onClickSetting = {  isShowSettingDialog = true }
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
}

@Composable
fun DialogContainer() {

}