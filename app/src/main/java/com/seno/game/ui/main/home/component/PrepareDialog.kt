package com.seno.game.ui.main.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.seno.game.R
import com.seno.game.extensions.noRippleClickable
import com.seno.game.extensions.textDp

@Composable
fun PrepareDialog(
    onDismissed: () -> Unit,
    onClickConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismissed) {
        Card(
            backgroundColor = Color.White,
            shape = RoundedCornerShape(size = 30.dp),
            modifier = Modifier.width(width = 200.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                    text = stringResource(id = R.string.home_prepare_message),
                    color = colorResource(id = R.color.color_b8c0ff),
                    fontSize = 16.textDp
                )
                Spacer(modifier = Modifier.height(height = 15.dp))
                PrepareDialogConfirmButton(modifier = Modifier.align(alignment = Alignment.CenterHorizontally), onClick = onClickConfirm)
                Spacer(modifier = Modifier.height(height = 20.dp))
            }
        }
    }
}

@Composable
fun PrepareDialogConfirmButton(modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .width(width = 130.dp)
            .noRippleClickable { onClick.invoke() }
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_dialog_button_n),
            contentDescription = null,
            modifier = Modifier.align(alignment = Alignment.Center)
        )
        Text(
            text = stringResource(id = R.string.home_prepare_confirm),
            color = colorResource(id = R.color.color_bbd0ff),
            fontSize = 16.textDp,
            modifier = Modifier.align(alignment = Alignment.Center)
        )
    }
}

