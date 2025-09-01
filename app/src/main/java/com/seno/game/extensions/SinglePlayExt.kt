package com.seno.game.extensions

import com.seno.game.prefs.PrefsManager

fun String?.saveOriginImageUrl(stage: String, round: String) {
    val jsonObject = PrefsManager.roundOriginImageUrl.getJSONObject().apply {
        put("${stage}-$round", this@saveOriginImageUrl)
    }
    PrefsManager.roundOriginImageUrl = jsonObject.toString()
}

fun String?.saveRoundImageUrl(stage: String, round: String) {
    val jsonObject = PrefsManager.roundOtherImageUrl.getJSONObject().apply {
        put("${stage}-$round", this@saveRoundImageUrl)
    }
    PrefsManager.roundOtherImageUrl = jsonObject.toString()
}

fun String.getOriginImageUrl(): String {
    val jsonObject = PrefsManager.roundOriginImageUrl.getJSONObject()
    return jsonObject.getStringOrDefault(this)
}

fun String.getOtherImageUrl(): String {
    val jsonObject = PrefsManager.roundOtherImageUrl.getJSONObject()
    return jsonObject.getStringOrDefault(this)
}
