package com.seno.game.ui.main.home.game.diff_picture.multi.entry.join.component

import androidx.annotation.ColorRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seno.game.R
import com.seno.game.extensions.noRippleClickable
import com.seno.game.extensions.textDp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun QRScanFooter(
    onClickFlash: () -> Unit,
    onClickRefocus: () -> Unit
) {
    var isOnFlash by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = 200.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height = 100.dp)
                .align(alignment = Alignment.TopCenter)
        ) {
            Text(
                text = "QR 코드를 카메라에 맞춰 주세요",
                style = TextStyle(
                    fontSize = 16.textDp,
                    color = colorResource(R.color.white),
                    fontWeight = FontWeight.W500
                ),
                modifier = Modifier.align(alignment = Alignment.Center)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(height = 100.dp)
                .clip(shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(color = colorResource(R.color.white))
                .align(alignment = Alignment.BottomCenter)
        ) {
            QRScanSplash(
                isOnFlash = isOnFlash,
                modifier = Modifier.weight(weight = 1f),
                onClick = {
                    isOnFlash = !isOnFlash
                    onClickFlash.invoke()
                }
            )
            QRScanReFocus(modifier = Modifier.weight(weight = 1f), onClick = onClickRefocus)
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
    val diagonal = remember { Animatable(80f) }


    LaunchedEffect(isOnFlash) {
        if (isOnFlash) {
            diagonal.snapTo(0f)
            bgSize.animateTo(
                targetValue = 36.dp,
                animationSpec = tween(200, easing = FastOutLinearInEasing)
            )
        } else {
            bgSize.animateTo(
                targetValue = 0.dp,
                animationSpec = tween(100, easing = FastOutLinearInEasing)
            )

            diagonal.animateTo(
                targetValue = 80f,
                animationSpec = tween(200, easing = FastOutSlowInEasing)
            )
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .height(intrinsicSize = IntrinsicSize.Min)
            .noRippleClickable(onClick = onClick)
    ) {
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
                    .clip(RoundedCornerShape(10.dp))
                    .background(color = colorResource(R.color.color_F0A33C))
                    .align(Alignment.Center)
            )
            Image(
                painter = painterResource(id = R.drawable.ic_outline_flash_on),
                contentDescription = null,
                colorFilter = ColorFilter.tint(
                    colorResource(R.color.black)
                ),
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.Center)
            )

            val angleRad = 50f * (Math.PI / 180f)  // 50도를 라디안으로 변환
            Canvas(
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.Center)
            ) {
                val startX = 20f
                val startY = 10f
                val dx = (diagonal.value * cos(angleRad)).toFloat()
                val dy = (diagonal.value * sin(angleRad)).toFloat()
                drawLine(
                    color = Color.Black,
                    start = Offset(startX, startY),
                    end = Offset(startX + dx, startY + dy), // start 기준으로 dx, dy 더하기
                    strokeWidth = 2.dp.toPx()
                )
            }
        }
        Spacer(modifier = Modifier.height(height = 4.dp))
        Text(
            text = "플래쉬",
            style = TextStyle(
                fontSize = 14.textDp,
                color = colorResource(R.color.black),
                fontWeight = FontWeight.W400
            ),
        )
    }
}

@Composable
fun QRScanReFocus(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.noRippleClickable(onClick = onClick)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_focus),
            contentDescription = null,
            tint = colorResource(R.color.black),
            modifier = Modifier.size(size = 36.dp)
        )
        Spacer(modifier = Modifier.height(height = 4.dp))
        Text(
            text = "초점",
            style = TextStyle(
                fontSize = 14.textDp,
                color = colorResource(R.color.black),
                fontWeight = FontWeight.W400
            ),
        )
    }
}