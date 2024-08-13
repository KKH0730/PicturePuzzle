package com.seno.game.ui.account.my_profile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.seno.game.R
import com.seno.game.extensions.textDp
import com.seno.game.extensions.toast

@Composable
fun NicknameEditDialog(
    initialNickname: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var nickname by remember { mutableStateOf(initialNickname) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(alpha = 0.7f)
            .background(color = Color.Black)

    ) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
            )
        ) {
            Card(shape = MaterialTheme.shapes.small) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 16.dp)
                        .background(color = Color.White)
                ) {
                    OutlinedTextField(
                        value = nickname,
                        onValueChange = { nickname = it },
                        textStyle = TextStyle(
                            color = colorResource(id = R.color.color_FF2F2F2F),
                            fontSize = 10.textDp,
                            fontWeight = FontWeight.W400
                        ),
                        label = {
                            Text(
                                text = stringResource(id = R.string.my_profile_nickname_edit_label),
                                color = colorResource(id = R.color.color_FF2F2F2F),
                                fontSize = 10.textDp,
                                fontWeight = FontWeight.W400
                            )
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = colorResource(id = R.color.color_FF2F2F2F), // 포커스 상태의 테두리 색상
                            unfocusedBorderColor = Color.Gray // 포커스되지 않은 상태의 테두리 색상
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text(
                                text = stringResource(id = R.string.cancel),
                                color = colorResource(id = R.color.color_FF2F2F2F),
                                fontSize = 12.textDp,
                                fontWeight = FontWeight.W500
                            )
                        }
                        TextButton(
                            onClick = { 
                                if (nickname.isEmpty()) {
                                    context.toast(context.getString(R.string.my_profile_nickname_edit_content_empty))
                                } else {
                                    onConfirm.invoke(nickname)
                                }
                            }
                        ) {
                            Text(
                                text = stringResource(id = R.string.confirm),
                                color = colorResource(id = R.color.color_FF2F2F2F),
                                fontSize = 12.textDp,
                                fontWeight = FontWeight.W500
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun NicknameEditDialogPreview() {
    NicknameEditDialog("", {}, {})
}