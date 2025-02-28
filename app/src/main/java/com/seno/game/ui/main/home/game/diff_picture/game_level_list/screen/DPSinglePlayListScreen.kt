package com.seno.game.ui.main.home.game.diff_picture.game_level_list.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.seno.game.R
import com.seno.game.ui.main.home.game.diff_picture.game_level_list.component.GameListHeader
import com.seno.game.ui.main.home.game.diff_picture.game_level_list.component.LifePointGuideTerm
import com.seno.game.ui.main.home.game.diff_picture.game_level_list.component.PlayButton
import com.seno.game.ui.main.home.game.diff_picture.game_level_list.component.SingleGameGridList
import com.seno.game.data.model.DPSingleGame
import com.seno.game.ui.main.home.game.diff_picture.game_level_list.rememberGameListState

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun DPSinglePlayListScreen(
    stageInfos: List<List<DPSingleGame>>,
    stage: Int,
    enablePlayButton: Boolean,
    onChangedStage: (Int) -> Unit,
    onClickBack: () -> Unit,
    onClickGameItem: (DPSingleGame) -> Unit,
    onClickPlayButton: () -> Unit,
    onChangedHeartTime: (Long) -> Unit
) {
    val pagerPage by rememberUpdatedState(newValue = stage)
    val gameListState = rememberGameListState(
        gridState = rememberLazyGridState(),
        pagerState = rememberPagerState(initialPage = pagerPage),
        stageInfos = mutableStateOf(stageInfos),
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.ic_home_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(height = 30.dp))
            GameListHeader(onClickBack = onClickBack, onChangedHeartTime = onChangedHeartTime)
            Spacer(modifier = Modifier.height(height = 53.dp))
            LifePointGuideTerm()
            Spacer(modifier = Modifier.height(height = 33.dp))
            SingleGameGridList(
                stageInfos = stageInfos,
                gameListState = gameListState,
                onChangedStage = onChangedStage,
                onClickGameItem = onClickGameItem,
                pagerPage = pagerPage,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(height = 40.dp))
            PlayButton(enablePlayButton = enablePlayButton, onClick = onClickPlayButton)
        }
    }
}