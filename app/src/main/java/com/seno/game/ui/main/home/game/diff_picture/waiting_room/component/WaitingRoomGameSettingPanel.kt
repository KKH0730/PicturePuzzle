package com.seno.game.ui.main.home.game.diff_picture.waiting_room.component

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.seno.game.R
import com.seno.game.extensions.noRippleClickable
import com.seno.game.extensions.textDp

@Composable
fun WaitingRoomGameSettingPanel(qrBitmap: Bitmap, onClickQRCode: () -> Unit) {
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
                text = "test",
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
            WaitingRoomQRCode(qrBitmap = qrBitmap, modifier = Modifier, onClickQRCode = onClickQRCode)
            WaitingGameSetting(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(width = 8.dp))
        }
    }
}

@Composable
fun WaitingRoomQRCode(qrBitmap: Bitmap, modifier: Modifier = Modifier, onClickQRCode: () -> Unit) {
    Box(
        modifier = Modifier
            .background(color = colorResource(id = R.color.transparent))
            .border(
                border = BorderStroke(
                    width = 2.dp,
                    color = Color.White
                ),
                shape = RoundedCornerShape(size = 16.dp)
            )
    ) {
        Image(
            painter = BitmapPainter(qrBitmap.asImageBitmap()),
            contentDescription = "Bitmap Image",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .width(150.dp)
                .height(150.dp)
                .noRippleClickable(onClick = onClickQRCode)
        )
    }
}

@Composable
fun WaitingGameSetting(modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(size = 16.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.color_FFD6FF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 0.dp, vertical = 12.dp)
    ) {

    }
}