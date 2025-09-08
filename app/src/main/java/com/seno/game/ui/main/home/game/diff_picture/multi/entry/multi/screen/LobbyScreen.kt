package com.seno.game.ui.main.home.game.diff_picture.multi.entry.multi.screen

import android.graphics.Bitmap
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.seno.game.R
import com.seno.game.model.Player
import com.seno.game.ui.component.BannerADView
import com.seno.game.ui.main.home.game.diff_picture.multi.entry.multi.Difficulty
import com.seno.game.ui.main.home.game.diff_picture.multi.entry.multi.GameRounds
import com.seno.game.ui.main.home.game.diff_picture.multi.entry.multi.GameTimeLimit
import com.seno.game.ui.main.home.game.diff_picture.multi.entry.multi.component.LobbyGameSettingPanel
import com.seno.game.ui.main.home.game.diff_picture.multi.entry.multi.component.LobbyInvitePanel
import com.seno.game.ui.main.home.game.diff_picture.multi.entry.multi.component.LobbyPlayerList
import com.seno.game.ui.main.home.game.diff_picture.multi.entry.multi.component.LobbyQROverlayView
import com.seno.game.ui.main.home.game.diff_picture.multi.entry.multi.component.LobbyToolbar

@Composable
fun LobbyRoomScreen(
    ownerUid: String,
    qrBitmap: Bitmap,
    gameTimeLimit: GameTimeLimit,
    gameRounds: GameRounds,
    selectedGameDifficulty: Difficulty,
    players: List<Player>,
    onClickTimeLimit: (GameTimeLimit) -> Unit,
    onClickGameRound: (GameRounds) -> Unit,
    onClickDifficulty: (Difficulty) -> Unit,
    onClickBack: () -> Unit,
) {
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
                    bottom = insets.calculateBottomPadding(),
                    start = 16.dp,
                    end = 16.dp
                )
        ) {
            LobbyToolbar(onClickBack = onClickBack)
            Spacer(modifier = Modifier.height(height = 10.dp))
            LobbyInvitePanel(qrBitmap = qrBitmap, onClickQRCode = { isShowQROverlay = true })
            Spacer(modifier = Modifier.height(height = 20.dp))
            LobbyGameSettingPanel(
                gameTimeLimit = gameTimeLimit,
                gameRounds = gameRounds,
                selectedGameDifficulty = selectedGameDifficulty,
                onClickTimeLimit = onClickTimeLimit,
                onClickGameRound = onClickGameRound,
                onClickDifficulty = onClickDifficulty
            )
            Spacer(modifier = Modifier.height(height = 20.dp))
            LobbyPlayerList(ownerUid = ownerUid, players = players, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(height = 20.dp))
            BannerADView()
            Spacer(modifier = Modifier.height(height = 16.dp))
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