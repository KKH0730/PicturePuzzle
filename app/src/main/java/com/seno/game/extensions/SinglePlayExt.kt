package com.seno.game.extensions

import com.seno.game.prefs.PrefsManager

fun String?.saveOriginImageUrl(round: String) {
    val jsonObject = PrefsManager.roundOriginImageUrl.getJSONObject().apply {
        put(round, this@saveOriginImageUrl)
    }
    PrefsManager.roundOriginImageUrl = jsonObject.toString()
}

fun String?.saveRoundImageUrl(round: String) {
    val jsonObject = PrefsManager.roundOtherImageUrl.getJSONObject().apply {
        put(round, this@saveRoundImageUrl)
    }
    PrefsManager.roundOtherImageUrl = jsonObject.toString()
}

fun String.getOriginImageUrl(): String {
    val jsonObject = PrefsManager.roundOriginImageUrl.getJSONObject()
    return jsonObject.getString(this)
}

fun String.getOtherImageUrl(): String {
    val jsonObject = PrefsManager.roundOtherImageUrl.getJSONObject()
    return jsonObject.getString(this)
}
