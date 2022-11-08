package com.seno.game.ui.main.home.component

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.seno.game.R
import com.seno.game.extensions.noRippleClickable
import com.seno.game.extensions.textDp
import com.seno.game.manager.AccountManager
import com.seno.game.util.BlueRippleTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SettingDialog(
    onClickClose: () -> Unit,
    onValueChangeBackgroundSlider: (Float) -> Unit,
    onDismissed: () -> Unit
) {
    CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
        Dialog(onDismissRequest = onDismissed) {
            Card(
                backgroundColor = Color.White,
                shape = RoundedCornerShape(size = 30.dp),
                modifier = Modifier
                    .width(width = 281.dp)
                    .height(height = 430.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    DialogTitle(onClickClose = onClickClose)
                    SoundControlPanel(
                        onValueChangeBackgroundSlider = onValueChangeBackgroundSlider,
                        modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(height = 15.dp))
                    NotificationPanel(
                        onCheckedChange = {},
                        modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(height = 20.dp))
                    AccountPanel(modifier = Modifier.align(alignment = Alignment.CenterHorizontally))
                }
            }
        }
    }
}

@Composable
fun DialogTitle(
    onClickClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = 71.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_dialog_close),
            contentDescription = null,
            modifier = Modifier
                .align(alignment = Alignment.TopEnd)
                .padding(top = 13.6.dp, end = 13.6.dp)
                .size(size = 36.dp)
                .noRippleClickable { onClickClose.invoke() }
        )

        Text(
            text = stringResource(id = R.string.home_setting_title),
            color = colorResource(id = R.color.color_b8c0ff),
            fontSize = 20.textDp,
            modifier = Modifier
                .align(alignment = Alignment.BottomCenter)
                .padding(bottom = 13.dp)
        )
    }
}

@Composable
fun SoundControlPanel(
    onValueChangeBackgroundSlider: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(0.83f)
    ) {
        Text(
            text = stringResource(id = R.string.home_setting_sound),
            fontSize = 16.textDp,
            color = colorResource(id = R.color.color_b8c0ff)
        )
        Spacer(modifier = Modifier.height(height = 7.dp))
        Box(
            modifier = Modifier
                .height(height = 1.dp)
                .fillMaxWidth()
                .background(color = colorResource(id = R.color.color_bbd0ff))
        )
        Spacer(modifier = Modifier.height(height = 13.5.dp))
        SliderUnit(
            text = stringResource(id = R.string.home_setting_sound_background),
            onValueChangeFinished = onValueChangeBackgroundSlider
        )
        Spacer(modifier = Modifier.height(height = 6.dp))
        SliderUnit(
            text = stringResource(id = R.string.home_setting_sound_effect),
            onValueChangeFinished = {

            }
        )
        Spacer(modifier = Modifier.height(height = 6.dp))
        SliderUnit(
            text = stringResource(id = R.string.home_setting_sound_vibration),
            onValueChangeFinished = {

            }
        )
    }
}

@Composable
fun SliderUnit(
    text: String,
    onValueChangeFinished: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var sliderPosition by remember { mutableStateOf(0.5f) }

    CompositionLocalProvider(
        LocalRippleTheme provides BlueRippleTheme,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxWidth()
        ) {
            Text(
                text = text,
                fontSize = 14.textDp,
                color = colorResource(id = R.color.color_b8c0ff),
                modifier = Modifier.weight(weight = 4f)
            )
            Slider(
                value = sliderPosition,
                onValueChange = { sliderPosition = it },
                valueRange = 0.0f..1.0f,
                onValueChangeFinished = { onValueChangeFinished.invoke(sliderPosition) },
                colors = SliderDefaults.colors(
                    thumbColor = colorResource(id = R.color.color_bbd0ff),
                    activeTrackColor = colorResource(id = R.color.color_80bbd0ff),
                ),
                modifier = Modifier
                    .weight(weight = 6f)
                    .height(height = 40.dp),
            )
        }
    }
}

