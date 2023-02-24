package com.seno.game.ui.main.home.game.diff_picture.model

import kotlinx.coroutines.flow.MutableStateFlow

data class DiffGameInfo(
    var imageList: ArrayList<Pair<Int, Int>> = ArrayList(),
    var answer: Answer? = null,
    var totalRound: Int = 1,
    val answerHashMap: HashMap<Float, Float> = HashMap(),
) {
    var score = 0
    var currentAnswerCount = 0
        set(value) {
            score += 1
            field = if (value == answer?.answerPointList?.size) {
                currentRound.value += 1
                0
            } else {
                value
            }
        }
    var currentRound = MutableStateFlow(0)
}