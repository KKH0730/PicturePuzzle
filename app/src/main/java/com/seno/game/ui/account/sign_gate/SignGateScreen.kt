package com.seno.game.ui.account.sign_gate

import android.widget.Space
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.seno.game.R
import com.seno.game.extensions.noRippleClickable
import com.seno.game.extensions.textDp
import com.seno.game.manager.FacebookAccountManager
import com.seno.game.manager.GoogleAccountManager
import com.seno.game.manager.KakaoAccountManager
import com.seno.game.manager.NaverAccountManager
import com.seno.game.ui.account.sign_gate.component.SocialLoginContainer

@Composable
fun SignGateScreen(
    googleAccountManager: GoogleAccountManager,
    facebookAccountManager: FacebookAccountManager,
    naverAccountManager: NaverAccountManager,
    kakaoAccountManager: KakaoAccountManager,
    onClickClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        SignGateContainer(
            googleAccountManager = googleAccountManager,
            facebookAccountManager = facebookAccountManager,
            naverAccountManager = naverAccountManager,
            kakaoAccountManager = kakaoAccountManager,
            modifier = Modifier.offset(y = 93.dp)
        )
        SignGateHeader(onClickClose = onClickClose)
        CatCircleImage(
            modifier = Modifier
                .offset(y = 42.dp)
                .align(alignment = Alignment.TopCenter)
        )
        Image(
            painter = painterResource(id = R.drawable.img_bottom_second_logo),
            contentDescription = null,
            modifier = Modifier
                .align(alignment = Alignment.BottomCenter)
                .padding(bottom = 44.dp)
        )
    }
}

@Composable
fun SignGateHeader(
    onClickClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = 93.dp)
            .background(color = colorResource(id = R.color.white))
    ) {
        Box(
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
                .noRippleClickable { onClickClose.invoke() }
        ) {
            Text(
                text = stringResource(id = R.string.sign_gate_close),
                color = colorResource(id = R.color.color_bbd0ff),
                fontSize = 14.textDp,
                modifier = Modifier.padding(start = 16.dp, top = 24.dp)
            )
        }

        Card(
            shape = CircleShape,
            backgroundColor = Color.White,
            modifier = Modifier
                .offset(y = 42.dp)
                .size(size = 96.dp)
                .align(alignment = Alignment.TopCenter)
        ){

        }
    }
}

@Composable
fun CatCircleImage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .border(
                width = 5.dp,
                color = colorResource(id = R.color.color_b8c0ff),
                shape = CircleShape
            )
            .size(size = 96.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_dialog_cat_crying),
            contentDescription = null,
            modifier = Modifier.align(alignment = Alignment.Center)
        )
    }
}

@Composable
fun SignGateContainer(
    googleAccountManager: GoogleAccountManager,
    facebookAccountManager: FacebookAccountManager,
    naverAccountManager: NaverAccountManager,
    kakaoAccountManager: KakaoAccountManager,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.color_b8c0ff))
    ) {
        Spacer(modifier = Modifier.height(height = 91.dp))
        Text(
            text = stringResource(id = R.string.sign_gate_title),
            fontSize = 24.textDp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(height = 43.dp))
        Text(
            text = stringResource(id = R.string.sign_gate_guide),
            fontSize = 14.textDp,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(height = 81.dp))
        SocialLoginContainer(
            googleAccountManager = googleAccountManager,
            facebookAccountManager = facebookAccountManager,
            naverAccountManager = naverAccountManager,
            kakaoAccountManager = kakaoAccountManager,
        )
    }
}