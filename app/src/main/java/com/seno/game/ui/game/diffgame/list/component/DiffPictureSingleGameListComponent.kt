package com.seno.game.ui.game.diffgame.list.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.seno.game.R
import com.seno.game.extensions.noRippleClickable
import com.seno.game.extensions.textDp
import com.seno.game.ui.game.diffgame.list.model.DPSingleGame
import com.seno.game.ui.game.diffgame.list.model.DPSingleGameLevel

@Composable
fun GridGameLevelList(
    gameList: List<DPSingleGame>,
    onClickItem: (position: Int) -> Unit
) {
    val state = rememberLazyGridState()

    LazyVerticalGrid(
        columns = GridCells.Fixed(count = 4),
        state = state,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(space = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(space = 10.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        gameList.forEach {
            item {
                GameLevelItem(
                    dpSingleGame = it,
                    onClickItem = onClickItem
                )
            }
        }
    }
}

@Composable
fun GameLevelItem(
    dpSingleGame: DPSingleGame,
    onClickItem: (position: Int) -> Unit,
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.lock)
    )
    val lottieAnimatable = rememberLottieAnimatable()

    LaunchedEffect(composition) {
        lottieAnimatable.animate(
            composition = composition,
            clipSpec = LottieClipSpec.Frame(0, 1200),
            initialProgress = 0f
        )
    }

    Card(
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .height(100.dp)
            .fillMaxWidth(0.3f)
            .noRippleClickable { onClickItem.invoke(dpSingleGame.id) }
    ) {
        Box() {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxHeight()
            ) {
                Spacer(modifier = Modifier.height(height = 20.dp))
                Text(
                    text = "Lv ${dpSingleGame.id}",
                    fontSize = 18.textDp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.weight(weight = 1f))
                Text(
                    text = DPSingleGameLevel.getLevelValue(dpSingleGame.level),
                    fontSize = 14.textDp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(height = 10.dp))
            }
            LottieAnimation(composition = composition, progress = { lottieAnimatable.progress })
        }
        
    }
}