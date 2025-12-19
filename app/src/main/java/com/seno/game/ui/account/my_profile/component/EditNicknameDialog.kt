package com.seno.game.ui.account.my_profile.component

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.seno.game.R
import com.seno.game.extensions.textDp

@SuppressLint("RememberInComposition")
@Composable
fun NicknameEditDialog(
    initialNickname: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var nickname by remember { mutableStateOf(initialNickname) }
    val maxLength = 15

    Box(modifier = Modifier.fillMaxSize()) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
            )
        ) {
            Card(
                shape = RoundedCornerShape(size = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Box(modifier = Modifier.padding(all = 24.dp)) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.background(color = Color.White)
                    ) {
                        NickNameTextField(
                            maxLength = maxLength,
                            nickname = nickname,
                            onChangeNickname = { nickname = it }
                        )
                        Spacer(modifier = Modifier.height(height = 24.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Card(
                                shape = RoundedCornerShape(size = 12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFF3F4F6)
                                ),
                                modifier = Modifier
                                    .weight(weight = 1f)
                                    .clickable(
                                        interactionSource = MutableInteractionSource(),
                                        indication = null,
                                        onClick = onDismiss
                                    )
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.cancel),
                                        color = Color(0xFF374151),
                                        fontSize = 12.textDp,
                                        fontWeight = FontWeight.W500,
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(width = 12.dp))
                            Card(
                                shape = RoundedCornerShape(size = 12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF3B82F6)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable(
                                        interactionSource = MutableInteractionSource(),
                                        indication = null,
                                        onClick = {
                                            onConfirm.invoke(nickname)
                                        }
                                    )
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.save),
                                        color = Color.White,
                                        fontSize = 14.textDp,
                                        fontWeight = FontWeight.W700,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ColumnScope.NickNameTextField(
    maxLength: Int,
    nickname: String,
    onChangeNickname: (String) -> Unit
) {
    Text(
        text = "닉네임 수정",
        textAlign = TextAlign.End,
        fontSize = 15.textDp,
        fontWeight = FontWeight.W700,
        color = Color(0xFF374151),
        modifier = Modifier.align(alignment = Alignment.Start)
    )
    Spacer(modifier = Modifier.height(height = 44.dp))
    Text(
        text = "새로운 닉네임",
        textAlign = TextAlign.End,
        fontSize = 12.textDp,
        fontWeight = FontWeight.W500,
        color = Color(0xFF374151),
        modifier = Modifier.align(alignment = Alignment.Start)
    )
    Spacer(modifier = Modifier.height(height = 10.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .border(
                width = 1.dp,
                color = Color(0xFFDDDDDD),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        BasicTextField(
            value = nickname,
            onValueChange = {
                if (it.length <= maxLength) {
                    onChangeNickname.invoke(it)
                }
            },
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 16.textDp,
                fontWeight = FontWeight.W400,
                color = colorResource(id = R.color.color_FF2F2F2F)
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Placeholder
        if (nickname.isEmpty()) {
            Text(
                text = nickname,
                fontSize = 14.textDp,
                color = colorResource(id = R.color.color_FF2F2F2F)
                    .copy(alpha = 0.4f)
            )
        }
    }

    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "${nickname.length} / $maxLength",
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.End,
        fontSize = 12.textDp,
        color = colorResource(id = R.color.color_FF2F2F2F)
            .copy(alpha = 0.6f)
    )
}

@Preview
@Composable
fun Preview() {
    NicknameEditDialog("", {}, {})
}