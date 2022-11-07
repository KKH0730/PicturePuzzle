package com.seno.game.ui.account.sign_gate

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.seno.game.R
import com.seno.game.extensions.textDp
import com.seno.game.ui.account.sign_gate.component.*

@Composable
fun SignGateScreen(
    onClickClose: () -> Unit,
    onClickLogin: () -> Unit,
    onClickCreateAccount: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        SignGateProfile(onClickClose = onClickClose)
        Spacer(modifier = Modifier.height(height = 28.dp))
        GuidTextContainer()
        Spacer(modifier = Modifier.height(height = 15.dp))
        SignGateButtonContainer(
            onClickLogin = onClickLogin,
            onClickCreateAccount = onClickCreateAccount
        )
    }
}

@Composable
fun GuidTextContainer() {
    Text(
        text = stringResource(id = R.string.sign_gate_guide),
        fontSize = 14.textDp,
        color = colorResource(id = R.color.color_b8c0ff),
        modifier = Modifier.offset(x = 16.dp)
    )
}

@Composable
fun SignGateProfile(onClickClose: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = 190.dp)
    ) {
        SignGateHeader(onClickClose = onClickClose)
        NicknameContainer(
            modifier = Modifier.offset(y = 102.dp),
            nickName = "닉네임"
        )
        ProfileImage(modifier = Modifier.offset(x = 16.dp, y = 66.dp))
    }
}

@Composable
fun SignGateButtonContainer(
    onClickLogin: () -> Unit,
    onClickCreateAccount: () -> Unit
) {
    Row() {
        Spacer(modifier = Modifier.width(width = 6.dp))
        LoginButton(onClick = onClickLogin)
        CreateAccountButton(onClick = onClickCreateAccount)
    }
}