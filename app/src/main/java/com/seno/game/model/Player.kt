package com.seno.game.model

import android.os.Parcelable
import com.seno.game.extensions.toJson
import kotlinx.parcelize.Parcelize

@Parcelize
data class Player(
    var uid: String,
    var nickName: String,
    var profileUri: String
) : Parcelable