package com.seno.game.prefs

import com.pixplicity.easyprefs.library.Prefs

object PrefsManager {
    var nickname: String
        get() = Prefs.getString("nickname", "")
        set(nickname) {
            Prefs.putString("nickname", nickname)
        }
}