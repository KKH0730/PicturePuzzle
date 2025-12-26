package com.seno.game.ui.main.home.game.diff_picture.list.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.seno.game.R
import com.seno.game.ui.component.BannerADView
import com.seno.game.ui.main.home.game.diff_picture.list.component.GameListHeader
import com.seno.game.ui.main.home.game.diff_picture.list.component.GameStageList
import com.seno.game.ui.main.home.game.diff_picture.list.component.LifePointGuideTerm
import com.seno.game.ui.main.home.game.diff_picture.list.model.DPSingleGame
import com.seno.game.ui.main.home.game.diff_picture.list.rememberGameListState

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnrememberedMutableState", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DPSinglePlayListScreen(
    stageInfos: List<List<DPSingleGame>>,
    stage: Int,
    onChangedStage: (Int) -> Unit,
    onClickBack: () -> Unit,
    onClickGameItem: (DPSingleGame) -> Unit,
    onChangedHeartTime: suspend (Long) -> Unit
) {
    val insets = WindowInsets.systemBars.asPaddingValues()
    val gameListState = rememberGameListState(
        gridState = rememberLazyGridState(),
        stageInfos = mutableStateOf(stageInfos)
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
            modifier = Modifier
                .fillMaxSize()
                .padding(top = insets.calculateTopPadding())
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(height = 14.dp))
            GameListHeader(onClickBack = onClickBack, onChangedHeartTime = onChangedHeartTime)
            Spacer(modifier = Modifier.height(height = 16.dp))
            LifePointGuideTerm()
            Spacer(modifier = Modifier.height(height = 33.dp))
            GameStageList(
                gameListState = gameListState,
                stage = stage,
                onChangedStage = onChangedStage,
                onClickGameItem = onClickGameItem
            )
            Spacer(modifier = Modifier.height(height = 40.dp))
            BannerADView(modifier = Modifier.height(height = 50.dp))
            Spacer(modifier = Modifier.height(height = 80.dp))
        }
    }
}