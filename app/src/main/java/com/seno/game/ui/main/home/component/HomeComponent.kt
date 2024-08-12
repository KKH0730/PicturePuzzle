package com.seno.game.ui.main.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.seno.game.R
import com.seno.game.extensions.noRippleClickable
import com.seno.game.extensions.textDp
import com.seno.game.manager.AccountManager
import com.seno.game.util.MusicPlayUtil
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun ProfileContainer(
    nickname: String,
    profileUri: String,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.noRippleClickable { onClick.invoke() }
    ) {
        Spacer(modifier = Modifier.width(width = 16.dp))
        if (profileUri.isEmpty()) {
            Image(
                painter = painterResource(id = R.drawable.ic_profile_not_login),
                contentDescription = null,
                modifier = Modifier
                    .size(size = 30.dp)
                    .clip(CircleShape)
            )
        } else {
            GlideImage(
                imageModel = profileUri,
                contentScale = ContentScale.Crop,
                placeHolder =  painterResource(id = R.drawable.ic_profile_not_login),
                modifier = Modifier
                    .size(size = 30.dp)
                    .clip(CircleShape)
            )
        }
        Spacer(modifier = Modifier.width(width = 10.dp))
        Text(
            text = if (AccountManager.isSignedIn) {
                nickname
            } else {
                "$nickname ${stringResource(id = R.string.guest2)}"
            },
            color = colorResource(id = R.color.color_fbf8cc),
            fontSize = 18.textDp,
            fontWeight = FontWeight.W600
        )
    }
}

@Composable
fun HomeQuickMenuContainer(
    onToggledSound: () -> Unit,
    onClickSetting: () -> Unit,
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

@Composable
fun GamePlayContainer(
    onClickSoloPlay: () -> Unit,
    onClickMultiPlay: () -> Unit,
    onClickQuit: () -> Unit,
    modifier: Modifier = Modifier,
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
        modifier = Modifier
            .width(width = 125.dp)
            .aspectRatio(2.6f)
            .noRippleClickable { onClick.invoke() }
    )
}

@Composable
fun MultiPlayButton(onClick: () -> Unit) {
    Image(
        painter = painterResource(id = R.drawable.ic_multi_play_button),
        contentDescription = null,
        modifier = Modifier
            .width(width = 125.dp)
            .aspectRatio(2.6f)
            .noRippleClickable { onClick.invoke() }
    )
}

@Composable
fun QuitButton(onClick: () -> Unit) {
    Image(
        painter = painterResource(id = R.drawable.ic_quit_button),
        contentDescription = null,
        modifier = Modifier
            .width(width = 125.dp)
            .aspectRatio(2.6f)
            .noRippleClickable { onClick.invoke() }
    )
}