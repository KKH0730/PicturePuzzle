package com.seno.game.ui.main.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.seno.game.R
import com.seno.game.extensions.getTodayDate
import com.seno.game.extensions.startActivity
import com.seno.game.manager.AccountManager
import com.seno.game.ui.CreateGameActivity
import com.seno.game.ui.account.SignGateActivity
import com.seno.game.ui.game.areagame.AreaGameActivity
import com.seno.game.ui.game.diffgame.DiffPictureGameActivity
import com.seno.game.ui.game.humminjeongeumgame.HunMinJeongEumActivity
import com.seno.game.ui.FindGameActivity
import com.seno.game.ui.main.LifecycleEventListener
import com.seno.game.ui.main.MainActivity
import java.util.*

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val homeViewModel = hiltViewModel<HomeViewModel>()
    var isUser by remember { mutableStateOf(false) }

    (context as MainActivity).LifecycleEventListener {
        when (it) {
            Lifecycle.Event.ON_CREATE -> {}
            Lifecycle.Event.ON_START -> {}
            Lifecycle.Event.ON_RESUME -> {
//                Timber.e("kkh ON_RESUME")
                isUser = AccountManager.currentUser != null
            }
            Lifecycle.Event.ON_PAUSE -> {}
            Lifecycle.Event.ON_STOP -> {
//                Timber.e("kkh ON_STOP")
            }
            Lifecycle.Event.ON_DESTROY -> {
//                Timber.e("kkh ON_DESTROY")
            }
            else -> return@LifecycleEventListener
        }
    }

    Column(Modifier.fillMaxSize()) {
        Text(text = if (isUser) {
            "로그인 O"
        } else {
            "로그인 X"
        })
        Button(
            onClick = {
                context.startActivity(SignGateActivity::class.java)
                (context as MainActivity).overridePendingTransition(
                    R.anim.slide_right_enter,
                    R.anim.slide_right_exit
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "로그인 화면")
        }

        Button(
            onClick = {
                context.startActivity(HunMinJeongEumActivity::class.java)
                (context as MainActivity).overridePendingTransition(
                    R.anim.slide_right_enter,
                    R.anim.slide_right_exit
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "훈민정음")
        }

        Button(
            onClick = {
                context.startActivity(AreaGameActivity::class.java)
                (context as MainActivity).overridePendingTransition(R.anim.slide_right_enter,
                    R.anim.slide_right_exit)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "땅따먹기")
        }

        Button(
            onClick = {
                context.startActivity(DiffPictureGameActivity::class.java)
                (context as MainActivity).overridePendingTransition(R.anim.slide_right_enter,
                    R.anim.slide_right_exit)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "틀린그림찾기")
        }

        Button(
            onClick = {
                AccountManager.firebaseUid?.let { uid ->
                    val date = Date(Calendar.getInstance().timeInMillis)
                    context.startActivity(CreateGameActivity::class.java) {
                        putExtra("date", date.getTodayDate())
                        putExtra("roomUid", "$uid${date.time}")
                        putExtra("isChief", true)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "방 생성")
        }

        Button(
            onClick = {
                AccountManager.firebaseUid?.let {
                    context.startActivity(FindGameActivity::class.java)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "방 찾기")
        }
    }
}