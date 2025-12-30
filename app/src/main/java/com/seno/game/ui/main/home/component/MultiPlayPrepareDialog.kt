package com.seno.game.ui.main.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.seno.game.R
import com.seno.game.extensions.noRippleClickable
import com.seno.game.extensions.textDp

@Composable
fun MultiPlayPrepareDialog(
    onClickYes: () -> Unit,
    onDismissed: () -> Unit
) {
    Dialog(onDismissRequest = onDismissed) {
        Card(
            backgroundColor = Color.White,
            shape = RoundedCornerShape(size = 30.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(height = 35.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_dialog_cat_crying),
                    contentDescription = null,
                    modifier = Modifier
                        .width(width = 56.dp)
                        .height(height = 59.dp)
                )
                Spacer(modifier = Modifier.height(height = 23.dp))
                Text(
                    text = stringResource(id = R.string.home_play_multi_prepare),
                    color = colorResource(id = R.color.color_b8c0ff),
                    fontSize = 16.textDp
                )
                Spacer(modifier = Modifier.height(height = 28.dp))
                Row() {
                    Spacer(modifier = Modifier.width(width = 10.dp))
                    QuitDialogYesButton(text = stringResource(id = R.string.diff_complete), onClick = onClickYes)
                    Spacer(modifier = Modifier.width(width = 10.dp))
                }
                Spacer(modifier = Modifier.height(height = 25.dp))
            }
        }
    }
}

@Preview
@Composable
fun MultiPlayPrepareDialogPreview() {
    MultiPlayPrepareDialog(
        onClickYes = {},
        onDismissed = {}
    )
}