package com.seno.game.ui.game.diffgame.list.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.seno.game.extensions.noRippleClickable
import com.seno.game.extensions.startActivity
import com.seno.game.extensions.textDp
import com.seno.game.ui.game.diffgame.single.DiffPictureSingleGameActivity

@Composable
fun GridGameLevelList() {
    val state = rememberLazyGridState()
    val context = LocalContext.current

    LazyVerticalGrid(
        columns = GridCells.Fixed(count = 4),
        state = state,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(space = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(space = 10.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        (0..100).forEach { index ->
            item {
                GameLevelItem(
                    index = index,
                    onClickItem = {
                        DiffPictureSingleGameActivity.start(context = context)
                    }
                )
            }
        }
    }
}

val levelArray = arrayOf("쉬움", "보통", "어려움", "지옥")

@Composable
fun GameLevelItem(
    index: Int,
    onClickItem: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .height(100.dp)
            .fillMaxWidth(0.3f)
            .noRippleClickable { onClickItem.invoke() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxHeight()
        ) {
            Spacer(modifier = Modifier.height(height = 20.dp))
            Text(
                text = "Lv $index",
                fontSize = 18.textDp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.weight(weight = 1f))
            Text(
                text = when {
                    index < 25 -> levelArray[0]
                    index < 50 -> levelArray[1]
                    index < 75 -> levelArray[2]
                    else -> levelArray[3]
                },
                fontSize = 14.textDp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(height = 10.dp))
        }
    }
}