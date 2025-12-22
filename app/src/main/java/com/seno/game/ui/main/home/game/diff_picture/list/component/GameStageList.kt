package com.seno.game.ui.main.home.game.diff_picture.list.component

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import com.seno.game.R
import com.seno.game.extensions.noRippleClickable
import com.seno.game.extensions.textDp
import com.seno.game.ui.main.home.game.diff_picture.list.GameListState
import com.seno.game.ui.main.home.game.diff_picture.list.model.DPSingleGame
import com.seno.game.ui.main.home.game.diff_picture.list.rememberGameListState
import kotlinx.coroutines.delay

@Composable
fun GameStageList(
    gameListState: GameListState,
    stage: Int,
    onChangedStage: (Int) -> Unit,
    onClickGameItem: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(space = 15.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        gameListState.stageInfos.value.fastForEachIndexed { index, list ->
            StageCard(
                gameListState = gameListState,
                stageIndex = index,
                isStageSelected = index == stage,
                gameList = list,
                onClickStage = onChangedStage,
                onClickGameItem = onClickGameItem
            )
        }
    }
}

@Composable
fun StageCard(
    gameListState: GameListState,
    stageIndex: Int,
    isStageSelected: Boolean,
    gameList: List<DPSingleGame>,
    modifier: Modifier = Modifier,
    onClickStage: (Int) -> Unit = {},
    onClickGameItem: () -> Unit
) {
    val height by animateDpAsState(targetValue = if (isStageSelected) 280.dp else 88.dp)

    Card(
        shape = RoundedCornerShape(size = 24.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.white)),
        modifier = modifier.height(height = height)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onClickStage.invoke(stageIndex) }
                    )
            ) {
                Spacer(modifier = Modifier.height(height = 20.dp))
                StageCardCollapsedContents(
                    gameListState = gameListState,
                    stageIndex = stageIndex,
                    isStageSelected = isStageSelected,
                    gameList = gameList
                )
                Spacer(modifier = Modifier.height(height = 20.dp))
            }
            if (isStageSelected) {
                StageCardDivider()
                StageCardExpandedContents(
                    gameListState = gameListState,
                    stageIndex = stageIndex,
                    gameList = gameList,
                    onClickGameItem = onClickGameItem
                )
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun StageCardCollapsedContents(
    gameListState: GameListState,
    stageIndex: Int,
    isStageSelected: Boolean,
    gameList: List<DPSingleGame>
) {
    val dropDownArrowAngle by animateFloatAsState(targetValue = if (isStageSelected) 180f else 0f)
    val isLockStage = if (stageIndex == 0) {
        false
    } else {
        gameListState.stageInfos.value[stageIndex - 1].lastOrNull()?.isComplete == false
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier = Modifier.width(width = 20.dp))
        Card(
            shape = RoundedCornerShape(size = 16.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp),
            modifier = Modifier.size(size = 48.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = if (isLockStage) listOf(Color(0x80FFFFFF), Color(0x80FFFFFF)) else listOf(Color(0xFFF472B6), Color(0xFFB794F6))
                        )
                    )
            ) {
                Text(
                    "${stageIndex + 1}",
                    color = if (isLockStage) colorResource(R.color.color_9CA3AF) else colorResource(R.color.white),
                    fontSize = 16.textDp,
                    fontWeight = FontWeight.W600,
                    modifier = Modifier.align(alignment = Alignment.Center)
                )
            }
        }
        Spacer(modifier = Modifier.width(width = 16.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(space = 10.dp),
            modifier = Modifier.weight(weight = 1f)
        ) {
            Text(
                "Stage ${String.format("%02d", stageIndex + 1)}",
                color = if (isLockStage) colorResource(R.color.color_6B7280) else colorResource(R.color.black),
                fontSize = 15.textDp,
                fontWeight = FontWeight.W600,
            )
            Text(
                if (isLockStage) "Locked" else "${gameList.count { it.isComplete } } /15 Cleared",
                color = if (isLockStage) colorResource(R.color.color_9CA3AF) else colorResource(R.color.color_B794F6),
                fontSize = 10.textDp,
                fontWeight = FontWeight.W500,
            )
        }
        if (isLockStage) {
            Icon(
                painterResource(R.drawable.ic_lock),
                contentDescription = null,
                tint = colorResource(R.color.color_9CA3AF),
                modifier = Modifier
                    .width(width = 20.dp)
                    .height(20.dp)
                    .rotate(degrees = 0f)
            )
        } else {
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = colorResource(R.color.color_B794F6),
                modifier = Modifier
                    .width(width = 20.dp)
                    .height(20.dp)
                    .rotate(degrees = dropDownArrowAngle)
            )
        }
        Spacer(modifier = Modifier.width(width = 20.dp))
    }
}

@Composable
fun StageCardDivider() = Spacer(modifier = Modifier.fillMaxWidth().height(height = 1.dp).padding(horizontal = 20.dp).background(color = colorResource(R.color.divider)))

