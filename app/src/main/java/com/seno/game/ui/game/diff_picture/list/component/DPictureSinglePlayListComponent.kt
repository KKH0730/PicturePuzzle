package com.seno.game.ui.game.diff_picture.list.component

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.seno.game.R
import com.seno.game.extensions.noRippleClickable
import com.seno.game.extensions.textDp
import com.seno.game.ui.game.diff_picture.list.model.DPSingleGame
import com.seno.game.ui.game.diff_picture.list.model.DPSingleGameLevel
import com.seno.game.ui.game.diff_picture.list.rememberGameListState

@SuppressLint("UnrememberedMutableState")
@Composable
fun GridGameLevelList(
    gameList: List<DPSingleGame>,
    onClickItem: (DPSingleGame) -> Unit,
) {
    val gameListState = rememberGameListState(
        gridState = rememberLazyGridState(),
        gameList = mutableStateOf(gameList),
        lottieAnimatableList = mutableStateOf(gameList.map { LottieAnimatable() })
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(count = 3),
        state = gameListState.gridState,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(space = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(space = 10.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        itemsIndexed(
            items = gameListState.gameList.value,
            key = { _, item: DPSingleGame -> item.id },
        ) { index: Int, dpSingleGame: DPSingleGame ->
            GameLevelItem(
                lottieAnimatable = gameListState.lottieAnimatableList.value[index],
                dpSingleGame = dpSingleGame,
                isNeedLock = gameList.any { !dpSingleGame.isComplete },
                onClickItem = onClickItem
            )
        }
    }
}

@Composable
fun GameLevelItem(
    lottieAnimatable: LottieAnimatable,
    dpSingleGame: DPSingleGame,
    isNeedLock: Boolean,
    onClickItem: (DPSingleGame) -> Unit,
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.locker)
    )

    LaunchedEffect(composition) {
        lottieAnimatable.animate(
            composition = composition,
            clipSpec = LottieClipSpec.Frame(35, 60),
            speed = 1f,
            initialProgress = lottieAnimatable.progress,
        )
    }

    Card(
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .height(100.dp)
            .fillMaxWidth(0.3f)
            .noRippleClickable {
                onClickItem
                    .takeIf { !isNeedLock }
                    ?.invoke(dpSingleGame)
            }
    ) {
        Box() {
            Image(
                painter = painterResource(id = dpSingleGame.thumbnail),
                contentDescription = "thumbnail",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            if (isNeedLock) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = if (isNeedLock){
                            colorResource(id = R.color.color_D3737372)
                        } else {
                            colorResource(id = R.color.transparent)
                        })
                ) {
                    Spacer(modifier = Modifier.height(height = 10.dp))
                    Text(
                        text = "Lv ${dpSingleGame.id + 1}",
                        fontSize = 20.textDp,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.weight(weight = 1f))
                    Text(
                        text = DPSingleGameLevel.getLevelValue(dpSingleGame.level),
                        fontSize = 16.textDp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(height = 10.dp))
                }

                LottieAnimation(
                    composition = composition,
                    progress = { lottieAnimatable.progress },
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}