package com.seno.game.ui.main.home.game.diff_picture.list

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.seno.game.ui.main.home.game.diff_picture.list.model.DPSingleGame
import kotlinx.coroutines.CoroutineScope

class GameListState @OptIn(ExperimentalPagerApi::class) constructor(
    val gridState: LazyGridState,
    val pagerState: PagerState,
    val stageInfos: MutableState<List<List<DPSingleGame>>>,
    val coroutineScope: CoroutineScope
) {
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun rememberGameListState(
    gridState: LazyGridState,
    pagerState: PagerState,
    stageInfos: MutableState<List<List<DPSingleGame>>>,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) = remember(stageInfos) {
    GameListState(
        gridState = gridState,
        pagerState = pagerState,
        stageInfos = stageInfos,
        coroutineScope = coroutineScope,
    )
}