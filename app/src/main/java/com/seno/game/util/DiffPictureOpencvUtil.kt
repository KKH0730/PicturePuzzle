package com.seno.game.util

import android.graphics.Bitmap
import com.seno.game.ui.main.home.game.diff_picture.model.Answer
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import kotlin.math.sqrt

//const val RADIUS_CORRECTION = 50
//class DiffPictureOpencvUtil {
//
//    fun diff(srcBitmap: Bitmap, copyBitmap: Bitmap): Mat {
//        val src = Mat()
//        Utils.bitmapToMat(srcBitmap, src)
//
//        val copy = Mat()
//        Utils.bitmapToMat(copyBitmap, copy)
//
//        val diffMat = Mat()
//        Core.absdiff(src, copy, diffMat)
//
//
//        Imgproc.cvtColor(diffMat, diffMat, Imgproc.COLOR_BGR2GRAY)
//
//        val contours = ArrayList<MatOfPoint>()
//        val hierarchy = Mat()
//
//        Imgproc.findContours(
//            diffMat,
//            contours,
//            hierarchy,
//            Imgproc.RETR_EXTERNAL,
//            Imgproc.CHAIN_APPROX_NONE
//        )
//
//        for (contourIdx in contours.indices) {
//            val contourArea = Imgproc.contourArea(contours[contourIdx]) // 면적 구하기
//
//            if (contourArea < 400) {
//                continue
//            }
//
//            // 근사화
//            val approxCurve = MatOfPoint2f()
//            val contour2f = MatOfPoint2f(*contours[contourIdx].toArray())
//            val approxDistance = Imgproc.arcLength(contour2f, true) * 0.02
//            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true)
//
//            //Convert back to MatOfPoint
//            val points = MatOfPoint(*approxCurve.toArray())
//
//            // Get bounding rect of contour
//            val rect = Imgproc.boundingRect(points)
//            Imgproc.rectangle(
//                diffMat,
//                Point(rect.x.toDouble(), rect.y.toDouble()),
//                Point((rect.x + rect.width).toDouble(), (rect.y + rect.height).toDouble()),
//                RED,
//                5
//            )
//        }
//        Imgproc.cvtColor(diffMat, diffMat, Imgproc.COLOR_RGB2BGR)
//        return diffMat
//    }
//
//    fun getDiffAnswer(srcBitmap: Bitmap?, copyBitmap: Bitmap?) : Answer? {
//        if (srcBitmap == null || copyBitmap == null) {
//            return null
//        }
//
//        try {
//            val src = Mat()
//            Utils.bitmapToMat(srcBitmap, src)
//
//            val copy = Mat()
//            Utils.bitmapToMat(copyBitmap, copy)
//
//            val diffMat = Mat()
//            Core.absdiff(src, copy, diffMat)
//
//            // 1️⃣ 그레이스케일 변환 + Threshold
//            val bin = Mat()
//            Imgproc.cvtColor(diffMat, bin, Imgproc.COLOR_BGR2GRAY)
//            Imgproc.threshold(bin, bin, 0.0, 255.0, Imgproc.THRESH_OTSU)
//
//            // 2️⃣ Morphological CLOSE 연산 (작은 구멍 제거, 인접 영역 병합)
//            val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(15.0, 15.0))
//            Imgproc.morphologyEx(bin, bin, Imgproc.MORPH_CLOSE, kernel)
//
//            // 3️⃣ Contour 검출
//            val contours = ArrayList<MatOfPoint>()
//            val hierarchy = Mat()
//            Imgproc.findContours(bin, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE)
//
//            // 4️⃣ BoundingRect 리스트 생성
////            val rects = contours.map { Imgproc.boundingRect(it) }.toMutableList()
//            val rects = contours
//                .filter {
//                    val contourArea = Imgproc.contourArea(it)
//                    val minArea = (src.width() * src.height() * 0.0005).toDouble()
//                    contourArea >= minArea
//                }
//                .map {
//                    // 근사화
//                    val approxCurve = MatOfPoint2f()
//                    val contour2f = MatOfPoint2f(*it.toArray())
//                    val approxDistance = Imgproc.arcLength(contour2f, true) * 0.02
//                    Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true)
//
//                    val points = MatOfPoint(*approxCurve.toArray())
//
//                    Imgproc.boundingRect(points)
//                }.toMutableList()
//
//            // 5️⃣ 반복적인 Rect 병합
//            val mergedRects = mergeRects(rects)
//
//            val pointList = ArrayList<com.seno.game.ui.game.diff_picture.model.Point>()
//            for (rect in mergedRects) {
////            for (contourIdx in contours.indices) {
////                val contourArea = Imgproc.contourArea(contours[contourIdx]) // 면적 구하기
////
////                val minArea = (src.width() * src.height() * 0.001).toDouble()
////                if (contourArea < minArea) continue
////
////                // 근사화
////                val approxCurve = MatOfPoint2f()
////                val contour2f = MatOfPoint2f(*contours[contourIdx].toArray())
////                val approxDistance = Imgproc.arcLength(contour2f, true) * 0.02
////                Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true)
////
////                val points = MatOfPoint(*approxCurve.toArray())
////
////                val rect = Imgproc.boundingRect(points)
//
//                val centerX = (rect.x + (rect.width / 2)).toDouble()
//                val centerY = (rect.y + (rect.height / 2)).toDouble()
//
//                pointList.add(com.seno.game.ui.game.diff_picture.model.Point(
//                    rectX = rect.x.toFloat(),
//                    rectY = rect.y.toFloat(),
//                    rectWidth = rect.width.toFloat(),
//                    rectHeight = rect.height.toFloat(),
//                    srcWidth = src.width().toFloat(),
//                    srcHeight = src.height().toFloat(),
//                    centerX = centerX.toFloat(),
//                    centerY = centerY.toFloat(),
//                    answerRadius = (rect.width.coerceAtLeast(rect.height) / 2).toFloat()
//                ))
//
//                Imgproc.circle(
//                    src,
//                    Point(centerX, centerY),
//                    (rect.width.coerceAtLeast(rect.height) / 2) + RADIUS_CORRECTION,
//                    RED,
//                    10
//                )
//
//                Imgproc.rectangle(
//                    src,
//                    Point(rect.x.toDouble(), rect.y.toDouble()),
//                    Point((rect.x + rect.width).toDouble(), (rect.y + rect.height).toDouble()),
//                    RED,
//                    10,
//                    1
//                )
//            }
//            Imgproc.cvtColor(src, src, Imgproc.COLOR_RGBA2BGR)
//            return Answer(answerMat = src, answerPointList = pointList)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            return null
//        }
//    }
//
//    // ------------------ Helper Functions ------------------
//
//    fun mergeRects(rects: MutableList<Rect>): List<Rect> {
//        var merged = rects.toMutableList()
//        var didMerge: Boolean
//
//        do {
//            didMerge = false
//            val newMerged = mutableListOf<Rect>()
//            val visited = BooleanArray(merged.size)
//
//            for (i in merged.indices) {
//                if (visited[i]) continue
//                var current = merged[i]
//
//                for (j in merged.indices) {
//                    if (i == j || visited[j]) continue
//                    if (isOverlap(current, merged[j])) {
//                        current = unionRect(current, merged[j])
//                        visited[j] = true
//                        didMerge = true
//                    }
//                }
//
//                newMerged.add(current)
//                visited[i] = true
//            }
//
//            merged = newMerged
//        } while (didMerge)
//
//        return merged
//    }
//
//    fun isOverlap(a: Rect, b: Rect): Boolean {
//        val xOverlap = a.x < b.x + b.width && a.x + a.width > b.x
//        val yOverlap = a.y < b.y + b.height && a.y + a.height > b.y
//        return xOverlap && yOverlap
//    }
//
//    fun unionRect(a: Rect, b: Rect): Rect {
//        val x = min(a.x, b.x)
//        val y = min(a.y, b.y)
//        val right = max(a.x + a.width, b.x + b.width)
//        val bottom = max(a.y + a.height, b.y + b.height)
//        return Rect(x, y, right - x, bottom - y)
//    }
//}

