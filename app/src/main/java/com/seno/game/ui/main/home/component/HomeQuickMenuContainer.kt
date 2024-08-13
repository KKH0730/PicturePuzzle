package com.seno.game.ui.main.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import com.seno.game.R
import com.seno.game.util.MusicPlayUtil

@Composable
fun HomeQuickMenuContainer(
    onToggledSound: () -> Unit,
    onClickSetting: () -> Unit,
) {
    Column {
        SoundOnOffButton(
            onToggledSound = onToggledSound
        )
        SettingButton(
            onClickSetting = onClickSetting
        )
    }
}

@Composable
fun SoundOnOffButton(onToggledSound: () -> Unit) {
    val isPlaying = MusicPlayUtil.isPlaying
    if (isPlaying != null) {
        var isPlayingSound by remember { mutableStateOf(isPlaying) }
        IconButton(onClick = {
            onToggledSound.invoke()
            isPlayingSound = MusicPlayUtil.isPlaying ?: false
        }) {
            Image(
                painter = if (isPlayingSound) {
                    painterResource(id = R.drawable.ic_sound_on)
                } else {
                    painterResource(id = R.drawable.ic_sound_off)
                },
                contentDescription = null
            )
        }
    }
}

@Composable
fun SettingButton(onClickSetting: () -> Unit) {

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val settingImage = if (isPressed) {
        painterResource(id = R.drawable.ic_home_setting_pressed)
    } else {
        painterResource(id = R.drawable.ic_home_setting)
    }

    IconButton(
        onClick = onClickSetting,
        interactionSource = interactionSource,
    ) {
        Image(
            painter = settingImage,
            contentDescription = null,
        )
    }
}