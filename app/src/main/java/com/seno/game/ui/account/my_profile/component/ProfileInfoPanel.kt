package com.seno.game.ui.account.my_profile.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.seno.game.extensions.textDp
import com.seno.game.view.RoundedButton
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun ProfileInfoPanel(
    nickname: String,
    profileUri: String,
    isSignedIn: Boolean,
    onClickLogin: () -> Unit,
    onClickLogout: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.width(width = 16.dp))
        ProfileImage(profileUri = profileUri)
        Spacer(modifier = Modifier.width(width = 14.dp))
        NicknameContainer(nickName = nickname)
        Spacer(modifier = Modifier.weight(weight = 1f))
        ButtonContainer(
            isSignedIn = isSignedIn,
            onClickLogin = onClickLogin,
            onClickLogout = onClickLogout
        )
        Spacer(modifier = Modifier.width(width = 16.dp))
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
fun NicknameContainer(nickName: String) {
    Text(
        text = nickName,
        fontSize = 20.textDp,
        color = Color.White,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
fun ButtonContainer(
    isSignedIn: Boolean,
    onClickLogin: () -> Unit,
    onClickLogout: () -> Unit
) {
    if (isSignedIn) {
        LogoutButton(onClickLogout = onClickLogout)
    } else {
        LoginButton(onClick = onClickLogin)
    }
}

@Composable
fun LoginButton(onClick: () -> Unit) {
    RoundedButton(
        text = stringResource(id = R.string.my_profile_login),
        textSize = 16.textDp,
        textColor = Color.White,
        fontWeight = FontWeight.W400,
        paddingValues = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
        backgroundColor = colorResource(id = R.color.color_b8c0ff),
        radius = 20.dp,
        onClick = onClick
    )
}

@Composable
fun LogoutButton(onClickLogout: () -> Unit) {
    RoundedButton(
        text = stringResource(id = R.string.my_profile_logout),
        textSize = 14.textDp,
        textColor = Color.White,
        fontWeight = FontWeight.W300,
        paddingValues = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
        backgroundColor = Color.Red,
        radius = 20.dp,
        onClick = onClickLogout
    )
}