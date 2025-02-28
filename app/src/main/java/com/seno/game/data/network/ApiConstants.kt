package com.seno.game.data.network

object ApiConstants {
    object Collection {
        const val GAME = "game"
        const val PROFILE = "profile"
        const val SAVE_GAME_INFO = "save_game_info"
    }

    object Document {
        const val DIFF_PICTURE = "diff_picture"
    }

    object UserInfo {
        const val UID = "uid"
        const val NICKNAME = "nickname"
        const val PLATFORM = "platform"
        const val PROFILE_URI = "profileUri"
        const val BACKGROUND_VOLUME = "backgroundVolume"
        const val EFFECT_VOLUME = "effectVolume"
        const val IS_VIBRATION_ON = "isVibrationOn"
        const val IS_PUSH_ON = "isPushOn"
        const val IS_SHOW_AD = "isShowAD"
    }

    object FirestoreKey {
        const val DIFF_PICTURE_GAME_CURRENT_STATE = "diffPictureGameCurrentStage"
        const val COMPLETE_GAME_ROUND = "completeGameRound"
        const val DIFF_PICTURE_GAME_HEART_COUNT = "diffPictureGameHeartCount"
        const val DIFF_PICTURE_GAME_HEART_CHANGE_TIME = "diffPictureGameHeartChangedTime"
    }
}