package com.seno.game.extensions

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

fun Date.getTodayDate(): String {
    val dateFormat = SimpleDateFormat("yyyyMMdd")
    return dateFormat.format(this)
}

fun getImageDate(): String {
    val today = LocalDate.now()
    val formattedDate = today.format(DateTimeFormatter.ofPattern("yyyyMM"))
    return formattedDate
}