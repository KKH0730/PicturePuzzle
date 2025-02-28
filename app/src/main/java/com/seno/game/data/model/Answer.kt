package com.seno.game.data.model

import org.opencv.core.Mat

data class Answer(
    val answerMat: Mat, // 정답 이미지
    val answerPointList: ArrayList<Point> // 정답 좌표
)
