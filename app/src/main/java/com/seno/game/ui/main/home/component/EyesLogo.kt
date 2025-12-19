package com.seno.game.ui.main.home.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.seno.game.R
import kotlinx.coroutines.delay


@Composable
fun EyesLogo(isBlink: Boolean, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.padding(16.dp)
    ) {
        BlinkingEye(isBlink = isBlink)
        BlinkingEye(isBlink = isBlink)
    }
}

@Composable
fun BlinkingEye(
    isBlink: Boolean,
    modifier: Modifier = Modifier,
    blinkInterval: Long = 800L,
    movingInterval: Long = 500L
) {
    val scaleY = remember { Animatable(1f) }
    val translateX = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        while (isBlink) {
            delay(blinkInterval)
            translateX.snapTo(1f)
            translateX.animateTo(
                targetValue = 110f,
                animationSpec = tween(durationMillis = 400)
            )
            translateX.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 400)
            )
            delay(movingInterval)
            scaleY.animateTo(
                targetValue = 0.1f,
                animationSpec = tween(durationMillis = 120)
            )
            scaleY.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 120)
            )
            scaleY.animateTo(
                targetValue = 0.1f,
                animationSpec = tween(durationMillis = 120)
            )
            scaleY.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 120)
            )
        }
    }

    Box(
        modifier = modifier
            .size(width = 100.dp, height = 80.dp)
            .graphicsLayer {
                this@graphicsLayer.scaleY = scaleY.value
                transformOrigin = TransformOrigin(0.5f, 0.5f)
            }
            .background(
                color = Color.White,
                shape = RoundedCornerShape(
                    topStartPercent = 80,
                    topEndPercent = 80,
                    bottomStartPercent = 20,
                    bottomEndPercent = 20
                )
            )
            .border(
                width = 2.dp,
                color = colorResource(R.color.color_606264),
                shape = RoundedCornerShape(
                    topStartPercent = 80,
                    topEndPercent = 80,
                    bottomStartPercent = 20,
                    bottomEndPercent = 20
                )
            ),
        contentAlignment = Alignment.BottomStart
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .offset(x = 20.dp, y = (-15).dp)
                .graphicsLayer {
                    this@graphicsLayer.translationX = translateX.value
                    transformOrigin = TransformOrigin(0.5f, 0.5f)
                }
                .background(
                    color = colorResource(R.color.color_606264),
                    shape = CircleShape
                )
        )
    }
}
