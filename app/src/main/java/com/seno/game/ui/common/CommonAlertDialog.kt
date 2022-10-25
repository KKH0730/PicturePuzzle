package com.seno.game.ui.common

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.seno.game.extensions.textDp

@Composable
fun RestartDialog(
    title: String,
    content: String,
    confirmText: String = "",
    dismissText: String = "",
    onClickConfirm: () -> Unit,
    onClickDismiss: (() -> Unit)? = null,
) {
    AlertDialog(
        onDismissRequest = {},
        title = {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 16.textDp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Text(
                text = content,
                style = TextStyle(
                    fontSize = 16.textDp,
                    fontWeight = FontWeight.Normal
                )
            )
        },
        confirmButton = {
            TextButton(onClick = onClickConfirm) {
                Text(
                    text = confirmText,
                    style = TextStyle(
                        fontSize = 14.textDp,
                        color = Color.Black,
                        fontWeight = FontWeight.Normal
                    )
                )
            }
        },
        dismissButton = {
            if (onClickDismiss != null) {
                TextButton(onClick = onClickDismiss) {
                    Text(
                        text = dismissText,
                        style = TextStyle(
                            fontSize = 14.textDp,
                            color = Color.Black,
                            fontWeight = FontWeight.Normal
                        )
                    )
                }
            }
        },
        shape = RoundedCornerShape(12.dp)
    )
}