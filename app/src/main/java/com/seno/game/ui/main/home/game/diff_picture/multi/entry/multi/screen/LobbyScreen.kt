package com.seno.game.ui.main.home.game.diff_picture.multi.entry.multi.screen

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.seno.game.R
import com.seno.game.extensions.createQRCode
import com.seno.game.manager.AccountManager
import com.seno.game.navigation.NavigationRoute
import com.seno.game.ui.main.home.game.diff_picture.multi.entry.multi.MultiGameViewModel
import com.seno.game.ui.main.home.game.diff_picture.multi.entry.multi.component.LobbyGameSettingPanel
import com.seno.game.ui.main.home.game.diff_picture.multi.entry.multi.component.LobbyInvitePanel
import com.seno.game.ui.main.home.game.diff_picture.multi.entry.multi.component.LobbyPlayerList
import com.seno.game.ui.main.home.game.diff_picture.multi.entry.multi.component.LobbyQROverlayView
import com.seno.game.ui.main.home.game.diff_picture.multi.entry.multi.component.LobbyToolbar

@Composable
fun LobbyRoomScreen(
    navController: NavController,
    viewModel: MultiGameViewModel = hiltViewModel(viewModelStoreOwner = LocalContext.current as ComponentActivity)
) {
    val qrBitmap = viewModel.path.createQRCode()
    val ownerUid = try {
        viewModel.path.split("_")[0]
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }

    if (qrBitmap == null || ownerUid.isEmpty()) return

    val insets = WindowInsets.systemBars.asPaddingValues()
    var isShowQROverlay by remember { mutableStateOf(false) }

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
                .padding(
                    top = insets.calculateTopPadding(),
                    bottom = insets.calculateBottomPadding() + 86.dp,
                    start = 16.dp,
                    end = 16.dp
                )
        ) {
            LobbyToolbar(onClickBack = {
                viewModel.updateMultiGamePlayer(isAdd = false)
                viewModel.finish()
            })
            Spacer(modifier = Modifier.height(height = 10.dp))
            LobbyInvitePanel(qrBitmap = qrBitmap, onClickQRCode = { isShowQROverlay = true })
            Spacer(modifier = Modifier.height(height = 20.dp))
            LobbyGameSettingPanel(
                isOwner = ownerUid == AccountManager.firebaseUid,
                gameTimeLimit = viewModel.gameTimeLimit.collectAsStateWithLifecycle().value,
                gameRounds = viewModel.gameRounds.collectAsStateWithLifecycle().value,
                selectedGameDifficulty = viewModel.gameDifficulty.collectAsStateWithLifecycle().value,
                onClickTimeLimit = viewModel::updateTimeLimit,
                onClickGameRound = viewModel::updateRounds,
                onClickDifficulty = viewModel::updateDifficulty
            )
            Spacer(modifier = Modifier.height(height = 20.dp))
            LobbyPlayerList(
                ownerUid = ownerUid,
                players = viewModel.players.collectAsStateWithLifecycle().value?.players ?: listOf(),
                modifier = Modifier.weight(1f),
                onClickMultiGameStart = {
                    navController.navigate(NavigationRoute.DP_MULTIPLAY_SCREEN.routeName) {
                        launchSingleTop = true
                    }
                }
            )
        }

        if (isShowQROverlay) {
            LobbyQROverlayView(
                qrBitmap = qrBitmap,
                onClick = { isShowQROverlay = false }
            )
        }
    }
}

//@Composable
//fun QuitDialog(
//    onClickQuit: () -> Unit,
//    onDismissed: () -> Unit
//) {
//    Dialog(onDismissRequest = onDismissed) {
//        Card(
//            backgroundColor = Color.White,
//            shape = RoundedCornerShape(size = 30.dp)
//        ) {
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally,
//                modifier = Modifier
//                    .padding(horizontal = 8.dp)
//            ) {
//                Spacer(modifier = Modifier.height(height = 35.dp))
//                Image(
//                    painter = painterResource(id = R.drawable.ic_dialog_cat_crying),
//                    contentDescription = null,
//                    modifier = Modifier
//                        .width(width = 56.dp)
//                        .height(height = 59.dp)
//                )
//                Spacer(modifier = Modifier.height(height = 23.dp))
//                Text(
//                    text = stringResource(id = R.string.network_request_error),
//                    color = colorResource(id = R.color.color_b8c0ff),
//                    style = TextStyle(
//                        fontSize = 16.textDp,
//                        textAlign = TextAlign.Center
//                    ),
//                    softWrap = true
//
//                )
//                Spacer(modifier = Modifier.height(height = 28.dp))
//                Row {
//                    Spacer(modifier = Modifier.width(width = 10.dp))
//                    QuitDialogYesButton(text = stringResource(id = R.string.confirm), onClick = onClickQuit)
//                    Spacer(modifier = Modifier.width(width = 10.dp))
//                }
//                Spacer(modifier = Modifier.height(height = 25.dp))
//            }
//        }
//    }
//}