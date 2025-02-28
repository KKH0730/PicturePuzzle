package com.seno.game.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Player(
    var id: Int = 0,
    var uid: String,
    var nickName: String,
    var isReady: Boolean = false
) : Parcelable