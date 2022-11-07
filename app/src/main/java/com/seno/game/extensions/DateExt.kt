package com.seno.game.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Date.getTodayDate(): String {
    val dateFormat = SimpleDateFormat("yyyyMMdd")
    return dateFormat.format(this)
}