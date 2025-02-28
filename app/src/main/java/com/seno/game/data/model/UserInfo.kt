package com.seno.game.data.model

import com.seno.game.prefs.PrefsManager

data class UserInfo(
    val uid: String = "",
    val nickname: String = PrefsManager.nickname,
    val platform: String = PrefsManager.platform, // 로그인 플랫폼(메일, 카카오, 네이버, 구글, 페이스북)
    val profileUri: String = PrefsManager.profileUri,
    val backgroundVolume: Float = PrefsManager.backgroundVolume,
    val effectVolume: Float = PrefsManager.effectVolume,
    val isVibrationOn: Boolean = PrefsManager.isVibrationOn,
    val isPushOn: Boolean = PrefsManager.isPushOn,
    val isShowAD: Boolean = PrefsManager.isShowAD,
    val diffPictureGameCurrentStage: Int = 0,
    val completeGameRound: String = ""
)