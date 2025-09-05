package com.seno.game.ui.main.home.game.diff_picture.multi.entry.lobby.component

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.seno.game.R
import com.seno.game.extensions.textDp

@Composable
fun LobbyQROverlayView(
    qrBitmap: Bitmap,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.color_000000B3))
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.weight(weight = 1f))
            BitmapImageDisplay(qrBitmap = qrBitmap)
            Spacer(modifier = Modifier.height(height = 24.dp))
            Text(
                stringResource(id = R.string.qr_guide_message),
                style = TextStyle(
                    fontSize = 18.textDp,
                    fontWeight = FontWeight.W500,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            )
            Spacer(modifier = Modifier.weight(weight = 0.6f))
            Box(
                modifier = Modifier
                    .size(size = 48.dp)
                    .border(
                        border = BorderStroke(
                            width = 2.dp,
                            color = Color.White
                        ),
                        shape = CircleShape
                    )
                    .clickable(onClick = onClick)
            ) {
                Icon(
                    Icons.Filled.Close,
                    tint = Color.White,
                    contentDescription = null,
                    modifier = Modifier
                        .size(size = 36.dp)
                        .align(alignment = Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.weight(weight = 0.4f))
        }
    }
}

@Composable
fun BitmapImageDisplay(qrBitmap: Bitmap) {
    Box(
        modifier = Modifier
            .background(color = colorResource(id = R.color.transparent))
            .border(
                border = BorderStroke(
                    width = 2.dp,
                    color = Color.White
                ),
                shape = RoundedCornerShape(size = 100.dp)
            )
    ) {
        Image(
            painter = BitmapPainter(qrBitmap.asImageBitmap()),
            contentDescription = "Bitmap Image",
            modifier = Modifier.Companion
                .width(250.dp)
                .height(250.dp),
            contentScale = ContentScale.Companion.Crop
        )
    }
}