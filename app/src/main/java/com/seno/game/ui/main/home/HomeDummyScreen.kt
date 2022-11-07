package com.seno.game.ui.main.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.seno.game.R

@Composable
fun HomeDummyScreen() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.color_e7c6ff))
    ) {
        Spacer(modifier = Modifier.height(height = 210.dp))
        Image(
            painter = painterResource(id = R.drawable.ic_splash),
            contentDescription = null,
            modifier = Modifier.width(width = 216.dp)
                .aspectRatio(ratio = 2.37f)
        )
        Spacer(modifier = Modifier.weight(weight = 1f))
        Image(
            painter = painterResource(id = R.drawable.ic_splash),
            contentDescription = null,
        )
        Spacer(modifier = Modifier.height(height = 36.dp))
    }
}