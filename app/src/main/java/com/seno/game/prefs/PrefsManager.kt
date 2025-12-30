package com.seno.game.prefs

import com.pixplicity.easyprefs.library.Prefs
import com.seno.game.extensions.getTodayDate
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

    fun setHeartCount(count: Int, from: Int) {
        Timber.e("setHeartCount -> count: $count, from: $from")
        diffPictureHeartCount = count
    }

    var diffPictureHeartChargedTime: Long
        get() = Prefs.getLong("diffPictureHeartChargedTime", 0L)
        set(value) {
            Prefs.putLong("diffPictureHeartChargedTime", value)
        }

    var roundOriginImageUrl: String
        get() = Prefs.getString("roundOriginImageUrl", "")
        set(value) {
            Prefs.putString("roundOriginImageUrl", value)
        }
    var roundOtherImageUrl: String
        get() = Prefs.getString("roundOtherImageUrl", "")
        set(value) {
            Prefs.putString("roundOtherImageUrl", value)
        }

    var recentSinglePlayDate: String // yyyyMMdd
        get() {
            return Prefs.getString("recentSinglePlayDate", getTodayDate())
        }
        set(value) {
            Prefs.putString("recentSinglePlayDate", value)
        }

    fun clearSinglePlayData(currentTimeMillis: Long) {
        Prefs.putString("diffPictureCompleteGameRound", "")
        Prefs.putInt("diffPictureStage", 0)
        Prefs.putLong("diffPictureHeartChargedTime", 5)
        Prefs.putLong("diffPictureHeartChargedTime", currentTimeMillis)
        Prefs.putString("roundOriginImageUrl", "")
        Prefs.putString("roundOtherImageUrl", "")
    }
}