package com.seno.game.ui.main.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.seno.game.R
import com.seno.game.extensions.textDp
import com.seno.game.ui.component.LiquidStyledBox

@Composable
fun GamePlayContainer(
    onClickSoloPlay: () -> Unit,
    onClickMultiPlay: () -> Unit,
    onClickQuit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(space = 20.dp),
        modifier = modifier
    ) {
        GamePlayCommonButton(
            title = stringResource(R.string.home_play_solo),
            subTitle = stringResource(R.string.home_play_solo_en),
            onClick = onClickSoloPlay
        )
        GamePlayCommonButton(
            title = stringResource(R.string.home_play_multi),
            subTitle = stringResource(R.string.home_play_multi_en),
            onClick = onClickMultiPlay
        )
        GamePlayCommonButton(
            title = stringResource(R.string.home_play_quit),
            subTitle = stringResource(R.string.home_play_quit_en),
            onClick = onClickQuit
        )
    }
}

@Composable
fun GamePlayCommonButton(
    title: String,
    subTitle: String,
    onClick: () -> Unit
) {
    LiquidStyledBox(
        isUseStroke = true,
        radius = 36.dp,
        onClick = onClick
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(width = 180.dp)
                .padding(vertical = 10.dp, horizontal = 24.dp)
                .fillMaxWidth(fraction = 0.7f)
        ) {
            Text(
                text = title,
                color = colorResource(R.color.color_606264),
                fontSize = 18.textDp,
                fontWeight = FontWeight.W500,
            )
            Spacer(modifier = Modifier.height(height = 2.dp))
            Text(
                text = subTitle,
                color = colorResource(R.color.color_9CA3AF),
                fontSize = 10.textDp,
                fontWeight = FontWeight.W600,
            )
        }
    }
}