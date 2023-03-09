package com.seno.game.ui.main.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.seno.game.manager.FacebookAccountManager
import com.seno.game.manager.GoogleAccountManager
import com.seno.game.manager.KakaoAccountManager
import com.seno.game.manager.NaverAccountManager
import com.seno.game.prefs.PrefsManager

data class HomeState(
    val facebookAccountManager: FacebookAccountManager,
    val googleAccountManager: GoogleAccountManager,
    val naverAccountManager: NaverAccountManager,
    val kakaoAccountManager: KakaoAccountManager,
    var isShowQuitDialog: MutableState<Boolean>,
    var isShowLogoutDialog: MutableState<Boolean>,
    var isShowSettingDialog: MutableState<Boolean>,
    var isShowPrepareDialog: MutableState<Boolean>,
    var isLoading: MutableState<Boolean>,
    var nickname: MutableState<String>,
    var profile: MutableState<String>,
)

@Composable
fun rememberHomeState(
    facebookAccountManager: FacebookAccountManager,
    googleAccountManager: GoogleAccountManager,
    naverAccountManager: NaverAccountManager,
    kakaoAccountManager: KakaoAccountManager,
    isShowQuitDialog: MutableState<Boolean> = mutableStateOf(false),
    isShowLogoutDialog: MutableState<Boolean> = mutableStateOf(false),
    isShowSettingDialog: MutableState<Boolean> = mutableStateOf(false),
    isShowPrepareDialog: MutableState<Boolean> = mutableStateOf(false),
    isLoading: MutableState<Boolean> = mutableStateOf(false),
    nickname: MutableState<String> = mutableStateOf(PrefsManager.nickname),
    profile: MutableState<String> = mutableStateOf(PrefsManager.profileUri),
) = remember() {
    HomeState(
        facebookAccountManager = facebookAccountManager,
        googleAccountManager = googleAccountManager,
        naverAccountManager = naverAccountManager,
        kakaoAccountManager = kakaoAccountManager,
        isShowQuitDialog = isShowQuitDialog,
        isShowLogoutDialog = isShowLogoutDialog,
        isShowSettingDialog = isShowSettingDialog,
        isShowPrepareDialog = isShowPrepareDialog,
        isLoading = isLoading,
        nickname = nickname,
        profile = profile
    )
}