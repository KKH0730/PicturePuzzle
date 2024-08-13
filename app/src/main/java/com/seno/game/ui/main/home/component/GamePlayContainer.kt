package com.seno.game.ui.main.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.seno.game.R
import com.seno.game.extensions.noRippleClickable

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
        SoloPlayButton(onClick = onClickSoloPlay)
        MultiPlayButton(onClick = onClickMultiPlay)
        QuitButton(onClick = onClickQuit)
    }
}

@Composable
fun SoloPlayButton(onClick: () -> Unit) {
    Image(
        painter = painterResource(id = R.drawable.ic_sol_play_button),
        contentDescription = null,
        modifier = Modifier
            .width(width = 125.dp)
            .aspectRatio(2.6f)
            .noRippleClickable { onClick.invoke() }
    )
}

@Composable
fun MultiPlayButton(onClick: () -> Unit) {
    Image(
        painter = painterResource(id = R.drawable.ic_multi_play_button),
        contentDescription = null,
        modifier = Modifier
            .width(width = 125.dp)
            .aspectRatio(2.6f)
            .noRippleClickable { onClick.invoke() }
    )
}

@Composable
fun QuitButton(onClick: () -> Unit) {
    Image(
        painter = painterResource(id = R.drawable.ic_quit_button),
        contentDescription = null,
        modifier = Modifier
            .width(width = 125.dp)
            .aspectRatio(2.6f)
            .noRippleClickable { onClick.invoke() }
    )
}