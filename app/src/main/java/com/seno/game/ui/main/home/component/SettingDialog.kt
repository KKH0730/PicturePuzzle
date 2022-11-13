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
import com.seno.game.prefs.PrefsManager
import com.seno.game.util.BlueRippleTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SettingDialog(
    onClickClose: () -> Unit,
    onValueChangeBackgroundSoundSlider: (Float) -> Unit,
    onValueChangeEffectSoundSlider: (Float) -> Unit,
    onCheckChangeVibration: (Boolean) -> Unit,
    onCheckChangePush: (Boolean) -> Unit,
    onClickLogin: () -> Unit,
    onClickLogout: () -> Unit,
    onClickManageProfile: () -> Unit,
    onDismissed: () -> Unit,
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
                Column(modifier = Modifier.fillMaxWidth()) {
                    DialogTitle(onClickClose = onClickClose)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        SoundControlPanel(
                            onValueChangeBackgroundSoundSlider = onValueChangeBackgroundSoundSlider,
                            onValueChangeEffectSoundSlider = onValueChangeEffectSoundSlider,
                            onCheckChangeVibration = onCheckChangeVibration,
                            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                        )
                        Spacer(modifier = Modifier.height(height = 15.dp))
                        NotificationPanel(
                            onCheckChangePush = onCheckChangePush,
                            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                        )
                        Spacer(modifier = Modifier.height(height = 20.dp))
                        AccountPanel(
                            onClickLogin = onClickLogin,
                            onClickLogout = onClickLogout,
                            onClickManageProfile = onClickManageProfile,
                            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                        )
                        Spacer(modifier = Modifier.height(height = 25.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DialogTitle(
    onClickClose: () -> Unit,
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
    onValueChangeBackgroundSoundSlider: (Float) -> Unit,
    onValueChangeEffectSoundSlider: (Float) -> Unit,
    onCheckChangeVibration: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
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
            isBackgroundSlider = true,
            onValueChangeFinished = onValueChangeBackgroundSoundSlider
        )
        Spacer(modifier = Modifier.height(height = 6.dp))
        SliderUnit(
            text = stringResource(id = R.string.home_setting_sound_effect),
            isBackgroundSlider = false,
            onValueChangeFinished = onValueChangeEffectSoundSlider
        )
        Spacer(modifier = Modifier.height(height = 6.dp))
        SwitchUnit(
            text = stringResource(id = R.string.home_setting_sound_vibration),
            isVibrationSwitch = true,
            onCheckedChange = onCheckChangeVibration
        )
    }
}

@Composable
fun SliderUnit(
    text: String,
    isBackgroundSlider: Boolean,
    onValueChangeFinished: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    var sliderPosition by remember {
        mutableStateOf(
            if (isBackgroundSlider) {
                PrefsManager.backgroundVolume
            } else {
                PrefsManager.effectVolume
            }
        )
    }

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
fun SwitchUnit(
    text: String,
    isVibrationSwitch: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    var isSwitchChecked by remember {
        mutableStateOf(
            if (isVibrationSwitch) {
                PrefsManager.isVibrationOn
            } else {
                PrefsManager.isPushOn
            }
        )
    }

    CompositionLocalProvider(
        LocalRippleTheme provides BlueRippleTheme,
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = text,
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

@Composable
fun NotificationPanel(
    onCheckChangePush: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
//    var isSwitchChecked by remember { mutableStateOf(false) }

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
            SwitchUnit(
                text = stringResource(id = R.string.home_setting_notification_push),
                isVibrationSwitch = false,
                onCheckedChange = onCheckChangePush
            )
//            Box(modifier = Modifier.fillMaxWidth()) {
//                Text(
//                    text = stringResource(id = R.string.home_setting_notification_push),
//                    fontSize = 14.textDp,
//                    color = colorResource(id = R.color.color_b8c0ff),
//                    modifier = Modifier.align(alignment = Alignment.CenterStart)
//                )
//                Switch(
//                    checked = isSwitchChecked,
//                    colors = SwitchDefaults.colors(
//                        checkedThumbColor = colorResource(id = R.color.color_bbd0ff),
//                        checkedTrackColor = colorResource(id = R.color.color_FFD6FF),
//                        uncheckedThumbColor = colorResource(id = R.color.color_bbd0ff),
//                        uncheckedTrackColor = colorResource(id = R.color.color_66FFD6FF),
//                    ),
//                    onCheckedChange = {
//                        isSwitchChecked = it
//                        onCheckedChange.invoke(it)
//                    },
//                    modifier = Modifier.align(alignment = Alignment.CenterEnd)
//                )
//            }
        }
    }
}

@Composable
fun AccountPanel(
    onClickLogin: () -> Unit,
    onClickLogout: () -> Unit,
    onClickManageProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
                text = if (AccountManager.isAnonymous) {
                    stringResource(id = R.string.home_setting_account_no_member)
                } else {
                    String.format(
                        format = stringResource(id = R.string.home_setting_account_member),
                        PrefsManager.nickname,
                        AccountManager.authProviderName
                    )
                },
                textAlign = TextAlign.Center,
                fontSize = 14.textDp,
                color = colorResource(id = R.color.color_b8c0ff),
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(height = 11.dp))
        AccountButtonContainer(
            onClickLogin = onClickLogin,
            onClickLogout = onClickLogout,
            onClickManageProfile = onClickManageProfile
        )
    }
}

@Composable
fun AccountButtonContainer(
    onClickLogin: () -> Unit,
    onClickLogout: () -> Unit,
    onClickManageProfile: () -> Unit,
) {
    if (AccountManager.isAnonymous) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .noRippleClickable { onClickLogin.invoke() }
                    .align(alignment = Alignment.Center)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bg_dialog_button_y),
                    contentDescription = null,
                    modifier = Modifier.align(alignment = Alignment.Center)
                )
                Text(
                    text = stringResource(id = R.string.home_setting_account_login),
                    color = Color.White,
                    fontSize = 16.textDp,
                    modifier = Modifier.align(alignment = Alignment.Center)
                )
            }
        }
    } else {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .weight(weight = 1f)
                    .noRippleClickable { onClickLogout.invoke() }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bg_dialog_button_y),
                    contentDescription = null,
                    modifier = Modifier.align(alignment = Alignment.Center)
                )
                Text(
                    text = stringResource(id = R.string.home_setting_account_logout),
                    color = Color.White,
                    fontSize = 16.textDp,
                    modifier = Modifier.align(alignment = Alignment.Center)
                )
            }
            Box(
                modifier = Modifier
                    .weight(weight = 1f)
                    .noRippleClickable { onClickManageProfile.invoke() }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bg_dialog_button_n),
                    contentDescription = null,
                    modifier = Modifier.align(alignment = Alignment.Center)
                )
                Text(
                    text = stringResource(id = R.string.home_setting_account_profile),
                    color = colorResource(id = R.color.color_bbd0ff),
                    fontSize = 16.textDp,
                    modifier = Modifier.align(alignment = Alignment.Center)
                )
            }
        }
    }
}