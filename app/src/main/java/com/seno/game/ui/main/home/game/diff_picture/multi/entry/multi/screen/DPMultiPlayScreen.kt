package com.seno.game.ui.main.home.game.diff_picture.multi.entry.multi.screen

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.seno.game.extensions.textDp
import com.seno.game.ui.main.home.game.diff_picture.multi.entry.multi.MultiGameViewModel
import timber.log.Timber

@Composable
fun DPMultiPlayScreen(
    navController: NavController,
    viewModel: MultiGameViewModel = hiltViewModel(viewModelStoreOwner = LocalContext.current as ComponentActivity)
) {
    Timber.e("DPMultiPlayScreen")
    BackHandler {
        navController.navigateUp()
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .height(intrinsicSize = IntrinsicSize.Min)
        ) {
            Text(
                viewModel.gameTimeLimit.collectAsStateWithLifecycle().value.seconds.toString(),
                style = TextStyle(
                    fontSize = 16.textDp,
                    color = Color.White
                )
            )
            Text(
                viewModel.gameRounds.collectAsStateWithLifecycle().value.count.toString(),
                style = TextStyle(
                    fontSize = 16.textDp,
                    color = Color.White
                )
            )
            Text(
                viewModel.gameDifficulty.collectAsStateWithLifecycle().value.text,
                style = TextStyle(
                    fontSize = 16.textDp,
                    color = Color.White
                )
            )
        }
    }
}