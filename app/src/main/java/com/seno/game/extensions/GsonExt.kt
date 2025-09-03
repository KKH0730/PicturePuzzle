package com.seno.game.extensions

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.seno.game.extensions.gson
import org.json.JSONObject

val gson = Gson()

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

fun Any.toJson(): String = gson.toJson(this) ?: ""

fun String.fromJson(): Map<String, Any> {
    val mapType = object : TypeToken<Map<String, Any>>() {}.type
    return gson.fromJson(this, mapType)
}

fun <T> String.fromJson(clazz: Class<T>): T? {
    return gson.fromJson(this, clazz)
}

inline fun <reified T> T.toMap(): Map<String, Any?> {
    val gson = Gson()
    val json = gson.toJson(this)
    val type = object : TypeToken<Map<String, Any?>>() {}.type
    return gson.fromJson(json, type)
}