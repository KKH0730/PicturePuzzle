package com.seno.game.ui.account.sign_gate.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.seno.game.R
import com.seno.game.extensions.noRippleClickable
import com.seno.game.manager.GoogleAccountManager
import com.seno.game.manager.KakaoAccountManager
import com.seno.game.manager.NaverAccountManager
import kotlinx.coroutines.launch

@Composable
fun SocialLoginContainer(
    googleAccountManager: GoogleAccountManager,
    naverAccountManager: NaverAccountManager,
    kakaoAccountManager: KakaoAccountManager,
    onClickSocialLogin: () -> Unit,
    onSignInSucceed: () -> Unit,
    onSignInFailed: (java.lang.Exception?) -> Unit
) {

    Row(
        horizontalArrangement = Arrangement.spacedBy(space = 22.dp),
    ) {
        GoogleLoginButton(
            googleAccountManager = googleAccountManager,
            onClickSocialLogin = onClickSocialLogin,
            onSignInSucceed = onSignInSucceed,
            onSignInFailed = onSignInFailed,
        )
        KakaoLoginButton(
            kakaoAccountManager = kakaoAccountManager,
            onClickSocialLogin = onClickSocialLogin,
            onSignInSucceed = onSignInSucceed,
            onSignInFailed = onSignInFailed
        )

        NaverLoginButton(
            naverAccountManager = naverAccountManager,
            onClickSocialLogin = onClickSocialLogin,
            onSignInSucceed = onSignInSucceed,
            onSignInFailed = onSignInFailed
        )
    }
}

@Composable
fun GoogleLoginButton(
    googleAccountManager: GoogleAccountManager,
    onClickSocialLogin: () -> Unit,
    onSignInSucceed: () -> Unit,
    onSignInFailed: (java.lang.Exception?) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    SnsLoginButton(snsImage = painterResource(id = R.drawable.ic_sns_google)) {
        onClickSocialLogin.invoke()
        coroutineScope.launch {
            googleAccountManager.login(
                onSignInSucceed = onSignInSucceed,
                onSignInFailed = onSignInFailed
            )
        }
    }
}

@Composable
fun KakaoLoginButton(
    kakaoAccountManager: KakaoAccountManager,
    onClickSocialLogin: () -> Unit,
    onSignInSucceed: () -> Unit,
    onSignInFailed: (java.lang.Exception?) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    SnsLoginButton(
        snsImage = painterResource(id = R.drawable.ic_sns_kakao)
    ) {
        coroutineScope.launch {
            onClickSocialLogin.invoke()
            kakaoAccountManager.login(
                onSignInSucceed = onSignInSucceed,
                onSignInFailed = onSignInFailed
            )
        }
    }
}

@Composable
fun NaverLoginButton(
    naverAccountManager: NaverAccountManager,
    onClickSocialLogin: () -> Unit,
    onSignInSucceed: () -> Unit,
    onSignInFailed: (java.lang.Exception?) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    SnsLoginButton(
        snsImage = painterResource(id = R.drawable.ic_sns_naver)
    ) {
        coroutineScope.launch {
            onClickSocialLogin.invoke()
            naverAccountManager.login(
                onSignInSucceed = onSignInSucceed,
                onSignInFailed = onSignInFailed,
            )
        }
    }
}

@Composable
fun SnsLoginButton(
    snsImage: Painter,
    onClick: () -> Unit,
) {
    Image(
        painter = snsImage,
        contentDescription = null,
        modifier = Modifier
            .size(size = 40.dp)
            .noRippleClickable { onClick.invoke() }
    )
}

