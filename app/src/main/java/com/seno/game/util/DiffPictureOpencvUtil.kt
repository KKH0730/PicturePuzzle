package com.seno.game.util

import android.graphics.Bitmap
import com.seno.game.ui.main.home.game.diff_picture.model.Answer
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import timber.log.Timber
import kotlin.math.sqrt


//지브리 화풍 이미지를 자유롭게 생성해줘. 단, 오브젝트를 많이 배치해서 복잡해보이도록.
//틀린그림찾기용 이미지의 원본 이미지를 만드는거고, 틀린그림용 이미지는 따로 다시 만들거야.
//Q
//신카이 마코토 애니메이션 스타일로, 가까운 시점에서 바라본 좁은 공간을 그린 이미지. 실내의 책상 위나 작은 카페 코너, 작업 공간, 작은 정원 등 한정된 공간 안에 테이블, 의자, 책, 식물, 컵, 조명, 소품 등 다양한 오브젝트가 빼곡히 배치되어 있어 복잡한 느낌을 줌. 카메라 앵글은 인물 키 높이 또는 약간 낮은 시점에서 근거리로 바라본 시점이며, 선명하고 디테일이 풍부함. 신카이 마코토 특유의 부드러운 빛 표현과 따뜻한 색감, 사실적인 디테일이 강조됨. 틀린그림찾기용으로 적합하게, 화면 곳곳에 다양한 물건과 시각 요소가 많이 배치된 장면.

//Q
//Please edit the attached image according to the following rules(For your information, This image is intended for a spot-the-difference game and is not the original source image):
//
//1. Change 4–5 incorrect parts in the image using one of the three methods below:
//- Don't replace an object with other object.
//- Don't Change the color of a part of an object.
//- Subtly add a new object to the attached image.
//- Intangible objects like sunlight or rainbows are excluded from being changed or added.
//2. Place the 4–5 altered parts as far apart from each other as possible.
//3. Keep all other parts of the image unchanged.
//4. Preserve the composition, alignment, and spacing exactly as in the original image.
//5. This image is intended for a spot-the-difference game and is not the original source image.


const val RADIUS_CORRECTION = 5
const val CIRCLE_THICKNESS = 5
class DiffPictureOpencvUtil {

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
//            val gray = Mat()
//            Imgproc.cvtColor(diffMat, gray, Imgproc.COLOR_BGR2GRAY)
//
//            Imgproc.GaussianBlur(gray, gray, Size(5.0, 5.0), 0.0) // 커널 크기(5x5) 조절 가능
//
//            // 2️⃣. 임계값(Threshold) 적용 -> 차이가 있는 부분만 흰색(255), 나머지는 검은색(0)
//            val thresh = Mat()
//            Imgproc.threshold(gray, thresh, 30.0, 255.0, Imgproc.THRESH_BINARY)
////            Imgproc.threshold(bin, bin, 0.0, 255.0, Imgproc.THRESH_OTSU)
//
//            // 3️⃣ Morphological CLOSE 연산 (작은 구멍 제거, 인접 영역 병합)
//            val kernelSize = 7.0 // 5.0 ~ 15.0 사이에서 테스트 필요
//            val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(kernelSize, kernelSize))
//
////            val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(15.0, 15.0))
////            Imgproc.morphologyEx(thresh, thresh, Imgproc.MORPH_CLOSE, kernel)
//
//            // 4️⃣ Contour 검출
//            val contours = ArrayList<MatOfPoint>()
//            val hierarchy = Mat()
//            Imgproc.findContours(thresh, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)
////            Imgproc.findContours(thresh, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE)
//
//            // 5️⃣ Contour → Circle 변환
//            val circles = contours
//                .filter {
//                    val contourArea = Imgproc.contourArea(it)
//                    val minArea = (src.width() * src.height() * 0.00015) // 최소 면적 (현재 유지)
//                    val maxArea = (src.width() * src.height() * 0.05) // 최대 면적 (추가: 이미지의 5% 초과 영역 제거)
//
//                    if (contourArea < minArea || contourArea > maxArea) return@filter false
//
//                    // Bounding Box 계산
//                    val rect = Imgproc.boundingRect(it)
//                    val aspectRatio = rect.width.toDouble() / rect.height.toDouble()
//
//                    // 종횡비 필터: 너무 길거나 얇은 형태 제거 (0.1 ~ 10.0 사이)
//                    if (aspectRatio < 0.1 || aspectRatio > 10.0) return@filter false
//
//                    true
//                }
//                .map {
//                    val contour2f = MatOfPoint2f(*it.toArray())
//                    val center = Point()
//                    val radius = FloatArray(1)
//                    Imgproc.minEnclosingCircle(contour2f, center, radius)
//                    Circle(center.x, center.y, radius[0].toDouble())
//                }
//                .toMutableList()
//                .let { mergeCircles(it) } // 원들 merge
//
//            val pointList = circles.map { c ->
//                com.seno.game.ui.main.home.game.diff_picture.model.Point(
//                    rectX = (c.cx - c.r).toFloat(),
//                    rectY = (c.cy - c.r).toFloat(),
//                    rectWidth = (c.r * 2).toFloat(),
//                    rectHeight = (c.r * 2).toFloat(),
//                    srcWidth = src.width().toFloat(),
//                    srcHeight = src.height().toFloat(),
//                    centerX = c.cx.toFloat(),
//                    centerY = c.cy.toFloat(),
//                    answerRadius = c.r.toFloat()
//                )
//            }
//            for (c in circles) {
//                Imgproc.circle(src, Point(c.cx, c.cy), c.r.toInt(), RED, 5)
//            }
//
//            val rects = contours
//                .filter {
//                    val contourArea = Imgproc.contourArea(it)
//                    val minArea = maxOf(100.0, src.width() * src.height() * 0.0001)
//                    contourArea >= minArea
//                }
//                .map {
//                    Imgproc.boundingRect(it) // 근사화 불필요
//                }
//
//            for (rect in rects) {
//                Imgproc.rectangle(
//                    src,
//                    Point(rect.x.toDouble(), rect.y.toDouble()),
//                    Point((rect.x + rect.width).toDouble(), (rect.y + rect.height).toDouble()),
//                    RED,
//                    3,                     // 두께 조절
//                    Imgproc.LINE_AA        // 안티에일리어싱 적용
//                )
//            }
//
//            Imgproc.cvtColor(src, src, Imgproc.COLOR_RGBA2BGR)
//            return Answer(answerMat = src, answerPointList = pointList as ArrayList<com.seno.game.ui.main.home.game.diff_picture.model.Point>)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Timber.e(e)
//            return null
//        }
//    }

