package com.seno.game.ui.main.home.game.diff_picture.multi.entry.multi.component

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.seno.game.R
import com.seno.game.extensions.copyToClipboard
import com.seno.game.extensions.getString
import com.seno.game.extensions.noRippleClickable
import com.seno.game.extensions.textDp
import com.seno.game.extensions.toast

@Composable
fun LobbyInvitePanel(
    qrBitmap: Bitmap,
    onClickQRCode: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(size = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.Black)
                .border(
                    width = 1.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
        ) {
            Text(
                text = stringResource(id = R.string.multi_lobby_invite_subtitle),
                style = TextStyle(
                    color = Color.White,
                    fontSize = 18.textDp,
                    fontWeight = FontWeight.W500
                ),
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 16.dp)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            LobbyQRCode(qrBitmap = qrBitmap, modifier = Modifier, onClickQRCode = onClickQRCode)
            Spacer(modifier = Modifier.width(width = 8.dp))
            LobbyInviteCode(modifier = Modifier.weight(weight = 1f))
            Spacer(modifier = Modifier.width(width = 8.dp))
        }
    }
}

@Composable
fun LobbyQRCode(qrBitmap: Bitmap, modifier: Modifier = Modifier, onClickQRCode: () -> Unit) {
    Image(
        painter = BitmapPainter(qrBitmap.asImageBitmap()),
        contentDescription = "Bitmap Image",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(size = 120.dp)
            .noRippleClickable(onClick = onClickQRCode)
    )
}

@Composable
fun LobbyInviteCode(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Box(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = modifier
                .fillMaxSize()
                .clip(shape = RoundedCornerShape(size = 24.dp))
                .padding(vertical = 12.dp)
                .background(color = colorResource(R.color.color_FDF2F8))
        ) {
            Row(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 16.dp)
            ) {
                Column {
                    Text(
                        text = stringResource(id = R.string.multi_lobby_invite_lobby_num_title),
                        style = TextStyle(
                            color = colorResource(id = R.color.color_DB2777),
                            fontSize = 14.textDp,
                            fontWeight = FontWeight.W500
                        )
                    )
                    Spacer(modifier = Modifier.height(height = 6.dp))
                    Text(
                        text = "A25FB9GF6F",
                        style = TextStyle(
                            color = colorResource(id = R.color.black),
                            fontSize = 19.textDp,
                            fontWeight = FontWeight.W700
                        )
                    )
                }
                Spacer(modifier = Modifier.weight(weight = 1f))
                Box(
                    modifier = Modifier
                        .size(size = 42.dp)
                        .clip(shape = CircleShape)
                        .background(color = colorResource(R.color.color_FCE7F3))
                        .align(alignment = Alignment.CenterVertically)
                        .noRippleClickable {
                            context.copyToClipboard(
                                label = getString(R.string.multi_lobby_invite_lobby_num_title),
                                text = "A25FB9GF6F"
                            )
                            context.toast(message = context.getString(R.string.multi_lobby_invite_copy_success))
                        }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_content_copy),
                        contentDescription = null,
                        tint = colorResource(R.color.color_DB2777),
                        modifier = Modifier
                            .size(size = 24.dp)
                            .align(alignment = Alignment.Center)
                    )
                }
            }
        }
    }
}