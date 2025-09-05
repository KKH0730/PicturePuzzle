package com.seno.game.ui.main.home.game.diff_picture.multi.entry.join.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seno.game.R
import com.seno.game.extensions.noRippleClickable
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun QRScanHeader(
    onClickFlash: () -> Unit,
    onClickBack: () -> Unit
) {
    var isOnFlash by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height = 100.dp)
                .padding(horizontal = 20.dp)
                .align(alignment = Alignment.Center)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_left_white),
                contentDescription = null,
                modifier = Modifier
                    .size(size = 18.dp)
                    .align(alignment = Alignment.CenterStart)
                    .noRippleClickable { onClickBack.invoke() }
            )
            QRScanSplash(
                isOnFlash = isOnFlash,
                modifier = Modifier.align(alignment = Alignment.CenterEnd),
                onClick = {
                    isOnFlash = !isOnFlash
                    onClickFlash.invoke()
                }
            )
        }
    }
}

@Composable
fun QRScanSplash(
    isOnFlash: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bgSize = remember { Animatable(0.dp, Dp.VectorConverter) }
    val diagonal = remember { Animatable(50f) }

    var showFlashOn by remember { mutableStateOf(isOnFlash) }

    LaunchedEffect(isOnFlash) {
        if (isOnFlash) {
            diagonal.snapTo(0f)
            showFlashOn = true
            bgSize.animateTo(
                targetValue = 30.dp,
                animationSpec = tween(300, easing = FastOutLinearInEasing)
            )
        } else {
            bgSize.animateTo(
                targetValue = 0.dp,
                animationSpec = tween(100, easing = FastOutLinearInEasing)
            )
            showFlashOn = false

            diagonal.animateTo(
                targetValue = 50f,
                animationSpec = tween(200, easing = FastOutSlowInEasing)
            )
        }
    }

    Box(
        modifier = modifier.noRippleClickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(20.dp))
                .align(Alignment.Center)
        )
        Box(
            modifier = Modifier
                .size(bgSize.value)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Yellow)
                .align(Alignment.Center)
        )
        Image(
            painter = painterResource(id = R.drawable.ic_flash_on),
            contentDescription = null,
            colorFilter = ColorFilter.tint(
                if (showFlashOn) colorResource(R.color.black) else colorResource(R.color.white)
            ),
            modifier = Modifier
                .size(18.dp)
                .align(Alignment.Center)
        )


        val angleRad = 50f * (Math.PI / 180f)  // 50도를 라디안으로 변환
        Canvas(modifier = Modifier.size(50.dp)) { // Canvas 크기 충분히 확보
            val startX = 51f
            val startY = 49f
            val dx = (diagonal.value * cos(angleRad)).toFloat()
            val dy = (diagonal.value * sin(angleRad)).toFloat()
            drawLine(
                color = Color.White,
                start = Offset(startX, startY),
                end = Offset(startX + dx, startY + dy), // start 기준으로 dx, dy 더하기
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}