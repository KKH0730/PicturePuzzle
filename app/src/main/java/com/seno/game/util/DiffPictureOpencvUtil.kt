package com.seno.game.util

import android.graphics.Bitmap
import com.seno.game.ui.main.home.game.diff_picture.model.Answer
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import timber.log.Timber
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt


//지브리 화풍으로 그린 애완동물 미용실을 1:1 비율로 보여줘.
//강아지·고양이 4~6마리, 미용사 2명 포함.
//가위, 드라이기, 브러시, 목욕통, 간식 바구니, 장난감, 수건, 리본, 선반, 스프레이, 가운 등 작은 사물을 풍부하게 배치.
//
//이미지 비율은 1:1
//이미지 스타일이나 사물 배치, 색감, 빛의 패턴이 반복되지 않도록 해줘.
//주변 사물은 다양한 형태와 크기, 색감을 사용해 자연스럽고 불규칙하게 배치해줘.
//동일한 사물이 일정 간격으로 반복되지 않도록 하고, 배경 텍스처나 조명 패턴도 규칙성이 없도록 만들어줘.
//지브리 스타일은 유지하되, 장면 전체가 너무 대칭적이거나 일정한 간격으로 구성되지 않도록 해줘.


//Please edit the attached image according to the following rules(For your information, This image is intended for a spot-the-difference game and is not the original source image):
//
//1. Change 5 incorrect parts in the image using one of the three methods below:
//- Replace an object with other object. If the object you want to modify is part of a pair, change only one of them.
//- Don't Change the color of a part of an object.
//- Subtly add a new object to the attached image.
//- Intangible objects like sunlight or rainbows are excluded from being changed or added.
//2. Place the 5 altered parts as far apart from each other as possible, because if the altered parts are too close together, the player may discover multiple differences at once.
//3. Ensure that each altered part is relatively small in size and not elongated; avoid long or stretched objects.
//4. Maintain sufficient distance between each altered part so that they are visually separate and will not merge when detected using contours in image processing.
//5. Keep all other parts of the image unchanged.
//6. Preserve the composition, alignment, and spacing exactly as in the original image.
//7. Do not change the overall brightness, tone, lighting, or color balance of the image.

//이미지 비율은 1:1.
//신카이 마코토 특유의 투명하고 맑은 색채감, 영화적 광원, 깊은 원근감, 공기 중에 빛이 흩어지는 듯한 대기광, 반사광과 하이라이트가 세밀하게 표현된 스타일을 강조해줘.
//사물은 최소 30개 이상으로 매우 빽빽하게 배치해 난이도가 높도록 만들어줘.
//이미지 스타일, 사물 배치, 색감, 빛의 패턴이 반복되지 않도록 해줘.
//동일한 사물이 일정 간격으로 반복되거나 규칙적인 텍스처·패턴이 생기지 않도록 하고, 배경 텍스처나 조명 패턴 또한 규칙성을 없애 자연스럽고 불규칙한 장면을 구성해줘.
//주변 사물은 다양한 형태·크기·색감을 사용해 랜덤하고 자연스럽게 흩뿌리듯 배치해줘.
//인물은 있을 수도 없을 수도 있으며, 등장 시 자연스럽게 배치해줘.
//
//공항 수하물 처리 구역 – 컨베이어, 상자, 케이블, 장치, 작은 장치

