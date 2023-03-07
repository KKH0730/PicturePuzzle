package com.seno.game.ui.main.home.game.diff_picture.list.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.seno.game.R
import com.seno.game.extensions.noRippleClickable
import com.seno.game.extensions.textDp
import com.seno.game.ui.main.home.game.diff_picture.list.model.DPSingleGame

@Composable
fun GameItem(
    index: Int,
    dpSingleGame: DPSingleGame,
    isComplete: Boolean,
    isFistIsNotCompleteIndex: Int?,
    isSelected: Boolean,
    onClickGameItem: (DPSingleGame) -> Unit,
) {
    StageCircle(
        index = index,
        isComplete = isComplete,
        isSelected = isSelected,
        isFistIsNotCompleteIndex = isFistIsNotCompleteIndex,
        dpSingleGame = dpSingleGame,
        onClickGameItem = onClickGameItem
    )
}

@Composable
fun StageCircle(
    index: Int,
    isComplete: Boolean,
    isSelected: Boolean,
    isFistIsNotCompleteIndex: Int?,
    dpSingleGame: DPSingleGame,
    onClickGameItem: (DPSingleGame) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = Modifier.size(size = 36.dp)) {
        if (isSelected) {
            Image(
                painter = painterResource(id = R.drawable.img_stage_selection_ring),
                contentDescription = null,
                modifier = Modifier
                    .size(size = 36.dp)
                    .align(alignment = Alignment.Center)
            )
        }

        if (isComplete) {
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
                        onClickGameItem
                            .takeIf { isComplete }
                            ?.invoke(dpSingleGame)
                    }
            )
        } else {
            Box(
                modifier = modifier
                    .clip(shape = CircleShape)
                    .size(30.dp)
                    .background(color = colorResource(id = R.color.color_B5EAEAE8))
                    .align(alignment = Alignment.Center)
                    .noRippleClickable {
                        onClickGameItem
                            .takeIf {
                                isFistIsNotCompleteIndex != null && dpSingleGame.id <= isFistIsNotCompleteIndex
                            }
                            ?.invoke(dpSingleGame)
                    }
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
}