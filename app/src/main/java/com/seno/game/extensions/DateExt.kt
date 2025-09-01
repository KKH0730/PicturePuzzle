package com.seno.game.extensions

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun getTodayDate(): String {
    val today = LocalDate.now()
    val formattedDate = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
    return formattedDate
}

fun getImageDate(): String {
    val today = LocalDate.now()
    val formattedDate = today.format(DateTimeFormatter.ofPattern("yyyyMM"))
    return formattedDate
}

fun String.parseImageDate() : String {
    val inputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    val outputFormatter = DateTimeFormatter.ofPattern("yyyyMM")
    val localDate = LocalDate.parse(this, inputFormatter)
    return localDate.format(outputFormatter)
}