package com.seno.game.prefs

import com.pixplicity.easyprefs.library.Prefs
import timber.log.Timber

object PrefsManager {
    var nickname: String
        get() = Prefs.getString("nickname", "")
        set(nickname) {
            Prefs.putString("nickname", nickname)
        }

    var platform: String
        get() = Prefs.getString("platform", "")
        set(snsPlatform) {
            Prefs.putString("platform", snsPlatform)
        }

    var profileUri: String
        get() = Prefs.getString("profileUri", "")
        set(uri) {
            Prefs.putString("profileUri", uri)
        }

    var backgroundVolume: Float
        get() = Prefs.getFloat("backgroundVolume", 0.5f)
        set(vol) {
            Prefs.putFloat("backgroundVolume", vol)
        }

    var effectVolume: Float
        get() = Prefs.getFloat("effectVolume", 0.5f)
        set(vol) {
            Prefs.putFloat("effectVolume", vol)
        }

    var isVibrationOn: Boolean
        get() = Prefs.getBoolean("isVibrationOn", true)
        set(isOn) {
            Prefs.putBoolean("isVibrationOn", isOn)
        }

    var isPushOn: Boolean
        get() = Prefs.getBoolean("isPushOn", true)
        set(isOn) {
            Prefs.putBoolean("isPushOn", isOn)
        }

    var isShowAD: Boolean
        get() = Prefs.getBoolean("isShowAD", true)
        set(isShow) {
            Prefs.putBoolean("isShowAD", isShow)
        }

    var diffPictureCompleteGameRound: String
        get() = Prefs.getString("diffPictureCompleteGameRound", "")
        set(value) {
            if (Prefs.getString("diffPictureCompleteGameRound").isEmpty()) {
                Prefs.putString("diffPictureCompleteGameRound", value)
            } else {
                if (!Prefs.getString("diffPictureCompleteGameRound").split(",").contains(value)) {
                    Prefs.putString(
                        "diffPictureCompleteGameRound",
                        "${Prefs.getString("diffPictureCompleteGameRound")},$value"
                    )
                }
            }
        }

    var diffPictureStage: Int
        get() = Prefs.getInt("diffPictureStage", 0)
        set(value) {
            Prefs.putInt("diffPictureStage", value)
        }

    var diffPictureHeartCount: Int
        get() = Prefs.getInt("diffPictureHeartCount", 5)
        set(value) {
            Prefs.putInt("diffPictureHeartCount", value)
        }

    var diffPictureHeartChangedTime: Long
        get() = Prefs.getLong("diffPictureHeartChangedTime", 0L)
        set(value) {
            Prefs.putLong("diffPictureHeartChangedTime", value)
        }
}