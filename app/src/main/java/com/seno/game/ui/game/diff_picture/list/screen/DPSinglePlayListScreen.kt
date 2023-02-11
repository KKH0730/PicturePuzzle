package com.seno.game.ui.game.diff_picture.list.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.seno.game.R
import com.seno.game.extensions.noRippleClickable
import com.seno.game.prefs.PrefsManager
import com.seno.game.ui.game.diff_picture.list.component.GameListHeader
import com.seno.game.ui.game.diff_picture.list.component.LifePointGuideTerm
import com.seno.game.ui.game.diff_picture.list.component.PlayButton
import com.seno.game.ui.game.diff_picture.list.component.SingleGameGridList
import com.seno.game.ui.game.diff_picture.list.model.DPSingleGame

@Composable
fun DPSinglePlayListScreen(
    stageInfos: List<List<DPSingleGame>>,
    onClickBack: () -> Unit,
    onClickGameItem: (DPSingleGame) -> Unit,
    onClickPlayButton: () -> Unit
) {

    var pagerPage by remember { mutableStateOf(PrefsManager.diifPictureStage) }
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
            GameListHeader(onClickBack = onClickBack)
            Spacer(modifier = Modifier.height(height = 53.dp))
            LifePointGuideTerm()
            Spacer(modifier = Modifier.height(height = 33.dp))
            SingleGameGridList(
                stageInfos = stageInfos,
                onClickGameItem = onClickGameItem,
                pagerPage = pagerPage,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(height = 40.dp))
            PlayButton(onClick = onClickPlayButton)
        }
    }
}