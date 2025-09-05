package com.seno.game.ui.main.home.game.diff_picture.waiting_room.screen

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.seno.game.R
import com.seno.game.extensions.textDp
import com.seno.game.model.Player
import com.seno.game.ui.common.BannerADView
import com.seno.game.ui.main.home.component.QuitDialogYesButton
import com.seno.game.ui.main.home.game.diff_picture.waiting_room.component.WaitingPlayerList
import com.seno.game.ui.main.home.game.diff_picture.waiting_room.component.WaitingRoomGameSettingPanel
import com.seno.game.ui.main.home.game.diff_picture.waiting_room.component.WaitingRoomHeader

@Composable
fun WaitingRoomScreen(
    ownerUid: String,
    qrBitmap: Bitmap,
    players: List<Player>,
    isShowQuitDialog: Boolean,
    onClickQRCode: () -> Unit,
    onClickQuit: () -> Unit,
    onDismissQuitDialog: () -> Unit,
    onClickBack: () -> Unit
) {
    val insets = WindowInsets.systemBars.asPaddingValues()
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
            WaitingRoomHeader(onClickBack = onClickBack)
            Spacer(modifier = Modifier.height(height = 10.dp))
            WaitingRoomGameSettingPanel(qrBitmap = qrBitmap, onClickQRCode = onClickQRCode)
            Spacer(modifier = Modifier.height(height = 20.dp))
            WaitingPlayerList(ownerUid = ownerUid, players = players, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(height = 20.dp))
            BannerADView()
            Spacer(modifier = Modifier.height(height = 16.dp))
        }
        if (isShowQuitDialog) {
            QuitDialog(
                onClickQuit = onClickQuit,
                onDismissed = onDismissQuitDialog
            )
        }
    }
}

@Composable
fun QuitDialog(
    onClickQuit: () -> Unit,
    onDismissed: () -> Unit
) {
    Dialog(onDismissRequest = onDismissed) {
        Card(
            backgroundColor = Color.White,
            shape = RoundedCornerShape(size = 30.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            ) {
                Spacer(modifier = Modifier.height(height = 35.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_dialog_cat_crying),
                    contentDescription = null,
                    modifier = Modifier
                        .width(width = 56.dp)
                        .height(height = 59.dp)
                )
                Spacer(modifier = Modifier.height(height = 23.dp))
                Text(
                    text = stringResource(id = R.string.network_request_error),
                    color = colorResource(id = R.color.color_b8c0ff),
                    style = TextStyle(
                        fontSize = 16.textDp,
                        textAlign = TextAlign.Center
                    ),
                    softWrap = true

                )
                Spacer(modifier = Modifier.height(height = 28.dp))
                Row {
                    Spacer(modifier = Modifier.width(width = 10.dp))
                    QuitDialogYesButton(text = stringResource(id = R.string.confirm), onClick = onClickQuit)
                    Spacer(modifier = Modifier.width(width = 10.dp))
                }
                Spacer(modifier = Modifier.height(height = 25.dp))
            }
        }
    }
}