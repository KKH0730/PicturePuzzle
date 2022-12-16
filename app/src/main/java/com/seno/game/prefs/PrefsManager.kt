package com.seno.game.prefs

import com.pixplicity.easyprefs.library.Prefs

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

    var diffPictureCompleteGameSingle: String = ""
        get() = Prefs.getString("diffPictureCompleteGameSingle", "")
        set(value) {
            if (field.isEmpty()) {
                Prefs.putString("isShowAD", value)
            } else {
                Prefs.putString("isShowAD", ",$value")
            }
        }
}