class DiffPictureOpencvUtil {

    fun getDiffAnswer(srcBitmap: Bitmap?, copyBitmap: Bitmap?) : Answer? {
        if (srcBitmap == null || copyBitmap == null) {
            return null
        }

        try {
            val src = Mat()
            Utils.bitmapToMat(srcBitmap, src)

            val copy = Mat()
            Utils.bitmapToMat(copyBitmap, copy)

            // ⭐ 크기 맞추기
            if (src.size() != copy.size()) {
                Imgproc.resize(copy, copy, src.size())
            }

            // ⭐ 채널 맞추기 (src가 3채널이라면 copy도 3채널로)
            if (src.channels() != copy.channels()) {
                if (src.channels() == 3 && copy.channels() == 1) {
                    Imgproc.cvtColor(copy, copy, Imgproc.COLOR_GRAY2BGR)
                } else if (src.channels() == 1 && copy.channels() == 3) {
                    Imgproc.cvtColor(copy, copy, Imgproc.COLOR_BGR2GRAY)
                }
            }

            val diffMat = Mat()
            Core.absdiff(src, copy, diffMat)

            // 1️⃣ 그레이스케일 변환 + Threshold
            val bin = Mat()
            Imgproc.cvtColor(diffMat, bin, Imgproc.COLOR_BGR2GRAY)
            Imgproc.threshold(bin, bin, 0.0, 255.0, Imgproc.THRESH_OTSU)

            // 2️⃣ Morphological CLOSE 연산 (작은 구멍 제거, 인접 영역 병합)
            val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(15.0, 15.0))
            Imgproc.morphologyEx(bin, bin, Imgproc.MORPH_CLOSE, kernel)

            // 3️⃣ Contour 검출
            val contours = ArrayList<MatOfPoint>()
            val hierarchy = Mat()
            Imgproc.findContours(bin, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE)

            // 4️⃣ Contour → Circle 변환
            val circles = contours
                .filter {
                    val contourArea = Imgproc.contourArea(it)
                    val minArea = (src.width() * src.height() * 0.00015)
                    contourArea >= minArea
                }
                .map {
                    val contour2f = MatOfPoint2f(*it.toArray())
                    val center = Point()
                    val radius = FloatArray(1)
                    Imgproc.minEnclosingCircle(contour2f, center, radius)
                    Circle(center.x, center.y, radius[0].toDouble())
                }
                .toMutableList()

            val mergedCircles = mergeCircles(circles)

            val pointList = mergedCircles.map { c ->
                com.seno.game.ui.main.home.game.diff_picture.model.Point(
                    rectX = (c.cx - c.r).toFloat(),
                    rectY = (c.cy - c.r).toFloat(),
                    rectWidth = (c.r * 2).toFloat(),
                    rectHeight = (c.r * 2).toFloat(),
                    srcWidth = src.width().toFloat(),
                    srcHeight = src.height().toFloat(),
                    centerX = c.cx.toFloat(),
                    centerY = c.cy.toFloat(),
                    answerRadius = c.r.toFloat()
                )
            }
            for (c in mergedCircles) {
                Imgproc.circle(src, Point(c.cx, c.cy), limitRadius(c.r).toInt(), RED, 5)
            }

            val rects = contours
                .filter {
                    val contourArea = Imgproc.contourArea(it)
                    val minArea = (src.width() * src.height() * 0.00015)
                    contourArea >= minArea
                }
                .map {
                    // 근사화
                    val approxCurve = MatOfPoint2f()
                    val contour2f = MatOfPoint2f(*it.toArray())
                    val approxDistance = Imgproc.arcLength(contour2f, true) * 0.002
                    Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true)

                    val points = MatOfPoint(*approxCurve.toArray())

                    Imgproc.boundingRect(points)
                }.toMutableList()

