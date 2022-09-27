package com.seno.game.data.network.model

import com.google.gson.annotations.SerializedName

data class HumMinJeongEumResponse(val channel: Channel)

data class Channel(
    val total: Int,
    val num: Int,
    val title: String,
    val start: Int,
    val description: String,
    val link: String,
    val lastbuilddate: String,
    val item: List<WordItem> = listOf()
)

data class WordItem(
    @SerializedName("sup_no")
    val supNo: String,
    val word: String,
    @SerializedName("target_code")
    val targetCode: String,
    val pos: String,
    val sense: Sense
)

data class Sense(
    val definition: String,
    val link: String,
    val type: String
)