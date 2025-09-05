package com.seno.game.ui.main.home.game.diff_picture.multi.entry

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.seno.game.R
import com.seno.game.core.ResultConstants
import com.seno.game.extensions.noRippleClickable
import com.seno.game.extensions.safeStartActivity
import com.seno.game.extensions.textDp
import com.seno.game.ui.base.BaseComposeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EntryActivity : BaseComposeActivity(
    isLightStatusBar = true,
    isLightNavigationBar = false
) {
    @Composable
    override fun ComposeContent() {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(alignment = Alignment.Center)
            ) {
                Spacer(modifier = Modifier.weight(weight = 1f))
                Row(modifier = Modifier) {
                    OptionButton(
                        painter = painterResource(R.drawable.ic_outline_add),
                        text = stringResource(R.string.entry_crate_lobby),
                        modifier = Modifier.weight(weight = 1f),
                        onClick = {
                            setResult(ResultConstants.RESULT_CREATE_LOBBY)
                            finish()
                        }
                    )
                    OptionButton(
                        painter = painterResource(R.drawable.ic_outline_manage_search),
                        text = stringResource(R.string.entry_search_lobby),
                        modifier = Modifier.weight(weight = 1f),
                        onClick = {
                            setResult(ResultConstants.RESULT_JOIN_LOBBY)
                            finish()
                        }
                    )
                }
                Spacer(modifier = Modifier.weight(weight = 0.6f))
                Box(
                    modifier = Modifier
                        .offset(y = (-50).dp)
                        .size(size = 48.dp)
                        .border(
                            border = BorderStroke(
                                width = 2.dp,
                                color = Color.White
                            ),
                            shape = CircleShape
                        )
                        .clickable(onClick = { finish() })
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

    companion object {
        fun start(context: Context, launcher: ActivityResultLauncher<Intent>) {
            context.safeStartActivity(EntryActivity::class.java, launcher)
        }
    }
}

@Composable
fun OptionButton(
    painter: Painter,
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .size(size = 150.dp)
            .aspectRatio(1f)
            .noRippleClickable(onClick = onClick)
    ) {
        Spacer(modifier = Modifier.weight(weight = 1f))
        Box(
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .border(
                    border = BorderStroke(
                        width = 2.dp,
                        color = colorResource(R.color.white)
                    ),
                    shape = RoundedCornerShape(size = 12.dp)
                )
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                colorFilter = ColorFilter.tint(colorResource(R.color.white)),
                modifier = Modifier
                    .size(size = 100.dp)
                    .padding(all = 12.dp)
                    .align(alignment = Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.height(height = 24.dp))
        Text(
            text = text,
            style = TextStyle(
                fontSize = 20.textDp,
                color = colorResource(R.color.white),
                fontWeight = FontWeight.W500
            ),
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.weight(weight = 1f))
    }
}