            for (rect in rects) {
//                Imgproc.rectangle(
//                    src,
//                    Point(rect.x.toDouble(), rect.y.toDouble()),
//                    Point((rect.x + rect.width).toDouble(), (rect.y + rect.height).toDouble()),
//                    RED,
//                    5,
//                    1
//                )
            }

            Imgproc.cvtColor(src, src, Imgproc.COLOR_RGBA2BGR)
            return Answer(answerMat = src, answerPointList = pointList as ArrayList<com.seno.game.ui.main.home.game.diff_picture.model.Point>)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun isOverlapCircle(a: Circle, b: Circle): Boolean {
        val dx = a.cx - b.cx
        val dy = a.cy - b.cy
        val dist = sqrt(dx * dx + dy * dy)
        return dist <= (limitRadius(a.r) + limitRadius(b.r)) // 중심 거리 <= 반지름 합
    }

    fun unionCircle(a: Circle, b: Circle): Circle {
        val dx = b.cx - a.cx
        val dy = b.cy - a.cy
        val dist = sqrt(dx * dx + dy * dy)

        return if (dist + minOf(a.r, b.r) <= maxOf(a.r, b.r)) {
            // 한 원이 다른 원을 포함하는 경우 → 큰 원 유지
            if (a.r >= b.r) a else b
        } else {
            // 두 원을 포함하는 최소 원 계산
            val newR = (dist + a.r + b.r) / 2.0
            val t = (newR - a.r) / dist
            val newCx = a.cx + dx * t
            val newCy = a.cy + dy * t
            Circle(newCx, newCy, newR)
        }
    }

    fun mergeCircles(circles: MutableList<Circle>): List<Circle> {
        var merged = circles.toMutableList()
        var didMerge: Boolean

        do {
            didMerge = false
            val newMerged = mutableListOf<Circle>()
            val visited = BooleanArray(merged.size)

            for (i in merged.indices) {
                if (visited[i]) continue
                var current = merged[i]

                for (j in merged.indices) {
                    if (i == j || visited[j]) continue
                    if (isOverlapCircle(current, merged[j])) {
                        current = unionCircle(current, merged[j])
                        visited[j] = true
                        didMerge = true
                    }
                }
                newMerged.add(current)
                visited[i] = true
            }

            merged = newMerged
        } while (didMerge)

        return merged
    }

    //    fun limitRadius(radius: Double): Double = radius * 2 / 3
    fun limitRadius(radius: Double): Double = radius
}

data class Circle(val cx: Double, val cy: Double, val r: Double)