const val RADIUS_CORRECTION = 5
const val CIRCLE_THICKNESS = 3
class DiffPictureOpencvUtil {
    /**
     * HSV + CLAHE + 밝기 정규화 + Gamma 보정 + 블러 + absdiff 방식
     */
    fun getDiffAnswer(srcBitmap: Bitmap?, copyBitmap: Bitmap?) : Answer? {
        if (srcBitmap == null || copyBitmap == null) {
            return null
        }

        try {
            val src = Mat()
            Utils.bitmapToMat(srcBitmap, src)

            val copy = Mat()
            Utils.bitmapToMat(copyBitmap, copy)

            // Step 1 - HSV 변환
            val hsv1 = Mat()
            val hsv2 = Mat()
            Imgproc.cvtColor(src, hsv1, Imgproc.COLOR_BGR2HSV)
            Imgproc.cvtColor(copy, hsv2, Imgproc.COLOR_BGR2HSV)

            val hsvSplit1 = ArrayList<Mat>()
            val hsvSplit2 = ArrayList<Mat>()
            Core.split(hsv1, hsvSplit1)
            Core.split(hsv2, hsvSplit2)

            // Step 2 - CLAHE 적용 (V 채널, 대비 향상)
            val clahe = Imgproc.createCLAHE(2.0, Size(8.0, 8.0))
            clahe.apply(hsvSplit1[2], hsvSplit1[2])
            clahe.apply(hsvSplit2[2], hsvSplit2[2])

            // Step 3 - 평균/표준편차 정규화 (두 이미지 밝기 맞춤)
            val meanStd1 = MatOfDouble()
            val stddev1 = MatOfDouble()
            Core.meanStdDev(hsvSplit1[2], meanStd1, stddev1)

            val meanStd2 = MatOfDouble()
            val stddev2 = MatOfDouble()
            Core.meanStdDev(hsvSplit2[2], meanStd2, stddev2)

            val meanDiff = meanStd1[0,0][0] - meanStd2[0,0][0]
            Core.add(hsvSplit2[2], Scalar(meanDiff), hsvSplit2[2])

            Core.merge(hsvSplit1, hsv1)
            Core.merge(hsvSplit2, hsv2)

            // Step 4 - HSV -> BGR 변환
            val normSrc = Mat()
            val normCopy = Mat()
            Imgproc.cvtColor(hsv1, normSrc, Imgproc.COLOR_HSV2BGR)
            Imgproc.cvtColor(hsv2, normCopy, Imgproc.COLOR_HSV2BGR)

            // Step 5 - 정규화 (전체 명도/색상 범위를 0~255로 맞춤)
            Core.normalize(normSrc, normSrc, 0.0, 255.0, Core.NORM_MINMAX)
            Core.normalize(normCopy, normCopy, 0.0, 255.0, Core.NORM_MINMAX)

            // Step 6 - Gamma 보정 (선택: 밝기 비율 맞춤)
            fun gammaCorrection(srcMat: Mat, gamma: Double): Mat {
                val lut = Mat(1, 256, CvType.CV_8UC1)
                val buffer = ByteArray(1)

                for (i in 0..255) {
                    val v = ((i / 255.0).pow(1.0 / gamma) * 255.0).roundToInt().coerceIn(0, 255)
                    buffer[0] = v.toByte()
                    lut.put(0, i, buffer)
                }

                val dst = Mat()
                Core.LUT(srcMat, lut, dst)
                return dst
            }

            val gamma = 1.0  // 필요에 따라 0.9~1.1 정도 조정 가능
            val gammaSrc = gammaCorrection(normSrc, gamma)
            val gammaCopy = gammaCorrection(normCopy, gamma)

            // Step 7 - Gray 변환 + Gaussian Blur
            val graySrc = Mat()
            val grayCopy = Mat()
            Imgproc.cvtColor(gammaSrc, graySrc, Imgproc.COLOR_BGR2GRAY)
            Imgproc.cvtColor(gammaCopy, grayCopy, Imgproc.COLOR_BGR2GRAY)
            Imgproc.GaussianBlur(graySrc, graySrc, Size(5.0, 5.0), 1.5)
            Imgproc.GaussianBlur(grayCopy, grayCopy, Size(5.0, 5.0), 1.5)

            // Step 8 - absdiff + Threshold + Morphology (차이 영역 추출)
            val grayDiffMat = Mat()
            Core.absdiff(graySrc, grayCopy, grayDiffMat)

//            return Answer(answerMat = grayDiffMat, answerPointList = arrayListOf())

            val threshMat = Mat()
            Imgproc.threshold(grayDiffMat, threshMat, 25.0, 255.0, Imgproc.THRESH_BINARY)

//            return Answer(answerMat = threshMat, answerPointList = arrayListOf())

            val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(15.0, 15.0))
            Imgproc.morphologyEx(threshMat, threshMat, Imgproc.MORPH_CLOSE, kernel)

            // Step 9 - Contour 검출
            val contours = ArrayList<MatOfPoint>()
            val hierarchy = Mat()
            Imgproc.findContours(threshMat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)

            // Step 10 - Contour → Circle 변환 및 병합
            val circles = contours
                .filter {
                    val contourArea = Imgproc.contourArea(it)
                    val minArea = (src.width() * src.height() * 0.00015)
                    val maxArea = (src.width() * src.height() * 0.05)

                    if (contourArea < minArea || contourArea > maxArea) return@filter false

                    val rect = Imgproc.boundingRect(it)
                    val aspectRatio = rect.width.toDouble() / rect.height.toDouble()
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
                .let { mergeCircles(it) }

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
                Imgproc.circle(src, Point(c.cx, c.cy), c.r.toInt(), RED, CIRCLE_THICKNESS)
            }

            // Step 11 - BoundingRect 그리기
            val rects = contours
                .filter {
                    val contourArea = Imgproc.contourArea(it)
                    val minArea = (src.width() * src.height() * 0.00015)
                    val maxArea = (src.width() * src.height() * 0.05)
                    if (contourArea < minArea || contourArea > maxArea) return@filter false

                    val rect = Imgproc.boundingRect(it)
                    val aspectRatio = rect.width.toDouble() / rect.height.toDouble()
                    if (aspectRatio < 0.1 || aspectRatio > 10.0) return@filter false
                    true
                }
                .map { Imgproc.boundingRect(it) }

            for (rect in rects) {
                Imgproc.rectangle(
                    src,
                    Point(rect.x.toDouble(), rect.y.toDouble()),
                    Point((rect.x + rect.width).toDouble(), (rect.y + rect.height).toDouble()),
                    RED,
                    2,
                    Imgproc.LINE_AA
                )
            }

            Imgproc.cvtColor(src, src, Imgproc.COLOR_RGBA2BGR)
            return Answer(answerMat = src, answerPointList = ArrayList(pointList))
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
        return dist <= (a.r + b.r) + (CIRCLE_THICKNESS * 2)
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

//        fun limitRadius(radius: Double): Double = radius * 2 / 3
    fun limitRadius(radius: Double): Double = radius + RADIUS_CORRECTION


    /**
     *  그레이 변환 + 블러 + absdiff 방식
     **/
    fun getDiffAnswerr(srcBitmap: Bitmap?, copyBitmap: Bitmap?) : Answer? {
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

//            return Answer(answerMat = grayDiffMat, answerPointList = arrayListOf())

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
//                Imgproc.circle(src, Point(c.cx, c.cy), c.r.toInt(), RED, 5)
            }

            val rects = contours
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
                    Imgproc.boundingRect(it) // 근사화 불필요
                }

            for (rect in rects) {
                Imgproc.rectangle(
                    src,
                    Point(rect.x.toDouble(), rect.y.toDouble()),
                    Point((rect.x + rect.width).toDouble(), (rect.y + rect.height).toDouble()),
                    RED,
                    2,                     // 두께 조절
                    Imgproc.LINE_AA        // 안티에일리어싱 적용
                )
            }

            Imgproc.cvtColor(src, src, Imgproc.COLOR_RGBA2BGR)
            return Answer(answerMat = src, answerPointList = pointList as ArrayList<com.seno.game.ui.main.home.game.diff_picture.model.Point>)
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.e(e)
            return null
        }
    }
}

data class Circle(val cx: Double, val cy: Double, val r: Double)
