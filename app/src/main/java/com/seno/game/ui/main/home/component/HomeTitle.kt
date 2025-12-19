package com.seno.game.ui.main.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.seno.game.R
import com.seno.game.extensions.textDp

@Composable
fun HomeTitle() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            stringResource(R.string.app_name_part1),
            fontSize = 45.textDp,
            color = Color.White,
            fontWeight = FontWeight.W400
        )
        Text(
            stringResource(R.string.app_name_part2),
            fontSize = 45.textDp,
            color = Color.White,
            fontWeight = FontWeight.W400,
            modifier = Modifier.graphicsLayer {
                scaleX = -1f
            }
        )
        Text(
            stringResource(R.string.app_name_part3),
            fontSize = 45.textDp,
            color = Color.White,
            fontWeight = FontWeight.W400
        )
    }
}