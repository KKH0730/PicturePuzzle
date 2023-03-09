package com.seno.game.ui.main.home.game.diff_picture.list.component

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.seno.game.R
import com.seno.game.extensions.noRippleClickable
import com.seno.game.extensions.textDp
import com.seno.game.prefs.PrefsManager
import com.seno.game.ui.main.home.game.diff_picture.list.GameListState
import com.seno.game.ui.main.home.game.diff_picture.list.model.DPSingleGame
import com.seno.game.ui.main.home.game.diff_picture.list.rememberGameListState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val SECOND = 1000
const val MINUTE_1 = 60000L
const val MINUTE_3 = MINUTE_1 * 3
const val TOTAL_HEART_COUNT = 5

@Composable
fun GameListHeader(
    onClickBack: () -> Unit,
    onChangedHeartTime: (Long) -> Unit
) {
    var heartCount by remember { mutableStateOf(PrefsManager.diffPictureHeartCount) }
    var heartTime by remember { mutableStateOf(MINUTE_3) }

    val lifeCycleOwner = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(key1 = lifeCycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event != Lifecycle.Event.ON_RESUME) {
                return@LifecycleEventObserver
            }

            val currentTime = System.currentTimeMillis()
            val prevHeartCount = PrefsManager.diffPictureHeartCount
            val prevChargeHeartTime = PrefsManager.diffPictureHeartChangedTime

            val onResumeChargeHeartCount = ((currentTime - PrefsManager.diffPictureHeartChangedTime) / MINUTE_3)
            heartCount = if (onResumeChargeHeartCount + PrefsManager.diffPictureHeartCount >= TOTAL_HEART_COUNT) {
                TOTAL_HEART_COUNT
            } else {
                onResumeChargeHeartCount.toInt() + PrefsManager.diffPictureHeartCount
            }.also {
                PrefsManager.diffPictureHeartCount = it
                if (prevHeartCount != it) {
                    PrefsManager.diffPictureHeartChangedTime = currentTime
                    onChangedHeartTime.invoke(PrefsManager.diffPictureHeartChangedTime)
                }
            }

            val onResumeTimeGab = MINUTE_3 - (currentTime - prevChargeHeartTime)
            heartTime = if (prevChargeHeartTime == 0L || heartCount == TOTAL_HEART_COUNT) {
                MINUTE_3
            } else if (onResumeTimeGab <= 0) {
                if (heartCount == TOTAL_HEART_COUNT) {
                    0
                } else {
                    MINUTE_3 + onResumeTimeGab
                }
            } else {
                onResumeTimeGab
            }
        }
        lifeCycleOwner.addObserver(observer)
        onDispose {
            lifeCycleOwner.removeObserver(observer)
        }
    }

    if (heartCount < TOTAL_HEART_COUNT) {
        LaunchedEffect(key1 = heartTime) {
            if (heartTime > 0L) {
                delay(1000)
            }

            if (heartCount == TOTAL_HEART_COUNT) {
                return@LaunchedEffect
            }

            if (heartTime <= 0L) {
                PrefsManager.diffPictureHeartChangedTime = System.currentTimeMillis()
                onChangedHeartTime.invoke(PrefsManager.diffPictureHeartChangedTime)

                heartTime = MINUTE_3

                if (heartCount < TOTAL_HEART_COUNT) {
                    heartCount += 1
                    PrefsManager.diffPictureHeartCount += 1
                } else {
                    heartCount -= 1
                    PrefsManager.diffPictureHeartCount -= 1
                }
            } else {
                heartTime -= SECOND
            }
        }
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        GamePlayHeartPoint(heartCount = heartCount, modifier = Modifier.align(alignment = Alignment.Center))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.width(width = 16.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_left_white),
                contentDescription = "back_arrow",
                modifier = Modifier.noRippleClickable { onClickBack.invoke() }
            )
            Spacer(modifier = Modifier.weight(weight = 1f))
            GamePlayHeartTimer(heartTime = heartTime, modifier = Modifier.align(alignment = Alignment.Bottom))
            Spacer(modifier = Modifier.width(width = 16.dp))
        }
    }
}

@Composable
fun GamePlayHeartPoint(heartCount: Int, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = 5.dp),
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = if (heartCount > 0) R.drawable.ic_heart_full else R.drawable.ic_heart_empty),
            contentDescription = "heart1"
        )
        Image(
            painter = painterResource(id = if (heartCount > 1) R.drawable.ic_heart_full else R.drawable.ic_heart_empty),
            contentDescription = "heart2"
        )
        Image(
            painter = painterResource(id = if (heartCount > 2) R.drawable.ic_heart_full else R.drawable.ic_heart_empty),
            contentDescription = "heart3"
        )
        Image(
            painter = painterResource(id = if (heartCount > 3) R.drawable.ic_heart_full else R.drawable.ic_heart_empty),
            contentDescription = "heart4"
        )
        Image(
            painter = painterResource(id = if (heartCount > 4) R.drawable.ic_heart_full else R.drawable.ic_heart_empty),
            contentDescription = "heart5"
        )
    }
}

