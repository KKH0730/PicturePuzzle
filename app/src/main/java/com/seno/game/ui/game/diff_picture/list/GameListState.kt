package com.seno.game.ui.game.diff_picture.list

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import com.airbnb.lottie.compose.LottieAnimatable
import com.seno.game.ui.game.diff_picture.list.model.DPSingleGame

class GameListState(
    val gridState: LazyGridState,
    val gameList: MutableState<List<DPSingleGame>>,
    val lottieAnimatableList: MutableState<List<LottieAnimatable>>
) {
}

@Composable
fun rememberGameListState(
    gridState: LazyGridState,
    gameList: MutableState<List<DPSingleGame>>,
    lottieAnimatableList: MutableState<List<LottieAnimatable>>,
) = remember(gameList) {
    GameListState(
        gridState = gridState,
        gameList = gameList,
        lottieAnimatableList = lottieAnimatableList
    )
}