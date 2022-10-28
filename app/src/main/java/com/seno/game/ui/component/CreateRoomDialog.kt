package com.seno.game.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun CreateRoomDialog(
    onClickCreateRoom: () -> Unit,
    onClickCloseDialog: () -> Unit,
    onDismissed:() -> Unit
) {
    Dialog(onDismissRequest = onDismissed) {
        Column(
            modifier = Modifier
                .background(color = Color.White)
                .fillMaxWidth(fraction = 0.8f)
        ) {
            Text(text = "제목")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TextButton(onClick = onClickCloseDialog) {
                    Text(text = "닫기", color = Color.Black)
                }

                TextButton(onClick = onClickCreateRoom) {
                    Text(text = "생성", color = Color.Black)
                }
            }
        }
    }
}