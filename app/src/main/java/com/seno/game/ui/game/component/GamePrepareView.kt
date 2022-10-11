package com.seno.game.ui.game.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.seno.game.R
import com.seno.game.extensions.getString
import com.seno.game.extensions.noRippleClickable
import com.seno.game.extensions.textDp
import kotlinx.coroutines.delay

@Composable
fun GamePrepareView(onFinishPrepare: () -> Unit) {
    var prepareText by remember { mutableStateOf("3") }
    var prepareCount by remember { mutableStateOf(3) }

    LaunchedEffect(key1 = prepareCount) {
        when {
            prepareCount - 1 > 0 -> {
                delay(1000)
                prepareCount -= 1
                prepareText = prepareCount.toString()
            }
            prepareCount - 1 == 0 -> {
                delay(1000)
                prepareText = getString(R.string.game_prepare_start)

                delay(700)
                onFinishPrepare.invoke()
            }
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0x804D4C4C))
            .noRippleClickable { }
    ) {
        Text(
            text = prepareText,
            style = TextStyle(
                fontSize = 48.textDp,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold
            )
        )
    }
}

@Composable
fun GameEndView(onGameFinished: () -> Unit) {

}