package com.seno.game.ui.main.home.game.diff_picture.multi.qr

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.core.view.WindowInsetsControllerCompat
import com.seno.game.R
import com.seno.game.extensions.createQRCode
import com.seno.game.extensions.startActivity
import com.seno.game.extensions.textDp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QRActivity : ComponentActivity() {
    private val path: String by lazy { intent.getStringExtra(PATH) ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = false
        }

        setContent {
            QRScanScreen()
        }
    }

    @Composable
    fun QRScanScreen() {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(color = colorResource(id = R.color.transparent))
        ) {
            Box(modifier = Modifier.Companion.size(200.dp)) {
                path.createQRCode()?.let {
                    BitmapImageDisplay(it)
                }
            }
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
            Spacer(modifier = Modifier.height(height = 72.dp))
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
                    .clickable { finish() }
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
        }
    }

    @Composable
    fun BitmapImageDisplay(bitmap: Bitmap) {
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
                painter = BitmapPainter(bitmap.asImageBitmap()),
                contentDescription = "Bitmap Image",
                modifier = Modifier.Companion
                    .width(300.dp)
                    .height(300.dp),
                contentScale = ContentScale.Companion.Crop // 필요에 따라 변경 가능
            )
        }
    }

    companion object {
        const val PATH = "path"

        fun start(context: Context, path: String) {
            context.startActivity(QRActivity::class.java) {
                putExtra(PATH, path)
            }
        }
    }
}