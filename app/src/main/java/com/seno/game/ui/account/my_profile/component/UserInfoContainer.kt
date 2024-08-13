package com.seno.game.ui.account.my_profile.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.seno.game.App
import com.seno.game.R
import com.seno.game.extensions.noRippleClickable
import com.seno.game.extensions.textDp
import com.seno.game.manager.PlatForm
import com.seno.game.prefs.PrefsManager

@Composable
fun UserInfoContainer(
    isSignedIn: Boolean,
    nickname: String,
    onClickChangeNickname: () -> Unit,
    onClickWithdrawal: () -> Unit
) {
    if (isSignedIn) {
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            Card(
                shape = RoundedCornerShape(size = 16.dp),
                backgroundColor = Color.White,
                elevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)) {
                    UserInfoPanel(
                        label = stringResource(id = R.string.my_profile_sso),
                        drawableRight = when(PrefsManager.platform) {
                            PlatForm.FACEBOOK.value -> painterResource(id = R.drawable.ic_sns_facebook)
                            PlatForm.GOOGLE.value -> painterResource(id = R.drawable.ic_sns_google)
                            PlatForm.KAKAO.value -> painterResource(id = R.drawable.ic_sns_kakao)
                            PlatForm.NAVER.value -> painterResource(id = R.drawable.ic_sns_naver)
                            else -> painterResource(id = R.drawable.bg_timer_wrong_answer)
                        }
                    )
                    UserInfoSpacer()
                    UserInfoPanel(
                        label = stringResource(id = R.string.my_profile_nickname),
                        value = nickname,
                        drawableRight = Icons.Filled.KeyboardArrowRight,
                        onClick = onClickChangeNickname
                    )
                    UserInfoSpacer()
                    UserInfoPanel(
                        label = stringResource(id = R.string.my_profile_version),
                        value = App.getAppVersionName()
                    )
                    UserInfoSpacer()
                    UserInfoPanel(
                        label = "",
                        value = stringResource(id = R.string.my_profile_withdrawal),
                        drawableRight = Icons.Filled.KeyboardArrowRight,
                        onClick = onClickWithdrawal
                    )
                }
            }
        }
    }
}

@Composable
fun UserInfoPanel(
    label: String,
    drawableRight: Painter? = null,
    onClick: (() -> Unit)? = null
) {
    Column {
        Spacer(modifier = Modifier.height(height = 14.dp))
        Row {
            Text(
                text = label,
                color = colorResource(id = R.color.color_FF2F2F2F),
                fontSize = 12.textDp,
                fontWeight = FontWeight.W600
            )
            Spacer(modifier = Modifier.weight(weight = 1f))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.noRippleClickable { onClick?.invoke() }
            ) {
                drawableRight?.let {
                    Image(
                        painter = it,
                        contentDescription = "right_arrow",
                        modifier = Modifier.size(size = 20.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(height = 14.dp))
    }
}

@Composable
fun UserInfoPanel(
    label: String,
    value: String,
    drawableRight: ImageVector? = null,
    onClick: (() -> Unit)? = null
) {
    Column {
        Spacer(modifier = Modifier.height(height = 14.dp))
        Row {
            Text(
                text = label,
                color = colorResource(id = R.color.color_FF2F2F2F),
                fontSize = 12.textDp,
                fontWeight = FontWeight.W600
            )
            Spacer(modifier = Modifier.weight(weight = 1f))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.noRippleClickable { onClick?.invoke() }
            ) {
                Text(
                    text = value,
                    color = colorResource(id = R.color.color_D33F3F3F),
                    fontSize = 12.textDp,
                    fontWeight = FontWeight.W500
                )
                drawableRight?.let {
                    Image(
                        imageVector = it,
                        colorFilter = ColorFilter.tint(color = colorResource(id = R.color.color_D33F3F3F)),
                        contentDescription = "right_arrow",
                        modifier = Modifier.size(size = 14.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(height = 14.dp))
    }
}

@Composable
fun UserInfoSpacer() {
    Spacer(
        modifier = Modifier
            .height(height = 1.dp)
            .fillMaxWidth()
            .background(color = colorResource(id = R.color.color_804D4C4C))
    )
}