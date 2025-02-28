package com.seno.game.data.model

import com.seno.game.prefs.PrefsManager

data class SavedGameInfo(
    val uid: String = "",
    val nickname: String = PrefsManager.nickname,
    val platform: String = PrefsManager.platform, // 로그인 플랫폼(메일, 카카오, 네이버, 구글, 페이스북)
    val profileUri: String = PrefsManager.profileUri,
    var backgroundVolume: Float = PrefsManager.backgroundVolume,
    var effectVolume: Float = PrefsManager.effectVolume,
    var isVibrationOn: Boolean = PrefsManager.isVibrationOn,
    var isPushOn: Boolean = PrefsManager.isPushOn,
    val isShowAD: Boolean = PrefsManager.isShowAD,
    val diffPictureGameCurrentStage: Int = PrefsManager.diffPictureStage,
    val completeGameRound: String = PrefsManager.diffPictureCompleteGameRound,
    val diffPictureHeartCount: Int = PrefsManager.diffPictureHeartCount,
    val diffPictureHeartChangedTime: Long = PrefsManager.diffPictureHeartChangedTime
)