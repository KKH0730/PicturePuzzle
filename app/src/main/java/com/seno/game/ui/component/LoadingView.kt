package com.seno.game.ui.component

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import com.airbnb.lottie.compose.*
import com.seno.game.R
import com.seno.game.extensions.noRippleClickable
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun LoadingView() {
    val scope = rememberCoroutineScope()
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.cat_loading)
    )
    val lottieAnimatable = rememberLottieAnimatable()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xA9252525))
            .noRippleClickable { }
    ) {
        LottieAnimation(
            composition = composition,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.align(alignment = Alignment.Center)
        )
    }
    scope.launch {
        lottieAnimatable.animate(
            composition = composition,
            clipSpec = null,
            speed = 2f,
            initialProgress = 0f
        )
    }
}