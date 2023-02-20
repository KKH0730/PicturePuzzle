package com.seno.game.model.response

data class UserInfoResponse(
    val uid: String,
    val nickname: String,
    val platform: String, // 로그인 플랫폼(메일, 카카오, 네이버, 구글, 페이스북)
    val profileUri: String,
    val backgroundVolume: Float,
    val effectVolume: Float,
    val isVibrationOn: Boolean,
    val isPushOn: Boolean,
    val isShowAD: Boolean,
)