package com.seno.game.ui.game.diffgame.model

import kotlinx.coroutines.flow.MutableStateFlow
import org.opencv.core.Mat

data class Setting(
    var imageList: ArrayList<Pair<Int, Int>> = ArrayList(),
    var answerInfoPair: Pair<Mat, ArrayList<Point>>? = null, // 정답 이미지와 좌표
    var totalRound: Int = 3,
    val answerHashMap: HashMap<Float, Float> = HashMap(),
) {
    var score = 0
    var currentAnswerCount = 0
        set(value) {
            score += 1
            field = if (value == answerInfoPair?.second?.size) {
                currentRound.value += 1
                0
            } else {
                value
            }
        }
    var currentRound = MutableStateFlow(0)
}