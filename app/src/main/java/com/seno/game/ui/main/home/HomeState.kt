package com.seno.game.ui.main.home

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.seno.game.manager.FacebookAccountManager
import com.seno.game.manager.GoogleAccountManager
import com.seno.game.manager.KakaoAccountManager
import com.seno.game.manager.NaverAccountManager
import com.seno.game.prefs.PrefsManager
import com.seno.game.ui.main.MainActivity
import com.seno.game.ui.main.home.game.diff_picture.list.GameListState
import com.seno.game.ui.main.home.game.diff_picture.list.model.DPSingleGame
import kotlinx.coroutines.CoroutineScope

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