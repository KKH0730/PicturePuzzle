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
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.seno.game.R
import com.seno.game.extensions.createRandomNickname
import com.seno.game.extensions.toast
import com.seno.game.manager.AccountManager
import com.seno.game.manager.FacebookAccountManager
import com.seno.game.manager.GoogleAccountManager
import com.seno.game.manager.KakaoAccountManager
import com.seno.game.manager.NaverAccountManager
import com.seno.game.prefs.PrefsManager
import com.seno.game.theme.AppTheme
import com.seno.game.ui.account.AccountViewModel
import com.seno.game.ui.account.my_profile.component.GuideTextContainer
import com.seno.game.ui.account.my_profile.component.MyProfileHeader
import com.seno.game.ui.account.my_profile.component.NicknameEditDialog
import com.seno.game.ui.account.my_profile.component.ProfileInfoPanel
import com.seno.game.ui.account.my_profile.component.UserInfoContainer
import com.seno.game.ui.common.CommonCustomDialog
import com.seno.game.ui.component.LoadingView
import kotlinx.coroutines.launch

@Composable
fun MyProfileScreen(
    onClickClose: () -> Unit,
    onClickLogin: () -> Unit
) {
    val accountViewModel = hiltViewModel<AccountViewModel>()
    val context = LocalContext.current

    val facebookAccountManager = FacebookAccountManager(activity = context as ComponentActivity)
    val googleAccountManager = GoogleAccountManager(activity = context)
    val naverAccountManager = NaverAccountManager()
    val kakaoAccountManager = KakaoAccountManager(context = context)

    val profileState = rememberProfileState()

    val lifeCycleOwner = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(key1 = lifeCycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                profileState.apply {
                    setNickname(nickname = PrefsManager.nickname)
                    setProfileUri(profileUri = PrefsManager.profileUri)
                    setSignedIn(isSignedIn = AccountManager.isSignedIn)
                }
            }
        }
        lifeCycleOwner.addObserver(observer)
        onDispose { lifeCycleOwner.removeObserver(observer) }
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
                nickname = profileState.nickname.value,
                profileUri = profileState.profileUri.value,
                isSignedIn = profileState.isSignedIn.value,
                onClickLogin = onClickLogin,
                onClickLogout = { profileState.showLogoutDialog(isShow = true) }
            )
            Spacer(modifier = Modifier.height(height = 28.dp))
            GuideTextContainer(isSignedIn = profileState.isSignedIn.value)
            Spacer(modifier = Modifier.height(height = 15.dp))
            UserInfoContainer(
                isSignedIn = profileState.isSignedIn.value,
                nickname = profileState.nickname.value,
                onClickChangeNickname = { profileState.showEditNicknameDialog(isShow = true) },
                onClickWithdrawal = { profileState.showWithdrawalDialog(isShow = true) }
            )
        }
    }

    if (profileState.isShowLogoutDialog.value) {
        CommonCustomDialog(
            image = painterResource(R.drawable.ic_dialog_cat_wow),
            mainDescription = stringResource(id = R.string.home_logout_message1),
            subDescription = stringResource(id = R.string.home_logout_message2),
            leftButtonText = stringResource(id = R.string.home_logout_n),
            rightButtonText = stringResource(id = R.string.home_logout_y),
            onClickLeft = { profileState.showLogoutDialog(isShow = false) },
            onClickRight = {
                profileState.showLoading(isShow = true)
                AccountManager.startLogout(
                    facebookAccountManager = facebookAccountManager,
                    googleAccountManager = googleAccountManager,
                    naverAccountManager = naverAccountManager,
                    kakaoAccountManager = kakaoAccountManager,
                    isCompleteLogout = {
                        profileState.showLoading(isShow = false)
                        profileState.showLogoutDialog(isShow = false)

                        PrefsManager.apply {
                            this.nickname = context.resources.createRandomNickname()
                            this.platform = ""
                            this.profileUri = ""
                        }
                        profileState.apply {
                            setNickname(nickname = PrefsManager.nickname)
                            setProfileUri(profileUri = "")
                            setSignedIn(isSignedIn = false)
                        }

                        context.toast(context.getString(R.string.my_profile_logout_success))
                    }
                )
            },
            onDismissed = { profileState.showLogoutDialog(isShow = false) }
        )
    }

    if (profileState.isShowWithdrawalDialog.value) {
        CommonCustomDialog(
            image = painterResource(R.drawable.ic_dialog_cat_crying),
            mainDescription = stringResource(id = R.string.my_profile_withdrawal_description_1),
            subDescription = stringResource(id = R.string.my_profile_withdrawal_description_2),
            leftButtonText = stringResource(id = R.string.my_profile_withdrawal_n),
            rightButtonText = stringResource(id = R.string.my_profile_withdrawal_y),
            onClickLeft = { profileState.showWithdrawalDialog(isShow = false) },
            onClickRight = {
                profileState.showLoading(isShow = true)

                profileState.coroutineScope.launch {
                    AccountManager.startWithdrawal(
                        isCompleteWithdrawal = {
                            profileState.showLoading(isShow = false)
                            profileState.showWithdrawalDialog(isShow = false)

                            PrefsManager.apply {
                                this.nickname = context.resources.createRandomNickname()
                                this.platform = ""
                                this.profileUri = ""
                            }
                            profileState.apply {
                                setNickname(nickname = PrefsManager.nickname)
                                setProfileUri(profileUri = "")
                                setSignedIn(isSignedIn = false)
                            }

                            context.toast(context.getString(R.string.my_profile_withdrawal_success))

                            AccountManager.startLogout(
                                facebookAccountManager = facebookAccountManager,
                                googleAccountManager = googleAccountManager,
                                naverAccountManager = naverAccountManager,
                                kakaoAccountManager = kakaoAccountManager,
                                isCompleteLogout = {}
                            )
                        },
                        onFail = {
                            context.toast(context.getString(R.string.my_profile_withdrawal_fail))
                        }
                    )
                }
            },
            onDismissed = { profileState.showWithdrawalDialog(isShow = false) }
        )
    }

    if (profileState.isShowEditNicknameDialog.value) {
        NicknameEditDialog(
            initialNickname = profileState.nickname.value,
            onConfirm = {
                accountViewModel.reqUpdateNickname(
                    uid = AccountManager.firebaseUid,
                    nickname = it,
                    onComplete = { nick ->
                        PrefsManager.nickname = nick
                        profileState.apply {
                            setNickname(nickname = nick)
                            showEditNicknameDialog(isShow = false)
                        }
                    }
                )
            },
            onDismiss = { profileState.showEditNicknameDialog(isShow = false) }
        )
    }

    if (profileState.isLoading.value) {
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