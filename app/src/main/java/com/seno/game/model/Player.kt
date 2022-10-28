package com.seno.game.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Player(
    var uid: String,
    var nickName: String,
    var isReady: Boolean = false
) : Parcelable