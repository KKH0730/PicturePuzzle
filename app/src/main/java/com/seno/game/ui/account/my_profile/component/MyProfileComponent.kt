package com.seno.game.ui.account.my_profile.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.seno.game.R
import com.seno.game.extensions.noRippleClickable
import com.seno.game.extensions.textDp
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun MyProfileHeader(
    onClickClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = 102.dp)
            .background(color = colorResource(id = R.color.color_e7c6ff))
    ) {
        Box(
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
                .noRippleClickable { onClickClose.invoke() }
        ) {
            Text(
                text = stringResource(id = R.string.my_profile_close),
                color = Color.White,
                fontSize = 14.textDp,
                modifier = Modifier.padding(start = 16.dp, top = 24.dp)
            )
        }
    }
}

@Composable
fun ProfileImage(
    modifier: Modifier = Modifier,
    profileUri: String
) {
    Box(
        modifier = modifier
            .clip(shape = CircleShape)
            .size(size = 72.dp)
            .background(color = colorResource(id = R.color.color_b8c0ff))
    ) {
        if (profileUri.isEmpty()) {
            Image(
                painter = painterResource(id = R.drawable.ic_user_profile),
                contentDescription = null,
                modifier = Modifier
                    .size(size = 64.dp)
                    .align(alignment = Alignment.Center)
            )
        } else {
            GlideImage(
                imageModel = profileUri,
                contentScale = ContentScale.Crop,
                placeHolder =  painterResource(id = R.drawable.ic_user_profile),
                modifier = Modifier
                    .size(size = 64.dp)
                    .clip(CircleShape)
                    .align(alignment = Alignment.Center)
            )
        }
    }
}

@Composable
fun NicknameContainer(
    nickName: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height = 88.dp)
            .background(color = colorResource(id = R.color.color_b8c0ff))
    ) {
        Text(
            text = nickName,
            fontSize = 20.textDp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.offset(x = 16.dp, y = 44.dp)
        )
    }
}

@Composable
fun LoginButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(width = 130.dp)
            .noRippleClickable { onClick.invoke() }
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_dialog_button_y),
            contentDescription = null,
            modifier = Modifier.align(alignment = Alignment.Center)
        )
        Text(
            text = stringResource(id = R.string.my_profile_login),
            color = Color.White,
            fontSize = 16.textDp,
            modifier = Modifier.align(alignment = Alignment.Center)
        )
    }
}

@Composable
fun CreateAccountButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(width = 130.dp)
            .noRippleClickable { onClick.invoke() }
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_dialog_button_n),
            contentDescription = null,
            modifier = Modifier.align(alignment = Alignment.Center)
        )
        Text(
            text = stringResource(id = R.string.my_profile_create_account),
            color = colorResource(id = R.color.color_bbd0ff),
            fontSize = 16.textDp,
            modifier = Modifier.align(alignment = Alignment.Center)
        )
    }
}