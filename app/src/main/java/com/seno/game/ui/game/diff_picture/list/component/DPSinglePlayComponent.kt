package com.seno.game.ui.game.diff_picture.list.component

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.seno.game.R
import com.seno.game.extensions.noRippleClickable
import com.seno.game.extensions.textDp
import com.seno.game.ui.game.diff_picture.list.model.DPSingleGame
import com.seno.game.ui.game.diff_picture.list.rememberGameListState

@Composable
fun GameListHeader() {
    Box(modifier = Modifier.fillMaxWidth()) {
        GamePlayHeartPoint(modifier = Modifier.align(alignment = Alignment.Center))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.width(width = 16.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_left_white),
                contentDescription = "left_arrow",
            )
            Spacer(modifier = Modifier.weight(weight = 1f))
            GamePlayHeartTimer(modifier = Modifier.align(alignment = Alignment.Bottom))
            Spacer(modifier = Modifier.width(width = 16.dp))
        }
    }

}

@Composable
fun GamePlayHeartPoint(modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = 5.dp),
        modifier = modifier
    ) {
        Image(painter = painterResource(id = R.drawable.ic_heart_full), contentDescription = "heart1")
        Image(painter = painterResource(id = R.drawable.ic_heart_full), contentDescription = "heart2")
        Image(painter = painterResource(id = R.drawable.ic_heart_full), contentDescription = "heart3")
        Image(painter = painterResource(id = R.drawable.ic_heart_full), contentDescription = "heart4")
        Image(painter = painterResource(id = R.drawable.ic_heart_full), contentDescription = "heart5")
    }
}

@Composable
fun GamePlayHeartTimer(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "04:57",
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
            fontWeight = FontWeight.Bold ,
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


@SuppressLint("UnrememberedMutableState")
@Composable
fun SingleGameGridList(
    gameList: List<DPSingleGame>,
    onClickItem: (DPSingleGame) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentStage: DPSingleGame? = try {
        gameList.first { !it.isComplete }
    } catch (e: Exception) {
        null
    }

    val gameListState = rememberGameListState(
        gridState = rememberLazyGridState(),
        gameList = mutableStateOf(gameList),
    )

    Box(modifier = modifier) {
        Card(
            elevation = 0.dp,
            backgroundColor = Color.Transparent,
            shape = RoundedCornerShape(size = 14.dp),
            border = BorderStroke(width = 3.dp, color = Color.White),
            modifier = Modifier
                .offset(y = 15.dp)
                .align(alignment = Alignment.TopCenter)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 36.dp),
                state = gameListState.gridState,
                contentPadding = PaddingValues(vertical = 24.dp, horizontal = 19.dp),
                verticalArrangement = Arrangement.spacedBy(space = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(space = 14.dp),
                modifier = Modifier.width(width = 244.dp)
            ) {
                itemsIndexed(
                    items = gameListState.gameList.value,
                    key = { _, item: DPSingleGame -> item.id },
                ) { index: Int, dpSingleGame: DPSingleGame ->
                    GameLevelItem(
                        index = index,
                        dpSingleGame = dpSingleGame,
                        isComplete = dpSingleGame.isComplete,
                        isCurrentStage = dpSingleGame.id == currentStage?.id,
                        onClickItem = onClickItem
                    )
                }
            }
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
                text = "STAGE PAGE 01",
                color = colorResource(id = R.color.color_c8b6ff),
                modifier = Modifier
                    .padding(paddingValues = PaddingValues(vertical = 5.dp, horizontal = 13.dp))
                    .align(alignment = Alignment.Center)
            )
        }
    }
}

@Composable
fun GameLevelItem(
    index: Int,
    dpSingleGame: DPSingleGame,
    isComplete: Boolean,
    isCurrentStage: Boolean,
    onClickItem: (DPSingleGame) -> Unit,
) {

    if (isComplete) {
        Box(modifier = Modifier.size(size = 36.dp)) {
            Image(
                painter = painterResource(id = R.drawable.ic_stage_done),
                contentDescription = "state_done",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(color = colorResource(id = R.color.color_B5EAEAE8))
                    .align(alignment = Alignment.Center)
                    .noRippleClickable {
                        onClickItem
                            .takeIf { isComplete }
                            ?.invoke(dpSingleGame)
                    }
            )
        }
    } else {
        if (isCurrentStage) {
            CurrentStateCircle(
                index = index,
                isCurrentStage = isCurrentStage,
                modifier = Modifier.noRippleClickable { onClickItem.invoke(dpSingleGame) }
                )
        } else {
            StageCircle(index = index)
        }
    }
}

@Composable
fun StageCircle(
    index: Int,
    modifier: Modifier = Modifier
) {
    Box(modifier = Modifier.size(size = 36.dp)) {
        Box(
            modifier = modifier
                .clip(shape = CircleShape)
                .size(30.dp)
                .background(color = colorResource(id = R.color.color_B5EAEAE8))
                .align(alignment = Alignment.Center)
        ) {
            Text(
                text = "${index + 1}",
                color = colorResource(id = R.color.color_b8c0ff),
                fontSize = 14.textDp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clip(shape = CircleShape)
                    .align(alignment = Alignment.Center)
            )
        }
    }
}

@Composable
fun CurrentStateCircle(
    index: Int,
    isCurrentStage: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.size(size = 36.dp)) {
        if (isCurrentStage) {
            Image(
                painter = painterResource(id = R.drawable.img_stage_selection_ring),
                contentDescription = null,
                modifier = Modifier
                    .size(size = 36.dp)
                    .align(alignment = Alignment.Center)
            )
        }

        Box(
            modifier = modifier
                .clip(shape = CircleShape)
                .size(30.dp)
                .background(color = colorResource(id = R.color.color_B5EAEAE8))
                .align(alignment = Alignment.Center)
        ) {
            Text(
                text = "${index + 1}",
                color = colorResource(id = R.color.color_b8c0ff),
                fontSize = 14.textDp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clip(shape = CircleShape)
                    .align(alignment = Alignment.Center)
            )
        }
    }
}