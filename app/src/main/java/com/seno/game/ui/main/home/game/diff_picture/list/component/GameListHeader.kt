package com.seno.game.ui.main.home.game.diff_picture.list.component

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.seno.game.R
import com.seno.game.extensions.noRippleClickable
import com.seno.game.extensions.textDp
import com.seno.game.prefs.PrefsManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val SECOND = 1000
const val MINUTE_1 = 60000L
const val MINUTE_3 = MINUTE_1 * 3
const val TOTAL_HEART_COUNT = 5

private fun getHeartCount(prevHeartCount: Int, currentTime: Long, prevChargeHeartTime: Long): Int {
    val heartsNeededForCharge = ((currentTime - prevChargeHeartTime) / MINUTE_3)
    return if (heartsNeededForCharge + prevHeartCount >= TOTAL_HEART_COUNT) {
        TOTAL_HEART_COUNT
    } else {
        heartsNeededForCharge.toInt() + prevHeartCount
    }
}

private fun getHeartTime(heartCount: Int, currentTime: Long, prevChargeHeartTime: Long): Long {
    val timeGab = MINUTE_3 - (currentTime - prevChargeHeartTime)
    return if (prevChargeHeartTime == 0L || heartCount == TOTAL_HEART_COUNT) {
        MINUTE_3
    } else if (timeGab <= 0) {
        MINUTE_3 + timeGab
    } else {
        timeGab
    }
}

@Composable
fun GameListHeader(
    onClickBack: () -> Unit,
    onChangedHeartTime: suspend (Long) -> Unit
) {
    val scope = rememberCoroutineScope()
    var heartCount by remember { mutableIntStateOf(PrefsManager.diffPictureHeartCount) }
    var heartTime by remember { mutableLongStateOf(MINUTE_3) }

    val lifeCycleOwner = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(key1 = lifeCycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event != Lifecycle.Event.ON_RESUME) {
                return@LifecycleEventObserver
            }

            val currentTime = System.currentTimeMillis()
            val prevHeartCount = PrefsManager.diffPictureHeartCount
            val prevChargeHeartTime = PrefsManager.diffPictureHeartChargedTime

            heartCount = getHeartCount(
                prevHeartCount = prevHeartCount,
                currentTime = currentTime,
                prevChargeHeartTime = prevChargeHeartTime
            ).also {
                PrefsManager.diffPictureHeartCount = it

                if (prevHeartCount != it) {
                    PrefsManager.diffPictureHeartChargedTime = if (prevChargeHeartTime == 0L) 0L else currentTime - (currentTime % prevChargeHeartTime)
                    scope.launch { onChangedHeartTime.invoke(PrefsManager.diffPictureHeartChargedTime) }
                }
            }

            heartTime = getHeartTime(
                heartCount = heartCount,
                currentTime = currentTime,
                prevChargeHeartTime = prevChargeHeartTime
            )
        }
        lifeCycleOwner.addObserver(observer)
        onDispose { lifeCycleOwner.removeObserver(observer) }
    }

    if (heartCount < TOTAL_HEART_COUNT) {
        LaunchedEffect(key1 = heartTime) {
            if (heartCount == TOTAL_HEART_COUNT) {
                return@LaunchedEffect
            }

            if (heartTime > 0L) {
                delay(1000)
                heartTime -= SECOND
            }  else {
                if (heartCount < TOTAL_HEART_COUNT) {
                    heartCount += 1
                    PrefsManager.diffPictureHeartCount += 1

                    PrefsManager.diffPictureHeartChargedTime = System.currentTimeMillis()
                    onChangedHeartTime.invoke(PrefsManager.diffPictureHeartChargedTime)
                }

                heartTime = MINUTE_3
            }
        }
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        GamePlayHeaderBack(onClickBack = onClickBack)
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .align(alignment = Alignment.CenterEnd)
                .offset(x = (-24).dp)
        ) {
            GamePlayHeartPoint(heartCount = heartCount)
            GamePlayHeartTimer(heartTime = heartTime)
        }
    }
}

@Composable
fun BoxScope.GamePlayHeaderBack(onClickBack: () -> Unit) {
    Image(
        painter = painterResource(id = R.drawable.ic_arrow_left_white),
        contentDescription = "back_arrow",
        modifier = Modifier
            .offset(x = 24.dp)
            .align(alignment = Alignment.TopStart)
            .noRippleClickable(onClick = onClickBack)
    )
}

@Composable
fun GamePlayHeartPoint(heartCount: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(space = 5.dp),) {
        Image(
            painter = painterResource(id = if (heartCount > 0) R.drawable.ic_heart_full else R.drawable.ic_heart_empty),
            contentDescription = "heart1",
            modifier = Modifier.size(size = 24.dp)
        )
        Image(
            painter = painterResource(id = if (heartCount > 1) R.drawable.ic_heart_full else R.drawable.ic_heart_empty),
            contentDescription = "heart2",
            modifier = Modifier.size(size = 24.dp)
        )
        Image(
            painter = painterResource(id = if (heartCount > 2) R.drawable.ic_heart_full else R.drawable.ic_heart_empty),
            contentDescription = "heart3",
            modifier = Modifier.size(size = 24.dp)
        )
        Image(
            painter = painterResource(id = if (heartCount > 3) R.drawable.ic_heart_full else R.drawable.ic_heart_empty),
            contentDescription = "heart4",
            modifier = Modifier.size(size = 24.dp)
        )
        Image(
            painter = painterResource(id = if (heartCount > 4) R.drawable.ic_heart_full else R.drawable.ic_heart_empty),
            contentDescription = "heart5",
            modifier = Modifier.size(size = 24.dp)
        )
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun GamePlayHeartTimer(heartTime: Long) {
    Column {
        Spacer(modifier = Modifier.height(height = 10.dp))
        Text(
            text = String.format("%02d", (heartTime / MINUTE_1))
                + ":"
                + String.format("%02d", (heartTime % MINUTE_1) / 1000),
            color = Color.White,
            fontSize = 16.textDp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(height = 10.dp))
    }
}

@Composable
fun LifePointGuideTerm() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "SELECT STAGE",
            fontSize = 25.textDp,
            fontWeight = FontWeight.W700,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(height = 18.dp))
        Text(
            text = stringResource(id = R.string.diff_game_list_life_point_term),
            fontSize = 13.textDp,
            fontWeight = FontWeight.W400,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 29.dp)
        )
    }
}

@Composable
@Preview
fun GameListHeaderPreview() {
    GameListHeader(
        onClickBack = {},
        onChangedHeartTime = {}
    )
}