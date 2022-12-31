package com.seno.game.ui.game.diff_picture.list.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.seno.game.R
import com.seno.game.extensions.textDp
import com.seno.game.ui.game.diff_picture.list.component.GameListHeader
import com.seno.game.ui.game.diff_picture.list.component.LifePointGuideTerm
import com.seno.game.ui.game.diff_picture.list.component.SingleGameGridList
import com.seno.game.ui.game.diff_picture.list.model.DPSingleGame

@Composable
fun DPSinglePlayListScreen(gameList: List<DPSingleGame>, onClickItem: (DPSingleGame) -> Unit) {
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
            GameListHeader()
            Spacer(modifier = Modifier.height(height = 53.dp))
            LifePointGuideTerm()
            Spacer(modifier = Modifier.height(height = 33.dp))
            SingleGameGridList(
                gameList = gameList,
                onClickItem = onClickItem,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            )
        }
    }

}