@Composable
fun GamePlayHeartTimer(heartTime: Long, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = String.format("%02d", (heartTime / MINUTE_1)) +
                ":" +
                String.format("%02d", (heartTime % MINUTE_1) / 1000),
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
            text = "SOLO PLAY MODE",
            fontSize = 24.textDp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(height = 12.dp))
        Text(
            text = stringResource(id = R.string.diff_game_list_life_point_term),
            fontSize = 14.textDp,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 29.dp)
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@SuppressLint("UnrememberedMutableState", "CoroutineCreationDuringComposition")
@Composable
fun SingleGameGridList(
    stageInfos: List<List<DPSingleGame>>,
    gameListState: GameListState,
    onChangedStage: (Int) -> Unit,
    onClickGameItem: (DPSingleGame) -> Unit,
    pagerPage: Int,
    modifier: Modifier = Modifier,
) {
    var fistIsNotCompleteGame: DPSingleGame? = null
    run {
        stageInfos.forEach { gameList ->
            val firstIsNotCompleteGameModel = try {
                gameList.first { !it.isComplete }
            } catch (e: Exception) {
                null
            }
            if (firstIsNotCompleteGameModel != null) {
                fistIsNotCompleteGame = firstIsNotCompleteGameModel
                return@run
            }
        }
    }

    gameListState.scrollToPosition(page = pagerPage)

    LaunchedEffect(key1 = gameListState.pagerState) {
        snapshotFlow { gameListState.pagerState.currentPage }.collect {
            onChangedStage.invoke(it)
        }
    }

    Box(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(space = 21.dp),
            modifier = Modifier
                .offset(y = 15.dp)
                .align(alignment = Alignment.TopCenter)
        ) {
            NavigateGameStageArrow(
                imgResource = R.drawable.ic_arrow_left_white,
                contentDescription = "right_arrow",
                isVisible = pagerPage > 0,
                onClick = {
                    gameListState.coroutineScope.launch {
                        gameListState.pagerState.animateScrollToPage(gameListState.pagerState.currentPage - 1)
                    }
                }
            )
            Card(
                elevation = 0.dp,
                backgroundColor = Color.Transparent,
                shape = RoundedCornerShape(size = 14.dp),
                border = BorderStroke(width = 3.dp, color = Color.White),
            ) {
                HorizontalPager(
                    count = stageInfos.size,
                    state = gameListState.pagerState,
                    userScrollEnabled = false,
                    modifier = Modifier.width(width = 264.dp)
                ) { page ->
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 36.dp),
                        state = gameListState.gridState,
                        contentPadding = PaddingValues(
                            top = 40.dp,
                            bottom = 24.dp,
                            start = 20.dp,
                            end = 20.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(space = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(space = 5.dp),
                    ) {
                        itemsIndexed(
                            items = gameListState.stageInfos.value[page],
                            key = { _, item: DPSingleGame -> item.id },
                        ) { index: Int, dpSingleGame: DPSingleGame ->
                            GameItem(
                                index = index,
                                dpSingleGame = dpSingleGame,
                                isComplete = dpSingleGame.isComplete,
                                isFistIsNotCompleteIndex = fistIsNotCompleteGame?.id,
                                isSelected = dpSingleGame.isSelect,
                                onClickGameItem = onClickGameItem
                            )
                        }
                    }
                }
            }
            NavigateGameStageArrow(
                imgResource = R.drawable.ic_arrow_right_white,
                contentDescription = "right_arrow",
                isVisible = pagerPage < stageInfos.size - 1,
                onClick = {
                    gameListState.coroutineScope.launch {
                        gameListState.pagerState.animateScrollToPage(gameListState.pagerState.currentPage + 1)
                    }
                }
            )
        }
        Card(
            elevation = 0.dp,
            backgroundColor = Color.White,
            shape = RoundedCornerShape(size = 14.dp),
            modifier = Modifier
                .height(height = 30.dp)
                .align(alignment = Alignment.TopCenter)
        ) {
            Text(
                text = "STAGE PAGE ${
                    String.format(
                        "%02d",
                        (gameListState.pagerState.currentPage + 1)
                    )
                }",
                color = colorResource(id = R.color.color_c8b6ff),
                fontSize = 14.textDp,
                fontWeight = FontWeight.W500,
                modifier = Modifier
                    .padding(paddingValues = PaddingValues(vertical = 5.dp, horizontal = 13.dp))
                    .align(alignment = Alignment.Center)
            )
        }
    }
}

@Composable
fun NavigateGameStageArrow(
    @DrawableRes imgResource: Int,
    contentDescription: String,
    isVisible: Boolean,
    onClick: () -> Unit
) {
    Image(
        painter = painterResource(id = imgResource),
        contentDescription = contentDescription,
        modifier = Modifier
            .alpha(alpha = if (isVisible) 1f else 0f)
            .noRippleClickable {
                if (isVisible){
                    onClick.invoke()
                }
            }
    )
}

@Composable
fun PlayButton(
    enablePlayButton: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .border(
                border = BorderStroke(
                    width = 2.dp,
                    color = Color.White
                ),
                shape = RoundedCornerShape(size = 22.dp)
            )
            .noRippleClickable {
                onClick.takeIf { enablePlayButton }?.invoke()
            }
    ) {
        Text(
            text = "PLAY",
            color = colorResource(id = R.color.color_fbf8cc),
            fontSize = 20.textDp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 40.dp)
                .align(alignment = Alignment.Center)
        )
    }
}