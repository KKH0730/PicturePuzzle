package com.seno.game.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import com.seno.game.R
import com.seno.game.extensions.noRippleClickable
import com.seno.game.extensions.textDp

@Composable
fun LoadingView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.color_4DBBD0FF))
            .noRippleClickable { }
    ) {
        Text(
            text = "로딩중!!!...",
            fontSize = 24.textDp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(alignment = Alignment.Center)
        )
    }
}