    fun getDiffAnswer(srcBitmap: Bitmap?, copyBitmap: Bitmap?) : Answer? {
        if (srcBitmap == null || copyBitmap == null) {
            return null
        }

        try {
            val src = Mat()
            Utils.bitmapToMat(srcBitmap, src)

            val copy = Mat()
            Utils.bitmapToMat(copyBitmap, copy)

            val graySrc = Mat()
            val grayCopy = Mat()
            Imgproc.cvtColor(src, graySrc, Imgproc.COLOR_BGR2GRAY)
            Imgproc.cvtColor(copy, grayCopy, Imgproc.COLOR_BGR2GRAY)

            Imgproc.GaussianBlur(graySrc, graySrc, Size(5.0, 5.0), 1.5)
            Imgproc.GaussianBlur(grayCopy, grayCopy, Size(5.0, 5.0), 1.5)

            val grayDiffMat = Mat()
            Core.absdiff(graySrc, grayCopy, grayDiffMat)

            // 2️⃣. 임계값(Threshold) 적용 -> 차이가 있는 부분만 흰색(255), 나머지는 검은색(0)
            val threshMat = Mat()
            Imgproc.threshold(grayDiffMat, threshMat, 15.0, 255.0, Imgproc.THRESH_BINARY)

            // ✅ 추가 1: 팽창(dilate) + 침식(erode) 또는 closing(모폴로지 닫기) 처리
            val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(15.0, 15.0))
            Imgproc.morphologyEx(threshMat, threshMat, Imgproc.MORPH_CLOSE, kernel)

            // 4️⃣ Contour 검출
            val contours = ArrayList<MatOfPoint>()
            val hierarchy = Mat()
            Imgproc.findContours(threshMat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)

            // 5️⃣ Contour → Circle 변환
            val circles = contours
                .filter {
                    val contourArea = Imgproc.contourArea(it)
                    val minArea = (src.width() * src.height() * 0.00015) // 최소 면적 (현재 유지)
                    val maxArea = (src.width() * src.height() * 0.05) // 최대 면적 (추가: 이미지의 5% 초과 영역 제거)

                    if (contourArea < minArea || contourArea > maxArea) return@filter false

                    // Bounding Box 계산
                    val rect = Imgproc.boundingRect(it)
                    val aspectRatio = rect.width.toDouble() / rect.height.toDouble()

                    // 종횡비 필터: 너무 길거나 얇은 형태 제거 (0.1 ~ 10.0 사이)
                    if (aspectRatio < 0.1 || aspectRatio > 10.0) return@filter false

                    true
                }
                .map {
                    val contour2f = MatOfPoint2f(*it.toArray())
                    val center = Point()
                    val radius = FloatArray(1)
                    Imgproc.minEnclosingCircle(contour2f, center, radius)
                    Circle(center.x, center.y, radius[0].toDouble())
                }
                .toMutableList()
                .let { mergeCircles(it) } // 원들 merge

            val pointList = circles.map { c ->
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
            for (c in circles) {
                Imgproc.circle(src, Point(c.cx, c.cy), c.r.toInt(), RED, 5)
            }

            val rects = contours
                .filter {
                    val contourArea = Imgproc.contourArea(it)
                    val minArea = maxOf(100.0, src.width() * src.height() * 0.0001)
                    contourArea >= minArea
                }
                .map {
                    Imgproc.boundingRect(it) // 근사화 불필요
                }

            for (rect in rects) {
//                Imgproc.rectangle(
//                    src,
//                    Point(rect.x.toDouble(), rect.y.toDouble()),
//                    Point((rect.x + rect.width).toDouble(), (rect.y + rect.height).toDouble()),
//                    RED,
//                    5,                     // 두께 조절
//                    Imgproc.LINE_AA        // 안티에일리어싱 적용
//                )
            }

            Imgproc.cvtColor(src, src, Imgproc.COLOR_RGBA2BGR)
            return Answer(answerMat = src, answerPointList = pointList as ArrayList<com.seno.game.ui.main.home.game.diff_picture.model.Point>)
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.e(e)
            return null
        }
    }

    fun isOverlapCircle(a: Circle, b: Circle): Boolean {
        val dx = a.cx - b.cx
        val dy = a.cy - b.cy
        val dist = sqrt(dx * dx + dy * dy)
        // 반지름 + 원두께까지 고려하여
        return dist <= (a.r + b.r)
//        + (CIRCLE_THICKNESS * 2)
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
    fun limitRadius(radius: Double): Double = radius + RADIUS_CORRECTION
}

data class Circle(val cx: Double, val cy: Double, val r: Double)
