package com.seno.game.ui.main.home.game.diff_picture.multi.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MultiGameProfileInfo(
    var path: String,
    var hostUid: String,
    var hostNickName: String,
    var hostProfileUri: String,
    var guestUid: String,
    var guestNickName: String,
    var guestProfileUri: String
) : Parcelable {

    fun isNotEmpty(): Boolean {
        return path.isNotEmpty() && hostUid.isNotEmpty() && guestUid.isNotEmpty()
    }
}