@Composable
fun ColumnScope.StageCardExpandedContents(
    gameListState: GameListState,
    stageIndex: Int,
    gameList: List<DPSingleGame>,
    onClickGameItem: () -> Unit
) {
    val fistIsNotCompleteGame: DPSingleGame? = gameList.firstOrNull { !it.isComplete }

    LazyVerticalGrid(
        columns = GridCells.Fixed(count = 5),
        state = gameListState.gridState,
        contentPadding = PaddingValues(
            top = 20.dp,
            bottom = 20.dp,
            start = 15.dp,
            end = 15.dp
        ),
        verticalArrangement = Arrangement.spacedBy(space = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(space = 5.dp),
        modifier = Modifier.weight(weight = 1f)
    ) {
        itemsIndexed(
            items = gameListState.stageInfos.value[stageIndex],
            key = { _, item: DPSingleGame -> "${item.stage}-${item.id}" },
        ) { index: Int, dpSingleGame: DPSingleGame ->
            StageCircle(
                stageIndex = stageIndex,
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

@Composable
fun StageCircle(
    stageIndex: Int,
    index: Int,
    isComplete: Boolean,
    isSelected: Boolean,
    isFistIsNotCompleteIndex: Int?,
    dpSingleGame: DPSingleGame,
    onClickGameItem: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scale = remember { Animatable(0f) }
    var animateTrigger by remember { mutableIntStateOf(0) }

    LaunchedEffect(key1 = stageIndex) {
        delay(100 + (index * 50L))

        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
    }

    LaunchedEffect(animateTrigger) {
        if (animateTrigger == 0) return@LaunchedEffect

        scale.snapTo(1f)
        scale.animateTo(
            1.3f,
            animationSpec = tween(durationMillis = 80)
        )
        scale.animateTo(
            1f,
            animationSpec = tween(durationMillis = 140)
        )
    }
    Box(
        modifier = Modifier
            .size(size = 40.dp)
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
    ) {
        if (isComplete) {
            Image(
                painter = painterResource(id = R.drawable.ic_stage_done),
                contentDescription = "state_done",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .border(
                        border = BorderStroke(width = 1.dp, color = colorResource(R.color.color_FF3e96)),
                        shape = CircleShape
                    )
                    .align(alignment = Alignment.Center)
                    .noRippleClickable {
                        onClickGameItem
                            .takeIf { isComplete }
                            ?.invoke()
                            ?.run { animateTrigger += 1 }
                    }
            )
        } else {
            if (!isSelected || dpSingleGame.id != isFistIsNotCompleteIndex) {
                Icon(
                    painterResource(R.drawable.ic_lock),
                    contentDescription = null,
                    tint = colorResource(R.color.color_9CA3AF),
                    modifier = Modifier
                        .size(size = 15.dp)
                        .align(alignment = Alignment.TopEnd)
                        .offset(x = (-5).dp, y = (-5).dp)
                )
            }
            Box(
                modifier = modifier
                    .clip(shape = CircleShape)
                    .size(40.dp)
                    .background(color = if (isSelected && dpSingleGame.id == isFistIsNotCompleteIndex) colorResource(id = R.color.color_B794F6) else colorResource(id = R.color.color_B5EAEAE8))
                    .align(alignment = Alignment.Center)
                    .noRippleClickable {
                        if (isSelected && dpSingleGame.id == isFistIsNotCompleteIndex) {
                            animateTrigger += 1
                            onClickGameItem.invoke()
                        } else {
                            onClickGameItem
                                .takeIf { isFistIsNotCompleteIndex != null && dpSingleGame.id <= isFistIsNotCompleteIndex }
                                ?.invoke()
                                ?.run { animateTrigger += 1 }
                        }

                    }
            ) {
                Text(
                    text = "${index + 1}",
                    color = if (isSelected && dpSingleGame.id == isFistIsNotCompleteIndex) colorResource(id = R.color.white) else colorResource(id = R.color.color_9CA3AF),
                    fontSize = 16.textDp,
                    fontWeight = FontWeight.W500,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clip(shape = CircleShape)
                        .align(alignment = Alignment.Center)
                )
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
fun GameStageListPreview() {
    val stageInfos: List<List<DPSingleGame>> = listOf(
        listOf(DPSingleGame(0, 0, true, false), DPSingleGame(0, 0, false, false)),
        listOf(DPSingleGame(0, 1, true, false), DPSingleGame(1, 1, true, false), DPSingleGame(2, 1, false, false))
    )
    val gameListState = rememberGameListState(
        gridState = rememberLazyGridState(),
        stageInfos = mutableStateOf(stageInfos)
    )

    GameStageList(
        gameListState = gameListState,
        stage = 0,
        onChangedStage = {},
        onClickGameItem = {}
    )
}

@Preview
@Composable
fun StageCirclePreview() {
    StageCircle(
        stageIndex = 0,
        index = 0,
        isComplete = false,
        isSelected = false,
        isFistIsNotCompleteIndex = null,
        dpSingleGame = DPSingleGame(0, 0, true, false),
        onClickGameItem = {}
    )
}
