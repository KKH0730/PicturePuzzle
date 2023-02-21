package com.seno.game.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.seno.game.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onFinished: () -> Unit
) {
    var countState by remember { mutableStateOf(0) }
    LaunchedEffect(countState) {
        if (countState > 2) {
            onFinished.invoke()
            return@LaunchedEffect
        }
        delay(1000)
        countState += 1
    }
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
            painter = painterResource(id = R.drawable.ic_studio_seno_logo),
            contentDescription = null,
        )
        Spacer(modifier = Modifier.height(height = 36.dp))
    }
}