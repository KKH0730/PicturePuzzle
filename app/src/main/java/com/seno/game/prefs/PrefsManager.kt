package com.seno.game.prefs

import com.pixplicity.easyprefs.library.Prefs

object PrefsManager {
    var nickname: String
        get() = Prefs.getString("nickname", "")
        set(nickname) {
            Prefs.putString("nickname", nickname)
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
}