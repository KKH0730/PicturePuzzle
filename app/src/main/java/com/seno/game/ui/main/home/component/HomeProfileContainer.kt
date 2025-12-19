package com.seno.game.ui.main.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.seno.game.R
import com.seno.game.extensions.noRippleClickable
import com.seno.game.extensions.textDp
import com.seno.game.manager.AccountManager
import com.seno.game.ui.component.LiquidStyledBox
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun HomeProfileContainer(
    nickname: String,
    profileUri: String,
    onClick: () -> Unit,
) {
    Box {
        LiquidStyledBox(
            isUseStroke = true,
            radius = 24.dp,
            modifier = Modifier.offset(x = 24.dp),
            onClick = onClick
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(vertical = 9.dp, horizontal = 17.dp)
                    .align(alignment = Alignment.Center)
            ) {
                if (profileUri.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(size = 24.dp)
                            .background(color = colorResource(R.color.color_F3E8FF))
                            .clip(CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_profile_not_login),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(color = colorResource(R.color.color_9333EA)),
                            modifier = Modifier
                                .size(size = 16.dp)
                                .align(alignment = Alignment.Center)
                        )
                    }
                } else {
                    GlideImage(
                        imageModel = profileUri,
                        contentScale = ContentScale.Crop,
                        placeHolder = painterResource(id = R.drawable.ic_profile_not_login),
                        modifier = Modifier
                            .size(size = 24.dp)
                            .clip(CircleShape)
                    )
                }
                Spacer(modifier = Modifier.width(width = 7.dp))
                Text(
                    text = if (AccountManager.isSignedIn) {
                        nickname
                    } else {
                        "$nickname ${stringResource(id = R.string.guest2)}"
                    },
                    color = colorResource(id = R.color.color_606264),
                    fontSize = 12.textDp,
                    fontWeight = FontWeight.W500
                )
            }
        }
    }
}

@Preview
@Composable
fun HomeProfileContainerPreview() {
    HomeProfileContainer(
        nickname = "nickname",
        profileUri = "",
        onClick = {}
    )
}