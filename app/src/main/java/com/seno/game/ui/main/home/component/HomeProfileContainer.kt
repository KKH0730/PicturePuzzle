package com.seno.game.ui.main.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun HomeProfileContainer(
    nickname: String,
    profileUri: String,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.noRippleClickable { onClick.invoke() }
    ) {
        if (profileUri.isEmpty()) {
            Spacer(modifier = Modifier.width(width = 16.dp))
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
                placeHolder = painterResource(id = R.drawable.ic_profile_not_login),
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