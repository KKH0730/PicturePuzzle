package com.seno.game.ui.account.my_profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.seno.game.R
import com.seno.game.extensions.textDp
import com.seno.game.prefs.PrefsManager
import com.seno.game.ui.account.my_profile.component.*

@Composable
fun MyProfileScreen(
    onClickClose: () -> Unit,
    onClickLogin: () -> Unit,
    onClickCreateAccount: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        MyProfile(onClickClose = onClickClose)
        Spacer(modifier = Modifier.height(height = 28.dp))
        GuidTextContainer()
        Spacer(modifier = Modifier.height(height = 15.dp))
        ButtonContainer(
            onClickLogin = onClickLogin,
            onClickCreateAccount = onClickCreateAccount
        )
    }
}

@Composable
fun GuidTextContainer() {
    Text(
        text = stringResource(id = R.string.my_profile_guide),
        fontSize = 14.textDp,
        color = colorResource(id = R.color.color_b8c0ff),
        modifier = Modifier.offset(x = 16.dp)
    )
}

@Composable
fun MyProfile(onClickClose: () -> Unit) {
    var nickName by remember { mutableStateOf(PrefsManager.nickname) }
    var profileUri by remember { mutableStateOf(PrefsManager.profileUri) }

    val lifeCycleOwner = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(key1 = lifeCycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                nickName = PrefsManager.nickname
                profileUri = PrefsManager.profileUri
            }
        }
        lifeCycleOwner.addObserver(observer)
        onDispose { lifeCycleOwner.removeObserver(observer) }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = 190.dp)
    ) {
        MyProfileHeader(onClickClose = onClickClose)
        NicknameContainer(
            modifier = Modifier.offset(y = 102.dp),
            nickName = nickName
        )
        ProfileImage(
            modifier = Modifier.offset(x = 16.dp, y = 66.dp),
            profileUri = profileUri
        )
    }
}

@Composable
fun ButtonContainer(
    onClickLogin: () -> Unit,
    onClickCreateAccount: () -> Unit
) {
    Row() {
        Spacer(modifier = Modifier.width(width = 6.dp))
        LoginButton(onClick = onClickLogin)
        CreateAccountButton(onClick = onClickCreateAccount)
    }
}