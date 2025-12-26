package com.seno.game.ui.main.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.seno.game.R
import com.seno.game.util.MusicPlayUtil

@Composable
fun HomeQuickMenuContainer(
    onClickSetting: () -> Unit,
    onToggledSound: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.End
    ) {
        SettingButton(onClickSetting = onClickSetting)
        SoundOnOffButton(onToggledSound = onToggledSound)
    }
}

@Composable
fun SoundOnOffButton(onToggledSound: () -> Unit) {
    val isPlaying = MusicPlayUtil.isPlaying
    if (isPlaying != null) {
        var isPlayingSound by remember { mutableStateOf(isPlaying) }

        IconButton(
            onClick = {
                onToggledSound.invoke()
                isPlayingSound = MusicPlayUtil.isPlaying ?: false
            },
            modifier = Modifier.size(size = 60.dp)
        ) {
            Image(
                painter = if (isPlayingSound) {
                    painterResource(id = R.drawable.ic_sound_on)
                } else {
                    painterResource(id = R.drawable.ic_sound_off)
                },
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
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
        modifier = Modifier.size(size = 60.dp)
    ) {
        Image(
            painter = settingImage,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}