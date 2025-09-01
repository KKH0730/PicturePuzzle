package com.seno.game.extensions

import org.json.JSONObject

fun String.getJSONObject(): JSONObject {
    return try {
        JSONObject(this)
    } catch (e: Exception) {
        e.printStackTrace()
        JSONObject()
    }
}

fun JSONObject.getStringOrDefault(key: String, default: String = ""): String {
    return try {
        this.getString(key)
    } catch (e: Exception) {
        e.printStackTrace()
        default
    }
}