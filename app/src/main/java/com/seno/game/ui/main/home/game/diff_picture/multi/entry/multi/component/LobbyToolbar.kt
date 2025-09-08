package com.seno.game.ui.main.home.game.diff_picture.multi.entry.multi.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.seno.game.R
import com.seno.game.extensions.noRippleClickable
import com.seno.game.extensions.textDp

@Composable
fun LobbyToolbar(onClickBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height = 55.dp)
                .padding(start = 16.dp)
                .align(alignment = Alignment.Center)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_left_white),
                contentDescription = "back_arrow",
                modifier = Modifier
                    .size(size = 18.dp)
                    .align(alignment = Alignment.CenterStart)
                    .noRippleClickable { onClickBack.invoke() }
            )
            Text(
                text = stringResource(R.string.multi_lobby_title),
                style = TextStyle(
                    color = Color.White,
                    fontSize = 18.textDp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.align(alignment = Alignment.Center)
            )
        }
    }
}