@Composable
fun NotificationPanel(
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var isSwitchChecked by remember { mutableStateOf(false) }

    CompositionLocalProvider(
        LocalRippleTheme provides BlueRippleTheme,
    ) {
        Column(
            modifier = modifier.fillMaxWidth(0.83f)
        ) {
            Text(
                text = stringResource(id = R.string.home_setting_notification),
                fontSize = 16.textDp,
                color = colorResource(id = R.color.color_b8c0ff)
            )
            Spacer(modifier = Modifier.height(height = 7.dp))
            Box(
                modifier = Modifier
                    .height(height = 1.dp)
                    .fillMaxWidth()
                    .background(color = colorResource(id = R.color.color_bbd0ff))
            )
            Spacer(modifier = Modifier.height(height = 13.5.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.home_setting_notification_push),
                    fontSize = 14.textDp,
                    color = colorResource(id = R.color.color_b8c0ff),
                    modifier = Modifier.align(alignment = Alignment.CenterStart)
                )
                Switch(
                    checked = isSwitchChecked,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colorResource(id = R.color.color_bbd0ff),
                        checkedTrackColor = colorResource(id = R.color.color_FFD6FF),
                        uncheckedThumbColor = colorResource(id = R.color.color_bbd0ff),
                        uncheckedTrackColor = colorResource(id = R.color.color_66FFD6FF),
                    ),
                    onCheckedChange = {
                        isSwitchChecked = it
                        onCheckedChange.invoke(it)
                    },
                    modifier = Modifier.align(alignment = Alignment.CenterEnd)
                )
            }
        }
    }
}

@Composable
fun AccountPanel(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = modifier.fillMaxWidth(0.83f)) {
            Text(
                text = stringResource(id = R.string.home_setting_account),
                fontSize = 16.textDp,
                color = colorResource(id = R.color.color_b8c0ff)
            )
            Spacer(modifier = Modifier.height(height = 7.dp))
            Box(
                modifier = Modifier
                    .height(height = 1.dp)
                    .fillMaxWidth()
                    .background(color = colorResource(id = R.color.color_bbd0ff))
            )
            Spacer(modifier = Modifier.height(height = 18.dp))
            Text(
                text = if (AccountManager.isSignedIn) {
                    String.format(
                        format = stringResource(id = R.string.home_setting_account_member),
                        "김경호", "카카오"
                    )
                } else {
                    stringResource(id = R.string.home_setting_account_no_member)
                },
                textAlign = TextAlign.Center,
                fontSize = 14.textDp,
                color = colorResource(id = R.color.color_b8c0ff),
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(height = 15.dp))
        AccountButtonContainer(
            onClickLeftButton = {},
            onClickRightButton = {},
        )
        Spacer(modifier = Modifier.height(height = 15.dp))
    }
}

@Composable
fun AccountButtonContainer(
    onClickLeftButton: () -> Unit,
    onClickRightButton: () -> Unit
) {
    Row() {
        Box(
            modifier = Modifier
                .weight(weight = 1f)
                .noRippleClickable { onClickLeftButton.invoke() }
        ) {
            Image(
                painter = painterResource(id = R.drawable.bg_dialog_button_y),
                contentDescription = null,
                modifier = Modifier.align(alignment = Alignment.Center)
            )
            Text(
                text = if (AccountManager.isSignedIn) {
                    stringResource(id = R.string.home_setting_account_logout)
                } else {
                    stringResource(id = R.string.home_setting_account_login)
                },
                color = Color.White,
                fontSize = 16.textDp,
                modifier = Modifier.align(alignment = Alignment.Center)
            )
        }
        Box(
            modifier = Modifier
                .weight(weight = 1f)
                .noRippleClickable { onClickRightButton.invoke() }
        ) {
            Image(
                painter = painterResource(id = R.drawable.bg_dialog_button_n),
                contentDescription = null,
                modifier = Modifier.align(alignment = Alignment.Center)
            )
            Text(
                text = if (AccountManager.isSignedIn) {
                    stringResource(id = R.string.home_setting_account_profile)
                } else {
                    stringResource(id = R.string.home_setting_account_create_account)
                },
                color = colorResource(id = R.color.color_bbd0ff),
                fontSize = 16.textDp,
                modifier = Modifier.align(alignment = Alignment.Center)
            )
        }
    }
}