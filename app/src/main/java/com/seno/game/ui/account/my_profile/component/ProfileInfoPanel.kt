package com.seno.game.ui.account.my_profile.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
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
import com.seno.game.ui.component.LiquidStyledBox
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun ProfileInfoPanel(
    nickname: String,
    profileUri: String,
    onClickLogout: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.width(width = 36.dp))
        ProfileImage(profileUri = profileUri)
        Spacer(modifier = Modifier.width(width = 14.dp))
        NicknameContainer(nickName = nickname)
        Spacer(modifier = Modifier.weight(weight = 1f))
        ButtonContainer(onClickLogout = onClickLogout)
        Spacer(modifier = Modifier.width(width = 36.dp))
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
            .size(size = 56.dp)
            .background(color = colorResource(id = R.color.white))
    ) {
        if (profileUri.isEmpty()) {
            Image(
                painter = painterResource(id = R.drawable.ic_user_profile),
                contentDescription = null,
                modifier = Modifier
                    .size(size = 50.dp)
                    .align(alignment = Alignment.Center)
            )
        } else {
            GlideImage(
                imageModel = profileUri,
                contentScale = ContentScale.Crop,
                placeHolder =  painterResource(id = R.drawable.ic_user_profile),
                modifier = Modifier
                    .size(size = 50.dp)
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
    onClickLogout: () -> Unit
) {
    LogoutButton(onClickLogout = onClickLogout)
}

@Composable
fun LogoutButton(onClickLogout: () -> Unit) {
    LiquidStyledBox(
        isUseStroke = true,
        liquidColor = Color.Red,
        radius = 20.dp,
        strokeAlpha = 0.7f,
        containerAlpha = 0.6f,
        onClick = onClickLogout
    ) {
        Text(
            text = stringResource(id = R.string.my_profile_logout),
            fontSize = 14.textDp,
            color = Color.White,
            fontWeight = FontWeight.W300,
            modifier = Modifier.padding(PaddingValues(horizontal = 14.dp, vertical = 8.dp))
        )
    }
}