package com.seno.game.util

import android.graphics.Bitmap
import com.seno.game.ui.game.diffgame.model.Answer
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import timber.log.Timber
const val RADIUS_CORRECTION = 20

class DiffPictureOpencvUtil {

    fun diff(srcBitmap: Bitmap, copyBitmap: Bitmap): Mat {
        val src = Mat()
        Utils.bitmapToMat(srcBitmap, src)

        val copy = Mat()
        Utils.bitmapToMat(copyBitmap, copy)

        val diffMat = Mat()
        Core.absdiff(src, copy, diffMat)


        Imgproc.cvtColor(diffMat, diffMat, Imgproc.COLOR_BGR2GRAY)

        val contours = ArrayList<MatOfPoint>()
        val hierarchy = Mat()

        Imgproc.findContours(
            diffMat,
            contours,
            hierarchy,
            Imgproc.RETR_EXTERNAL,
            Imgproc.CHAIN_APPROX_NONE
        )

        for (contourIdx in contours.indices) {
            val contourArea = Imgproc.contourArea(contours[contourIdx]) // 면적 구하기

            if (contourArea < 400) {
                continue
            }

            // 근사화
            val approxCurve = MatOfPoint2f()
            val contour2f = MatOfPoint2f(*contours[contourIdx].toArray())
            val approxDistance = Imgproc.arcLength(contour2f, true) * 0.02
            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true)

            //Convert back to MatOfPoint
            val points = MatOfPoint(*approxCurve.toArray())

            // Get bounding rect of contour
            val rect = Imgproc.boundingRect(points)
            Imgproc.rectangle(
                diffMat,
                Point(rect.x.toDouble(), rect.y.toDouble()),
                Point((rect.x + rect.width).toDouble(), (rect.y + rect.height).toDouble()),
                RED,
                5
            )
        }
        Imgproc.cvtColor(diffMat, diffMat, Imgproc.COLOR_RGB2BGR)
        return diffMat
    }

    fun drawCircle(srcBitmap: Bitmap?, copyBitmap: Bitmap?) : Answer? {

        if (srcBitmap == null || copyBitmap == null) {
            return null
        }
        try {
            val src = Mat()
            Utils.bitmapToMat(srcBitmap, src)

            val copy = Mat()
            Utils.bitmapToMat(copyBitmap, copy)

            val diffMat = Mat()
            Core.absdiff(src, copy, diffMat)
            Imgproc.cvtColor(diffMat, diffMat, Imgproc.COLOR_RGB2BGR)

            // contour를 통해서 꼭지점 좌표를 구하여 원을 그림
            val bin = Mat()
            Imgproc.cvtColor(diffMat, bin, Imgproc.COLOR_BGR2GRAY)
            Imgproc.threshold(bin, bin, 0.0, 255.0, Imgproc.THRESH_OTSU)

            val pointList = ArrayList<com.seno.game.ui.game.diffgame.model.Point>()
            val contours = ArrayList<MatOfPoint>()
            val hierarchy = Mat()

            Imgproc.findContours(
                bin,
                contours,
                hierarchy,
                Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_NONE
            )

            for (contourIdx in contours.indices) {
                val contourArea = Imgproc.contourArea(contours[contourIdx]) // 면적 구하기

                if (contourArea < 200) {
                    continue
                }

                // 근사화
                val approxCurve = MatOfPoint2f()
                val contour2f = MatOfPoint2f(*contours[contourIdx].toArray())
                val approxDistance = Imgproc.arcLength(contour2f, true) * 0.02
                Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true)

                //Convert back to MatOfPoint
                val points = MatOfPoint(*approxCurve.toArray())

                // Get bounding rect of contour
                val rect = Imgproc.boundingRect(points)

                val centerX = (rect.x + (rect.width / 2)).toDouble()
                val centerY = (rect.y + (rect.height / 2)).toDouble()

                pointList.add(com.seno.game.ui.game.diffgame.model.Point(
                    srcWidth = src.width().toFloat(),
                    srcHeight = src.height().toFloat(),
                    centerX = centerX.toFloat(),
                    centerY = centerY.toFloat(),
                    answerRadius = (rect.height.coerceAtLeast(rect.width) + RADIUS_CORRECTION).toFloat()
                ))

                Imgproc.circle(
                    src,
                    Point(centerX, centerY),
                    rect.height.coerceAtLeast(rect.width) + RADIUS_CORRECTION,
                    RED,
                    10
                )
            }
            Imgproc.cvtColor(src, src, Imgproc.COLOR_RGB2BGR)
            return Answer(answerMat = src, answerPointList = pointList)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun diff2(srcBitmap: Bitmap, copyBitmap: Bitmap): Mat {
        val src = Mat()
        Utils.bitmapToMat(srcBitmap, src)

        val copy = Mat()
        Utils.bitmapToMat(copyBitmap, copy)

        val diffMat = Mat()
        Core.absdiff(src, copy, diffMat)
//        Imgproc.cvtColor(diffMat, diffMat, Imgproc.COLOR_RGB2BGR)

        // contour를 통해서 꼭지점 좌표를 구하여 원을 그림
        val bin = Mat()
        Imgproc.cvtColor(diffMat, bin, Imgproc.COLOR_BGR2GRAY)
        Imgproc.threshold(bin, bin, 0.0, 255.0, Imgproc.THRESH_OTSU)

        val contours = ArrayList<MatOfPoint>()
        val hierarchy = Mat()

        Imgproc.findContours(
            bin,
            contours,
            hierarchy,
            Imgproc.RETR_EXTERNAL,
            Imgproc.CHAIN_APPROX_NONE
        )

        for (contourIdx in contours.indices) {
            val contourArea = Imgproc.contourArea(contours[contourIdx]) // 면적 구하기

            if (contourArea < 200) {
                continue
            }

            // 근사화
            val approxCurve = MatOfPoint2f()
            val contour2f = MatOfPoint2f(*contours[contourIdx].toArray())
            val approxDistance = Imgproc.arcLength(contour2f, true) * 0.02
            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true)

            //Convert back to MatOfPoint
            val points = MatOfPoint(*approxCurve.toArray())

            // Get bounding rect of contour
            val rect = Imgproc.boundingRect(points)

            val centerX = (rect.x + (rect.width / 2)).toDouble()
            val centerY = (rect.y + (rect.height / 2)).toDouble()


            Imgproc.circle(
                src,
                Point(centerX, centerY),
                rect.height.coerceAtLeast(rect.width) + RADIUS_CORRECTION,
                RED,
                10
            )
        }
        Imgproc.cvtColor(src, src, Imgproc.COLOR_RGB2BGR)
        return src
    }
}

