package com.seno.game.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.seno.game.R
import com.seno.game.extensions.noRippleClickable
import com.seno.game.extensions.textDp

@Composable
fun CommonCustomDialog(
    image: Painter,
    mainDescription: String,
    subDescription: String,
    leftButtonText: String,
    rightButtonText: String,
    onClickLeft: () -> Unit,
    onClickRight: () -> Unit,
    onDismissed: () -> Unit
) {
    Dialog(onDismissRequest = onDismissed) {
        Card(
            backgroundColor = Color.White,
            shape = RoundedCornerShape(size = 30.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(height = 35.dp))
                Image(
                    painter = image,
                    contentDescription = null,
                    modifier = Modifier
                        .width(width = 56.dp)
                        .height(height = 59.dp)
                )
                Spacer(modifier = Modifier.height(height = 14.dp))
                Text(
                    text = mainDescription,
                    color = colorResource(id = R.color.color_b8c0ff),
                    fontSize = 16.textDp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(height = 7.dp))
                Text(
                    text = subDescription,
                    color = colorResource(id = R.color.color_b8c0ff),
                    fontSize = 14.textDp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(height = 11.dp))
                Row() {
                    Spacer(modifier = Modifier.width(width = 10.dp))
                    CommonDialogLeftButton(leftButtonText = leftButtonText, onClick = onClickLeft)
                    CommonDialogRightButton(rightButtonText = rightButtonText, onClick = onClickRight)
                    Spacer(modifier = Modifier.width(width = 10.dp))
                }
                Spacer(modifier = Modifier.height(height = 25.dp))
            }
        }
    }
}

@Composable
fun CommonDialogLeftButton(leftButtonText: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(width = 130.dp)
            .noRippleClickable { onClick.invoke() }
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_dialog_button_n),
            contentDescription = null,
            modifier = Modifier.align(alignment = Alignment.Center)
        )
        Text(
            text = leftButtonText,
            color = colorResource(id = R.color.color_bbd0ff),
            fontSize = 16.textDp,
            modifier = Modifier.align(alignment = Alignment.Center)
        )
    }
}

@Composable
fun CommonDialogRightButton(rightButtonText: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(width = 130.dp)
            .noRippleClickable { onClick.invoke() }
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_dialog_button_y),
            contentDescription = null,
            modifier = Modifier.align(alignment = Alignment.Center)
        )
        Text(
            text = rightButtonText,
            color = Color.White,
            fontSize = 16.textDp,
            modifier = Modifier.align(alignment = Alignment.Center)
        )
    }
}

@Preview
@Composable
fun PreviewCommonCustomDialog() {
    CommonCustomDialog(
        image = painterResource(R.drawable.ic_dialog_cat_wow),
        mainDescription = stringResource(id = R.string.home_logout_message1),
        subDescription = stringResource(id = R.string.home_logout_message2),
        leftButtonText = stringResource(id = R.string.home_logout_n),
        rightButtonText = stringResource(id = R.string.home_logout_y),
        onClickLeft = {},
        onClickRight = {},
        onDismissed = {}
    )
}