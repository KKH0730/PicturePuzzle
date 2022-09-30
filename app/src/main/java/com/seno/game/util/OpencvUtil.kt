package com.seno.game.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import com.seno.game.R
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

val GREEN = Scalar(0.0, 255.0, 0.0)
val RED = Scalar(0.0, 0.0, 100.0)

enum class GameColor() {
    GREEN,
    RED,
    BLUE
}

object OpencvUtil {
    // 윤곽선 그리기 및 면적 구하기
    fun drawBorder(mat: Mat): Mat {
        val src = Mat()
        mat.copyTo(src)

        val bin = Mat()
        Imgproc.cvtColor(src, bin, Imgproc.COLOR_BGR2GRAY)
        Imgproc.threshold(bin, bin, 0.0, 255.0, Imgproc.THRESH_OTSU)

        val contours = ArrayList<MatOfPoint>()
        val hierarchy = Mat()

        Imgproc.findContours(
            bin,
            contours,
            hierarchy,
            Imgproc.RETR_TREE,
            Imgproc.CHAIN_APPROX_SIMPLE
        )

        for (i in 0 until contours.size) {
            val contour = contours[i] // 윤곽선 좌표
            val contourArea = Imgproc.contourArea(contour) // 면적 구하기

            if (contourArea < 400) {
                continue
            }

            // 도형위에 윤곽선 그리기
            Imgproc.drawContours(src, contours, i, RED, 5, Imgproc.LINE_4, hierarchy, 0)
        }

        return src
    }

    private fun getPixelCount(src: Mat, maxPixel: Int): Int {
        val mat = Mat()

        Imgproc.cvtColor(src, mat, Imgproc.COLOR_BGR2GRAY)
        Imgproc.threshold(mat, mat, 150.0, 255.0, Imgproc.THRESH_BINARY)
        val size = Core.countNonZero(mat)
        return maxPixel - size
    }

    private fun getMask(bitmap: Bitmap, gameColor: GameColor): Mat {
        val src = Mat()
        Utils.bitmapToMat(bitmap, src)

        val srcHSV = Mat()
        val mask = Mat()

        Imgproc.cvtColor(src, srcHSV, Imgproc.COLOR_RGB2HSV)
        val lowerb = when (gameColor) {
            GameColor.GREEN -> {
                Scalar(50.0, 100.0, 100.0)
            }
            GameColor.RED -> {
                Scalar(-10.0, 100.0, 100.0)
            }
            else -> {
                Scalar(110.0, 100.0, 100.0)
            }
        }

        val upperb = when (gameColor) {
            GameColor.GREEN -> {
                Scalar(70.0, 255.0, 255.0)
            }
            GameColor.RED -> {
                Scalar(10.0, 255.0, 255.0)
            }
            else -> {
                Scalar(130.0, 255.0, 255.0)
            }
        }

        Core.inRange(srcHSV, lowerb, upperb, mask)
        return mask
    }

    // 특정 범위의 색상 추출
    @SuppressLint("UseCompatLoadingForDrawables")
    fun extractColor(bitmap: Bitmap, gameColor: GameColor, context: Context): Pair<Mat, Int> {
        val mask = getMask(bitmap = bitmap, gameColor = gameColor)

        val colorDrawable = when (gameColor) {
            GameColor.GREEN -> context.getDrawable(R.drawable.bg_color_green)
            GameColor.RED -> context.getDrawable(R.drawable.bg_color_red)
            else -> context.getDrawable(R.drawable.bg_color_blue)
        }
        val colorBitmap = colorDrawable?.toBitmap(
            width = mask.width(),
            height = mask.height()
        )
        val colorMat = Mat()
        Utils.bitmapToMat(colorBitmap, colorMat)

        val whiteDrawable = context.getDrawable(R.drawable.bg_color_white)
        val whiteBitmap = whiteDrawable?.toBitmap(
            width = mask.width(),
            height = mask.height()
        )
        val whiteMat = Mat()
        Utils.bitmapToMat(whiteBitmap, whiteMat)

        Core.copyTo(colorMat, whiteMat, mask) // 마스크에 합성할 이미지, 원본이미지(합성 되어질 원본 이미지), 마스크
        Imgproc.cvtColor(whiteMat, whiteMat, Imgproc.COLOR_RGB2BGR)

        return whiteMat to getPixelCount(src = whiteMat, maxPixel = mask.rows() * mask.cols())
    }
}