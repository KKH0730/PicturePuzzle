package com.seno.game.ui.main.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.seno.game.R
import com.seno.game.extensions.noRippleClickable
import com.seno.game.extensions.textDp
import com.seno.game.util.MusicPlayUtil


@Composable
fun ProfileContainer(onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.noRippleClickable { onClick.invoke() }
    ) {
        Spacer(modifier = Modifier.width(width = 6.dp))
        Image(
            painter = painterResource(id = R.drawable.ic_profile_not_login), 
            contentDescription = null
        )
        Text(
            text = "닉네임",
            color = colorResource(id = R.color.color_fbf8cc),
            fontSize = 14.textDp
        )
    }
}

@Composable
fun HomeQuickMenuContainer(
    onToggledSound: () -> Unit,
    onClickSetting: () -> Unit
) {
    Column() {
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
    IconButton(onClick = onToggledSound) {
        Image(
            painter = if (isPlaying == null || !isPlaying) {
                painterResource(id = R.drawable.ic_sound_on_off) // on 아이콘
            } else {
                painterResource(id = R.drawable.ic_sound_on_off)
            },
            contentDescription = null
        )
    }
}

@Composable
fun SettingButton(onClickSetting: () -> Unit) {
    IconButton(onClick = onClickSetting) {
        Image(
            painter = painterResource(id = R.drawable.ic_sound_on_off),
            contentDescription = null
        )
    }
}

@Composable
fun GamePlayContainer(
    onClickSoloPlay: () -> Unit,
    onClickMultiPlay: () -> Unit,
    onClickQuit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(space = 20.dp),
        modifier = modifier
    ) {
        SoloPlayButton(onClick = onClickSoloPlay)
        MultiPlayButton(onClick = onClickMultiPlay)
        QuitButton(onClick = onClickQuit)
    }
}

@Composable
fun SoloPlayButton(onClick: () -> Unit) {
    Image(
        painter = painterResource(id = R.drawable.ic_sol_play_button),
        contentDescription = null,
        modifier = Modifier.width(width = 125.dp)
            .aspectRatio(2.6f)
            .noRippleClickable { onClick.invoke() }
    )
}

@Composable
fun MultiPlayButton(onClick: () -> Unit) {
    Image(
        painter = painterResource(id = R.drawable.ic_multi_play_button),
        contentDescription = null,
        modifier = Modifier.width(width = 125.dp)
            .aspectRatio(2.6f)
            .noRippleClickable { onClick.invoke() }
    )
}

@Composable
fun QuitButton(onClick: () -> Unit) {
    Image(
        painter = painterResource(id = R.drawable.ic_quit_button),
        contentDescription = null,
        modifier = Modifier.width(width = 125.dp)
            .aspectRatio(2.6f)
            .noRippleClickable { onClick.invoke() }
    )
}