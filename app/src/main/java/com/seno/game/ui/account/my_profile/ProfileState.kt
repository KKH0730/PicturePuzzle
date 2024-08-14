package com.seno.game.ui.account.my_profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.seno.game.prefs.PrefsManager
import kotlinx.coroutines.CoroutineScope

class ProfileState(
    val coroutineScope: CoroutineScope,
    val isLoading: MutableState<Boolean>,
    val isShowLogoutDialog: MutableState<Boolean>,
    val isShowWithdrawalDialog: MutableState<Boolean>,
    val isShowEditNicknameDialog: MutableState<Boolean>,
    val nickname: MutableState<String>,
    val profileUri: MutableState<String>,
    val isSignedIn: MutableState<Boolean>,
) {
    fun showLoading(isShow: Boolean) {
        this.isLoading.value = isShow
    }

    fun showLogoutDialog(isShow: Boolean) {
        this.isShowLogoutDialog.value = isShow
    }

    fun showWithdrawalDialog(isShow: Boolean) {
        this.isShowWithdrawalDialog.value = isShow
    }

    fun showEditNicknameDialog(isShow: Boolean) {
        this.isShowEditNicknameDialog.value = isShow
    }

    fun setNickname(nickname: String) {
        this.nickname.value = nickname
    }

    fun setProfileUri(profileUri: String) {
        this.profileUri.value = profileUri
    }

    fun setSignedIn(isSignedIn: Boolean) {
        this.isSignedIn.value = isSignedIn
    }
}

@Composable
fun rememberProfileState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    isLoading: MutableState<Boolean> = mutableStateOf(false),
    isShowLogoutDialog: MutableState<Boolean> = mutableStateOf(false),
    isShowWithdrawalDialog: MutableState<Boolean> = mutableStateOf(false),
    isShowEditNicknameDialog: MutableState<Boolean> = mutableStateOf(false),
    nickname: MutableState<String> = mutableStateOf(PrefsManager.nickname),
    profileUri: MutableState<String> = mutableStateOf(PrefsManager.profileUri),
    isSignedIn: MutableState<Boolean> = mutableStateOf(false),
): ProfileState =
    remember {
        ProfileState(
            coroutineScope = coroutineScope,
            isLoading = isLoading,
            isShowLogoutDialog = isShowLogoutDialog,
            isShowWithdrawalDialog = isShowWithdrawalDialog,
            isShowEditNicknameDialog = isShowEditNicknameDialog,
            nickname = nickname,
            profileUri = profileUri,
            isSignedIn = isSignedIn
        )
    }