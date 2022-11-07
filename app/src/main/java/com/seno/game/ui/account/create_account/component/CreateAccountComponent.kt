package com.seno.game.ui.account.create_account.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import com.seno.game.extensions.noRippleClickable
import com.seno.game.extensions.textDp

@Composable
fun SnsLoginButton(
    snsImage: Painter,
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(fraction = 0.8f)
            .noRippleClickable(onClick = onClick)
    ) {
        Icon(
            painter = snsImage,
            contentDescription = null,
            modifier = Modifier.align(Alignment.CenterStart)
        )
        Text(
            text = text,
            style = TextStyle(
                fontSize = 14.textDp,
                color = Color.Black
            ),
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

