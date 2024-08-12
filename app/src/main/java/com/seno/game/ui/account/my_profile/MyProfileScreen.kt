package com.seno.game.ui.account.my_profile

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.seno.game.R
import com.seno.game.extensions.createRandomNickname
import com.seno.game.extensions.snackbar
import com.seno.game.extensions.toast
import com.seno.game.manager.AccountManager
import com.seno.game.manager.FacebookAccountManager
import com.seno.game.manager.GoogleAccountManager
import com.seno.game.manager.KakaoAccountManager
import com.seno.game.manager.NaverAccountManager
import com.seno.game.prefs.PrefsManager
import com.seno.game.theme.AppTheme
import com.seno.game.ui.account.my_profile.component.GuideTextContainer
import com.seno.game.ui.account.my_profile.component.MyProfileHeader
import com.seno.game.ui.account.my_profile.component.ProfileInfoPanel
import com.seno.game.ui.account.my_profile.component.UserInfoContainer
import com.seno.game.ui.common.CommonCustomDialog
import com.seno.game.ui.component.LoadingView
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MyProfileScreen(
    onClickClose: () -> Unit,
    onClickLogin: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val facebookAccountManager = FacebookAccountManager(activity = context as ComponentActivity)
    val googleAccountManager = GoogleAccountManager(activity = context as ComponentActivity)
    val naverAccountManager = NaverAccountManager()
    val kakaoAccountManager = KakaoAccountManager(context = context)

    var isLoading by remember { mutableStateOf(false) }
    var isShowLogoutDialog by remember { mutableStateOf(false) }
    var isShowWidthdrawalDialog by remember { mutableStateOf(false) }
    var nickname by remember { mutableStateOf(PrefsManager.nickname) }
    var profileUri by remember { mutableStateOf(PrefsManager.profileUri) }
    var isSignedIn by remember { mutableStateOf(AccountManager.isSignedIn) }


    val lifeCycleOwner = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(key1 = lifeCycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                nickname = PrefsManager.nickname
                profileUri = PrefsManager.profileUri
                isSignedIn = AccountManager.isSignedIn
            }
        }
        lifeCycleOwner.addObserver(observer)
        onDispose { lifeCycleOwner.removeObserver(observer) }
    }

    if (isShowLogoutDialog) {
        CommonCustomDialog(
            image = painterResource(R.drawable.ic_dialog_cat_wow),
            mainDescription = stringResource(id = R.string.home_logout_message1),
            subDescription = stringResource(id = R.string.home_logout_message2),
            leftButtonText = stringResource(id = R.string.home_logout_n),
            rightButtonText = stringResource(id = R.string.home_logout_y),
            onClickLeft = { isShowLogoutDialog = false },
            onClickRight = {
                isLoading = true
                AccountManager.startLogout(
                    facebookAccountManager = facebookAccountManager,
                    googleAccountManager = googleAccountManager,
                    naverAccountManager = naverAccountManager,
                    kakaoAccountManager = kakaoAccountManager,
                    isCompleteLogout = {
                        isLoading = false
                        isShowLogoutDialog = false

                        PrefsManager.apply {
                            this.nickname = context.resources.createRandomNickname()
                            this.platform = ""
                            this.profileUri = ""
                        }
                        nickname = PrefsManager.nickname
                        profileUri = ""
                        isSignedIn = false

                        context.toast("로그아웃 성공")
                    }
                )
            },
            onDismissed = { isShowLogoutDialog = false }
        )
    }

    if (isShowWidthdrawalDialog) {
        CommonCustomDialog(
            image = painterResource(R.drawable.ic_dialog_cat_crying),
            mainDescription = stringResource(id = R.string.my_profile_withdrawal_description_1),
            subDescription = stringResource(id = R.string.my_profile_withdrawal_description_2),
            leftButtonText = stringResource(id = R.string.my_profile_withdrawal_n),
            rightButtonText = stringResource(id = R.string.my_profile_withdrawal_y),
            onClickLeft = { isShowWidthdrawalDialog = false },
            onClickRight = {
                isLoading = true

                scope.launch {
                    AccountManager.startWithdrawal(
                        isCompleteWithdrawal = {
                            isLoading = false
                            isShowWidthdrawalDialog = false

                            PrefsManager.apply {
                                this.nickname = context.resources.createRandomNickname()
                                this.platform = ""
                                this.profileUri = ""
                            }
                            nickname = PrefsManager.nickname
                            profileUri = ""
                            isSignedIn = false


                            context.toast("회원탈퇴 성공")

                            AccountManager.startLogout(
                                facebookAccountManager = facebookAccountManager,
                                googleAccountManager = googleAccountManager,
                                naverAccountManager = naverAccountManager,
                                kakaoAccountManager = kakaoAccountManager,
                                isCompleteLogout = {}
                            )
                        },
                        onFail = {
                            context.toast("회원탈퇴 실패")
                        }
                    )
                }
            },
            onDismissed = { isShowWidthdrawalDialog = false }
        )
    }

    Box {
        Image(
            painter = painterResource(id = R.drawable.ic_home_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(modifier = Modifier.fillMaxSize()) {
            MyProfileHeader(onClickClose = onClickClose)
            Spacer(modifier = Modifier.height(height = 20.dp))
            ProfileInfoPanel(
                nickname = nickname,
                profileUri = profileUri,
                isSignedIn = isSignedIn,
                onClickLogin = onClickLogin,
                onClickLogout = { isShowLogoutDialog = true }
            )
            Spacer(modifier = Modifier.height(height = 28.dp))
            GuideTextContainer(isSignedIn = isSignedIn)
            Spacer(modifier = Modifier.height(height = 15.dp))
            UserInfoContainer(
                isSignedIn = isSignedIn,
                onClickChangeNickname = {},
                onClickWithdrawal = { isShowWidthdrawalDialog = true }
            )
        }
    }


    if (isLoading) {
        LoadingView()
    }
}

@Preview
@Composable
fun Preview() {
    AppTheme {
        Surface(Modifier.fillMaxSize()) {
            MyProfileScreen(
                onClickClose = {},
                onClickLogin = {}
            )
        }
    }
}