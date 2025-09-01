package com.seno.game.extensions

import org.json.JSONObject

fun String.getJSONObject(): JSONObject {
    return try {
        JSONObject(this)
    } catch (e: Exception) {
        JSONObject()
    }
}